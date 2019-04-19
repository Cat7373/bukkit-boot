package org.cat73.bukkitboot.annotation.core.condition;

import java.lang.annotation.*;

/**
 * 自定义的判断条件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Conditional {
    /**
     * 自定义判断条件的实现类列表，应包含无参的构造方法
     * @return 自定义判断条件的实现类列表
     */
    Class<? extends Condition>[] value();
}
