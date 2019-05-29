package org.cat73.bukkitboot.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 针对每个玩家用于存储数据的容器
 */
public class PlayerSession {
    /**
     * 实际用于存储数据的 Map
     */
    private final Map<String, Object> map = new HashMap<>();

    /**
     * 设置一个变量
     * @param key 变量名
     * @param value 变量的值
     * @param <T> 变量的数据类型
     * @return 变量被替换之前的值
     * @throws ClassCastException 如果泛型猜解的数据类型和实际的类型不符，实际这个异常是在调用方代码出现的
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T set(@Nonnull String key, @Nullable T value) {
        return (T) this.map.put(key, value);
    }

    /**
     * 判断一个变量是否存在
     * @param key 变量名
     * @return 变量是否存在
     */
    public boolean contains(@Nonnull String key) {
        return this.map.containsKey(key);
    }

    /**
     * 获取一个变量
     * @param key 变量名
     * @param <T> 变量的数据类型
     * @return 获取到的变量，如变量不存在则返回 null
     * @throws ClassCastException 如果泛型猜解的数据类型和实际的类型不符，实际这个异常是在调用方代码出现的
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(@Nonnull String key) throws ClassCastException {
        return (T) this.map.get(key);
    }

    /**
     * 移除一个变量
     * @param key 变量名
     * @param <T> 变量的数据类型
     * @return 变量被删除之前的值
     * @throws ClassCastException 如果泛型猜解的数据类型和实际的类型不符，实际这个异常是在调用方代码出现的
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T remove(@Nonnull String key) {
        return (T) this.map.remove(key);
    }

    // TODO javadoc
    @Nonnull
    public static PlayerSession forPlayer(@Nonnull Player player) {
        return forPlayer(player.getUniqueId());
    }

    // TODO javadoc
    @Nonnull
    public static PlayerSession forPlayer(@Nonnull String playerName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(playerName)) {
                return forPlayer(player.getUniqueId());
            }
        }

        throw new NullPointerException("玩家不在线");
    }

    // TODO javadoc
    @Nonnull
    public static PlayerSession forPlayer(@Nonnull UUID uuid) {
        return Plugins.currentContext().playerSession(uuid);
    }
}
