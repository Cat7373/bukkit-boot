package org.cat73.bukkitboot.command.internal;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.cat73.bukkitboot.annotation.Command;
import org.cat73.bukkitboot.command.CommandManager;

import java.util.Arrays;

/**
 * 内置的帮助命令
 */
@RequiredArgsConstructor
public class HelpCommand {
    // TODO javadoc
    private final int pageSize = 8;
    // TODO javadoc
    private final CommandManager commandManager;

    /**
     * 帮助命令
     * @param args 参数列表(命令名或页码)
     */
    @Command(usage = "[page | commandName]", desc = "显示帮助信息")
    public void help(CommandSender sender, String[] args) {
        sender.sendMessage(String.format("入参: %s", Arrays.asList(args)));
        // TODO 实现
    }
}
