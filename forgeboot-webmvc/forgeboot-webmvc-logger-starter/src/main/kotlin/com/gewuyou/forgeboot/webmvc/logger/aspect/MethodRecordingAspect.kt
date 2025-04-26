package com.gewuyou.forgeboot.webmvc.logger.aspect


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.webmvc.logger.annotation.MethodRecording
import com.gewuyou.forgeboot.webmvc.logger.entities.CoroutineLogger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
/**
 * 方法记录切面
 *
 * 该切面用于拦截带有@MethodRecording注解的方法，记录方法的执行信息，包括方法开始执行、执行完成、执行失败等信息
 * 可以通过@MethodRecording注解的属性选择性打印方法的入参、返回值和执行耗时等信息
 *
 * @since 2025-01-23 14:25:04
 * @author gewuyou
 */
@Aspect
class MethodRecordingAspect {

    /**
     * 环绕通知，用于在方法执行前后进行操作
     *
     * @param joinPoint 切入点，包含被拦截方法的信息
     * @param methodRecording 方法记录注解，包含配置信息
     * @return 方法的执行结果
     * @throws Throwable 方法执行过程中抛出的异常
     */
    @Around("@annotation(methodRecording)")
    fun methodRecording(joinPoint: ProceedingJoinPoint, methodRecording: MethodRecording): Any? {
        // 获取方法名
        val methodName = joinPoint.signature.name
        // 获取类名
        val className = joinPoint.signature.declaringTypeName
        // 获取注解的描述信息
        val description = methodRecording.description
        // 获取方法的入参
        val args = joinPoint.args

        // 安全地记录日志，防止日志记录失败
        CoroutineLogger.safeLog {
            // 构建日志字符串，包括方法开始执行的信息、注解的描述信息和入参信息
            val logStr = buildString {
                append("开始执行方法: $className.$methodName\n")
                append("$description\n")
                if (methodRecording.printArgs) {
                    append("入参: ${args.joinToString()}\n")
                }
            }
            log.info(logStr)
        }

        // 记录方法开始执行的时间
        val startTime = System.currentTimeMillis()

        // 尝试执行方法
        return try {
            val result = joinPoint.proceed()
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            // 安全地记录日志，记录方法执行完成的信息、返回值信息和执行耗时信息
            CoroutineLogger.safeLog {
                val logStr = buildString {
                    append("方法 $className.$methodName 执行完成\n")
                    if (methodRecording.printResult) {
                        append("返回值: $result\n")
                    }
                    if (methodRecording.printTime) {
                        append("执行耗时: ${duration}ms")
                    }
                }
                log.info(logStr)
            }

            result
        } catch (ex: Throwable) {
            // 如果方法执行失败，安全地记录错误日志
            CoroutineLogger.safeLog {
                log.error("方法 $className.$methodName 执行失败: ${ex.message}", ex)
            }
            throw ex
        }
    }
}
