package org.cat73.bukkitboot.exception;

/**
 * 无法解析参数的异常
 */
public class CanNotResolveParameterException extends RuntimeException {
    public CanNotResolveParameterException() {
    }

    public CanNotResolveParameterException(String message) {
        super(message);
    }

    public CanNotResolveParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotResolveParameterException(Throwable cause) {
        super(cause);
    }

    public CanNotResolveParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
