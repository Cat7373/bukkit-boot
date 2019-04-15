package org.cat73.demoplugin.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.command.Command;
import org.cat73.bukkitboot.util.Logger;

import java.util.Arrays;

@Bean
public class DemoCommand {
    @Command(desc = "测试命令 - 1")
    public void demo1(CommandSender sender, String arg1, int arg2, boolean arg3) {
        Logger.debug("DemoCommand.demo1(): sender: %s, args: %s, %s, %s", sender.getName(), arg1, arg2, arg3);
    }

    @Command(desc = "测试命令 - 2")
    public void demo2(CommandSender sender, String[] args) {
        Logger.debug("DemoCommand.demo2(): sender: %s, args: %s", sender.getName(), Arrays.asList(args));
    }

    @Command(desc = "测试命令 - 3", target = Command.Target.PLAYER)
    public void demo3(CommandSender sender, int arg1, Player player, boolean arg2) {
        Logger.debug("DemoCommand.demo3(): sender: %s, playerName: %s, int: %s, boolean: %s", sender.getName(), player.getName(), arg1, arg2);
    }
}
