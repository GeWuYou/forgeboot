package com.gewuyou.forgeboot.i18n.impl.resolver


import com.gewuyou.forgeboot.i18n.api.MessageResolver
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

/**
 * i18 n消息解析器
 *
 * 该类实现了MessageResolver接口，用于解析国际化消息它依赖于Spring的MessageSource接口
 * 来获取本地化消息文本，并根据当前用户的区域设置来确定使用哪种语言的消息
 *
 * @since 2025-05-03 16:58:36
 * @author gewuyou
 */
class I18nMessageResolver(
    private val i18nMessageSource: MessageSource
) : MessageResolver {
    /**
     * 解析消息
     *
     * 根据给定的消息代码和可选的参数数组，解析并返回具体的消息字符串
     * 这个方法允许在不同的上下文中重用消息解析逻辑，并可以根据需要提供不同的实现
     *
     * @param code 消息代码，用于标识特定的消息类型或模板
     * @param args 可选的消息参数数组，用于替换消息模板中的占位符
     * @return 解析后的消息字符串
     */
    override fun resolve(code: String, args: Array<Any>?): String {
        // 使用Spring的MessageSource来获取与当前区域设置相匹配的消息文本
        return i18nMessageSource.getMessage(code, args, LocaleContextHolder.getLocale())
    }
}
