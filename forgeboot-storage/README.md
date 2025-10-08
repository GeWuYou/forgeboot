# Forgeboot Storage Module

Forgeboot 文件存储模块提供了统一的文件存储抽象接口，支持多种存储后端（当前支持 MinIO）。

## 📦 模块结构

```
forgeboot-storage/
├── forgeboot-storage-api/           # API 接口定义
│   ├── component/                   # 存储组件接口
│   ├── config/                      # 配置类
│   ├── entities/                    # 实体类
│   ├── exception/                   # 异常定义
│   └── generator/                   # 生成器接口
├── forgeboot-storage-impl/          # 实现层
│   ├── component/minio/             # MinIO 实现
│   └── generator/                   # 默认实现
└── forgeboot-storage-autoconfigure/ # 自动配置
    └── config/                      # Spring Boot 自动配置
```

## 🚀 快速开始

### 1. 添加依赖

在你的 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation(project(":forgeboot-storage:forgeboot-storage-autoconfigure"))
}
```

### 2. 配置文件

在 `application.yml` 中配置存储服务：

```yaml
forgeboot:
  storage:
    # MinIO 服务端点
    endpoint: http://localhost:9000
    # 访问密钥
    access-key: minioadmin
    secret-key: minioadmin
    # 存储模式 (minio, local)
    mode: minio
    # 默认存储桶名称
    bucket: uploads
    # 上传预签名URL有效期
    presign-upload-ttl: 15m
    # 下载预签名URL有效期
    presign-download-ttl: 10m
    # 是否自动创建存储桶
    auto-create-bucket: true
    # 文件名生成规则
    filename-pattern: "{original}_{uuid}_{timestamp}"
