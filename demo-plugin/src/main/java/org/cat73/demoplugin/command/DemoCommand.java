package org.cat73.demoplugin.command;

import org.bukkit.command.CommandSender;
import org.cat73.bukkitboot.annotation.Bean;
import org.cat73.bukkitboot.annotation.Command;
import org.cat73.bukkitboot.util.Logger;

@Bean
public class DemoCommand {
    @Command
    public void demo(CommandSender sender, String arg1, int arg2, boolean arg3) {
        Logger.debug("DemoCommand.demo(): sender: %s, args: %s, %s, %s", sender.getName(), arg1, arg2, arg3);
    }
}
