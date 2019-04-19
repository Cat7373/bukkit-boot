package org.cat73.getcommand.nms;

/**
 * 命令的生成器
 * @param <T> 参数的数据类型
 */
public interface ICommandGenerator<T> {
    /**
     * 将参数转换为命令
     * @param obj 参数
     * @return 转换结果
     * @throws Exception 如果转换过程中出现了任何异常
     */
    String generator(T obj) throws Exception;
}
