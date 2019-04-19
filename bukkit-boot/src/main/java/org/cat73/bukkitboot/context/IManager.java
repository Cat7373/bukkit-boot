package org.cat73.bukkitboot.context;

import javax.annotation.Nonnull;

/**
 * 管理器的公共接口
 */
public interface IManager {
    /**
     * 注册一个 Bean
     * @param context 插件的上下文
     * @param bean 被注册的 Bean
     */
    void register(@Nonnull PluginContext context, @Nonnull Object bean);

    /**
     * 初始化
     * @param context 插件的上下文
     */
    default void initialize(@Nonnull PluginContext context) {}
}
