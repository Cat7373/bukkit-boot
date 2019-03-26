package org.cat73.catbase.util;

import org.cat73.catbase.context.PluginContextManager;

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
    public static void log(Level level, String format, Object... args) {
        PluginContextManager.current().getPlugin().getLogger().log(level, String.format(format, args));
    }

    /**
     * 输出信息日志
     * @param msg 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void info(String msg, Object... args) {
        Logger.log(Level.INFO, msg, args);
    }

    /**
     * 输出调试日志
     * @param msg 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void debug(String msg, Object... args) {
        Logger.info("[Debug] " + msg, args);
    }

    /**
     * 输出警告日志
     * @param msg 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void warn(String msg, Object... args) {
        Logger.log(Level.WARNING, msg, args);
    }

    /**
     * 输出警告日志
     * @param msg 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void warning(String msg, Object... args) {
        Logger.warn(msg, args);
    }

    /**
     * 输出错误日志
     * @param msg 日志的格式
     * @param args 格式化时使用的参数
     */
    public static void error(String msg, Object... args) {
        Logger.log(Level.SEVERE, msg, args);
    }
}
