package org.cat73.bukkitboot.schedule;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.cat73.bukkitboot.annotation.Scheduled;
import org.cat73.bukkitboot.annotation.Scheduleds;
import org.cat73.bukkitboot.context.IManager;
import org.cat73.bukkitboot.context.PluginContext;
import org.cat73.bukkitboot.util.Lang;
import org.cat73.bukkitboot.util.reflect.Reflects;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * 定时任务管理器
 * <!-- TODO 详细说明 -->
 */
public class ScheduleManager implements IManager {
    /**
     * Bukkit 的定时任务
     */
    private final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    @Override
    public void register(@Nonnull PluginContext context, @Nonnull Object bean) {
        Reflects.forEachMethodByAnnotation(bean.getClass(), Scheduled.class, (m, a) -> this.register(context, bean, m, a));
        Reflects.forEachMethodByAnnotation(bean.getClass(), Scheduleds.class, (m, a) -> {
            for (Scheduled annotation : a.value()) {
                this.register(context, bean, m, annotation);
            }
        });
    }

    /**
     * 注册一个定时任务
     * @param context 插件的上下文
     * @param bean 方法所在的 Bean 的实例
     * @param method 定时任务要执行的方法
     * @param annotation 定时任务的注解
     */
    private void register(@Nonnull PluginContext context, @Nonnull Object bean, @Nonnull Method method, @Nonnull Scheduled annotation) {
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
    }
}
