package org.cat73.bukkitboot.command;

import lombok.Value;
import org.cat73.bukkitboot.annotation.command.TabCompleter;

import java.lang.reflect.Method;

/**
 * Tab 补全器的信息
 */
@Value
public class TabCompleteInfo {
    /**
     * 命令的信息
     */
    private final CommandInfo commandInfo;
    /**
     * 补全器的方法
     */
    private final Method method;
    /**
     * 补全器的注解
     */
    private final TabCompleter tabCompleter;
    /**
     * 补全器的 Bean
     */
    private final Object bean;
}
