package org.cat73.bukkitboot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.bukkitboot.context.PluginContextManager;
import org.cat73.bukkitboot.exception.InitializeError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 插件主类
 */
public final class BukkitBoot extends JavaPlugin {
    /**
     * 自身的实例
     */
    private static BukkitBoot instance;

    /**
     * 获取自身的实例
     * @return 自身的实例
     */
    @Nonnull
    public static BukkitBoot instance() {
        return instance;
    }

    public BukkitBoot() {
        // 保留自身的实例
        instance = this;
    }

    @Override
    public void onEnable() {
        // 搜索使用此框架的插件并注册
        for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
            PluginContextManager.register(plugin);
        }

        // 初始化这些插件
        PluginContextManager.initialize();
    }

    // TODO javadoc
    // TODO plugin?
    @Nonnull
    public static Error startupFail(@Nonnull String msg, @Nullable Throwable ex, @Nullable Object... args) throws InitializeError {
        // 停掉服务器
        Bukkit.getServer().shutdown();
        // 返回启动失败的异常
        if (ex == null) {
            throw new InitializeError(String.format(msg, args));
        } else {
            throw new InitializeError(String.format(msg, args), ex);
        }
    }

    // TODO 暂不支持重载(Spigot 已不建议使用 /reload)
    // TODO 暂不支持 onDisable，如果需要插件现在应自行覆盖这个方法来操作
}
