package org.cat73.bukkitboot.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.cat73.bukkitboot.BukkitBoot;
import org.cat73.bukkitboot.annotation.Command;
import org.cat73.bukkitboot.command.internal.HelpCommand;
import org.cat73.bukkitboot.context.IManager;
import org.cat73.bukkitboot.context.PluginContext;
import org.cat73.bukkitboot.util.Lang;
import org.cat73.bukkitboot.util.Strings;
import org.cat73.bukkitboot.util.reflect.ParameterInject;
import org.cat73.bukkitboot.util.reflect.Reflects;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// TODO TAB 补全
/**
 * 命令管理器
 * <!-- TODO 详细说明 -->
 */
public class CommandManager implements IManager, CommandExecutor {
    /**
     * 命令名或简写到命令信息的速查表
     */
    private final Map<String, CommandInfo> commandInfos = new HashMap<>();
    /**
     * 命令名到命令注解的速查表
     */
    private final Map<String, Command> commands = new HashMap<>();
    /**
     * 主命令名
     */
    @Getter
    private String baseCommandName;

    @Override
    public void register(@Nonnull PluginContext context, @Nonnull Object bean) {
        Reflects.forEachMethodByAnnotation(bean.getClass(), Command.class, (method, annotation) -> {
            // 注册 name
            String name = annotation.name();
            if (Strings.isEmpty(name)) {
                name = method.getName();
            }
            name = name.toLowerCase();

            this.register(name, name, annotation, method, bean);

            // 保存命令的信息
            this.commands.put(name, annotation);

            // 注册 alias
            for (String alias : annotation.aliases()) {
                this.register(name, alias, annotation, method, bean);
            }
        });
    }

    @Override
    public void initialize(@Nonnull PluginContext context) {
        // 如果没有 help，则使用内置的 help
        if (!commandInfos.containsKey("help")) {
            Method method;
            try {
                method = HelpCommand.class.getMethod("help", CommandSender.class, String.class);
            } catch (NoSuchMethodException e) {
                throw Lang.impossible();
            }
            Command annotation = method.getAnnotation(Command.class);
            this.commands.put("help", annotation);
            this.register("help", "help", annotation, method, new HelpCommand(this));
        }

        // TODO 等配置部分做完后可自定义
        this.baseCommandName = context.getPlugin().getClass().getSimpleName().toLowerCase();

        // 注册命令
        PluginCommand command = Bukkit.getServer().getPluginCommand(this.baseCommandName);
        if (command != null) {
            command.setExecutor(this);
        } else {
            if (this.commands.size() > 1) {
                throw BukkitBoot.startupFail("无法注册主命令 %s", null, this.baseCommandName);
            }
        }
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull org.bukkit.command.Command command, @Nonnull String label, @Nonnull String[] args) {
        if (command.getName().equalsIgnoreCase(this.baseCommandName)) {
            // 是否跳过参数修剪
            boolean skipParamClip = false;

            // 获取要执行的子命令名，如没有参数，则执行 help
            String subCommandName;
            if (args.length < 1) {
                subCommandName = "help";
            } else {
                subCommandName = args[0];
            }

            // 获取子命令的信息，如获取失败，则改为执行 help
            CommandInfo subCommandInfo = this.commandInfos.get(subCommandName.toLowerCase());
            if (subCommandInfo == null) {
                subCommandInfo = this.commandInfos.get("help");
                skipParamClip = true;
            }

            Command annotation = subCommandInfo.getCommand();

            // 校验执行身份
            if (!this.checkTarget(sender, annotation.target())) {
                sender.sendMessage(String.format("%s%s您无法执行这个命令.", ChatColor.RED, ChatColor.BOLD));
                return true;
            }

            // 校验执行权限
            for (String permission : annotation.permission()) {
                if (!sender.hasPermission(permission)) {
                    sender.sendMessage(String.format("%s%s你需要 %s 权限才能执行这个命令.", ChatColor.RED, ChatColor.BOLD, permission));
                    return true;
                }
            }

            // 修剪参数 (删除子命令名)
            String[] tmp;
            if (!skipParamClip && args.length > 0) {
                tmp = new String[args.length - 1];
                System.arraycopy(args, 1, tmp, 0, args.length - 1);
            } else {
                tmp = args;
            }

            // 准备参数
            Object[] params = ParameterInject.resolve(null, Arrays.asList(sender, command, args), Arrays.asList(tmp), subCommandInfo.getMethod().getParameters());

            // 执行子命令
            try {
                // 执行子命令
                Object result = subCommandInfo.getMethod().invoke(subCommandInfo.getBean(), params);
                if (result instanceof Boolean && !((Boolean) result)) {
                    // TODO 如果返回 false 则打印该子命令的使用方法
                }
            } catch (Exception e) {
                // 如果出现任何未捕获的异常则打印提示
                sender.sendMessage(String.format("%s%s执行命令的过程中出现了一个未处理的错误.", ChatColor.RED, ChatColor.BOLD));
                e.printStackTrace();
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取命令名或简写到命令信息的速查表
     * @return 命令名或简写到命令信息的速查表
     */
    @Nonnull
    public Map<String, CommandInfo> getCommandInfos() {
        return Collections.unmodifiableMap(this.commandInfos);
    }

    /**
     * 获取命令名到命令注解的速查表
     * @return 命令名到命令注解的速查表
     */
    @Nonnull
    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(this.commands);
    }

    /**
     * 注册一个命令
     * @param name 命令所属的主命令的名称
     * @param alias 命令的简写(注册主命令时请直接将主命令作为这个参数)
     * @param command 命令的注解
     * @param method 命令的执行方法
     * @param bean 方法所在的类的实例
     */
    private void register(@Nonnull String name, @Nonnull String alias, @Nonnull Command command, @Nonnull Method method, @Nonnull Object bean) {
        String lowerName = alias.toLowerCase();

        if (this.commandInfos.containsKey(lowerName)) {
            throw BukkitBoot.startupFail("命令或简写 %s 和已存在的 %s 冲突", null, alias, this.commandInfos.get(lowerName).getCommand().name());
        }

        this.commandInfos.put(lowerName, new CommandInfo(name, alias, method, command, bean));
    }

    /**
     * 判断是否属于目标执行者类型
     * @param sender 命令的执行者
     * @param targets 允许的执行者类型
     * @return 是否属于目标执行者类型
     */
    private boolean checkTarget(@Nonnull CommandSender sender, @Nonnull Command.Target[] targets) {
        if (targets.length == 0) {
            return true;
        }

        for (Command.Target target : targets) {
            if (target.allow(sender)) {
                return true;
            }
        }
        return false;
    }
}

