package org.cat73.bukkitboot.annotation.schedule;

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
    Scheduled[] value();
}
