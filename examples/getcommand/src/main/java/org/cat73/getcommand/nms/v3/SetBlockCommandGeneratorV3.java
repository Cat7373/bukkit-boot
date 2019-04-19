package org.cat73.getcommand.nms.v3;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.core.condition.ConditionalOnNMSVersion;
import org.cat73.bukkitboot.util.reflect.NMS;
import org.cat73.bukkitboot.util.reflect.Reflects;
import org.cat73.getcommand.nms.ISetBlockCommandGenerator;
import org.cat73.getcommand.util.NBT2YamlUtil;

/**
 * 通过方块来获取 setblock 命令
 * <p>支持 1.13 ~ 1.13.2</p>
 */
@Bean
@ConditionalOnNMSVersion({NMS.v1_13_R1, NMS.v1_13_R2})
public class SetBlockCommandGeneratorV3 implements ISetBlockCommandGenerator {
    @Override
    public String generator(Block block) throws Exception {
        // 获取方块名
        String nameAndState = this.getNMSNameAndState(block);
        // 获取附加数据标签
        String dataTag = this.getDataTag(block);
        // 拼凑 setblock 命令并返回结果
        return String.format("/minecraft:setblock %s %s %s %s%s %s", "~", "~1", "~", nameAndState, dataTag, "replace");
    }

    /**
     * 获取 Block 的附加数据标签
     * @param block 目标方块
     * @return 目标方块的附加数据标签
     * @throws Exception 如果获取过程中出现了异常
     */
    private String getDataTag(Block block) throws Exception {
        // 方块所在的世界
        World world = block.getWorld();

        // WorldServer worldServer = world.world;
        Object worldServer = Reflects.readField(world, "world");

        // BlockPosition pos = block.position
        Object pos = Reflects.readField(block, "position");

        // TileEntity tileEntity = worldServer.getTileEntity(pos);
        Object tileEntity = Reflects.invoke(worldServer, "getTileEntity", pos);

        // 如果没有附加数据标签则直接返回
        if (tileEntity == null) {
            return "";
        }

        // NBTTagCompound NBTTagCompound = new NBTTagCompound();
        Class<?> NBTTagCompoundClass = NMS.nms("NBTTagCompound");
        Object NBTTagCompound = Reflects.newInstance(NBTTagCompoundClass);

        // tileEntity.save(NBTTagCompound);
        Reflects.invoke(tileEntity, "save", NBTTagCompound);

        // 将 NBTTagCompound 序列化成 YAML 并返回
        return NBT2YamlUtil.toYaml(NBTTagCompound, null);
    }

    /**
     * 获取 Block 的名字和 State
     * @param block 目标方块
     * @return 目标方块的名字和 State
     */
    private String getNMSNameAndState(Block block) {
        return block.getBlockData().getAsString();
    }
}