```

### 3. 使用示例

#### 3.1 基本文件操作

```kotlin
import com.gewuyou.forgeboot.storage.api.component.FullFileStorageComponent
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileService(
    private val storageComponent: FullFileStorageComponent
) {

    /**
     * 上传文件到默认存储桶
     */
    fun uploadFile(file: MultipartFile): String {
        val meta = storageComponent.uploadFile(file)
        return meta.storageKey // 返回存储键
    }

    /**
     * 上传文件到指定存储桶
     */
    fun uploadFileToCustomBucket(file: MultipartFile, bucketName: String): String {
        val meta = storageComponent.uploadFile(file, bucketName)
        return meta.storageKey
    }

    /**
     * 下载文件
     */
    fun downloadFile(objectName: String): InputStream {
        return storageComponent.downloadFile(objectName)
    }

    /**
     * 删除文件
     */
    fun deleteFile(objectName: String) {
        storageComponent.deleteFile(objectName)
    }

    /**
     * 批量上传文件
     */
    fun uploadMultipleFiles(files: List<MultipartFile>): List<String> {
        return storageComponent.uploadFiles(files).map { it.storageKey }
    }
}
```

#### 3.2 预签名URL操作

```kotlin
@Service
class PresignedUrlService(
    private val storageComponent: FullFileStorageComponent
) {

    /**
     * 生成上传预签名URL (使用默认过期时间)
     */
    fun generateUploadUrl(objectName: String): String {
        return storageComponent.generateUploadUrl("uploads", objectName)
    }

    /**
     * 生成下载预签名URL (自定义过期时间)
     */
    fun generateDownloadUrl(objectName: String, expiresInSeconds: Int): String {
        return storageComponent.generateDownloadUrl("uploads", objectName, expiresInSeconds)
    }

    /**
     * 验证预签名URL是否有效
     */
    fun validateUrl(presignUrl: String): Boolean {
        return storageComponent.validatePresignUrl(presignUrl)
    }

    /**
     * 刷新预签名URL
     */
    fun refreshUrl(presignUrl: String, newExpiresInSeconds: Int): String {
        return storageComponent.refreshPresignUrl(presignUrl, newExpiresInSeconds)
    }
}
```

#### 3.3 存储桶管理

```kotlin
@Service
class BucketService(
    private val storageComponent: FullFileStorageComponent
) {

    /**
     * 检查存储桶是否存在
     */
    fun checkBucketExists(bucketName: String): Boolean {
        return storageComponent.bucketExist(bucketName)
    }

    /**
     * 创建存储桶
     */
    fun createBucket(bucketName: String) {
        if (!storageComponent.bucketExist(bucketName)) {
            storageComponent.createBucket(bucketName)
        }
    }

    /**
     * 列出所有存储桶
     */
    fun listAllBuckets(): List<StorageBucket> {
        return storageComponent.listBucket()
    }

    /**
     * 列出存储桶中的对象
     */
    fun listObjects(bucketName: String, prefix: String = ""): List<StorageItem> {
        return storageComponent.listObjects(bucketName, prefix, recursive = true, maxKeys = 1000)
    }
}
```

#### 3.4 对象管理操作

```kotlin
@Service
class ObjectManagementService(
    private val storageComponent: FullFileStorageComponent
) {

    /**
     * 检查对象是否存在
     */
    fun checkObjectExists(bucketName: String, objectName: String): Boolean {
        return storageComponent.objectExist(bucketName, objectName)
    }

    /**
     * 复制对象
     */
    fun copyObject(
        sourceBucket: String,
        sourceObject: String,
        targetBucket: String,
        targetObject: String
    ) {
        storageComponent.copyObject(sourceBucket, sourceObject, targetBucket, targetObject)
    }

    /**
     * 移动对象 (剪切)
     */
    fun moveObject(
        sourceBucket: String,
        sourceObject: String,
        targetBucket: String,
        targetObject: String
    ) {
        storageComponent.cutObject(sourceBucket, sourceObject, targetBucket, targetObject)
    }

    /**
     * 获取对象URL
     */
    fun getObjectUrl(bucketName: String, objectName: String, expires: Int = 3600): String {
        return storageComponent.getObjectUrl(bucketName, objectName, expires)
    }
}
```

#### 3.5 RESTful API 示例

```kotlin
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/storage")
class StorageController(
    private val storageComponent: FullFileStorageComponent
) {

    /**
     * 上传单个文件
     */
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, Any>> {
        val meta = storageComponent.uploadFile(file)
        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "storageKey" to meta.storageKey,
                "fileName" to meta.fileName,
                "originalFileName" to meta.originalFileName,
                "size" to meta.size,
                "contentType" to meta.contentType
            )
        )
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/upload/batch", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadMultipleFiles(@RequestParam("files") files: List<MultipartFile>): ResponseEntity<Map<String, Any>> {
        val metas = storageComponent.uploadFiles(files)
        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "count" to metas.size,
                "files" to metas.map { meta ->
                    mapOf(
                        "storageKey" to meta.storageKey,
                        "fileName" to meta.fileName,
                        "size" to meta.size
                    )
                }
            )
        )
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{objectName}")
    fun downloadFile(@PathVariable objectName: String): ResponseEntity<ByteArray> {
        val inputStream = storageComponent.downloadFile(objectName)
        val bytes = inputStream.readBytes()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$objectName\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(bytes)
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete/{objectName}")
    fun deleteFile(@PathVariable objectName: String): ResponseEntity<Map<String, Any>> {
        storageComponent.deleteFile(objectName)
        return ResponseEntity.ok(mapOf("success" to true, "message" to "File deleted successfully"))
    }

    /**
     * 获取上传预签名URL
     */
    @GetMapping("/presign/upload/{objectName}")
    fun getUploadPresignUrl(@PathVariable objectName: String): ResponseEntity<Map<String, String>> {
        val url = storageComponent.generateUploadUrl("uploads", objectName)
        return ResponseEntity.ok(mapOf("uploadUrl" to url))
    }

    /**
     * 获取下载预签名URL
     */
    @GetMapping("/presign/download/{objectName}")
    fun getDownloadPresignUrl(@PathVariable objectName: String): ResponseEntity<Map<String, String>> {
        val url = storageComponent.generateDownloadUrl("uploads", objectName)
        return ResponseEntity.ok(mapOf("downloadUrl" to url))
    }

    /**
     * 列出所有存储桶
     */
    @GetMapping("/buckets")
    fun listBuckets(): ResponseEntity<List<Map<String, Any>>> {
        val buckets = storageComponent.listBucket()
        return ResponseEntity.ok(
            buckets.map { bucket ->
                mapOf(
                    "name" to bucket.name,
                    "creationDate" to bucket.creationDate.toString()
                )
            }
        )
    }

    /**
     * 列出存储桶中的对象
     */
    @GetMapping("/objects/{bucketName}")
    fun listObjects(
        @PathVariable bucketName: String,
        @RequestParam(required = false, defaultValue = "") prefix: String
    ): ResponseEntity<List<Map<String, Any>>> {
        val objects = storageComponent.listObjects(bucketName, prefix, recursive = true, maxKeys = 100)
        return ResponseEntity.ok(
            objects.map { item ->
                mapOf(
                    "name" to item.name,
                    "size" to item.size,
                    "lastModified" to item.lastModified.toString(),
                    "etag" to (item.etag ?: "")
                )
            }
        )
    }
}
```

## 🔧 配置说明

### 核心配置项

| 配置项                                      | 类型       | 默认值                             | 说明                 |
|------------------------------------------|----------|---------------------------------|--------------------|
| `forgeboot.storage.endpoint`             | String   | -                               | 存储服务端点URL (必需)     |
| `forgeboot.storage.access-key`           | String   | -                               | 访问密钥ID (必需)        |
| `forgeboot.storage.secret-key`           | String   | -                               | 私有密钥 (必需)          |
| `forgeboot.storage.mode`                 | String   | `minio`                         | 存储模式 (minio/local) |
| `forgeboot.storage.bucket`               | String   | `uploads`                       | 默认存储桶名称            |
| `forgeboot.storage.presign-upload-ttl`   | Duration | `15m`                           | 上传预签名URL有效期        |
| `forgeboot.storage.presign-download-ttl` | Duration | `10m`                           | 下载预签名URL有效期        |
| `forgeboot.storage.auto-create-bucket`   | Boolean  | `false`                         | 是否自动创建存储桶          |
| `forgeboot.storage.filename-pattern`     | String   | `{original}_{uuid}_{timestamp}` | 文件名生成模式            |
| `forgeboot.storage.max-part-size-bytes`  | Long     | `5242880`                       | 分片大小(5MB)          |

### 文件名生成模式

支持以下占位符：

- `{original}` - 原始文件名（不含扩展名）
- `{uuid}` - UUID
- `{timestamp}` - 时间戳
- `{extension}` - 文件扩展名

示例模式：

- `{original}_{uuid}` → `document_123e4567-e89b-12d3-a456-426614174000.pdf`
- `{timestamp}_{original}` → `1728384000000_document.pdf`
- `files/{timestamp}/{uuid}.{extension}` → `files/1728384000000/123e4567-e89b-12d3-a456-426614174000.pdf`

## 🏗️ 架构设计

### 组件层次

```
FullFileStorageComponent (完整功能)
├── SimpleFileStorageComponent (基础功能)
│   ├── BucketManagementComponent (存储桶管理)
│   ├── FileOperationComponent (文件操作)
│   └── ObjectManagementComponent (对象管理)
└── PresignUrlComponent (预签名URL)
```

### 扩展性

如果需要支持其他存储后端（如AWS S3、阿里云OSS等），只需：

1. 实现 [
   `FullFileStorageComponent`](forgeboot-storage-api/src/main/kotlin/com/gewuyou/forgeboot/storage/api/component/FullFileStorageComponent.kt:32)
   接口
2. 在 [
   `StorageAutoConfiguration`](forgeboot-storage-autoconfigure/src/main/kotlin/com/gewuyou/forgeboot/storage/autoconfigure/config/StorageAutoConfiguration.kt:1)
   中添加对应的Bean配置
3. 添加必要的依赖

## 🧪 测试

### 单元测试示例

```kotlin
import org.junit.jupiter.api.Test

