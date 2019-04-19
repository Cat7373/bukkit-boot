package org.cat73.bukkitboot.exception;

import javax.annotation.Nonnull;

/**
 * 启动失败的异常
 */
public class InitializeError extends Error {
    public InitializeError() {
        super();
    }

    public InitializeError(@Nonnull String message) {
        super(message);
    }

    public InitializeError(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }

    public InitializeError(@Nonnull Throwable cause) {
        super(cause);
    }

    protected InitializeError(@Nonnull String message, @Nonnull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
