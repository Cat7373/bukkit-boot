package org.cat73.bukkitboot.util.reflect;

import org.cat73.bukkitboot.annotation.core.Inject;
import org.cat73.bukkitboot.context.PluginContext;
import org.cat73.bukkitboot.util.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 参数注入工具类(额外支持按顺序的 String 参数，用于处理命令参数)
 */
public final class ParameterInject {
    private ParameterInject() {
        throw new UnsupportedOperationException();
    }

    /**
     * 空白数组
     */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};
    /**
     * 默认的 Inject 注解
     */
    @Inject
    private static final Inject DEFAULT_INJECT;

    static {
        try {
            Field field = ParameterInject.class.getDeclaredField("DEFAULT_INJECT");
            DEFAULT_INJECT = field.getAnnotation(Inject.class);
        } catch (NoSuchFieldException e) {
            throw Lang.noImplement();
        }
    }

    /**
     * 尝试解决一些参数
     * @param context 插件的上下文(可选，如果希望从上下文中解决依赖)
     * @param objs 自定义的实体列表
     * @param byIdxParams 基于顺序的参数，如果从上面两个中无法解决依赖，且参数类型为基本数据类型或其包装类，或为 String，则从这里来按顺序解析
     *                    <p>每次成功解析则后移一个元素，不允许出现 null 元素</p>
     * @param parameters 要被解决的参数数组
     * @return 解决后的值实例数组
     */
    @Nonnull
    public static Object[] resolve(@Nullable PluginContext context, @Nullable Collection<?> objs, @Nullable Iterable<String> byIdxParams, @Nonnull Parameter[] parameters) {
        if (parameters.length == 0) {
            return EMPTY_OBJECT_ARRAY;
        }

        Object[] results = new Object[parameters.length];
        Iterator<String> it = Optional.ofNullable(byIdxParams).map(Iterable::iterator).orElse(null);
        String byIdxParam = null;
        for (int idx = 0; idx < parameters.length; idx++) {
            // 参数
            Parameter parameter = parameters[idx];
            // 注入注解
            Inject inject = parameter.getAnnotation(Inject.class);
            if (inject == null) {
                inject = DEFAULT_INJECT;
            }
            // 参数的类型
            Class<?> clazz = parameter.getType();

            Object result = null;

            // 尝试从 context 解析
            if (context != null) {
                result = context.resolveBean(clazz, inject.name());
            }
            // 尝试从 objs 解析
            if (result == null && objs != null) {
                List<Object> resultList = objs.stream()
                        .filter(i -> clazz.isAssignableFrom(i.getClass()))
                        .collect(Collectors.toList());

                if (resultList.size() == 1) {
                    result = resultList.get(0);
                }
            }
            // 尝试按顺序解析参数
            if (result == null && it != null) {
                if (byIdxParam == null && it.hasNext()) {
                    byIdxParam = it.next();
                }
                if (byIdxParam != null) {
                    result = ParameterInject.tryResolve(parameter, byIdxParam);
                    if (result != null) {
                        byIdxParam = null;
                    }
                }
            }

            // 如果是必须的参数，且未解析成功，则报错
            if (result == null && inject.required()) {
                throw new NullPointerException(String.format("无法解析参数: %s", parameter));
            }

            // 保存结果
            results[idx] = result;
        }

        return results;
    }

    /**
     * 尝试从字符串中解决参数
     * <p>目前支持基本数据类型或其包装类，或 String</p>
     * @param parameter 被解决的参数
     * @param str 字符串
     * @return 解决结果，如解决失败，则会返回 null
     */
    @Nullable
    private static Object tryResolve(@Nonnull Parameter parameter, @Nonnull String str) {
        Class<?> type = parameter.getType();

        try {
            if (type == boolean.class || type == Boolean.class) {
                // boolean / Boolean: true / false、1 / 0、y / n、yes / no
                return Objects.equals(str, "true") || Objects.equals(str, "1") || Objects.equals(str, "y") || Objects.equals(str, "yes");
            } else if (type == byte.class || type == Byte.class) {
                return Byte.valueOf(str);
            } else if (type == short.class || type == Short.class) {
                return Short.valueOf(str);
            } else if (type == char.class || type == Character.class) {
                return str.charAt(0);
            } else if (type == int.class || type == Integer.class) {
                return Integer.valueOf(str);
            } else if (type == float.class || type == Float.class) {
                return Float.valueOf(str);
            } else if (type == long.class || type == Long.class) {
                return Long.valueOf(str);
            } else if (type == double.class || type == Double.class) {
                return Double.valueOf(str);
            } else if (type == String.class) {
                return str;
            }
        } catch (Exception e) {
            // quiet
        }

        return null;
    }
}
