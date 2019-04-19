package org.cat73.bukkitboot.annotation.core.condition;

import javax.annotation.Nonnull;

/**
 * 自定义的判断条件
 * <p><em>应自行保证线程安全性</em></p>
 */
@FunctionalInterface
public interface Condition {
    /**
     * 判断是否应将一个类注册为 Bean
     * @param clazz 被判断的类
     * @return 判断结果
     */
    boolean matches(@Nonnull Class<?> clazz);
}
