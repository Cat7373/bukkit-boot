package org.cat73.bukkitboot.util;

import org.cat73.bukkitboot.context.PluginContextManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Level;

/**
 * 日志输出工具类
 */
public final class Logger {
    private Logger() {
        throw new UnsupportedOperationException();
    }

    /**
     * 输出日志
     * @param level 日志的级别
     * @param format 内容的格式
     * @param args 格式化时使用的参数
     */
    public static void log(@Nonnull Level level, @Nonnull String format, @Nullable Object... args) {
        PluginContextManager.current().getPlugin().getLogger().log(level, String.format(format, args));
    }

    /**
     * 输出信息日志
     * @param format 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void info(@Nonnull String format, @Nullable Object... args) {
        Logger.log(Level.INFO, format, args);
    }

    /**
     * 输出调试日志
     * @param format 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void debug(@Nonnull String format, @Nullable Object... args) {
        Logger.info("[Debug] " + format, args);
    }

    /**
     * 输出警告日志
     * @param format 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void warn(@Nonnull String format, @Nullable Object... args) {
        Logger.log(Level.WARNING, format, args);
    }

    /**
     * 输出警告日志
     * @param format 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void warning(@Nonnull String format, @Nullable Object... args) {
        Logger.warn(format, args);
    }

    /**
     * 输出错误日志
     * @param format 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void error(@Nonnull String format, @Nullable Object... args) {
        Logger.log(Level.SEVERE, format, args);
    }
}
