package org.cat73.bukkitboot.command.internal;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cat73.bukkitboot.annotation.Command;
import org.cat73.bukkitboot.annotation.Inject;
import org.cat73.bukkitboot.command.CommandInfo;
import org.cat73.bukkitboot.command.CommandManager;
import org.cat73.bukkitboot.util.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内置的帮助命令
 */
@RequiredArgsConstructor
public class HelpCommand {
    /**
     * 输出某页帮助时，每页的命令数
     */
    private final int pageSize = 8;
    /**
     * 命令管理器
     */
    private final CommandManager commandManager;

    /**
     * 帮助命令
     * @param sender 命令的执行者
     * @param name 命令名或页码
     */
    @Command(usage = "[page | commandName]", desc = "显示帮助信息")
    public void help(@Nonnull CommandSender sender, @Nullable @Inject(required = false) String name) {
        // 首先判断是否有参数
        if (Strings.isEmpty(name)) {
            // 没参数直接输出第一页
            this.sendHelp(sender, 1);
        } else {
            // 尝试将参数作为命令名
            CommandInfo commandInfo = this.commandManager.getCommandInfos().get(name);
            if (commandInfo != null && this.hasPermissions(sender, commandInfo.getCommand().permission()) && this.checkTarget(sender, commandInfo.getCommand().target())) {
                // 打印这个命令的帮助信息
                this.sendHelp(sender, commandInfo);
            } else {
                // 输出的页码
                int page = 1;

                // 尝试将输入转换为页码
                try {
                    page = Integer.parseInt(name);
                } catch (NumberFormatException e) {
                    // quiet
                }

                // 输出指定页码的帮助信息
                this.sendHelp(sender, page);
            }
        }
    }

    /**
     * 发送一页帮助
     * @param sender 命令的执行者
     * @param page 第几页
     */
    private void sendHelp(@Nonnull CommandSender sender, int page) {
        // 找出可以执行的命令列表
        List<Map.Entry<String, Command>> commands = this.commandManager.getCommands().entrySet().stream()
                // 滤掉执行者类型不符的
                .filter(e -> this.checkTarget(sender, e.getValue().target()))
                // 滤掉无权执行的
                .filter(e -> this.hasPermissions(sender, e.getValue().permission()))
                .collect(Collectors.toList());

        // 计算最大页码
        int maxPage = (int) Math.ceil((double) commands.size() / this.pageSize);
        // 防止超出总页数
        int usePage = Math.min(Math.max(1, page), maxPage);

        // 输出头
        sender.sendMessage(String.format("%s%s------- 命令列表 (%d / %d) ----------------", ChatColor.AQUA, ChatColor.BOLD, usePage, maxPage));

        // 获取命令列表的迭代器
        commands.stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                // 跳过前几页的内容
                .skip((usePage - 1) * this.pageSize)
                // 限制单页的条数
                .limit(this.pageSize)
                // 输出目标页的内容
                .forEach(e -> {
                    String desc = e.getValue().desc();
                    if (desc.isEmpty()) {
                        sender.sendMessage(ChatColor.GREEN + String.format("%s", e.getKey()));
                    } else {
                        sender.sendMessage(ChatColor.GREEN + String.format("%s -- %s", e.getKey(), desc));
                    }
                });
    }

    /**
     * 发送一条命令的帮助
     * @param sender 命令的执行者
     * @param info 这个命令的信息
     */
    private void sendHelp(@Nonnull CommandSender sender, @Nonnull CommandInfo info) {
        sender.sendMessage(String.format("%s%s------- help %s ----------------", ChatColor.AQUA, ChatColor.BOLD, info.getName()));
        // 命令的用法 / 参数
        if (Strings.notEmpty(info.getCommand().usage())) {
            sender.sendMessage(String.format("%s/%s %s %s", ChatColor.GREEN, commandManager.getBaseCommandName(), info.getName(), info.getCommand().usage()));
        }
        // 命令的说明
        if (Strings.notEmpty(info.getCommand().desc())) {
            sender.sendMessage(ChatColor.GREEN + info.getCommand().desc());
        }
        // 命令的帮助信息
        for (String line : info.getCommand().help()) {
            if (!line.isEmpty()) {
                sender.sendMessage(ChatColor.GREEN + line);
            }
        }

        // 命令的简写列表
        StringBuilder aliases = new StringBuilder("aliases: ");
        int aliasCount = 0;
        for (String alias : info.getCommand().aliases()) {
            if (!alias.isEmpty()) {
                aliasCount += 1;
                aliases.append(alias);
                aliases.append(" ");
            }
        }
        if (aliasCount > 0) {
            sender.sendMessage(ChatColor.GREEN + aliases.toString());
        }
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
