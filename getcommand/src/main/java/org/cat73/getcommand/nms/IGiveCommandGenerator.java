package org.cat73.getcommand.nms;

import org.bukkit.entity.Player;

/**
 * 通过玩家手上拿的物品获取 give 命令的接口
 * <p>版本列表：</p>
 * <ul>
 *     <li>v1: 1.8.3 ~ 1.8.8</li>
 *     <li>v2: 1.9 ~ 1.12.2</li>
 *     <li>v3: 1.13</li>
 *     <li>v3: 1.13.1 ~ 1.13.2</li>
 * </ul>
 */
public interface IGiveCommandGenerator extends ICommandGenerator<Player> {
    /**
     * 通过玩家手上拿的物品获取 give 命令
     *
     * @param player 目标玩家
     * @return 如果手上有物品，则返回对应的 give 命令，如果没有则返回 null
     * @throws Exception 如果获取过程中出现了任何异常
     */
    String generator(Player player) throws Exception;
}
