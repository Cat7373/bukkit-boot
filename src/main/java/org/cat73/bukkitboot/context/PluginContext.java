package org.cat73.bukkitboot.context;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    // TODO javadoc
    private final ListenerManager listenerManager = new ListenerManager();
    // TODO javadoc
    private final ScheduleManager scheduleManager = new ScheduleManager();
    // TODO javadoc
    private final CommandManager commandManager = new CommandManager();

    // TODO javadoc
    @Setter(AccessLevel.PRIVATE)
    private List<BeanInfo> beans = new ArrayList<>();
    // TODO javadoc
    @Setter(AccessLevel.PRIVATE)
    private Map<String, BeanInfo> name2Bean = new HashMap<>();
    // TODO javadoc
    @Setter(AccessLevel.PRIVATE)
    private Map<Class<?>, List<BeanInfo>> type2Bean = new HashMap<>();

    // TODO javadoc
    void registerBean(@Nonnull Object bean, @Nullable String name) {
        // bean name
        String beanName = name;
        if (Strings.isEmpty(beanName)) {
            beanName = bean.getClass().getSimpleName();
            beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
        }
        // class
        Class<?> clazz = bean.getClass();

        // 校验重复
        if (name2Bean.containsKey(beanName)) {
            throw BukkitBoot.startupFail("bean name 冲突: %s", null, beanName);
        }

        // 创建 BeanInfo 并保存
        BeanInfo beanInfo = new BeanInfo(beanName, bean.getClass(), bean);
        this.beans.add(beanInfo);
        this.name2Bean.put(beanName, beanInfo);
        this.type2Bean.computeIfAbsent(clazz, c -> new ArrayList<>()).add(beanInfo);
    }

    // TODO javadoc
    void registerBean(@Nonnull Object bean) {
        this.registerBean(bean, null);
    }

    // TODO javadoc
    @Nullable
    public Object resolveBean(@Nonnull Class<?> clazz, @Nullable String name) {
        if (Strings.notEmpty(name)) {
            return name2Bean.get(name);
        }

        List<Object> resultList = this.beans.stream()
                .filter(i -> clazz.isAssignableFrom(i.getType()))
                .map(BeanInfo::getBean)
                .collect(Collectors.toList());

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
