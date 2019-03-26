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
     * 需要注册的类列表
     * @return 需要注册的类列表
     */
    @Nonnull
    Class<?>[] classes() default {};
}
