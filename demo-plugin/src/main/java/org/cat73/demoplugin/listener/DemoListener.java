package org.cat73.demoplugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.cat73.catbase.annotation.Bean;
import org.cat73.catbase.util.Logger;

@Bean
public class DemoListener implements Listener {
    @EventHandler
    public void test(ServerLoadEvent event) {
        Logger.debug("DemoListener.test(): %s", event);
    }
}