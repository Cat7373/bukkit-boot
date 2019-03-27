package org.cat73.catbase.annotation;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * 被修饰的插件视为使用 CatPlugin 的插件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Documented
public @interface CatPlugin {
    /**
     * 需要注册为 Bean 的类列表
     * <p>作为 Bean 的 Class 必须拥有无参的构造方法</p>
     * @return 需要注册的类列表
     */
    @Nonnull
    Class<?>[] classes() default {};

    /**
     * 是否自动扫描包中的类，如果为 true，则自动扫描包中的类，并将被 @Bean 注解修饰的类自动注册为 Bean
     * @return 是否自动扫描包中的类
     */
    boolean autoScanPackage() default true;
}
