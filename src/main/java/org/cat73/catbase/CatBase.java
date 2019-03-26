package org.cat73.catbase;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.catbase.context.PluginContextManager;

/**
 * 插件主类
 */
public final class CatBase extends JavaPlugin {
    /**
     * 自身的实例
     */
    public static Plugin instance;

    @Override
    public void onEnable() {
        // 保留自身的实例
        instance = this;

        // 搜索使用此框架的插件并注册
        for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
            PluginContextManager.register(plugin);
        }

        // 初始化这些插件
        PluginContextManager.initialize();
    }

    // TODO 暂不支持重载(Spigot 已不建议使用 /reload)
    // TODO 暂不支持 onDisable，如果需要插件现在应自行覆盖这个方法来操作
}
