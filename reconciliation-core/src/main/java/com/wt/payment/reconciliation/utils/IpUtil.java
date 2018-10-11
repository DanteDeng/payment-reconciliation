package com.wt.payment.reconciliation.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * ip工具类
 */
public class IpUtil {

    private static final Map<String, String> cache = new HashMap<>();

    /**
     * 获取当前机器局域网中的IP
     * @return 当前机器局域网中的IP
     */
    public static String getLocalHostLANAddress() {
        String ipAddress = cache.get("ipAddress"); // 缓存获取
        if (ipAddress != null) {
            return ipAddress;
        }

        try {
            InetAddress candidateAddress = null;
            InetAddress inetAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            inetAddress = inetAddr;
                            break;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
                if (inetAddress != null) {
                    break;
                }
            }

            if (inetAddress == null && candidateAddress != null) {
                inetAddress = candidateAddress;
            }
            if (inetAddress == null) {
                // 如果没有发现 non-loopback地址.只能用最次选的方案
                inetAddress = InetAddress.getLocalHost();
            }
            ipAddress = inetAddress.getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException("get local host LAN address error");
        }
        if (ipAddress == null) {
            throw new RuntimeException("get local host LAN address error");
        }
        // 存入缓存
        cache.put("ipAddress", ipAddress);
        return ipAddress;
    }
}