## 🏗️ 策略模式架构设计

### 设计模式

本模块采用 * * 策略模式 * * 实现多存储后端的动态切换, 具有高度的可扩展性和灵活性。

### 架构图

```

业务代码
↓ (依赖接口)
FullFileStorageComponent (接口)
↓ (由选择器创建)
StorageComponentSelector (选择器)
↓ (根据mode选择)
StorageComponentFactory (工厂接口)
├── MinIOStorageComponentFactory → MinIOFullFileStorageComponent
├── LocalStorageComponentFactory → LocalSimpleFileStorageComponent
├── OSSStorageComponentFactory → OSSFullFileStorageComponent (待实现)
└── S3StorageComponentFactory → S3FullFileStorageComponent (待实现)

```

### 组件层次

```

FullFileStorageComponent (完整功能)
├── SimpleFileStorageComponent (基础功能)
│ ├── BucketManagementComponent (存储桶管理)
│ ├── FileOperationComponent (文件操作)
│ └── ObjectManagementComponent (对象管理)
└── PresignUrlComponent (预签名URL)

```

### 工作流程

1. **应用启动**: Spring Boot自动扫描并注册所有 `StorageComponentFactory` 实现
2. **选择器创建**: `StorageComponentSelector` 收集所有工厂实例
3. **策略选择**: 根据配置的 `forgeboot.storage.mode` 选择对应工厂
4. **组件创建**: 工厂创建具体的存储组件实例
5. **业务使用**: 业务代码通过接口使用,无需关心具体实现

### 切换存储后端

只需修改配置,无需修改代码:

```yaml
# 使用 MinIO
forgeboot:
  storage:
    mode: minio
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin

# 切换到本地存储
forgeboot:
  storage:
    mode: local
    # 本地存储不需要endpoint等配置

# 切换到阿里云OSS (需要先实现)
forgeboot:
  storage:
    mode: oss
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key: your-key
    secret-key: your-secret
```

