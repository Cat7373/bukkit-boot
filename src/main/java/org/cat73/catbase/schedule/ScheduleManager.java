package org.cat73.catbase.schedule;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.cat73.catbase.annotation.Scheduled;
import org.cat73.catbase.context.IManager;
import org.cat73.catbase.context.PluginContext;
import org.cat73.catbase.util.Lang;
import org.cat73.catbase.util.reflect.Reflects;

import javax.annotation.Nonnull;

// TODO javadoc
public class ScheduleManager implements IManager {
    // TODO javadoc
    private final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    @Override
    public void register(@Nonnull PluginContext context, @Nonnull Object bean) {
        Reflects.forEachMethodByAnnotation(bean.getClass(), Scheduled.class, (method, annotation) -> {
            long delay = Math.max(annotation.delay(), 0L);
            long period = annotation.period();
            boolean async = annotation.async();
            Plugin plugin = context.getPlugin();
            Runnable task = ((Lang.ThrowableRunnable) () -> method.invoke(bean)).wrap();

            if (period < 0) {
                if (delay == 0L) {
                    if (async) {
                        this.scheduler.runTaskAsynchronously(plugin, task);
                    } else {
                        this.scheduler.runTask(plugin, task);
                    }
                } else {
                    if (async) {
                        this.scheduler.runTaskLaterAsynchronously(plugin, task, delay);
                    } else {
                        this.scheduler.runTaskLater(plugin, task, delay);
                    }
                }
            } else {
                if (async) {
                    this.scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
                } else {
                    this.scheduler.runTaskTimer(plugin, task, delay, period);
                }
            }
        });
    }
}
