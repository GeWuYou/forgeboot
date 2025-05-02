package com.gewuyou.forgeboot.trace.config.entities;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 跟踪属性
 * 该类用于配置和管理请求跟踪相关的属性，通过这些属性可以对请求进行唯一的标识和跟踪
 * 主要功能包括定义请求ID的HTTP头名称、请求ID在MDC中的键名称，以及忽略跟踪的URL模式
 *
 * @author gewuyou
 * @since 2025-05-02 20:58:45
 */
@ConfigurationProperties(prefix = "forgeboot.trace")
public class TraceProperties {
    /**
     * HTTP请求头中用于传递请求ID的字段名称，默认为"X-Request-Id"。
     */
    private String requestIdHeaderName = "X-Request-Id";

    /**
     * MDC（Mapped Diagnostic Context）中用于存储请求ID的键名，默认为"requestId"。
     */
    private String requestIdMdcKey = "requestId";

    /**
     * 配置忽略日志记录的路径模式，通常用于静态资源文件，
     * 默认忽略以.css、.js、.png等结尾的静态资源请求。
     */
    private String[] ignorePatten = new String[]{".*\\.(css|js|png|jpg|jpeg|gif|svg)"};

    public String getRequestIdHeaderName() {
        return requestIdHeaderName;
    }

    public String[] getIgnorePatten() {
        return ignorePatten;
    }

    public void setIgnorePatten(String[] ignorePatten) {
        this.ignorePatten = ignorePatten;
    }

    public void setRequestIdHeaderName(String requestIdHeaderName) {
        this.requestIdHeaderName = requestIdHeaderName;
    }

    public String getRequestIdMdcKey() {
        return requestIdMdcKey;
    }

    public void setRequestIdMdcKey(String requestIdMdcKey) {
        this.requestIdMdcKey = requestIdMdcKey;
    }
}
