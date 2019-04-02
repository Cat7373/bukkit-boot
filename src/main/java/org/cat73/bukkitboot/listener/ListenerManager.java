package org.cat73.bukkitboot.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.cat73.bukkitboot.context.IManager;
import org.cat73.bukkitboot.context.PluginContext;

import javax.annotation.Nonnull;

/**
 * Listener 管理器
 */
public class ListenerManager implements IManager {
    /**
     * Bukkit 的插件管理器
     */
    private static final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

    @Override
    public void register(@Nonnull PluginContext context, @Nonnull Object bean) {
        if (bean instanceof Listener) {
            pluginManager.registerEvents((Listener) bean, context.getPlugin());
        }
    }
}
