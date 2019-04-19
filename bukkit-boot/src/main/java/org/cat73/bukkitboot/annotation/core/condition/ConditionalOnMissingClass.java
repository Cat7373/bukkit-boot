package org.cat73.bukkitboot.annotation.core.condition;

import java.lang.annotation.*;

/**
 * 当指定 Class 不存在时才将类注册为 Bean
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ConditionalOnMissingClass {
    /**
     * Class 的全路径列表
     * @return Class 的全路径列表
     */
    String[] value();
}
