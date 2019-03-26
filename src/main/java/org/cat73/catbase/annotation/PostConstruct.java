package org.cat73.catbase.annotation;

import java.lang.annotation.*;

/**
 * 初始化方法
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostConstruct {}
