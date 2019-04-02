package org.cat73.bukkitboot.context.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Bean 的信息
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class BeanInfo {
    /**
     * Bean 的名字
     */
    private String name;
    /**
     * Bean 的 Class
     */
    private Class<?> type;
    /**
     * Bean 的实例
     */
    private Object bean;
}
