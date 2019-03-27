package org.cat73.catbase.annotation;

import java.lang.annotation.*;

/**
 * 被修饰的插件视为需要被注册的 Bean
 * <p>作为 Bean 的 Class 必须拥有无参的构造方法</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Documented
public @interface Bean {}
