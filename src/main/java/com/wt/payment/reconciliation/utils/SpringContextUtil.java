package com.wt.payment.reconciliation.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring上下文管理工具
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    /**
     * spring上下文实例
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 按类型获取bean
     * @param aClass 类
     * @param <T>    type
     * @return bean
     */
    public static <T> T getBean(Class<T> aClass) {
        return applicationContext.getBean(aClass);
    }

    /**
     * 根据bean名称获取指定类型的bean
     * @param beanName bean名称
     * @param tClass   bean class
     * @param <T>      bean类型
     * @return bean
     */
    public static <T> T getBean(String beanName, Class<T> tClass) {
        return applicationContext.getBean(beanName, tClass);
    }
}
