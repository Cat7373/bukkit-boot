package org.cat73.bukkitboot.annotation;

import java.lang.annotation.*;

/**
 * 初始化方法
 * <p>所需参数会尝试自动注入<!-- TODO，如有需要可以通过 @Inject(name = "beanName") 来限定名字 --></p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostConstruct {}
