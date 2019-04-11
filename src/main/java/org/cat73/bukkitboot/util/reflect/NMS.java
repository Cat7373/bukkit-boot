package org.cat73.bukkitboot.util.reflect;

import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

/**
 * NMS 版本枚举
 */
public enum NMS {
    /**
     * 1.8
     */
    v1_8_R1,
    /**
     * 1.8.3
     */
    v1_8_R2,
    /**
     * 1.8.4
     * <p>1.8.5</p>
     * <p>1.8.6</p>
     * <p>1.8.7</p>
     * <p>1.8.8</p>
     */
    v1_8_R3,
    /**
     * 1.9
     * <p>1.9.2</p>
     */
    v1_9_R1,
    /**
     * 1.9.4
     */
    v1_9_R2,
    /**
     * 1.10
     * <p>1.10.2</p>
     */
    v1_10_R1,
    /**
     * 1.11
     * <p>1.11.1</p>
     * <p>1.11.2</p>
     */
    v1_11_R1,
    /**
     * 1.12
     * <p>1.12.1</p>
     * <p>1.12.2</p>
     */
    v1_12_R1,
    /**
     * 1.13
     */
    v1_13_R1,
    /**
     * 1.13.1
     * <p>1.13.2</p>
     */
    v1_13_R2,
    /**
     * 未知
     */
    UNKNOWN;

    /**
     * 当前正运行的服务器的 NMS 版本的名字
     **/
    public static final String CURRENT_NMS_VERSION_NAME = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    /**
     * 当前正运行的服务器的 NMS 版本
     **/
    public static final NMS CURRENT_NMS_VERSION = NMS.valueOf(CURRENT_NMS_VERSION_NAME);

    /**
     * 基于版本的名字获取枚举实例
     * @param name 版本的名字
     * @return 版本的枚举实例，如未获取到，则会返回 UNKNOWN
     */
    @Nonnull
    public static NMS forName(@Nonnull String name) {
        for (NMS version : NMS.values()) {
            if (version.name().equals(name)) {
                return version;
            }
        }

        return UNKNOWN;
    }

    /**
     * 获取 NMS Class(net.minecraft.server.*)
     * @param className 类名
     * @return 获取到的类
     * @throws ClassNotFoundException 如果类不存在
     */
    @Nonnull
    public static Class<?> nms(@Nonnull String className) throws ClassNotFoundException {
        return Class.forName(String.format("net.minecraft.server.%s.%s", NMS.CURRENT_NMS_VERSION_NAME, className));
    }

    /**
     * 获取 CraftBukkit Class(org.bukkit.craftbukkit.*)
     * @param className 类名
     * @return 获取到的类
     * @throws ClassNotFoundException 如果类不存在
     */
    @Nonnull
    public static Class<?> cb(@Nonnull String className) throws ClassNotFoundException {
        return Class.forName(String.format("org.bukkit.craftbukkit.%s.%s", NMS.CURRENT_NMS_VERSION_NAME, className));
    }
}
