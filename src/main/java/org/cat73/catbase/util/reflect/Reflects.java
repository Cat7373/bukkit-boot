package org.cat73.catbase.util.reflect;

import org.bukkit.Bukkit;
import org.cat73.catbase.util.Lang;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类
 */
public final class Reflects {
    private Reflects() {
        throw new UnsupportedOperationException();
    }

    /**
     * NMS 的版本
     **/
    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

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
    @Nonnull
    public static List<Field> findDeclaredFieldByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<? extends Annotation> annotationClazz) {
        List<Field> result = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClazz)) {
                trySetAccessible(field);
                result.add(field);
            }
        }
        return result;
    }

    // TODO javadoc
    @Nonnull
    public static List<Method> findMethodByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<? extends Annotation> annotationClazz) {
        List<Method> result = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(annotationClazz)) {
                trySetAccessible(method);
                result.add(method);
            }
        }
        return result;
    }

    // TODO javadoc
    public static void trySetAccessible(@Nonnull AccessibleObject accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
    }
}
