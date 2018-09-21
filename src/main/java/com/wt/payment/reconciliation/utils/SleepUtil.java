package com.wt.payment.reconciliation.utils;

import java.util.concurrent.TimeUnit;

/**
 * 休眠工具类
 */
public class SleepUtil {

    /**
     * 睡眠指定秒数
     * @param milliSeconds 毫秒
     */
    public static void sleepMilliSeconds(long milliSeconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
