package org.cat73.catbase.util;

import org.bukkit.plugin.Plugin;
import org.cat73.catbase.context.PluginContextManager;

public final class Plugins {
    private Plugins() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取当前插件的实例
     * @return 当前插件的实例
     */
    public Plugin current() {
        return PluginContextManager.current().getPlugin();
    }
}
