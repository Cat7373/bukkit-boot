package org.cat73.bukkitboot.context;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.plugin.Plugin;
import org.cat73.bukkitboot.BukkitBoot;
import org.cat73.bukkitboot.annotation.BukkitBootPlugin;
import org.cat73.bukkitboot.command.CommandManager;
import org.cat73.bukkitboot.context.bean.BeanInfo;
import org.cat73.bukkitboot.listener.ListenerManager;
import org.cat73.bukkitboot.schedule.ScheduleManager;
import org.cat73.bukkitboot.util.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件的上下文
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PluginContext {
    /**
     * 插件主类的实例
     */
    private final Plugin plugin;
    /**
     * 插件主类上的插件注解
     */
    private final BukkitBootPlugin pluginAnnotation;
    /**
     * ProtectionDomain 的实例，用于标识同一个包里的类
     */
    private final ProtectionDomain protectionDomain;
    /**
     * Listener 管理器
     */
    private final ListenerManager listenerManager = new ListenerManager();
    /**
     * 定时任务管理器
     */
    private final ScheduleManager scheduleManager = new ScheduleManager();
    /**
     * 命令管理器
     */
    private final CommandManager commandManager = new CommandManager();

    /**
     * Bean 列表
     */
    private List<BeanInfo> beans = new ArrayList<>();
    /**
     * 基于 name 的 Bean 速查表
     */
    private Map<String, BeanInfo> name2Bean = new HashMap<>();

    /**
     * 注册一个 Bean
     * @param bean 被注册的 Bean
     * @param name Bean 的名字，如果为 null 或为空字符串，则会将其 Class 的名字转驼峰式后作为名字使用
     */
    void registerBean(@Nonnull Object bean, @Nullable String name) {
        // bean name
        String beanName = name;
        if (Strings.isEmpty(beanName)) {
            beanName = bean.getClass().getSimpleName();
            beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
        }

        // 校验重复
        if (name2Bean.containsKey(beanName)) {
            throw BukkitBoot.startupFail("bean name 冲突: %s", null, beanName);
        }

        // 创建 BeanInfo 并保存
        BeanInfo beanInfo = new BeanInfo(beanName, bean.getClass(), bean);
        this.beans.add(beanInfo);
        this.name2Bean.put(beanName, beanInfo);
    }

    /**
     * 注册一个 Bean，会将其 Class 的名字转驼峰式后作为名字使用
     * @param bean 被注册的 Bean
     */
    void registerBean(@Nonnull Object bean) {
        this.registerBean(bean, null);
    }

    /**
     * 尝试解决一个 Bean 依赖
     * @param clazz 需求的 Class
     * @param name 需求的名字(可空)
     * @return 找到的 Bean 实例，如未找到，则会返回 null
     */
    @Nullable
    public Object resolveBean(@Nonnull Class<?> clazz, @Nullable String name) {
        if (Strings.notEmpty(name)) {
            return Optional.ofNullable(name2Bean.get(name)).map(BeanInfo::getBean).orElse(null);
        }

        List<Object> resultList = this.beans.stream()
                .filter(i -> clazz.isAssignableFrom(i.getType()))
                .map(BeanInfo::getBean)
                .collect(Collectors.toList());

        if (resultList.isEmpty()) {
            // 尝试从公共 Bean 里找
        }

        if (resultList.size() != 1) {
            return null;
        }
        return resultList.get(0);
    }

    /**
     * 基于插件初始化一个插件的上下文
     * <p>这个插件必须是一个被此框架管理的插件</p>
     * @param plugin 插件的实例
     * @return 插件的上下文
     */
    @Nonnull
    static PluginContext valueOf(@Nonnull Plugin plugin) {
        BukkitBootPlugin pluginAnnotation = plugin.getClass().getAnnotation(BukkitBootPlugin.class);
        ProtectionDomain protectionDomain = plugin.getClass().getProtectionDomain();

        return new PluginContext(plugin, pluginAnnotation, protectionDomain);
    }
}
