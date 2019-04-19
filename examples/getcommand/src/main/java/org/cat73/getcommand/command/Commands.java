package org.cat73.getcommand.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cat73.bukkitboot.annotation.command.Command;
import org.cat73.bukkitboot.annotation.command.TabCompleter;
import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.core.Inject;
import org.cat73.bukkitboot.util.Strings;
import org.cat73.getcommand.nms.IGiveCommandGenerator;
import org.cat73.getcommand.status.PlayersStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件的命令
 */
@Bean
public class Commands {
    @Inject
    private PlayersStatus playersStatus;
    @Inject
    private IGiveCommandGenerator giveCommandGenerator;

    /**
     * 获取手上物品的 Give 命令
     * @param player 执行命令的玩家
     * @throws Exception 如果获取过程中出现了异常
     */
    @Command(permission = "getcommand.item", target = Command.Target.PLAYER, desc = "获取手上物品的 Give 命令", aliases = "i")
    public void item(Player player) throws Exception {
        // 获取玩家名
        String playerName = player.getName();
        // 获取玩家手上物品的 give 命令
        String command = this.giveCommandGenerator.generator(player);
        // 根据接口定义 返回 null 视为手上没有物品
        if (command == null) {
            player.sendMessage(String.format("%s必须手持一个物品才能执行这个命令", ChatColor.RED));
        } else {
            // 设置状态
            this.playersStatus.getCommands().put(playerName, command);
            this.playersStatus.getStatus().remove(playerName);

            // 发送提示
            player.sendMessage(String.format("%s获取 give 命令成功，请用 save 来保存命令", ChatColor.GREEN));
        }
    }

    /**
     * 点一下方块来获取 setblock 命令
     * @param player 执行命令的玩家
     */
    @Command(permission = "getcommand.block", target = Command.Target.PLAYER, desc = "点一下方块来获取 setblock 命令", aliases = "b")
    public void block(Player player) {
        // 设置玩家状态为等待方块
        this.playersStatus.getStatus().put(player.getName(), PlayersStatus.Status.WAIT_BLOCK);
        // 发送提示
        player.sendMessage(String.format("%s请左键点一下目标方块来获取 setblock 命令(不会真的破坏方块)", ChatColor.GREEN));
    }

    /**
     * 打一下生物来获取 summon 命令
     * @param player 执行命令的玩家
     */
    // TODO 加个参数，用正在看着的生物
    @Command(permission = "getcommand.entity", target = Command.Target.PLAYER, desc = "打一下生物来获取 summon 命令", aliases = "e")
    public void entity(Player player) {
        // 设置玩家状态为等待实体
        this.playersStatus.getStatus().put(player.getName(), PlayersStatus.Status.WAIT_ENTITY);
        // 发送提示
        player.sendMessage(String.format("%s请打一下目标生物来获取 summon 命令(不会真的造成伤害)", ChatColor.GREEN));
    }

    /**
     * 取消当前操作
     * @param player 执行命令的玩家
     */
    @Command(permission = "getcommand.cancel", target = Command.Target.PLAYER, desc = "取消当前操作", aliases = "c")
    public void cancen(Player player) {
        // 移除玩家的状态
        this.playersStatus.getStatus().remove(player.getName());
        // 发送提示
        player.sendMessage(String.format("%s成功取消当前操作", ChatColor.GREEN));
    }

    /**
     * 保存命令
     * @param player 执行命令的玩家
     * @param mode 保存模式(可选，默认为 chat)
     */
    @Command(
            permission = "getcommand.save",
            target = Command.Target.PLAYER,
            usage = "[chat | file | console | command_block]",
            desc = "保存上一个获取到的命令",
            aliases = "s",
            help = {
                    "chat: 将指令在聊天信息输出(默认)",
                    "file: 将指令保存到 log 文件里, 简写: f(未实现)",
                    "console: 将指令输出到控制台, 简写: c",
                    "command_block: 将指令输出到空白的命令方块中, 简写: cb"
            })
    public void save(Player player, @Inject(required = false) String mode) {
        // 获取玩家名
        String playerName = player.getName();
        // 获取上一个获取到的命令
        String command = this.playersStatus.getCommands().get(playerName);
        // 判断命令是否为空
        if (command != null && !command.isEmpty()) {
            // 如果无参数则默认为 chat
            if (Strings.isEmpty(mode)) {
                mode = "chat";
            }

            // 保存类型 0 什么也不做, 1 chat, 2 file, 3 console, 4 command_block
            int saveType;
            // 所需权限
            String permission;
            // 获取保存类型 获取所需权限名 过滤无效参数
            switch (mode.toLowerCase()) {
                case "f":
                case "file":
                    saveType = 2;
                    permission = "getcommand.save.file";
                    break;
                case "c":
                case "console":
                    saveType = 3;
                    permission = "getcommand.save.console";
                    break;
                case "cb":
                case "command_block":
                    saveType = 4;
                    permission = "getcommand.save.command_block";
                    break;
                default:
                case "chat":
                    saveType = 1;
                    permission = "getcommand.save.chat";
                    break;
            }
            // 判断是否有权限
            if (!player.hasPermission(permission)) {
                player.sendMessage(String.format("%s你需要 %s 权限才能执行这个命令", ChatColor.RED, permission));
                return;
            }

            // 执行具体命令
            switch (saveType) {
                case 3: // console
                    CommandSender sender = Bukkit.getConsoleSender();
                    sender.sendMessage(String.format("%s%s------- 当前获取到的命令 ----------------", ChatColor.AQUA, ChatColor.BOLD));
                    sender.sendMessage(String.format("%s%s", ChatColor.GREEN, command));
                    break;
                case 1: // chat
                    player.sendMessage(String.format("%s%s------- 当前获取到的命令 ----------------", ChatColor.AQUA, ChatColor.BOLD));
                    player.sendMessage(String.format("%s%s", ChatColor.GREEN, command));
                    break;
                case 2: // file // TODO save file
                    player.sendMessage(String.format("%s保存到文件的功能暂未实现", ChatColor.RED));
                    break;
                case 4: // command_block
                    this.playersStatus.getStatus().put(playerName, PlayersStatus.Status.WAIT_COMMAND_BLOCK);
                    player.sendMessage(String.format("%s请在创造模式打一下一个空白的命令方块来保存命令", ChatColor.GREEN));
                    break;
            }
        } else {
            player.sendMessage(String.format("%s当前没有已获取到的命令", ChatColor.RED));
        }
    }

    /**
     * Save 命令的模式列表
     */
    private static final List<String> saveModes = Arrays.asList("chat", "cb", "command_block", "c", "console", "f", "file");

    /**
     * Save 命令的 Tab 补全器
     * @param mode 保存模式
     * @return 可补全出的参数列表
     */
    @TabCompleter(name = "save")
    public List<String> saveTabCompleter(@Inject(required = false) String mode) {
        if (Strings.isEmpty(mode)) {
            return saveModes;
        } else {
            return saveModes.stream()
                    .filter(m -> m.startsWith(mode))
                    .collect(Collectors.toList());
        }
    }
}
