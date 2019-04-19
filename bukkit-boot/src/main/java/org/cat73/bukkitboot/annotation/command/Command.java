package org.cat73.bukkitboot.annotation.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.lang.annotation.*;

/**
 * 一个命令的执行器
 * <p>所需参数会自动注入</p>
 * <!-- TODO 说明可注入的东西 -->
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Documented
@Inherited
@Repeatable(Commands.class)
public @interface Command {
    /**
     * 命令的名字
     * <p>默认会使用方法名</p>
     **/
    String name() default "";

    /**
     * 执行这个命令所需的权限列表
     * <p>默认无需任何权限即可执行</p>
     **/
    String[] permission() default {};

    /**
     * 命令的使用方法
     **/
    String usage() default "";

    /**
     * 命令的单行帮助内容
     **/
    String desc() default "";

    /**
     * 命令的多行帮助内容
     **/
    String[] help() default {};

    /**
     * 命令的简写列表
     **/
    String[] aliases() default {};

    /**
     * 可以执行命令的执行者类型
     * <p>默认允许所有类型的执行者</p>
     **/
    Command.Target[] target() default {};

    /**
     * 命令执行者的类型
     */
    enum Target {
        /**
         * 玩家
         */
        PLAYER {
            @Override
            public boolean allow(CommandSender sender) {
                return sender instanceof Player; // CraftPlayer
            }
        },
        /**
         * 命令方块
         */
        COMMAND_BLOCK {
            @Override
            public boolean allow(CommandSender sender) {
                return  sender instanceof BlockCommandSender || // CraftBlockCommandSender
                        sender instanceof CommandMinecart; // CraftMinecartCommand
            }
        },
        /**
         * 控制台
         */
        CONSOLE {
            @Override
            public boolean allow(CommandSender sender) {
                return sender instanceof ConsoleCommandSender; // ColouredConsoleSender
            }
        },
        /**
         * 其它
         */
        OTHER {
            @Override
            public boolean allow(CommandSender sender) {
                for (Target target : Target.values()) {
                    if (target != this && target.allow(sender)) {
                        return false;
                    }
                }
                return true;
            }
        };

        /**
         * 判断是否支持一个命令执行者执行
         * @param sender 命令执行者
         * @return 是否允许其执行
         */
        public abstract boolean allow(CommandSender sender);
    }
}
