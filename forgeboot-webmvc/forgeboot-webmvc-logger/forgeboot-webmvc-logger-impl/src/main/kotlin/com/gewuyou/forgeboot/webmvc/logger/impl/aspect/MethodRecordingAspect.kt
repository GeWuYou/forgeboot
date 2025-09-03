/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.webmvc.logger.impl.aspect

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.webmvc.logger.api.annotation.MethodRecording
import com.gewuyou.forgeboot.webmvc.logger.api.entities.CoroutineLogger
import kotlinx.coroutines.flow.Flow
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.reactivestreams.Publisher
import org.springframework.core.io.Resource
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStream
import java.security.Principal
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation

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

    // ---- Options & Meta (参数收拢，降低方法参数个数) ----

    /**
     * 记录选项数据类
     *
     * @property printArgs 是否打印方法入参
     * @property printResult 是否打印方法返回值
     * @property printTime 是否打印方法执行耗时
     */
    private data class RecOptions(
        val printArgs: Boolean,
        val printResult: Boolean,
        val printTime: Boolean,
    )

    /**
     * 记录元数据类
     *
     * @property className 类名
     * @property methodName 方法名
     * @property desc 方法描述
     * @property prettyArgs 格式化后的参数字符串
     * @property opts 记录选项
     * @property start 方法开始执行时间戳
     */
    private data class RecMeta(
        val className: String,
        val methodName: String,
        val desc: String,
        val prettyArgs: String,
        val opts: RecOptions,
        val start: Long,
    )

    /**
     * 环绕通知方法，处理被@MethodRecording注解标记的方法执行过程
     *
     * @param joinPoint 连接点对象，包含方法执行的相关信息
     * @param methodRecording 方法上的注解实例，用于获取记录配置
     * @return 方法执行结果
     */
    @Around("@annotation(methodRecording)")
    fun methodRecording(joinPoint: ProceedingJoinPoint, methodRecording: MethodRecording): Any? {
        val sig = joinPoint.signature as MethodSignature
        val className = sig.declaringTypeName
        val methodName = sig.name

        val meta = RecMeta(
            className = className,
            methodName = methodName,
            desc = methodRecording.description,
            prettyArgs = prettyArgs(joinPoint.args),
            opts = RecOptions(
                printArgs = methodRecording.printArgs,
                printResult = methodRecording.printResult,
                printTime = methodRecording.printTime
            ),
            start = System.currentTimeMillis()
        )

        return try {
            when (val result = joinPoint.proceed()) {
                is Mono<*> -> instrumentMono(result, meta)
                is Flux<*> -> instrumentFlux(result, meta)
                is Publisher<*> -> result // 其它 Publisher：保持不侵入
                is Flow<*> -> {
                    // Flow 无法无侵入拿到终止：仅在返回时记一笔（无结果值）
                    if (meta.opts.printArgs || meta.opts.printTime) logOnce(meta, resultText = null)
                    result
                }

                else -> {
                    // 普通/挂起方法：这里已拿到真实返回值
                    logOnce(meta, result.safeRender())
                    result
                }
            }
        } catch (ex: Throwable) {
            logErrorOnce(meta, ex)
            throw ex
        }
    }

    /**
     * 对Mono类型的结果进行增强处理，添加成功、错误和完成时的日志记录
     *
     * @param T 泛型类型参数
     * @param source 原始Mono对象
     * @param meta 方法记录元数据
     * @return 增强后的Mono对象
     */
    private fun <T> instrumentMono(source: Mono<T>, meta: RecMeta): Mono<T> {
        val logged = AtomicBoolean(false)
        return source
            .doOnSuccess { value ->
                if (logged.compareAndSet(false, true)) logOnce(meta, value.safeRender())
            }
            .doOnError { ex ->
                if (logged.compareAndSet(false, true)) logErrorOnce(meta, ex)
            }
            .doFinally {
                // 空完成/取消也输出（只含入参/耗时，无返回值）
                if (logged.compareAndSet(false, true)) logOnce(meta, resultText = null)
            }
    }

    /**
     * 对Flux类型的结果进行增强处理，添加成功、错误和完成时的日志记录
     *
     * @param T 泛型类型参数
     * @param source 原始Flux对象
     * @param meta 方法记录元数据
     * @return 增强后的Flux对象
     */
    private fun <T> instrumentFlux(source: Flux<T>, meta: RecMeta): Flux<T> {
        val logged = AtomicBoolean(false)
        var last: Any? = null
        return source
            .doOnNext { last = it }
            .doOnError { ex ->
                if (logged.compareAndSet(false, true)) logErrorOnce(meta, ex)
            }
            .doFinally {
                if (logged.compareAndSet(false, true)) {
                    val text = if (meta.opts.printResult) "Flux(last=${last.safeRender()})" else null
                    logOnce(meta, text)
                }
            }
    }

    /**
     * 执行一次方法成功日志记录
     *
     * @param meta 方法记录元数据
     * @param resultText 返回值文本表示，可为null
     */
    private fun logOnce(meta: RecMeta, resultText: String?) {
        CoroutineLogger.safeLog {
            val sb = StringBuilder(128)
            sb.append("\n方法 ").append(meta.className).append('.')
                .append(meta.methodName).append(" 执行完成\n")
            if (meta.desc.isNotBlank()) {
                sb.append("方法描述: ").append(meta.desc).append('\n')
            }
            if (meta.opts.printArgs && meta.prettyArgs.isNotBlank()) {
                sb.append("入参: ").append(meta.prettyArgs).append('\n')
            }
            if (meta.opts.printResult && resultText != null) {
                sb.append("返回值: ").append(resultText).append('\n')
            }
            if (meta.opts.printTime) {
                sb.append("执行耗时: ").append(System.currentTimeMillis() - meta.start).append("ms")
            }
            log.info(sb.toString())
        }
    }

    /**
     * 执行一次方法错误日志记录
     *
     * @param meta 方法记录元数据
     * @param ex 异常对象
     */
    private fun logErrorOnce(meta: RecMeta, ex: Throwable) {
        CoroutineLogger.safeLog {
            val cost = System.currentTimeMillis() - meta.start
            log.error("方法 ${meta.className}.${meta.methodName} 执行失败(${cost}ms): ${ex.message}", ex)
        }
    }

    // ---- Rendering & args ----

    /**
     * 安全地将对象转换为字符串表示形式
     *
     * @return 对象的字符串表示
     */
    private fun Any?.safeRender(): String = when (this) {
        null -> "null"
        is ByteArray -> "ByteArray(size=$size)"
        is CharArray -> "CharArray(size=$size)"
        is InputStream -> "<InputStream>"
        is Resource -> "<Resource:${this::class.simpleName}>"
        is MultipartFile -> "<MultipartFile name=${this.name} size=${this.size}>"
        is ServerHttpRequest -> "<ServerHttpRequest>"
        is ServerHttpResponse -> "<ServerHttpResponse>"
        is Principal -> "<Principal name=${this.name}>"
        is Publisher<*> -> "<Publisher:${this::class.simpleName}>"
        else -> runCatching { toString() }.getOrDefault("<${this::class.simpleName}>")
    }

    /**
     * 格式化方法参数列表为易读字符串
     *
     * @param args 参数数组
     * @return 格式化后的参数字符串
     */
    private fun prettyArgs(args: Array<Any?>): String {
        if (args.isEmpty()) return ""
        val filtered = args.filterNot {
            it is Continuation<*> ||
                    it is ServerHttpRequest || it is ServerHttpResponse ||
                    it is InputStream || it is Resource ||
                    it is MultipartFile
        }
        if (filtered.isEmpty()) return ""
        return filtered.joinToString(prefix = "[", postfix = "]") { it.safeRender() }
    }
}
