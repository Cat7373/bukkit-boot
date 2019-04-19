package org.cat73.getcommand.nms.v1;

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
 * <p>支持 1.8.3 ~ 1.8.8</p>
 */
@Bean
@ConditionalOnNMSVersion({NMS.v1_8_R2, NMS.v1_8_R3})
public class SetBlockCommandGeneratorV1 implements ISetBlockCommandGenerator {
    @Override
    public String generator(Block block) throws Exception {
        // 获取方块名
        String tileName = this.getNMSName(block);
        // 获取附加数据值
        byte dataValue = block.getData();
        // 获取附加数据标签
        String dataTag = this.getDataTag(block);
        // 拼凑 setblock 命令，并返回结果
        return String.format("/minecraft:setblock %s %s %s %s %d %s %s", "~", "~1", "~", tileName, dataValue, "replace", dataTag);
    }

    /**
     * 获取 Block 的 MinecraftKey
     * @param block 目标方块
     * @return 目标方块的 MinecraftKey
     * @throws Exception 如果获取过程中出现了异常
     */
    private String getNMSName(Block block) throws Exception {
        // Block NMSBlock = block.getNMSBlock();
        Object NMSBlock = Reflects.invoke(block, "getNMSBlock");

        // RegistryBlocks<MinecraftKey, Block> REGISTRY = Block.REGISTRY;
        Class<?> BlockClass = NMS.nms("Block");
        Object REGISTRY = Reflects.readField(BlockClass, null, "REGISTRY");

        // MinecraftKey minecraftKey = REGISTRY.c(NMSBlock);
        Object minecraftKey = Reflects.invoke(REGISTRY.getClass(), REGISTRY, "c", new Class<?>[] { Object.class }, NMSBlock);

        return minecraftKey.toString();
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

        // 方块的位置
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        // TileEntity tileEntity = world.getTileEntityAt(x, y, z);
        Object tileEntity = Reflects.invoke(world.getClass(), world, "getTileEntityAt", new Class<?>[] { int.class, int.class, int.class }, x, y, z);

        // 如果没有附加数据标签则直接返回
        if (tileEntity == null) {
            return "";
        }

        // NBTTagCompound NBTTagCompound = new NBTTagCompound();
        Class<?> NBTTagCompoundClass = NMS.nms("NBTTagCompound");
        Object NBTTagCompound = Reflects.newInstance(NBTTagCompoundClass);

        // tileEntity.save(NBTTagCompound);
        Reflects.invoke(tileEntity, "b", NBTTagCompound);

        // 将 NBTTagCompound 序列化成 YAML 并返回
        return NBT2YamlUtil.toYaml(NBTTagCompound, null);
    }
}