### 扩展新的存储提供商

假设要添加**阿里云OSS**支持,只需三步:

#### 步骤1: 实现存储组件

```kotlin
package com.gewuyou.forgeboot.storage.impl.component.oss

class OSSFullFileStorageComponent(
    private val ossClient: OSS,
    private val storageProperties: StorageProperties,
    private val filenameGenerator: FilenameGenerator
) : FullFileStorageComponent {

    override fun uploadFile(file: MultipartFile, bucketName: String): UploadMeta {
        val fileName = filenameGenerator.generateFilename(
            file.originalFilename ?: "unnamed",
            storageProperties.filenamePattern
        )

        val putResult = ossClient.putObject(
            bucketName,
            fileName,
            file.inputStream
        )

        return UploadMeta(
            originalFileName = file.originalFilename ?: "",
            contentType = file.contentType,
            size = file.size,
            storageKey = fileName,
            bucket = bucketName,
            fileName = fileName
        )
    }

    override fun generateUploadUrl(bucketName: String, objectName: String, expires: Int): String {
        val expiration = Date(System.currentTimeMillis() + expires * 1000L)
        return ossClient.generatePresignedUrl(bucketName, objectName, expiration, HttpMethod.PUT).toString()
    }

    // 实现其他方法...
}
```

#### 步骤2: 创建工厂类

```kotlin
package com.gewuyou.forgeboot.storage.impl.factory

class OSSStorageComponentFactory(
    private val ossClient: OSS,
    private val storageProperties: StorageProperties,
    private val filenameGenerator: FilenameGenerator
) : StorageComponentFactory {

    override fun getMode(): String = "oss"

    override fun createSimpleStorageComponent(): SimpleFileStorageComponent {
        return OSSFullFileStorageComponent(ossClient, storageProperties, filenameGenerator)
    }

    override fun createFullStorageComponent(): FullFileStorageComponent {
        return OSSFullFileStorageComponent(ossClient, storageProperties, filenameGenerator)
    }

    override fun isSupported(): Boolean {
        return storageProperties.endpoint != null &&
                storageProperties.accessKey != null &&
                storageProperties.secretKey != null
    }
}
```

#### 步骤3: 添加自动配置

在 `StorageAutoConfiguration` 中添加:

```kotlin
@Bean
@ConditionalOnClass(OSS::class)
@ConditionalOnProperty(prefix = "forgeboot.storage", name = ["mode"], havingValue = "oss")
fun ossClient(properties: StorageProperties): OSS {
    return OSSClientBuilder().build(
        properties.endpoint,
        properties.accessKey,
        properties.secretKey
    )
}

@Bean
@ConditionalOnClass(OSS::class)
@ConditionalOnProperty(prefix = "forgeboot.storage", name = ["mode"], havingValue = "oss")
fun ossStorageComponentFactory(
    ossClient: OSS,
    properties: StorageProperties,
    filenameGenerator: FilenameGenerator
): StorageComponentFactory {
    return OSSStorageComponentFactory(ossClient, properties, filenameGenerator)
}
```

完成!现在可以通过配置 `forgeboot.storage.mode=oss` 来使用阿里云OSS了。

### 设计优势

| 优势         | 说明                         |
|------------|----------------------------|
| ✅ **开闭原则** | 对扩展开放,对修改封闭,新增存储后端无需修改现有代码 |
| ✅ **依赖倒置** | 业务代码依赖抽象接口,不依赖具体实现         |
| ✅ **单一职责** | 每个工厂只负责创建对应的存储组件           |
| ✅ **易于测试** | 可以轻松Mock不同的存储实现进行单元测试      |
| ✅ **配置驱动** | 通过配置文件即可切换存储后端,无需重新编译      |
| ✅ **热插拔**  | 支持运行时动态切换存储策略              |
| ✅ **解耦合**  | 业务逻辑与存储实现完全解耦              |

### 支持的存储后端

| 存储类型       | Mode值   | 状态     | 说明             |
|------------|---------|--------|----------------|
| MinIO      | `minio` | ✅ 已实现  | 完整支持所有功能       |
| 本地存储       | `local` | ✅ 已实现  | 示例实现,不支持预签名URL |
| 阿里云OSS     | `oss`   | 📝 待实现 | 按上述步骤可快速实现     |
| 腾讯云COS     | `cos`   | 📝 待实现 | 按上述步骤可快速实现     |
| AWS S3     | `s3`    | 📝 待实现 | 按上述步骤可快速实现     |
| Azure Blob | `azure` | 📝 待实现 | 按上述步骤可快速实现     |
