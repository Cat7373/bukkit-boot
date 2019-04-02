package org.cat73.bukkitboot.util.reflect;

import org.cat73.bukkitboot.util.Lang;

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
    public static <T extends Annotation> void forEachDeclaredFieldByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Field, T> action) {
        for (Field field : clazz.getDeclaredFields()) {
            T annotation = field.getAnnotation(annotationClazz);
            if (annotation != null) {
                trySetAccessible(field);
                action.wrap().accept(field, annotation);
            }
        }
    }

    // TODO javadoc
    public static <T extends Annotation> void forEachMethodByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Method, T> action) {
        forEachMethods(clazz, m -> {
            T annotation = m.getAnnotation(annotationClazz);
            if (annotation != null) {
                trySetAccessible(m);
                action.wrap().accept(m, annotation);
            }
        });
    }

    // TODO javadoc
    public static void forEachMethods(@Nonnull Class<?> clazz, @Nonnull Lang.ThrowableConsumer<Method> action) {
        for (Method method : clazz.getMethods()) {
            action.wrap().accept(method);
        }
    }

    // TODO javadoc
    public static void trySetAccessible(@Nonnull AccessibleObject accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
    }
}
