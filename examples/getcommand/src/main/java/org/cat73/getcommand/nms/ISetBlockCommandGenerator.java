package org.cat73.getcommand.nms;

import org.bukkit.block.Block;

/**
 * 通过方块来获取 setblock 命令的接口
 * <p>版本列表：</p>
 * <ul>
 *     <li>v1: 1.8.3 ~ 1.8.8</li>
 *     <li>v2: 1.9 ~ 1.12.2</li>
 *     <li>v3: 1.13 ~ 1.13.2</li>
 * </ul>
 */
public interface ISetBlockCommandGenerator extends ICommandGenerator<Block> {
    /**
     * 通过方块来获取 setblock 命令
     *
     * @param block 目标方块
     * @return 对应的 setblock 命令
     * @throws Exception 如果获取过程中出现了任何异常
     */
    String generator(Block block) throws Exception;
}
