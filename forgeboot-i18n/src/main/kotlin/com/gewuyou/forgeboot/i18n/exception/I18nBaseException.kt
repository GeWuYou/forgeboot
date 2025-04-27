package com.gewuyou.forgeboot.i18n.exception;


import com.gewuyou.forgeboot.i18n.entity.ResponseInformation;
/**
 * i18n异常
 *
 * @author gewuyou
 * @since 2024-11-12 00:11:32
 */
public class I18nBaseException extends RuntimeException {
    /**
     *  响应信息对象，用于存储错误代码和国际化消息代码
     */
    protected final transient ResponseInformation responseInformation;

    /**
     * 构造函数
     *
     * @param responseInformation 响应信息对象，包含错误代码和国际化消息代码
     */
    public I18nBaseException(ResponseInformation responseInformation) {
        super();
        this.responseInformation = responseInformation;
    }

    /**
     * 构造函数
     *
     * @param responseInformation 响应信息对象，包含错误代码和国际化消息代码
     * @param cause               异常原因
     */
    public I18nBaseException(ResponseInformation responseInformation, Throwable cause) {
        super(cause);
        this.responseInformation = responseInformation;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public int getErrorCode() {
        return responseInformation.getResponseCode();
    }

    /**
     * 获取国际化消息代码
     *
     * @return 国际化消息代码
     */
    public String getErrorI18nMessageCode() {
        return responseInformation.getResponseI8nMessageCode();
    }

}
