package org.cat73.catbase.context;

import org.bukkit.plugin.Plugin;
import org.cat73.catbase.CatBase;
import org.cat73.catbase.annotation.Bean;
import org.cat73.catbase.annotation.CatPlugin;
import org.cat73.catbase.annotation.Inject;
import org.cat73.catbase.annotation.PostConstruct;
import org.cat73.catbase.util.Lang;
import org.cat73.catbase.util.reflect.Reflects;
import org.cat73.catbase.util.reflect.Scans;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件上下文的管理器
 */
public final class PluginContextManager {
    /**
     * 搜索调用树时忽略的包(框架自身)
     */
    private static final String IGNORE_PACKAGE = "org.cat73.catbase";
    /**
     * 插件到上下文的缓存
     */
    private static final Map<Plugin, PluginContext> plugin2context = Collections.synchronizedMap(new HashMap<>());
    /**
     * ProtectionDomain 到上下文的缓存
     */
    private static final Map<ProtectionDomain, PluginContext> protectionDomain2Context = Collections.synchronizedMap(new HashMap<>());
    /**
     * 类名到上下文的缓存
     */
    private static final Map<String, PluginContext> className2Context = Collections.synchronizedMap(new HashMap<>());
    /**
     * 是否允许注册插件
     */
    private static boolean allowRegister = true;

    /**
     * 注册一个插件，会自动忽略无本框架注解的插件
     * @param plugin 插件的主类
     */
    public static void register(@Nonnull Plugin plugin) {
        if (!allowRegister) {
            return;
        }
        if (plugin2context.containsKey(plugin)) {
            return;
        }
        if (!plugin.getClass().isAnnotationPresent(CatPlugin.class)) {
            return;
        }

        // 初始化这个插件的上下文，并存到各个缓存中
        PluginContext context = PluginContext.valueOf(plugin);
        plugin2context.put(plugin, context);
        protectionDomain2Context.put(context.getProtectionDomain(), context);
        className2Context.put(plugin.getClass().getName(), context);
    }

    /**
     * 初始化被注册的插件
     * <p>一旦调用此方法，注册方法将不可用</p>
     */
    public static void initialize() {
        // 禁用注册插件
        allowRegister = false;

        try {
            // 步骤1 - 创建所有 Bean
            createBeans();

            // 步骤2 - 执行自动依赖注入
            injectionDependencies();

            // 步骤3 - 执行自动注册
            autoRegister();

            // 步骤4 - 调用各个实例的初始化方法
            invokePostConstructs();
        } catch (Exception e) {
            throw CatBase.startupFail("启动失败", e);
        }
    }

    /**
     * 初始化 - 步骤1 - 创建所有 Bean
     */
    // TODO 支持按需初始化，如 @NMSVersion
    private static void createBeans() {
        // 遍历插件
        forEachPlugins(context -> {
            // 循环创建 Bean
            Map<Class<?>, Object> beans = new HashMap<>();
            for (Class<?> clazz : context.getPluginAnnotation().classes()) {
                createBean(clazz, beans);
            }
            // 如果启用了自动扫描，则以插件主类为引，进行自动扫描
            if (context.getPluginAnnotation().autoScanPackage()) {
                for (Class<?> clazz : Scans.scanClass(context.getPlugin().getClass())) {
                    // 如果包含 @Bean 注解，则自动创建这个 Bean
                    if (clazz.isAnnotationPresent(Bean.class)) {
                        createBean(clazz, beans);
                    }
                }
            }
            // 保存 Bean 列表到 Context 中
            context.setBeans(Collections.unmodifiableMap(beans));
        });
    }

    private static void createBean(Class<?> clazz, Map<Class<?>, Object> beans) {
        // TODO 或许应该要求 Class 上带注解？然后就可以给起名字了
        //   以及可能可以给默认名称
        // 重复的 Class 跳过
        if (clazz == null || beans.containsKey(clazz)) {
            return;
        }
        // 创建并保存 Bean
        try {
            beans.put(clazz, Reflects.newInstance(clazz));
        } catch (Exception e) {
            throw CatBase.startupFail("实例化 Bean %s 失败", e, clazz.getName());
        }
    }

