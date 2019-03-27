package org.cat73.bukkitboot.util;

import org.bukkit.plugin.Plugin;
import org.cat73.bukkitboot.context.PluginContextManager;

import javax.annotation.Nonnull;

public final class Plugins {
    private Plugins() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取当前插件的实例
     * @return 当前插件的实例
     */
    @Nonnull
    public Plugin current() {
        return PluginContextManager.current().getPlugin();
    }
}