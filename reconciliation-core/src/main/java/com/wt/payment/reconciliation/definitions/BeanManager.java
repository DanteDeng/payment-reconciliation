package com.wt.payment.reconciliation.definitions;

/**
 * bean管理器
 */
public interface BeanManager {
    /**
     * 根据bean类型获取bean
     * @param tClass 类
     * @param <T>    类
     * @return bean
     */
    <T> T getBean(Class<T> tClass);

    /**
     * 根据bean名称与类型获取bean
     * @param beanName bean名称
     * @param tClass   类
     * @param <T>      类
     * @return bean
     */
    <T> T getBean(String beanName, Class<T> tClass);
}