    /**
     * 初始化 - 步骤2 - 执行自动依赖注入
     */
    private static void injectionDependencies() {
        // 遍历 Bean
        forEachBeans((context, bean) -> {
            // 搜索并遍历包含注入注解的字段
            Reflects.forEachDeclaredFieldByAnnotation(bean.getClass(), Inject.class, (field, annotation) -> {
                // 根据类型搜索注入的属性
                Class<?> type = field.getType();
                Object injectBean = context.resolveBean(type);
                if (injectBean == null) {
                    throw CatBase.startupFail("无法解决 Bean %s 的依赖 %s，其类型为 %s", null, bean.getClass().getName(), field.getName(), type.getName());
                }

                // 设置权限
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                // 注入内容
                try {
                    field.set(bean, injectBean);
                } catch (IllegalAccessException e) {
                    throw Lang.noImplement();
                }
            });
        });
    }

    /**
     * 初始化 - 步骤3 - 自动注册
     * <p>目前支持注册：</p>
     * <ul>
     *     <!-- TODO <li>Command</li> -->
     *     <li>Listener</li>
     *     <!-- TODO <li>Task</li> -->
     * </ul>
     */
    private static void autoRegister() {
        // 遍历 Bean
        forEachBeans((context, bean) -> {
            context.getListenerManager().register(context, bean);
            context.getScheduleManager().register(context, bean);
        });
    }

    /**
     * 初始化 - 步骤4 - 调用初始化方法
     */
    private static void invokePostConstructs() {
        // 遍历 Bean
        forEachBeans((context, bean) -> {
            // 搜索并遍历包含初始化注解的方法
            Reflects.forEachMethodByAnnotation(bean.getClass(), PostConstruct.class, (method, annotation) -> {
                // 调用初始化方法
                try {
                    method.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw Lang.wrapThrow(e);
                }
            });
        });
    }

    // TODO javadoc
    private static void forEachPlugins(@Nonnull Lang.ThrowableConsumer<PluginContext> action) {
        for (PluginContext context : plugin2context.values()) {
            action.wrap().accept(context);
        }
    }

    // TODO javadoc
    private static void forEachBeans(@Nonnull Lang.ThrowableBiConsumer<PluginContext, Object> action) {
        for (PluginContext context : plugin2context.values()) {
            for (Object bean : context.getBeans().values()) {
                action.wrap().accept(context, bean);
            }
        }
    }

    /**
     * 从调用树中分析查找出调用插件的上下文
     * @return 调用插件的上下文
     * @throws NullPointerException 如果本框架有 bug 或非插件的第三方包直接调用了本方法
     */
    @Nonnull
    public static PluginContext current() throws NullPointerException {
        // 获取当前的调用树
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

        int lastIdx = stackTraces.length - 1;
        // 遍历调用树，查找插件
        for (int i = 1; i <= lastIdx; i++) {
            StackTraceElement stackTrace = stackTraces[i];
            // 如果不在忽略的包下面(本框架的包)，则尝试获取插件的上下文
            if (!stackTrace.getClassName().startsWith(IGNORE_PACKAGE)) {
                PluginContext context = getContextByClass(stackTrace.getClassName());
                // 获取到则返回
                if (context != null) {
                    return context;
                }
            }
        }

        // 不要输出 null，因为会调用这个的都是被框架管理的类
        // 每个都去让用户自己判断 null 的话毫无意义又超级麻烦
        // 干脆在这里直接干掉 null 即可，码农看到的话要么会修 bug 要么会给这个框架提 bug
        throw new NullPointerException();
    }

    /**
     * 通过类名查找插件并返回插件的上下文
     * @param className 类名
     * @return 如果这个类在被本框架管理的插件中，则返回这个插件的上下文，否则返回 null
     */
    @Nullable
    private static PluginContext getContextByClass(@Nonnull String className) {
        // 查找缓存
        PluginContext context = className2Context.get(className);

        // 若缓存中未找到，则尝试获取
        if (context == null && !className2Context.containsKey(className)) {
            try {
                ProtectionDomain protectionDomain = Class.forName(className).getProtectionDomain();
                context = protectionDomain2Context.get(protectionDomain);
                // 哪怕没获取到也要 put 进去，这样下次就只需要低成本的一次 get、一次 containsKey 即可，无需再尝试获取
                className2Context.put(className, context);
            } catch (ClassNotFoundException e) {
                throw Lang.impossible();
            }
        }

        // 返回结果
        return context;
    }
}
