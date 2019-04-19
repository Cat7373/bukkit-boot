package org.cat73.getcommand.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.core.Inject;
import org.cat73.getcommand.nms.ISummonCommandGenerator;
import org.cat73.getcommand.status.PlayersStatus;

/**
 * 实体相关的事件的处理器
 */
@Bean
public class EntityListener implements Listener {
    @Inject
    private PlayersStatus playersStatus;
    @Inject
    private ISummonCommandGenerator summonCommandGenerator;

    /**
     * 当玩家攻击一个实体时，根据状态来保存命令
     *
     * @param event 玩家攻击实体的事件
     * @throws Exception 如果处理过程中出现了异常
     */
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) throws Exception {
        // 获取攻击者
        Entity damager = event.getDamager();
        // 如果攻击者为玩家
        if (damager instanceof Player) {
            // 获取玩家对象
            Player player = (Player) damager;
            // 获取玩家名
            String playerName = player.getName();
            // 如果玩家正在等待实体
            if (this.playersStatus.getStatus().get(playerName) == PlayersStatus.Status.WAIT_ENTITY) {
                // 取消这次攻击
                event.setCancelled(true);

                // 准备数据
                Entity entity = event.getEntity();
                String command = this.summonCommandGenerator.generator(entity);

                // 设置状态
                this.playersStatus.getCommands().put(playerName, command);
                this.playersStatus.getStatus().remove(playerName);

                // 发送提示
                player.sendMessage(String.format("%s获取 summon 命令成功，请用 save 来保存命令", ChatColor.GREEN));
            }
        }
    }
}
