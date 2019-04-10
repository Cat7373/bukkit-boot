package org.cat73.bukkitboot.command;

import lombok.Value;
import org.cat73.bukkitboot.annotation.Command;

import java.lang.reflect.Method;

/**
 * 命令的信息
 */
@Value
public class CommandInfo {
    /**
     * 命令的执行方法
     */
    private final Method method;
    /**
     * 命令的注解
     */
    private final Command command;
    /**
     * 命令执行器的 Bean
     */
    private Object bean;
}
