package org.cat73.bukkitboot.context;

import javax.annotation.Nonnull;

/**
 * 管理器的公共接口
 */
public interface IManager {
    // TODO javadoc
    void register(@Nonnull PluginContext context, @Nonnull Object bean);
}
