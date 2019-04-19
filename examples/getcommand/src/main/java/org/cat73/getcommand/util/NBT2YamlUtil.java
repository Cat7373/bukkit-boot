package org.cat73.getcommand.util;

import lombok.NonNull;
import org.cat73.bukkitboot.util.Lang;
import org.cat73.bukkitboot.util.reflect.Reflects;

/**
 * NBT 转 YAML 工具类
 */
public final class NBT2YamlUtil {
    private NBT2YamlUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 将 NBT 标签内的指定列表转为 YAML
     * @param nbt NBT 标签
     * @param tagName 子标签名, 如为 null 或空则直接转换不取子标签
     * @return NBT 标签内的 tag 转为 YAML 后的结果
     */
    public static String toYaml(@NonNull Object nbt, @NonNull String tagName) {
        if (tagName != null && !tagName.isEmpty()) {
            try {
                nbt = Reflects.invoke(nbt, "getCompound", tagName);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw Lang.wrapThrow(e);
            }
        }

        return nbt.toString();
    }
}
