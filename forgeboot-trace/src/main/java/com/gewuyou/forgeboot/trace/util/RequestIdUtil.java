package com.gewuyou.forgeboot.trace.util;

import java.util.UUID;

/**
 * 请求 ID Util
 * 这个类需配合 RequestIdFilter 使用，用于生成请求 ID，并将其绑定到线程变量中，供后续可能需要的地方使用。
 * @author gewuyou
 * @since 2025-01-02 14:27:45
 */
public class RequestIdUtil {
    private static final ThreadLocal<String> REQUEST_ID_HOLDER = new ThreadLocal<>();
    private RequestIdUtil() {
    }
    public static void generateRequestId() {
        REQUEST_ID_HOLDER.set(UUID.randomUUID().toString());
    }

    public static String getRequestId() {
        return REQUEST_ID_HOLDER.get();
    }

    public static void setRequestId(String uuid) {
        REQUEST_ID_HOLDER.set(uuid);
    }

    public static void removeRequestId() {
        REQUEST_ID_HOLDER.remove();
    }
}
