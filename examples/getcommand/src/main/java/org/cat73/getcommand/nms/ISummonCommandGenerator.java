package org.cat73.getcommand.nms;

import org.bukkit.entity.Entity;

/**
 * 通过实体获取 summon 命令的接口
 * <p>版本列表：</p>
 * <ul>
 *     <li>v1: 1.8.3 ~ 1.10.2</li>
 *     <li>v2: 1.11 ~ 1.12.2</li>
 *     <li>v3: 1.13</li>
 *     <li>v3: 1.13.1 ~ 1.13.2</li>
 * </ul>
 */
public interface ISummonCommandGenerator extends ICommandGenerator<Entity> {
    /**
     * 通过实体获取 summon 命令
     *
     * @param entity 目标实体
     * @return 对应的 summon 命令
     * @throws Exception 如果获取过程中出现了任何异常
     */
    String generator(Entity entity) throws Exception;
}
