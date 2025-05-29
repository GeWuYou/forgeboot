package com.gewuyou.forgeboot.webmvc.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.gewuyou.forgeboot.webmvc.dto.i18n.I18nKeys
import jakarta.validation.constraints.NotEmpty

/**
 * 根据id列表删除请求
 *
 * 该类用于封装删除操作中需要传入的id列表，确保删除操作能够接收一个明确且不为空的id集合
 * 它使用了泛型T，使得它可以适用于不同类型的id（例如Long，String等）
 *
 * @author gewuyou
 * @since 2025-01-18 17:39:18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class DeleteByIdsReq<T>(
    /**
     * 待删除的实体id列表
     *
     * 这个字段是删除操作的核心参数，它不能为空，以确保至少有一个id被指定用于删除
     * 使用@NotNull注解来确保在序列化和反序列化过程中，该字段不能为空
     *
     * @param ids 实体的唯一标识符列表，用于指定哪些实体应当被删除
     */
    @field:NotEmpty(message = I18nKeys.Forgeboot.Webmvc.Dto.DELETE_IDS_NOT_EMPTY)
    var ids: List<T>,
)