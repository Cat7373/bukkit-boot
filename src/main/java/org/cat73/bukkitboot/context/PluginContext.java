package org.cat73.bukkitboot.context;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.plugin.Plugin;
import org.cat73.bukkitboot.annotation.CatPlugin;
import org.cat73.bukkitboot.listener.ListenerManager;
import org.cat73.bukkitboot.schedule.ScheduleManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.ProtectionDomain;
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
    private final CatPlugin pluginAnnotation;
    /**
     * ProtectionDomain 的实例，用于标识同一个包里的类
     */
    private final ProtectionDomain protectionDomain;
    // TODO javadoc
    private final ListenerManager listenerManager = new ListenerManager();
    // TODO javadoc
    private final ScheduleManager scheduleManager = new ScheduleManager();
    /**
     * Bean 类名 -> Bean 实例
     */
    // TODO Bean 可能需要个名字
    private Map<Class<?>, Object> beans;

    // TODO commandManager

    // TODO javadoc
    @Nullable
    public Object resolveBean(@Nonnull Class<?> clazz) {
        List<Object> resultList = this.beans.entrySet().stream()
                .filter(e -> clazz.isAssignableFrom(e.getKey()))
                .map(Map.Entry::getValue)
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
        CatPlugin pluginAnnotation = plugin.getClass().getAnnotation(CatPlugin.class);
        ProtectionDomain protectionDomain = plugin.getClass().getProtectionDomain();

        return new PluginContext(plugin, pluginAnnotation, protectionDomain);
    }
}