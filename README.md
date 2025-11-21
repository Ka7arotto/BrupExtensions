# BurpExtensions - Burp Suite 安全扫描插件集合

![Java](https://img.shields.io/badge/Java-17-orange)
![Burp Suite](https://img.shields.io/badge/Burp%20Suite-Professional%2FCommunity-blue)
![Security](https://img.shields.io/badge/Security-Scanning-red)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 项目简介

自研的一些 Brupsuite 的安全扫描插件

## 🚀 插件概览

### 🔍 [XssScan - XSS 漏洞扫描插件](./XssScan/)

跨站脚本（XSS）漏洞检测工具，专注于存储型 XSS 漏洞的发现和验证。

### 🌐 [SpringScan - Spring Boot 漏洞扫描插件](./SpringScan/)

专门针对 Spring Boot 应用程序的安全漏洞扫描工具，自动识别并检测 Spring Boot 特有的安全风险。

### 🔐 [SecretScan - 敏感信息泄露扫描插件](./SecretScan/)

专业的敏感信息和凭据泄露检测工具，能够识别各种类型的敏感数据暴露问题。

### 🔗 [SSRFScan - SSRF 漏洞扫描插件](./SSRFScan/)

服务端请求伪造（SSRF）漏洞的专业检测工具，支持多种 SSRF 攻击向量和检测方法。

### 核心组件

所有插件都基于统一的架构设计：

#### 🔧 HttpHandler

-   实现 `HttpHandler` 接口
-   负责 HTTP 请求/响应拦截
-   执行具体的漏洞检测逻辑

#### 📋 TableModel

-   继承 `AbstractTableModel`
-   管理扫描结果数据展示
-   支持排序、筛选和导出

#### 🗂 数据管理

-   统一的数据结构设计
-   请求/响应关联管理
-   结果去重和优化

#### 🔍 流量过滤

-   智能来源识别（Proxy/Repeater）
-   Content-Type 检测
-   请求范围控制

## 📦 快速开始

### 环境要求

-   **Java 17** 或更高版本
-   **Burp Suite** Professional/Community Edition
-   **Maven 3.6+** 构建工具
-   **内存要求**：建议 4GB 以上

### 批量编译

```bash
# 克隆整个项目
git clone <repository-url>
cd BurpExtensions

# 编译所有插件
for plugin in XssScan SpringScan SecretScan SSRFScan; do
    cd $plugin
    mvn clean compile package
    cd ..
done

# 编译产物位置
# XssScan/target/XssScan-1.0-SNAPSHOT.jar
# SpringScan/target/SpringScan-1.0-SNAPSHOT.jar
# SecretScan/target/SecretScan-1.0-SNAPSHOT.jar
# SSRFScan/target/SSRFScan-1.0-SNAPSHOT.jar
```

### 使用方式

所有插件加载后会自动开始工作：

-   **自动模式**：监听 Proxy 和 Repeater 的流量
-   **实时检测**：对符合条件的请求自动进行安全扫描
-   **结果展示**：每个插件都有独立的结果标签页
-   **详情查看**：点击结果项可查看完整的请求/响应信息

### 扫描控制

每个插件都支持灵活的配置选项：

-   **扫描开关**：可独立启用/禁用各个插件
-   **目标过滤**：支持域名、路径、参数过滤
-   **检测深度**：可调整扫描的详细程度
-   **超时设置**：自定义请求超时时间

### 结果管理

-   **结果导出**：支持 CSV、JSON 等格式导出
-   **结果筛选**：按严重程度、类型等筛选
-   **历史记录**：保存和查看历史扫描结果
-   **报告生成**：自动生成扫描报告

## 📚 详细文档

每个插件都有独立的详细文档：

-   [XssScan 详细文档](./XssScan/README.md) - XSS 漏洞检测使用指南
-   [SpringScan 详细文档](./SpringScan/README.md) - Spring Boot 扫描配置说明
-   [SecretScan 详细文档](./SecretScan/) - 敏感信息检测规则
-   [SSRFScan 详细文档](./SSRFScan/README.md) - SSRF 漏洞检测方法

### 开发环境

```bash
# 开发环境要求
Java 17+
Maven 3.6+
IntelliJ IDEA / Eclipse
Burp Suite Professional (推荐)

# 项目结构
BurpExtensions/
├── XssScan/          # XSS 扫描插件
├── SpringScan/       # Spring Boot 扫描插件
├── SecretScan/       # 敏感信息扫描插件
├── SSRFScan/         # SSRF 扫描插件
└── README.md         # 项目总览文档
```
