package org.cat73.bukkitboot.util.reflect;

import org.cat73.bukkitboot.context.PluginContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * 参数注入工具类(额外支持按顺序的 String 参数，用于处理命令参数)
 */
public final class ParameterInject {
    private ParameterInject() {
        throw new UnsupportedOperationException();
    }

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};

    // TODO javadoc
    public static Object[] resolve(@Nullable PluginContext context, @Nullable Map<Class<?>, Object> objs, @Nullable List<String> byIdxParams, @Nonnull Parameter[] parameters) {
        if (parameters.length == 0) {
            return EMPTY_OBJECT_ARRAY;
        }

        Object[] results = new Object[parameters.length];
        for (int idx = 0; idx < parameters.length; idx++) {
            // 参数
            Parameter parameter = parameters[idx]; // TODO @Param("foo")

            Object result = null;

            // 尝试从 context 解析
            if (context != null) {
                result = context.resolveBean(parameter.getType(), null);
            }
            // 尝试从 objs 解析
            if (result == null && objs != null) {
                result = objs.get(parameter.getType());
            }
            // TODO 尝试按顺序解析参数

            // TODO 允许可选参
            if (result == null) {
                throw new NullPointerException(String.format("无法解析参数: %s", parameter));
            }

            // 保存结果
            results[idx] = result;
        }

        return results;
    }
}
