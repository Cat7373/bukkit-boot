package org.cat73.bukkitboot.annotation;

import java.lang.annotation.*;

/**
 * 定时任务
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(Scheduleds.class)
public @interface Scheduled {
    /**
     * 首次运行之前的等待时间(tick)
     * @return 首次运行之前的等待时间
     */
    long delay() default -1L;

    /**
     * 两次运行之间的间隔时间(tick)
     * <p>若小于 0，则视为一次性任务，只会运行一次，不会重复运行</p>
     * @return 两次运行之间的间隔时间
     */
    long period() default -1L;

    /**
     * 是否为异步任务
     * <p>同步任务会阻塞主线程的运行</p>
     * <p>异步任务不可访问 Bukkit API，否则可能会导致不确定的结果</p>
     * @return 是否为异步任务
     */
    boolean async() default false;
}
