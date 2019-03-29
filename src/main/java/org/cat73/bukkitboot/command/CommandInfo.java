package org.cat73.bukkitboot.command;

import lombok.Value;
import org.cat73.bukkitboot.annotation.Command;

import java.lang.reflect.Method;

// TODO javadoc
@Value
public class CommandInfo {
    // TODO javadoc
    private final Method method;
    // TODO javadoc
    private final Command command;
}
