package org.cat73.bukkitboot.annotation.core;

import java.lang.annotation.*;

/**
 * 初始化方法
 * <p>所需参数会尝试自动注入，如有需要可以通过 @Inject(name = "beanName") 来限定名字</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface PostConstruct {}
