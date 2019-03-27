package org.cat73.catbase;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.catbase.context.PluginContextManager;
import org.cat73.catbase.exception.InitializeError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 插件主类
 */
public final class CatBase extends JavaPlugin {
    /**
     * 自身的实例
     */
    private static CatBase instance;

    /**
     * 获取自身的实例
     * @return 自身的实例
     */
    public static CatBase instance() {
        return instance;
    }

    public CatBase() {
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
    @Nonnull
    public static Error startupFail(@Nonnull String msg, @Nullable Throwable e, @Nullable Object... args) throws InitializeError {
        // 停掉服务器
        Bukkit.getServer().shutdown();
        // 返回启动失败的异常
        if (e == null) {
            throw new InitializeError(String.format(msg, args));
        } else {
            throw new InitializeError(String.format(msg, args), e);
        }
    }

    // TODO 暂不支持重载(Spigot 已不建议使用 /reload)
    // TODO 暂不支持 onDisable，如果需要插件现在应自行覆盖这个方法来操作
}
