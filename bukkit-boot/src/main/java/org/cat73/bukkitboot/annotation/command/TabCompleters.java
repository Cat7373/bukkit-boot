package org.cat73.bukkitboot.annotation.command;

import java.lang.annotation.*;

/**
 * 多个命令的 TAB 补全方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Documented
@Inherited
public @interface TabCompleters {
    /**
     * 命令的名字
     * <p>默认会使用方法名</p>
     * <p>只支持使用名字，不支持使用简写</p>
     **/
    TabCompleter[] value();
}
