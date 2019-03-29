package org.cat73.bukkitboot.command;

import org.cat73.bukkitboot.BukkitBoot;
import org.cat73.bukkitboot.annotation.Command;
import org.cat73.bukkitboot.context.IManager;
import org.cat73.bukkitboot.context.PluginContext;
import org.cat73.bukkitboot.util.reflect.Reflects;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO 注册主命令，把自己注册成执行器即可
// TODO TAB 补全
// TODO javadoc
public class CommandManager implements IManager {
    // TODO javadoc
    private final Map<String, CommandInfo> commands = new HashMap<>();
    // TODO javadoc
    private final List<Command> commandInfoList = new ArrayList<>();

    @Override
    public void register(@Nonnull PluginContext context, @Nonnull Object bean) {
        Reflects.forEachMethodByAnnotation(bean.getClass(), Command.class, (method, annotation) -> {
            this.commandInfoList.add(annotation);
            // TODO name is blank
            this.register(annotation.name(), annotation, method);
            for (String alias : annotation.aliases()) {
                this.register(alias, annotation, method);
            }
        });

        // TODO 如果不存在 help，则注册默认的 help
    }

    // TODO javadoc
    private void register(@Nonnull String name, @Nonnull Command command, @Nonnull Method method) {
        if (this.commands.containsKey(name)) {
            throw BukkitBoot.startupFail("命令或简写 %s 和已存在的 %s 冲突", null, name, this.commands.get(name).getCommand().name());
        }

        this.commands.put(name, new CommandInfo(method, command));
    }

    // TODO 执行器
    // TODO 参数注入主要分两部分：
    //  上下文当中的对象(执行器参数
    //  用户输入的内容(可做简单的类型转换
}

