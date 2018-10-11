package com.wt.payment.reconciliation.definitions;

import java.util.concurrent.Callable;

/**
 * 对账过程
 * @param <R> 对账完成返回结果类型
 */
public interface DataCheckProcess<R> extends Callable<R> {

}
