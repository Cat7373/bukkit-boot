package org.cat73.bukkitboot.annotation.core;

import java.lang.annotation.*;

/**
 * 当正在运行的服务器符合指定的 NMS 版本之一时才将类注册为 Bean
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface NMSVersions {
    /**
     * @return NMSVersion 版本列表
     */
    NMSVersion[] value();
}
