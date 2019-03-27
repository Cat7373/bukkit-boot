package org.cat73.demoplugin.schedule;

import org.cat73.catbase.annotation.Bean;
import org.cat73.catbase.annotation.Scheduled;
import org.cat73.catbase.util.Logger;

@Bean
public class DemoTask {
    @Scheduled
    public void once() {
        Logger.debug("DemoTask.once()");
    }

    @Scheduled(delay = 20 * 10, period = 20 * 5)
    public void timer() {
        Logger.debug("DemoTask.timer()");
    }

    @Scheduled(async = true)
    public void async() {
        Logger.debug("DemoTask.async(): %s", Thread.currentThread().getName());
    }
}
