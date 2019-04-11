package org.cat73.bukkitboot.util;

import org.bukkit.plugin.Plugin;
import org.cat73.bukkitboot.context.PluginContext;
import org.cat73.bukkitboot.context.PluginContextManager;

import javax.annotation.Nonnull;

/**
 * 插件操作的工具类
 */
public final class Plugins {
    private Plugins() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取当前插件的实例
     * @return 当前插件的实例
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends Plugin> T current() {
        return (T) currentContext().getPlugin();
    }

    /**
     * 获取当前插件的上下文
     * @return 当前插件的上下文
     */
    @Nonnull
    public static PluginContext currentContext() {
        return PluginContextManager.current();
    }
}
