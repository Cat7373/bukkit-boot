package org.cat73.bukkitboot.annotation;

import java.lang.annotation.*;

/**
 * 定时任务列表
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Scheduleds {
    /**
     * @return NMSVersion 版本列表
     */
    Scheduled[] value() default {};
}
