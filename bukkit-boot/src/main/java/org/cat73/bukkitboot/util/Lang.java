package org.cat73.bukkitboot.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.*;

/**
 * 一些异常相关的工具类
 */
public final class Lang {
    private Lang() {
        throw new UnsupportedOperationException();
    }

    /**
     * 将参数包装为 RuntimeException 后返回，如果它已经是一个 RuntimeException 则直接返回
     * @param e 被包装的异常
     * @return 包装后的异常
     */
    @Nonnull
    public static RuntimeException wrapThrow(@Nonnull Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 根据格式化字符串生成 RuntimeException
     * @param format 格式化字符串
     * @param args 格式化时的参数
     * @return 生成的异常
     */
    @Nonnull
    public static RuntimeException makeThrow(@Nonnull String format, @Nullable Object... args) {
        return new RuntimeException(String.format(format, args));
    }

    /**
     * 生成一个未实现的运行时异常，用于暂未实现的代码
     * @return 未实现的运行时异常
     */
    @Nonnull
    public static RuntimeException noImplement() {
        return Lang.makeThrow("Not implement yet!");
    }

    /**
     * 生成一个不可能出现的运行时异常
     * @return 不可能出现的运行时异常
     */
    @Nonnull
    public static RuntimeException impossible() {
        return Lang.makeThrow("r u kidding me?! It is impossible!");
    }

    /**
     * 将传入的异常直接抛出<br>
     * @param e 被抛出的异常
     * @param <T> 异常的类型，其实主要是为了绕过 Java 不允许抛 Exception 的检查
     * @return 永远不会返回任何东西，返回值只是为了方便在必须有返回值的方法中 throw 出去来通过语法检查的。
     * @throws T 直接抛出传入的异常
     */
    @Nonnull // 实际上一定不会返回的
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException throwAny(@Nonnull Throwable e) throws T {
        throw (T) e;
    }

    /**
     * 执行一段可能抛出异常代码，自动用 warpThrow 包装出现的异常
     * @param s 被包装的会抛出异常的代码
     * @param <T> 返回值参数的数据类型
     * @return 返回的数据
     */
    @Nullable
    public static <T> T wrapCode(@Nonnull ThrowableSupplier<T> s) {
        try {
            return s.get();
        } catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 执行一段可能抛出异常代码，自动用 warpThrow 包装出现的异常
     * @param r 被包装的会抛出异常的代码
     */
    public static void wrapCode(@Nonnull ThrowableRunnable r) {
        try {
            r.run();
        } catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    // ***** java8 functions *****

    /**
     * 允许抛出异常的 Consumer
     * @param <T> 参数的数据类型
     */
    @FunctionalInterface
    public interface ThrowableConsumer<T> {
        void accept(T param) throws Exception;

        /**
         * 包装成普通的 Consumer
         * @return 包装后的 Consumer
         */
        @Nonnull
        default Consumer<T> wrap() {
            return p -> Lang.wrapCode(() -> this.accept(p));
        }
    }

    /**
     * 允许抛出异常的 BiConsumer
     * @param <T> 参数 1 的数据类型
     * @param <U> 参数 2 的数据类型
     */
    @FunctionalInterface
    public interface ThrowableBiConsumer<T, U> {
        void accept(T param1, U param2) throws Exception;

        /**
         * 包装成普通的 BiConsumer
         * @return 包装后的 BiConsumer
         */
        @Nonnull
        default BiConsumer<T, U> wrap() {
            return (p1, p2) -> Lang.wrapCode(() -> this.accept(p1, p2));
        }
    }

    /**
     * 允许抛出异常的 Function
     * @param <T> 参数的数据类型
     * @param <R> 返回值的数据类型
     */
    @FunctionalInterface
    public interface ThrowableFunction<T, R> {
        R apply(T param) throws Exception;

        /**
         * 包装成普通的 Function
         * @return 包装后的 Function
         */
        @Nonnull
        default Function<T, R> wrap() {
            return p -> Lang.wrapCode(() -> this.apply(p));
        }
    }

    /**
     * 允许抛出异常的 BiFunction
     * @param <T> 参数 1 的数据类型
     * @param <U> 参数 2 的数据类型
     * @param <R> 返回值的数据类型
     */
    @FunctionalInterface
    public interface ThrowableBiFunction<T, U, R> {
        R apply(T param1, U param2) throws Exception;

        /**
         * 包装成普通的 BiFunction
         * @return 包装后的 BiFunction
         */
        @Nonnull
        default BiFunction<T, U, R> wrap() {
            return (p1, p2) -> Lang.wrapCode(() -> this.apply(p1, p2));
        }
    }

    /**
     * 允许抛出异常的 Supplier
     * @param <T> 返回值的数据类型
     */
    @FunctionalInterface
    public interface ThrowableSupplier<T> {
        T get() throws Exception;

        /**
         * 包装成普通的 Supplier
         * @return 包装后的 Supplier
         */
        @Nonnull
        default Supplier<T> wrap() {
            return () -> Lang.wrapCode(this);
        }
    }

    /**
     * 允许抛出异常的 Runnable
     */
    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;

        /**
         * 包装成普通的 Runnable
         * @return 包装后的 Runnable
         */
        @Nonnull
        default Runnable wrap() {
            return () -> Lang.wrapCode(this);
        }
    }

    /**
     * 允许抛出异常的 Predicate
     */
    @FunctionalInterface
    public interface ThrowablePredicate<T> {
        boolean test(T param) throws Exception;

        /**
         * 包装成普通的 Predicate
         * @return 包装后的 Predicate
         */
        @Nonnull
        default Predicate<T> wrap() {
            return p -> {
                try {
                    return this.test(p);
                } catch (Exception e) {
                    throw Lang.wrapThrow(e);
                }
            };
        }
    }

    /**
     * 允许 Consumer 抛出异常的包装器
     * @param c 被包装的会抛出异常的 Consumer
     * @param <T> 参数的数据类型
     * @return 包装后的 Consumer
     */
    @Nonnull
    public static <T> Consumer<T> wrapConsumer(@Nonnull ThrowableConsumer<T> c) {
        return c.wrap();
    }

    /**
     * 允许 BiConsumer 抛出异常的包装器
     * @param c 被包装的会抛出异常的 BiConsumer
     * @param <T> 参数 1 的数据类型
     * @param <U> 参数 2 的数据类型
     * @return 包装后的 BiConsumer
     */
    @Nonnull
    public static <T, U> BiConsumer<T, U> wrapBiConsumer(@Nonnull ThrowableBiConsumer<T, U> c) {
        return c.wrap();
    }

    /**
     * 允许 Function 抛出异常的包装器
     * @param f 被包装的会抛出异常的 Function
     * @param <T> 参数的数据类型
     * @param <R> 返回值的数据类型
     * @return 包装后的 Function
     */
    @Nonnull
    public static <T, R> Function<T, R> wrapFunction(@Nonnull ThrowableFunction<T, R> f) {
        return f.wrap();
    }

    /**
     * 允许 BiFunction 抛出异常的包装器
     * @param f 被包装的会抛出异常的 BiFunction
     * @param <T> 参数 1 的数据类型
     * @param <U> 参数 2 的数据类型
     * @param <R> 返回值的数据类型
     * @return 包装后的 Function
     */
    @Nonnull
    public static <T, U, R> BiFunction<T, U, R> wrapBiFunction(@Nonnull ThrowableBiFunction<T, U, R> f) {
        return f.wrap();
    }

    /**
     * 允许 Supplier 抛出异常的包装器
     * @param s 被包装的会抛出异常的 Supplier
     * @param <T> 返回值参数的数据类型
     * @return 包装后的 Supplier
     */
    @Nonnull
    public static <T> Supplier<T> wrapSupplier(@Nonnull ThrowableSupplier<T> s) {
        return s.wrap();
    }

    /**
     * 允许 Runnable 抛出异常的包装器
     * @param r 被包装的会抛出异常的 Runnable
     * @return 包装后的 Runnable
     */
    @Nonnull
    public static Runnable wrapRunnable(@Nonnull ThrowableRunnable r) {
        return r.wrap();
    }

    /**
     * 允许 Predicate 抛出异常的包装器
     * @param p 被包装的会抛出异常的 Predicate
     * @param <T> 参数的数据类型
     * @return 包装后的 Predicate
     */
    @Nonnull
    public static <T> Predicate<T> wrapPredicate(@Nonnull ThrowablePredicate<T> p) {
        return p.wrap();
    }
}
