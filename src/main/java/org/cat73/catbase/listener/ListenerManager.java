package org.cat73.catbase.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.cat73.catbase.context.IManager;
import org.cat73.catbase.context.PluginContext;

import javax.annotation.Nonnull;

// TODO javadoc
public class ListenerManager implements IManager {
    // TODO javadoc
    private final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

    @Override
    public void register(@Nonnull PluginContext context, @Nonnull Object bean) {
        if (bean instanceof Listener) {
            pluginManager.registerEvents((Listener) bean, context.getPlugin());
        }
    }
}
