package org.cat73.getcommand.status;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cat73.bukkitboot.annotation.core.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 内部保存的玩家状态
 */
@Bean
public class PlayersStatus implements Listener {
    /**
     * 玩家获取到的命令
     **/
    @Getter
    private final Map<String, String> commands = new HashMap<>();
    /**
     * 玩家的当前状态
     **/
    @Getter
    private final Map<String, Status> status = new HashMap<>();

    /**
     * 当玩家退出时，清理玩家保存的数据
     * @param event 玩家退出事件
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        commands.remove(event.getPlayer().getName());
        status.remove(event.getPlayer().getName());
    }

    /**
     * 玩家的当前状态
     */
    public enum Status {
        /**
         * 等待点一下方块(获取命令)
         */
        WAIT_BLOCK,
        /**
         * 等待打一下实体(获取命令)
         */
        WAIT_ENTITY,
        /**
         * 等待点一下命令方块(保存命令)
         */
        WAIT_COMMAND_BLOCK,
    }
}
