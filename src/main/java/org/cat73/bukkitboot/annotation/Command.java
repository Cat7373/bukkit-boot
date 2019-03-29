package org.cat73.bukkitboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个可以被执行的命令
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
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
     * <p>在用户输入的参数格式有误时，会输出这里的内容作为帮助信息</p>
     **/
    String usage() default "";

    /**
     * 命令的单行帮助内容
     * <p>在 help 命令列表时，会输出这里的内容作为帮助信息</p>
     * <p>如果没设置，则默认使用 {@link #usage}</p>
     **/
    String desc() default "";

    /**
     * 命令的多行帮助内容
     * <p>在 help 具体命令时，会输出这里的内容作为帮助信息</p>
     * <p>如果没设置，则默认使用 {@link #desc}</p>
     **/
    String[] help() default "";

    /**
     * 命令的简写列表
     **/
    String[] aliases() default "";

    /**
     * 可以执行命令的执行者类型
     **/
    Command.Target[] target() default {
            Command.Target.PLAYER,
            Command.Target.COMMAND_BLOCK,
            Command.Target.CONSOLE,
            Command.Target.OTHER
    };

    /**
     * 命令执行者的类型
     */
    enum Target {
        /**
         * 玩家
         */
        PLAYER,
        /**
         * 命令方块
         */
        COMMAND_BLOCK,
        /**
         * 控制台
         */
        CONSOLE,
        /**
         * 其它
         */
        OTHER
    }
}
