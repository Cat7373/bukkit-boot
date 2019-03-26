package org.cat73.catbase.annotation;

import java.lang.annotation.*;

// TODO 实现
/**
 * 根据类型自动注入
 * <p>目前仅支持按类型搜索 Bean，如果存在且仅存在一个可转换为这个类型的 Bean，则注入它，否则按注入失败处理</p>
 */
// TODO 支持 List<T> 这种的注入
// TODO 支持 Map<String, T> 这种的注入
// TODO 支持 Map<String, List<T>> 这种的注入
// TODO 支持往继承的类里注入
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {
    /**
     * 是否是必须的，如果非必须，则未找到值时不会阻止服务器启动
     * @return 是否是必须的
     */
    boolean required() default true;
}
