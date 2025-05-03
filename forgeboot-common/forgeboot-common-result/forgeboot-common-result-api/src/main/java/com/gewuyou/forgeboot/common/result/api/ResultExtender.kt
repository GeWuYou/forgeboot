package com.gewuyou.forgeboot.common.result.api
/**
 * 结果扩展器
 *
 * 用于扩展结果映射，通过实现此接口，可以自定义逻辑以向结果映射中添加、修改或删除元素
 * 主要用于在某个处理流程结束后，对结果数据进行额外的处理或装饰
 *
 * @since 2025-05-03 16:08:55
 * @author gewuyou
 */
fun interface ResultExtender {
    /**
     * 扩展结果映射
     *
     * 实现此方法以执行扩展逻辑，可以访问并修改传入的结果映射
     * 例如，可以用于添加额外的信息，修改现有值，或者根据某些条件删除条目
     *
     * @param resultMap 一个包含结果数据的可变映射，可以在此方法中对其进行修改
     */
    fun extend(resultMap: MutableMap<String, Any?>)
}
