package org.cat73.bukkitboot.util.reflect;

import org.cat73.bukkitboot.context.PluginContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 参数注入工具类(额外支持按顺序的 String 参数，用于处理命令参数)
 */
public final class ParameterInject {
    private ParameterInject() {
        throw new UnsupportedOperationException();
    }

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};

    /**
     * 尝试解决一些参数
     * @param context 插件的上下文(可选，如果希望从上下文中解决依赖)
     * @param objs 自定义的 Class 到实体的解决关系
     * @param byIdxParams 基于顺序的参数，如果从上面两个中无法解决依赖，且参数类型为基本数据类型或其包装类，或为 String，则从这里来按顺序解析，每次成功解析则后移一个元素
     * @param parameters 要被解决的参数数组
     * @return 解决后的值实例数组
     */
    public static Object[] resolve(@Nullable PluginContext context, @Nullable Map<Class<?>, Object> objs, @Nullable List<String> byIdxParams, @Nonnull Parameter[] parameters) {
        if (parameters.length == 0) {
            return EMPTY_OBJECT_ARRAY;
        }

        Object[] results = new Object[parameters.length];
        int paramIdx = 0;
        for (int idx = 0; idx < parameters.length; idx++) {
            // 参数
            Parameter parameter = parameters[idx];

            Object result = null;

            // 尝试从 context 解析
            if (context != null) {
                result = context.resolveBean(parameter.getType(), null); // TODO 根据名字注入 @Inject("foo")
            }
            // 尝试从 objs 解析
            if (result == null && objs != null) {
                result = objs.get(parameter.getType()); // TODO 超类？
            }
            // 尝试按顺序解析参数
            if (byIdxParams != null && byIdxParams.size() > paramIdx) {
                result = ParameterInject.tryResolve(parameter, byIdxParams.get(paramIdx));
                if (result != null) {
                    idx += 1;
                }
            }

            // TODO 允许可选参(注意考虑上面的顺序？
            if (result == null) {
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
    private static Object tryResolve(@Nonnull Parameter parameter, @Nonnull String str) {
        Class<?> type = parameter.getType();

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

        return null;
    }
}
