package com.wt.payment.reconciliation.utils;

import java.util.UUID;

/**
 * uuid工具类
 */
public class UidUtil {

    /**
     * 生成uuid
     * @return uuid
     */
    public static String generate() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
