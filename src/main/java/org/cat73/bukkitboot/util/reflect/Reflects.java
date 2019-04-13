package org.cat73.bukkitboot.util.reflect;

import org.cat73.bukkitboot.util.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    // ==== lookup ====

    /**
     * 遍历一个类的超类，注意类自己也会被执行操作
     * @param clazz 被遍历的类
     * @param action 要进行的操作
     */
    public static void lookupSuperClasses(@Nonnull Class<?> clazz, @Nonnull Lang.ThrowableConsumer<Class<?>> action) {
        Class<?> c = clazz;
        do {
            action.wrap().accept(c);
        } while ((c = c.getSuperclass()) != null);
    }

    /**
     * 遍历类中的字段
     * @param clazz 用做遍历的类
     * @param action 要进行的操作
     */
    public static void lookupFields(@Nonnull Class<?> clazz, @Nonnull Lang.ThrowableConsumer<Field> action) {
        lookupSuperClasses(clazz, c -> {
            for (Field field : c.getFields()) {
                action.wrap().accept(field);
            }
        });
    }

    /**
     * 遍历类中的方法
     * @param clazz 用做遍历的类
     * @param action 要进行的操作
     */
    public static void lookupMethods(@Nonnull Class<?> clazz, @Nonnull Lang.ThrowableConsumer<Method> action) {
        lookupSuperClasses(clazz, c -> {
            for (Method method : c.getMethods()) {
                action.wrap().accept(method);
            }
        });
    }

    /**
     * 遍历类中的字段(包括私有)
     * @param clazz 用做遍历的类
     * @param action 要进行的操作
     */
    public static void lookupDeclaredFields(@Nonnull Class<?> clazz, @Nonnull Lang.ThrowableConsumer<Field> action) {
        lookupSuperClasses(clazz, c -> {
            for (Field field : c.getDeclaredFields()) {
                action.wrap().accept(field);
            }
        });
    }

    /**
     * 遍历类中的方法(包括私有)
     * @param clazz 用做遍历的类
     * @param action 要进行的操作
     */
    public static void lookupDeclaredMethods(@Nonnull Class<?> clazz, @Nonnull Lang.ThrowableConsumer<Method> action) {
        lookupSuperClasses(clazz, c -> {
            for (Method method : c.getDeclaredMethods()) {
                action.wrap().accept(method);
            }
        });
    }

    // ==== loopup by annotation ====

    /**
     * 遍历类中的所有方法，寻找被指定注解修饰的字段，并对它们做一些操作
     * @param clazz 用做遍历的类
     * @param annotationClazz 要查找的注解
     * @param action 要进行的操作
     * @param <T> 注解的类型
     */
    public static <T extends Annotation> void lookupFieldByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Field, T> action) {
        lookupFields(clazz, f -> {
            T annotation = f.getAnnotation(annotationClazz);
            if (annotation != null) {
                action.wrap().accept(f, annotation);
            }
        });
    }

    /**
     * 遍历类中的所有方法，寻找被指定注解修饰的方法，并对它们做一些操作
     * @param clazz 用做遍历的类
     * @param annotationClazz 要查找的注解
     * @param action 要进行的操作
     * @param <T> 注解的类型
     */
    public static <T extends Annotation> void lookupMethodByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Method, T> action) {
        lookupMethods(clazz, m -> {
            T annotation = m.getAnnotation(annotationClazz);
            if (annotation != null) {
                action.wrap().accept(m, annotation);
            }
        });
    }

    /**
     * 遍历类中的所有方法(包括私有)，寻找被指定注解修饰的方法，并对它们做一些操作
     * @param clazz 用做遍历的类
     * @param annotationClazz 要查找的注解
     * @param action 要进行的操作
     * @param <T> 注解的类型
     */
    public static <T extends Annotation> void lookupDeclaredMethodByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Method, T> action) {
        lookupDeclaredMethods(clazz, m -> {
            T annotation = m.getAnnotation(annotationClazz);
            if (annotation != null) {
                action.wrap().accept(m, annotation);
            }
        });
    }

    /**
     * 遍历类中的所有字段(包括私有)，寻找被指定注解修饰的字段，并对它们做一些操作
     * @param clazz 用做遍历的类
     * @param annotationClazz 要查找的注解
     * @param action 要进行的操作
     * @param <T> 注解的类型
     */
    public static <T extends Annotation> void lookupDeclaredFieldByAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<T> annotationClazz, @Nonnull Lang.ThrowableBiConsumer<Field, T> action) {
        lookupDeclaredFields(clazz, f -> {
            T annotation = f.getAnnotation(annotationClazz);
            if (annotation != null) {
                action.wrap().accept(f, annotation);
            }
        });
    }

    // ==== search ====

    /**
     * 搜索字段(包括私有)，会自动遍历所有超类
     * @param clazz 用做搜索的类
     * @param name 字段的名字
     * @return 找到的字段
     * @throws NoSuchFieldException 如果没找到字段
     */
    @Nonnull
    public static Field searchField(@Nonnull Class<?> clazz, @Nonnull String name) throws NoSuchFieldException {
        // TODO 接口中的字段是否支持？
        while (true) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
                if (clazz == null) {
                    throw e;
                }
            }
        }
    }

    /**
     * 搜索方法(包括私有)，会自动遍历所有超类
     * @param clazz 用做搜索的类
     * @param name 方法的名字
     * @return 找到的方法
     * @param argTypes 参数的类型列表
     * @throws NoSuchMethodException 如果没找到字段
     */
    @Nonnull
    public static Method searchMethod(@Nonnull Class<?> clazz, @Nonnull String name, @Nonnull Class<?>[] argTypes) throws NoSuchMethodException {
        // TODO 接口中的 default 方法是否支持？
        while (true) {
            try {
                return clazz.getDeclaredMethod(name, argTypes);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
                if (clazz == null) {
                    throw e;
                }
            }
        }
    }

    // ==== get / set ====

    /**
     * 读取对象中一个字段的值
     * @param obj 用于搜索的对象，会自动遍历所有超类
     * @param name 字段的名称
     * @param <T> 返回值的数据类型
     * @return 读取到的数据
     * @throws NoSuchFieldException 如果字段不存在
     * @throws IllegalAccessException 如果无权访问字段
     */
    @Nullable
    public static <T> T readField(@Nonnull Object obj, @Nonnull String name) throws NoSuchFieldException, IllegalAccessException {
        return readField(obj.getClass(), obj, name);
    }

    /**
     * 读取一个字段的值
     * @param clazz 用于搜索的类，会自动遍历所有超类
     * @param obj 用于读取数据的对象(如果是静态字段，可给 null)
     * @param name 字段的名称
     * @param <T> 返回值的数据类型
     * @return 读取到的数据
     * @throws NoSuchFieldException 如果字段不存在
     * @throws IllegalAccessException 如果无权访问字段
     */
    @Nullable
    public static <T> T readField(@Nonnull Class<?> clazz, @Nullable Object obj, @Nonnull String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = searchField(clazz, name);
        trySetAccessible(field);
        return conv(field.get(obj));
    }

    /**
     * 修改一个字段的值
     * @param obj 用于搜索的对象，会自动遍历所有超类
     * @param name 字段的名称
     * @param newValue 字段的新值
     * @param <T> 字段新值的数据类型
     * @throws NoSuchFieldException 如果字段不存在
     * @throws IllegalAccessException 如果无权访问字段
     */
    public static <T> void writeField(@Nonnull Object obj, @Nonnull String name, @Nullable T newValue) throws NoSuchFieldException, IllegalAccessException {
        writeField(obj.getClass(), obj, name, newValue);
    }

    /**
     * 修改一个字段的值
     * @param clazz 用于搜索的类，会自动遍历所有超类
     * @param obj 用于修改数据的对象(如果是静态字段，可给 null)
     * @param name 字段的名称
     * @param newValue 字段的新值
     * @param <T> 字段新值的数据类型
     * @throws NoSuchFieldException 如果字段不存在
     * @throws IllegalAccessException 如果无权访问字段
     */
    public static <T> void writeField(@Nonnull Class<?> clazz, @Nullable Object obj, @Nonnull String name, @Nullable T newValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = searchField(clazz, name);
        trySetAccessible(field);
        field.set(obj, newValue);
    }

    // ==== invoke ====

    /**
     * 调用一个方法
     * @param obj 用于搜索的对象，会自动遍历所有超类
     * @param name 方法的名称
     * @param args 方法的参数列表
     * @param <T> 返回值的数据类型
     * @return 方法的返回值
     * @throws NoSuchMethodException 如果方法不存在
     * @throws IllegalAccessException 如果无权调用方法
     */
    public static <T> T invoke(@Nonnull Object obj, @Nonnull String name, @Nonnull Object... args) throws NoSuchMethodException, IllegalAccessException {
        return invoke(obj.getClass(), obj, name, args);
    }

    /**
     * 调用一个方法
     * @param clazz 用于搜索的类，会自动遍历所有超类
     * @param obj 用于调用方法的对象(如果是静态方法，可给 null)
     * @param name 方法的名称
     * @param args 方法的参数列表
     * @param <T> 返回值的数据类型
     * @return 方法的返回值
     * @throws NoSuchMethodException 如果方法不存在
     * @throws IllegalAccessException 如果无权调用方法
     */
    public static <T> T invoke(@Nonnull Class<?> clazz, @Nullable Object obj, @Nonnull String name, @Nonnull Object... args) throws NoSuchMethodException, IllegalAccessException {
        Class<?>[] argTypes = new Class<?>[args.length];
        if (args.length > 0) {
            for (int i = args.length - 1; i >= 0; i--) {
                argTypes[i] = args[i].getClass();
            }
        }

        return invoke(clazz, obj, name, argTypes, args);
    }

    /**
     * 调用一个方法
     * @param clazz 用于搜索的类，会自动遍历所有超类
     * @param obj 用于调用方法的对象(如果是静态方法，可给 null)
     * @param name 方法的名称
     * @param argTypes 方法的参数类型列表
     * @param args 方法的参数列表
     * @param <T> 返回值的数据类型
     * @return 方法的返回值
     * @throws NoSuchMethodException 如果方法不存在
     * @throws IllegalAccessException 如果无权调用方法
     */
    public static <T> T invoke(@Nonnull Class<?> clazz, @Nullable Object obj, @Nonnull String name, @Nonnull Class<?>[] argTypes, @Nonnull Object... args) throws NoSuchMethodException, IllegalAccessException {
        Method method = searchMethod(clazz, name, argTypes);
        trySetAccessible(method);
        try {
            return conv(method.invoke(obj, args));
        } catch (InvocationTargetException e) {
            throw Lang.noImplement();
        }
    }

    // ==== newInstance ====

    /**
     * 用无参构造创建一个新实例
     * @param clazz 被创建新实例的 Class
     * @param <T> Class 的类型
     * @return 创建出的新实例
     */
    @Nonnull
    public static <T> T newInstance(@Nonnull Class<?> clazz) {
        try {
            return conv(clazz.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw Lang.wrapThrow(e);
        }
    }

    // TODO 有参构造

    // ==== misc =====

    /**
     * 将输入的对象强行转换为目标类型
     * @param obj 输入的对象
     * @param <T> 目标类型
     * @return obj 自身
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T conv(@Nullable Object obj) {
        return (T) obj;
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
