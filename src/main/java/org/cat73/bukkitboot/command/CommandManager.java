package org.cat73.bukkitboot.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
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
import java.util.HashMap;
import java.util.Map;

// TODO TAB 补全
/**
 * 命令管理器
 * <!-- TODO 详细说明 -->
 */
public class CommandManager implements IManager, CommandExecutor {
    // TODO javadoc
    private final Map<String, CommandInfo> commandInfos = new HashMap<>();
    // TODO javadoc
    private final Map<String, Command> commands = new HashMap<>();
    // TODO javadoc
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

            this.register(name, annotation, method, bean);

            // 保存命令的信息
            this.commands.put(name, annotation);

            // 注册 alias
            for (String alias : annotation.aliases()) {
                this.register(alias, annotation, method, bean);
            }
        });
    }

    // TODO javadoc
    private void register(@Nonnull String name, @Nonnull Command command, @Nonnull Method method, @Nonnull Object bean) {
        String lowerName = name.toLowerCase();

        if (this.commandInfos.containsKey(lowerName)) {
            throw BukkitBoot.startupFail("命令或简写 %s 和已存在的 %s 冲突", null, name, this.commandInfos.get(name).getCommand().name());
        }

        this.commandInfos.put(lowerName, new CommandInfo(method, command, bean));
    }

    // TODO javadoc
    private boolean allowExecute(@Nonnull CommandSender sender, @Nonnull Command.Target[] targets) {
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

    // TODO javadoc
    public void initial(Plugin plugin) {
        // 如果没有 help，则使用内置的 help
        if (!commandInfos.containsKey("help")) {
            Method method;
            try {
                method = HelpCommand.class.getMethod("help", CommandSender.class, String[].class);
            } catch (NoSuchMethodException e) {
                throw Lang.impossible();
            }
            Command annotation = method.getAnnotation(Command.class);
            this.register("help", annotation, method, new HelpCommand(this));
        }

        // TODO 可自定义
        this.baseCommandName = plugin.getClass().getSimpleName().toLowerCase();

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
            // 获取要执行的子命令名，如没有参数，则执行 help
            String subCommandName;
            if (args.length < 1) {
                subCommandName = "help";
            } else {
                subCommandName = args[0];
            }

            // 获取子命令的信息，如获取失败，则改为执行 help
            CommandInfo subCommandInfo = this.commandInfos.get(subCommandName);
            if (subCommandInfo == null) {
                subCommandInfo = this.commandInfos.get("help");
            }

            Command annotation = subCommandInfo.getCommand();

            // 校验执行身份
            if (!this.allowExecute(sender, annotation.target())) {
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
            String[] tmp = new String[args.length - 1];
            System.arraycopy(args, 1, tmp, 0, args.length - 1);

            // 准备参数
            Object[] params = ParameterInject.resolve(null, Arrays.asList(sender, command, args), Arrays.asList(tmp), subCommandInfo.getMethod().getParameters());

            // 执行子命令
            try {
                // 执行子命令
                Object result = subCommandInfo.getMethod().invoke(subCommandInfo.getBean(), params);
                if (result instanceof Boolean && !((Boolean) result)) {
                    // TODO 如果返回 false 则打印该子命令的帮助
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
}

