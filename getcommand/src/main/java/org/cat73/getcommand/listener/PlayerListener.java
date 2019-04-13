package org.cat73.getcommand.listener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cat73.bukkitboot.annotation.Bean;
import org.cat73.bukkitboot.annotation.Inject;
import org.cat73.getcommand.nms.ISetBlockCommandGenerator;
import org.cat73.getcommand.status.PlayersStatus;

import java.util.Optional;

/**
 * 玩家相关的事件的处理器
 */
@Bean
public class PlayerListener implements Listener {
    @Inject
    private PlayersStatus playersStatus;
    @Inject
    private ISetBlockCommandGenerator setBlockCommandGenerator;

    /**
     * 当玩家点击一个方块时，根据状态来保存命令或获取命令
     * @param event 玩家点击方块的事件
     * @throws Exception 如果处理过程中出现了异常
     */
    @EventHandler
    public void onEntityDamageByEntityEvent(PlayerInteractEvent event) throws Exception {
        if (event.useItemInHand() == Event.Result.DENY) {
            return;
        }

        // 如果是左键点击
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // 获取玩家对象
            Player player = event.getPlayer();
            // 获取玩家名
            String playerName = player.getName();
            // 获取玩家当前的状态
            PlayersStatus.Status status = this.playersStatus.getStatus().get(playerName);
            if (status == PlayersStatus.Status.WAIT_BLOCK) {
                // 如果玩家正在等待方块
                // 取消这次操作
                event.setCancelled(true);

                // 准备数据
                Block block = event.getClickedBlock();
                String command = this.setBlockCommandGenerator.generator(block);

                // 设置状态
                this.playersStatus.getCommands().put(playerName, command);
                this.playersStatus.getStatus().remove(playerName);

                // 发送提示
                player.sendMessage(String.format("%s获取 setblock 命令成功，请用 save 来保存命令", ChatColor.GREEN));
            } else if (status == PlayersStatus.Status.WAIT_COMMAND_BLOCK) {
                // 如果玩家正在等待方块命令方块
                // 获取被点击的方块
                Block block = event.getClickedBlock();

                // 判断是否为命令方块
                if (Optional.ofNullable(block).map(Block::getState).map(Object::getClass).filter(CommandBlock.class::isAssignableFrom).isPresent()) {
                    // 取消这次操作
                    event.setCancelled(true);

                    // 判断玩家是否在创造模式
                    if (player.getGameMode() == GameMode.CREATIVE) {
                        // 获取命令方块的附加数据
                        CommandBlock commandBlock = (CommandBlock) block.getState();

                        // 判断目标命令方块中的命令是否为空
                        if (commandBlock.getCommand().trim().isEmpty()) {
                            // 设置目标命令方块的命令
                            commandBlock.setCommand(this.playersStatus.getCommands().get(playerName));
                            commandBlock.update();

                            // 设置状态
                            this.playersStatus.getStatus().remove(playerName);

                            // 输出提示
                            player.sendMessage(String.format("%s保存命令到命令方块成功", ChatColor.GREEN));
                        } else {
                            player.sendMessage(String.format("%s必须选择一个没有命令的命令方块作为目标", ChatColor.RED));
                        }
                    } else {
                        player.sendMessage(String.format("%s保存命令到命令方块需要在创造模式", ChatColor.RED));
                    }
                }
            }
        }
    }
}
