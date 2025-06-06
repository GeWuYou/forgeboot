package com.gewuyou.forgeboot.webmvc.logger.config

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.webmvc.logger.aspect.MethodRecordingAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Logger自动配置
 *
 * 该类用于自动配置Logger，通过Spring的自动配置机制，自动加载和配置Logger
 * 它定义了一个Bean方法，用于创建和配置MethodRecordingAspect切面对象
 *
 * @since 2025-04-26 21:02:56
 * @author gewuyou
 */
@Configuration
open class LoggerAutoConfiguration {
    /**
     * 创建并返回一个MethodRecordingAspect切面对象
     *
     * 该方法定义了一个Bean，用于在Spring容器中创建并配置一个MethodRecordingAspect切面对象
     * MethodRecordingAspect切面对象用于记录方法的执行日志，以便于监控和调试
     *
     * @return MethodRecordingAspect切面对象
     */
    @Bean
    open fun methodRecordingAspect(): MethodRecordingAspect {
        log.info("创建方法记录切面!")
        return MethodRecordingAspect()
    }
}
