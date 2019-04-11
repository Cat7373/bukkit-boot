package org.cat73.bukkitboot.annotation;

import org.cat73.bukkitboot.util.reflect.NMS;

import java.lang.annotation.*;

/**
 * 仅当正在运行的服务器符合特定 NMS 版本时才将类注册为 Bean
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(NMSVersions.class)
public @interface NMSVersion {
    /**
     * 需要的 NMS 版本
     * @return NMS 版本
     */
    NMS value();
}
