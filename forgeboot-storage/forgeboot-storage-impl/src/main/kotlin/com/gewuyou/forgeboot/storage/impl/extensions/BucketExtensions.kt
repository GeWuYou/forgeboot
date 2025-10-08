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

package com.gewuyou.forgeboot.storage.impl.extensions

import com.gewuyou.forgeboot.storage.api.entities.StorageBucket
import io.minio.messages.Bucket

/**
 * 将Bucket对象转换为StorageBucket对象
 *
 * @return StorageBucket对象，包含名称、创建时间和区域信息
 */
fun Bucket.toStorageBucket(): StorageBucket {
    return StorageBucket(this.name(), this.creationDate().toInstant(), this.bucketRegion())
}
