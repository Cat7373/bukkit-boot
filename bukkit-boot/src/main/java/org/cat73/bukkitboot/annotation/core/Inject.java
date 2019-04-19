package org.cat73.bukkitboot.annotation.core;

import org.cat73.bukkitboot.annotation.command.Command;

import java.lang.annotation.*;

/**
 * 根据类型自动注入
 * <p>在类中，支持按类型和名称注入 Bean</p>
 * <p>在方法的参数中，请参考相关注解的说明查看支持哪些注入</p>
 * @see Command
 */
// TODO 支持 List<T> 这种的注入
// TODO 支持 Map<String, T> 这种的注入
// TODO 支持 Map<String, List<T>> 这种的注入
// TODO 支持往继承的类里注入
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Inject {
    /**
     * 是否是必须的，如果非必须，则未找到值时不会阻止服务器启动
     * @return 是否是必须的
     */
    boolean required() default true;

    /**
     * Bean 的名称
     * @return Bean 的名称
     */
    String name() default "";
}
