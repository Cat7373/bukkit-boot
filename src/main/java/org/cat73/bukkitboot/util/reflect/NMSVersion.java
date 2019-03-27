package org.cat73.bukkitboot.util.reflect;

import javax.annotation.Nonnull;

/**
 * NMS 的版本
 */
public enum NMSVersion {
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

    // TODO javadoc
    @Nonnull
    public static NMSVersion forName(@Nonnull String name) {
        for (NMSVersion version : NMSVersion.values()) {
            if (version.name().equals(name)) {
                return version;
            }
        }

        return UNKNOWN;
    }
}
