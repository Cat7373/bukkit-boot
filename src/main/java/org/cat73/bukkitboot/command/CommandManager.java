package org.cat73.bukkitboot.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.cat73.bukkitboot.BukkitBoot;
import org.cat73.bukkitboot.annotation.command.Command;
import org.cat73.bukkitboot.annotation.command.TabCompleter;
import org.cat73.bukkitboot.command.internal.HelpCommand;
import org.cat73.bukkitboot.context.IManager;
import org.cat73.bukkitboot.context.PluginContext;
import org.cat73.bukkitboot.util.Lang;
import org.cat73.bukkitboot.util.Strings;
import org.cat73.bukkitboot.util.reflect.ParameterInject;
import org.cat73.bukkitboot.util.reflect.Reflects;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 命令管理器
 * <!-- TODO 详细说明 -->
 */
public class CommandManager implements IManager, TabExecutor {
    /**
     * 命令名或简写到命令信息的速查表
     */
    private final Map<String, CommandInfo> commandInfos = new HashMap<>();
    /**
     * 命令名到命令注解的速查表
     */
    private final Map<String, Command> commands = new HashMap<>();
    /**
     * 命令名到命令的 Tab 补全器的速查表
     */
    private final Map<String, Method> tabCompletes = new HashMap<>();
    /**
     * 主命令名
     */
    @Getter
    private String baseCommandName;

    @Override
    public void register(@Nonnull PluginContext context, @Nonnull Object bean) {
        // 注册命令
        Reflects.lookupMethodByAnnotation(bean.getClass(), Command.class, (method, annotation) -> {
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
        // 注册 Tab 补全器
        Reflects.lookupMethodByAnnotation(bean.getClass(), TabCompleter.class, (method, annotation) -> {
            // 注册 name
            String name = annotation.name();
            if (Strings.isEmpty(name)) {
                name = method.getName();
            }
            name = name.toLowerCase();

            // 保存补全器的信息
            this.tabCompletes.put(name, method);
        });
    }

    @Override
    public void initialize(@Nonnull PluginContext context) {
        // 如果没有 help，则使用内置的 help
        if (!commandInfos.containsKey("help")) {
            Method commandMethod;
            Method tabCompleterMethod;
            try {
                commandMethod = HelpCommand.class.getMethod("help", CommandSender.class, String.class);
                tabCompleterMethod = HelpCommand.class.getMethod("tabCompleter", CommandSender.class, String.class);
            } catch (NoSuchMethodException e) {
                throw Lang.impossible();
            }
            Command annotation = commandMethod.getAnnotation(Command.class);
            this.commands.put("help", annotation);
            this.register("help", "help", annotation, commandMethod, new HelpCommand(this));
            this.tabCompletes.put("help", tabCompleterMethod);
        }

        // TODO 主命令名，等配置部分做完后可自定义
        this.baseCommandName = context.getPlugin().getClass().getSimpleName().toLowerCase();

        // 注册命令
        PluginCommand command = Bukkit.getServer().getPluginCommand(this.baseCommandName);
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
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
            // TODO 无法解析参数不要炸，输出使用方法
            Object[] params = ParameterInject.resolve(null, Arrays.asList(sender, command, args, this), Arrays.asList(tmp), subCommandInfo.getMethod().getParameters());

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

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull org.bukkit.command.Command command, @Nonnull String alias, @Nonnull String[] args) {
        if (args.length == 0) {
            // 补全部子命令
            return this.commandInfos.entrySet().stream()
                    .filter(e -> this.hasPermissions(sender, e.getValue().getCommand().permission()))
                    .filter(e -> this.checkTarget(sender, e.getValue().getCommand().target()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } else if (args.length == 1) {
            // 补特定前缀的子命令
            return this.commandInfos.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(args[0]))
                    .filter(e -> this.hasPermissions(sender, e.getValue().getCommand().permission()))
                    .filter(e -> this.checkTarget(sender, e.getValue().getCommand().target()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } else {
            // 获取命令的信息
            String subCommandName = args[0];
            CommandInfo subCommandInfo = this.commandInfos.get(subCommandName.toLowerCase());
            if (
                    // 命令不存在
                    subCommandInfo == null ||
                    // 无权执行
                    !this.hasPermissions(sender, subCommandInfo.getCommand().permission()) ||
                    // 不是目标执行者类型
                    !this.checkTarget(sender, subCommandInfo.getCommand().target())
            ) {
                // 返回空白
                return Collections.emptyList();
            } else {
                // 获取命令的补全器
                Method method = this.tabCompletes.get(subCommandInfo.getName());
                if (method == null) {
                    // 没找到则返回空白
                    return Collections.emptyList();
                } else {
                    // 修剪参数 (删除子命令名)
                    String[] tmp = new String[args.length - 1];
                    System.arraycopy(args, 1, tmp, 0, args.length - 1);

                    // 准备参数
                    // TODO 无法解析参数不要炸，返回空白(输出错误日志)
                    Object[] params = ParameterInject.resolve(null, Arrays.asList(sender, command, args, this), Arrays.asList(tmp), subCommandInfo.getMethod().getParameters());

                    try {
                        // 执行补全器，获取结果并返回
                        return Reflects.conv(method.invoke(subCommandInfo.getBean(), params));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        // 如果出现了任何异常则返回空白，并打印异常信息
                        e.printStackTrace();
                        return Collections.emptyList();
                    }
                }
            }
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
     * 判断是否有足够的权限
     * @param sender 命令的执行者
     * @param permissions 需要满足的权限列表
     * @return 是否有足够的权限
     */
    private boolean hasPermissions(@Nonnull CommandSender sender, @Nonnull String[] permissions) {
        for (String permission : permissions) {
            if (!sender.hasPermission(permission)) {
                return false;
            }
        }
        return true;
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
