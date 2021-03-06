package org.cat73.demoplugin.schedule;

import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.schedule.Scheduled;
import org.cat73.bukkitboot.util.Logger;

@Bean
public class DemoTask {
    @Scheduled
    @Scheduled(delay = 5 * 20)
    public void once() {
        Logger.debug("DemoTask.once()");
    }

    @Scheduled(delay = 20 * 10, period = 20 * 30)
    public void timer() {
        Logger.debug("DemoTask.timer()");
    }

    @Scheduled(async = true)
    public void async() {
        Logger.debug("DemoTask.async(): %s", Thread.currentThread().getName());
    }
}
