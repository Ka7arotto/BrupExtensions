# SSRFScan - SSRF 漏洞扫描插件

![Java](https://img.shields.io/badge/Java-17-orange)
![Burp Suite](https://img.shields.io/badge/Burp%20Suite-Professional%2FCommunity-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 项目简介

SSRFScan 是一个基于 Burp Suite Montoya API 开发的专业 SSRF（服务端请求伪造）漏洞扫描插件。该插件通过智能参数分析和 DNS LOG 检测技术，自动识别应用程序中的 SSRF 安全漏洞，帮助安全研究人员快速定位潜在的服务端请求伪造风险。

## ✨ 核心功能

### 🎯 SSRF 检测

-   **智能参数识别**：自动检测 URL 参数中可能存在 SSRF 的字段
-   **DNS LOG 验证**：使用 DNS 外带技术验证 SSRF 漏洞的真实性
-   **多协议支持**：支持 HTTP/HTTPS 等多种协议的 SSRF 检测
-   **参数类型覆盖**：支持 GET、POST、JSON 等多种参数格式

### 🔧 高级配置

-   **可配置载荷**：支持自定义 SSRF 测试载荷
-   **参数过滤**：智能过滤可能包含 URL 的参数
-   **扫描开关**：灵活的扫描启停控制
-   **实时配置**：运行时动态调整扫描参数

### 🛠 检测机制

-   **URL 模式识别**：检测参数值中的 URL 模式
-   **子域名生成**：动态生成随机子域名进行测试
-   **DNS 回显验证**：通过 DNS LOG 服务验证外带数据
-   **多重验证**：减少误报，提高检测准确性

### 📊 结果展示

-   **实时监控**：扫描结果实时显示在专用面板
-   **详细信息**：完整的请求/响应信息展示
-   **漏洞标记**：发现的 SSRF 漏洞高亮显示
-   **参数管理**：可视化的参数配置界面

## 🛠 技术架构

### 核心技术栈

-   **开发语言**：Java 17
-   **API 框架**：Burp Suite Montoya API
-   **构建工具**：Maven
-   **UI 框架**：Java Swing
-   **检测技术**：DNS LOG 外带

### 关键组件

#### 🔍 SSRFDetector

-   SSRF 检测核心引擎
-   实现 DNS LOG 外带验证
-   管理检测逻辑和结果分析

#### ⚙️ PayloadConfig

-   载荷配置管理器
-   支持动态添加/删除测试载荷
-   扫描开关控制

#### 🌐 MyHttpHandler

-   HTTP 流量拦截处理器
-   实现`HttpHandler`接口
-   执行 SSRF 检测流程

#### 📋 MyTableModel

-   扫描结果数据模型
-   管理检测结果展示
-   支持结果排序和过滤

## 📦 安装使用

### 环境要求

-   Java 17 或更高版本
-   Burp Suite Professional/Community Edition
-   Maven 3.6+
-   有效的 DNS LOG 服务（如 ceye.io、dnslog.cn 等）

### 编译安装

```bash
# 克隆项目
git clone <repository-url>
cd SSRFScan

# 编译打包
mvn clean compile package

# 生成的JAR文件
target/SSRFScan-1.0-SNAPSHOT.jar
```

### 插件加载

1. 打开 Burp Suite
2. 进入 `Extensions` → `Installed`
3. 点击 `Add` 按钮
4. 选择编译好的 JAR 文件
5. 确认插件加载成功

## 🚀 使用指南

### 基础配置

1. **DNS LOG 服务配置**

    - 注册 DNS LOG 服务账号（推荐 ceye.io）
    - 获取专属的 DNS LOG 域名
    - 在插件中配置 DNS LOG 域名

2. **启动扫描**
    - 在 SSRF-Scan 标签页中启用"激活扫描"
    - 配置扫描参数和载荷
    - 开始监听 HTTP 流量

### 检测流程

1. **参数识别**

    - 在 Proxy 或 Repeater 中发送目标请求
    - 插件自动识别可能存在 SSRF 的参数
    - 检查参数值是否包含 URL 模式

2. **载荷注入**

    - 生成包含 DNS LOG 域名的测试载荷
    - 替换原始参数值进行测试
    - 发送带有外带域名的请求

3. **结果验证**
    - 检查 DNS LOG 服务是否收到回显
    - 验证 SSRF 漏洞的真实性
    - 记录漏洞详细信息

### 高级功能

#### 参数配置面板

-   **载荷管理**：添加、删除、清空测试载荷
-   **参数过滤**：配置需要检测的参数名称
-   **扫描控制**：实时启停扫描功能

#### 结果管理

-   **漏洞列表**：显示所有检测到的 SSRF 漏洞
-   **详情查看**：点击记录查看完整的请求/响应
-   **数据导出**：支持结果数据的导出功能

## 🔄 检测原理

### SSRF 检测机制

```
参数扫描 → URL模式识别 → DNS LOG载荷生成 → 请求发送 → DNS回显验证 → 漏洞确认
```

### 详细流程

1. **流量拦截**

    - 监听来自 Proxy 和 Repeater 的 HTTP 请求
    - 提取所有请求参数进行分析

2. **参数分析**

    - 检查参数值是否符合 URL 格式
    - 识别可能用于外部请求的参数
    - 过滤掉明显不相关的参数

3. **载荷构造**

    - 生成随机子域名标识符
    - 构造包含 DNS LOG 域名的测试载荷
    - 替换原始参数值

4. **请求测试**

    - 发送包含测试载荷的 HTTP 请求
    - 记录请求时间和参数信息
    - 等待服务端处理响应

5. **漏洞验证**
    - 检查 DNS LOG 服务的访问记录
    - 验证是否收到目标服务器的 DNS 请求
    - 确认 SSRF 漏洞的存在

## 📝 检测结果

扫描结果包含以下关键信息：

-   **漏洞 ID**：唯一标识符
-   **目标 URL**：存在漏洞的请求地址
-   **响应状态**：HTTP 响应状态码
-   **响应长度**：响应内容大小
-   **漏洞描述**：详细的漏洞信息
-   **请求详情**：完整的 HTTP 请求/响应数据
