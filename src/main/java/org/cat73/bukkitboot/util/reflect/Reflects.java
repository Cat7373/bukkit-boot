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

    /**
     * 创建一个新实例
     * @param clazz 被创建新实例的 Class
     * @param <T> Class 的类型
     * @return 创建出的新实例
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> T newInstance(@Nonnull Class<?> clazz) {
        try {
            return (T) clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 遍历类中的所有字段(包含私有)，寻找被指定注解修饰的字段，并对它们做一些操作
     * @param clazz 用做遍历的类
     * @param annotationClazz 要查找的注解
     * @param action 要进行的操作
     * @param <T> 注解的类型
     */
    public static <T extends Annotation> void forEachDeclaredFieldByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Field, T> action) {
        for (Field field : clazz.getDeclaredFields()) { // TODO 遍历超类
            T annotation = field.getAnnotation(annotationClazz);
            if (annotation != null) {
                action.wrap().accept(field, annotation);
            }
        }
    }

    /**
     * 遍历类中的所有方法，寻找被指定注解修饰的方法，并对它们做一些操作
     * @param clazz 用做遍历的类
     * @param annotationClazz 要查找的注解
     * @param action 要进行的操作
     * @param <T> 注解的类型
     */
    public static <T extends Annotation> void forEachMethodByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Method, T> action) {
        forEachMethods(clazz, m -> {
            T annotation = m.getAnnotation(annotationClazz);
            if (annotation != null) {
                action.wrap().accept(m, annotation);
            }
        });
    }

    /**
     * 遍历类中的方法
     * @param clazz 用做遍历的类
     * @param action 要进行的操作
     */
    public static void forEachMethods(@Nonnull Class<?> clazz, @Nonnull Lang.ThrowableConsumer<Method> action) {
        for (Method method : clazz.getMethods()) { // TODO 遍历超类
            action.wrap().accept(method);
        }
    }

    /**
     * 尝试修改通过反射访问的权限为允许
     * @param accessibleObject 被修改的实例
     */
    public static void trySetAccessible(@Nonnull AccessibleObject accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
    }
}
