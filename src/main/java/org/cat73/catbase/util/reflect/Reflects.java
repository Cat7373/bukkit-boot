package org.cat73.catbase.util.reflect;

import org.bukkit.Bukkit;
import org.cat73.catbase.util.Lang;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public final class Reflects {
    private Reflects() {
        throw new UnsupportedOperationException();
    }

    /**
     * 当前正运行的服务器的 NMS 版本的名字
     **/
    public static final String CURRENT_NMS_VERSION_NAME = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    /**
     * 当前正运行的服务器的 NMS 版本
     **/
    public static final NMSVersion CURRENT_NMS_VERSION = NMSVersion.valueOf(CURRENT_NMS_VERSION_NAME);

    // TODO javadoc
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> T newInstance(@Nonnull Class<?> clazz) {
        try {
            return (T) clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw Lang.wrapThrow(e);
        }
    }

    // TODO javadoc
    public static <T extends Annotation> void forEachDeclaredFieldByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, Lang.ThrowableBiConsumer<Field, T> action) {
        for (Field field : clazz.getDeclaredFields()) {
            T annotation = field.getAnnotation(annotationClazz);
            if (annotation != null) {
                trySetAccessible(field);
                action.wrap().accept(field, annotation);
            }
        }
    }

    // TODO javadoc
    public static <T extends Annotation> void forEachMethodByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, Lang.ThrowableBiConsumer<Method, T> action) {
        for (Method method : clazz.getMethods()) {
            T annotation = method.getAnnotation(annotationClazz);
            if (annotation != null) {
                trySetAccessible(method);
                action.wrap().accept(method, annotation);
            }
        }
    }

    // TODO javadoc
    public static void trySetAccessible(@Nonnull AccessibleObject accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
    }
}
