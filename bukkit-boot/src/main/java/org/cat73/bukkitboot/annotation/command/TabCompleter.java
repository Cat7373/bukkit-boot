package org.cat73.bukkitboot.annotation.command;

import java.lang.annotation.*;

/**
 * 一个命令的 TAB 补全方法
 * <p>所需参数会自动注入</p>
 * <!-- TODO 说明可注入的东西，说明要求返回值为 List&lt;String&gt; -->
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Documented
@Inherited
@Repeatable(TabCompleters.class)
public @interface TabCompleter {
    /**
     * 命令的名字
     * <p>默认会使用方法名</p>
     * <p>只支持使用名字，不支持使用简写</p>
     **/
    String name() default "";
}
