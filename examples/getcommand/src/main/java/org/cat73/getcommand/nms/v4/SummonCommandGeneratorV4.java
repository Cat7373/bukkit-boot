package org.cat73.getcommand.nms.v4;

import org.bukkit.entity.Entity;
import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.core.condition.ConditionalOnNMSVersion;
import org.cat73.bukkitboot.util.reflect.NMS;
import org.cat73.bukkitboot.util.reflect.Reflects;
import org.cat73.getcommand.nms.ISummonCommandGenerator;
import org.cat73.getcommand.util.NBT2YamlUtil;

/**
 * 通过实体获取 summon 命令
 * <p>支持 1.13.1 ~ 1.13.2</p>
 */
@Bean
@ConditionalOnNMSVersion(NMS.v1_13_R2)
public class SummonCommandGeneratorV4 implements ISummonCommandGenerator {
    @Override
    public String generator(Entity entity) throws Exception {
        // 获取实体名
        String entityName = this.getNMSName(entity);
        // 获取实体附加数据标签
        String dataTag = this.getDataTag(entity);
        // 拼凑 summon 命令并返回结果
        return String.format("/minecraft:summon %s %s %s %s %s", entityName, "~", "~1", "~", dataTag);
    }

    /**
     * 获取 Entity 的 MinecraftKey
     * @param entity 目标实体
     * @return 目标实体的 MinecraftKey
     * @throws Exception 如果获取过程中出现了异常
     */
    private String getNMSName(Entity entity) throws Exception {
        // IRegistry<EntityTypes> REGISTRY = IRegistry.ENTITY_TYPE;
        Class<?> IRegistryClass = NMS.nms("IRegistry");
        Object REGISTRY = Reflects.readField(IRegistryClass, null, "ENTITY_TYPE");

        // Entity NMSEntity = entity.entity
        Object NMSEntity = Reflects.readField(entity, "entity");

        // EntityTypes type = NMSEntity.P()
        Object type = Reflects.invoke(NMSEntity, "P");

        // return REGISTRY.getKey(handle.getClass());
        return Reflects.invoke(IRegistryClass, REGISTRY, "getKey", new Class<?>[] { Object.class }, type).toString();
    }

    /**
     * 获取 Entity 的附加数据标签
     * @param entity 要被获取附加数据标签的实体
     * @return 目标实体的附加数据标签
     * @throws Exception 如果获取过程中出现了异常
     */
    private String getDataTag(Entity entity) throws Exception {
        // 获取 entity 的 NBTTagCompound
        // NBTTagCompound NBTTagCompound = new NBTTagCompound();
        Class<?> NBTTagCompoundClass = NMS.nms("NBTTagCompound");
        Object NBTTagCompound = Reflects.newInstance(NBTTagCompoundClass);

        // Entity NMSEntity = entity.entity
        Object NMSEntity = Reflects.readField(entity, "entity");
        // NMSEntity.b(NBTTagCompound);
        Reflects.invoke(NMSEntity, "b", NBTTagCompound);

        // 将 NBTTagCompound 序列化成 YAML 并返回
        return NBT2YamlUtil.toYaml(NBTTagCompound, null);
    }
}
