package org.cat73.getcommand.nms.v4;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cat73.bukkitboot.annotation.Bean;
import org.cat73.bukkitboot.annotation.NMSVersion;
import org.cat73.bukkitboot.util.reflect.NMS;
import org.cat73.bukkitboot.util.reflect.Reflects;
import org.cat73.getcommand.nms.IGiveCommandGenerator;
import org.cat73.getcommand.util.NBT2YamlUtil;

/**
 * 通过玩家手上拿的物品获取 give 命令
 * <p>支持 1.13.1 ~ 1.13.2</p>
 */
@Bean
@NMSVersion(NMS.v1_13_R2)
public class GiveCommandGeneratorV4 implements IGiveCommandGenerator {
    @Override
    public String generator(Player player) throws Exception {
        // 获取玩家手上的物品
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.AIR) {
            // 获取玩家名
            String playerName = player.getName();
            // 获取物品名
            String itemName = this.getNMSName(item);
            // 获取物品的附加数据标签
            String dataTag = this.getDataTag(item);

            // 拼凑 give 命令并返回
            return String.format("/minecraft:give %s %s%s %d", playerName, itemName, dataTag, 1);
        }
        return null;
    }

    /**
     * 获取 Item 的 MinecraftKey
     * @param item 目标物品
     * @return 目标方块的 MinecraftKey
     * @throws Exception 如果获取过程中出现了异常
     */
    private String getNMSName(ItemStack item) throws Exception {
        // IRegistry<Item> REGISTRY = IRegistry.ITEM;
        Class<?> IRegistryClass = NMS.nms("IRegistry");
        Object REGISTRY = Reflects.readField(IRegistryClass, null, "ITEM");

        // ItemStack NMSItemStack = item.handle;
        Object NMSItemStack = Reflects.readField(item, "handle");

        // Item NMSItem = NMSItemStack.getItem();
        Object NMSItem = Reflects.invoke(NMSItemStack, "getItem");

        // MinecraftKey minecraftKey = REGISTRY.getKey(NMSItem);
        Object minecraftKey = Reflects.invoke(REGISTRY.getClass(), REGISTRY, "getKey", new Class<?>[] { Object.class }, NMSItem);

        return minecraftKey.toString();
    }

    /**
     * 获取 Item 的附加数据标签
     * @param item 要被获取附加数据标签的物品
     * @return 目标物品的附加数据标签
     * @throws Exception 如果获取过程中出现了异常
     */
    private String getDataTag(ItemStack item) throws Exception {
        // 获取 item 的 NBTTagCompound
        // NBTTagCompound NBTTagCompound = new NBTTagCompound();
        Class<?> NBTTagCompoundClass = NMS.nms("NBTTagCompound");
        Object NBTTagCompound = Reflects.newInstance(NBTTagCompoundClass);

        // item.handle.save(NBTTagCompound)
        Object handle = Reflects.readField(item, "handle");
        Reflects.invoke(handle, "save", NBTTagCompound);

        // 将 NBTTagCompound 序列化成 YAML 并返回
        return NBT2YamlUtil.toYaml(NBTTagCompound, "tag");
    }
}
