package org.cat73.bukkitboot.annotation;

import java.lang.annotation.*;

/**
 * 被修饰的类视为需要被注册的 Bean
 * <p>作为 Bean 的 Class 必须拥有无参的构造方法</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE) // TODO 支持在 Method 上
@Documented
@Inherited
public @interface Bean {
    /**
     * Bean 的名字
     * <p>默认会使用小驼峰式的类名</p>
     * @return Bean 的名字
     */
    String name() default "";
}
