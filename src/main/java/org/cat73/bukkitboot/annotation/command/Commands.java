package org.cat73.bukkitboot.annotation.command;

import java.lang.annotation.*;

/**
 * 多个命令注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Documented
@Inherited
public @interface Commands {
    Command[] value();
}
