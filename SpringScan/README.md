# SpringScan - Spring Boot 漏洞扫描插件

![Java](https://img.shields.io/badge/Java-17-orange)
![Burp Suite](https://img.shields.io/badge/Burp%20Suite-Professional%2FCommunity-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Vulnerability%20Scanner-green)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 项目简介

SpringScan 是一个专门针对 Spring Boot 应用程序的安全漏洞扫描插件，基于 Burp Suite Montoya API 开发。该插件能够自动识别 Spring Boot 应用并检测其潜在的安全风险，包括敏感路径泄露、配置文件暴露、Actuator 端点安全问题等 Spring Boot 特有的安全漏洞。

## ✨ 核心功能

### 🎯 Spring Boot 识别

-   **框架指纹识别**：自动检测目标是否为 Spring Boot 应用
-   **版本信息获取**：识别 Spring Boot 版本和相关组件
-   **技术栈分析**：分析应用使用的 Spring 技术栈
-   **服务信息收集**：获取应用基础服务信息

### 🔍 安全漏洞检测

-   **Actuator 端点扫描**：检测暴露的 Spring Boot Actuator 监控端点
-   **敏感路径发现**：扫描可能泄露敏感信息的路径
-   **配置文件暴露**：检测 application.properties 等配置文件泄露
-   **调试信息泄露**：发现调试模式下的信息泄露

### 📊 常见检测项目

-   `/actuator/*` 系列端点
-   `/health` 健康检查端点
-   `/info` 应用信息端点
-   `/env` 环境变量端点
-   `/metrics` 指标监控端点
-   `/trace` 请求追踪端点
-   `/dump` 线程转储端点
-   `/configprops` 配置属性端点

### 🛠 高级功能

-   **路径爆破**：使用内置字典进行路径爆破
-   **响应分析**：智能分析响应内容判断路径有效性
-   **批量检测**：同时检测多个潜在的敏感路径
-   **结果过滤**：自动过滤无效和重复的检测结果

## 🛠 技术架构

### 核心技术栈

-   **开发语言**：Java 17
-   **API 框架**：Burp Suite Montoya API
-   **构建工具**：Maven
-   **UI 框架**：Java Swing
-   **检测引擎**：自定义 Spring Boot 扫描引擎

### 关键组件

#### 🔍 SpringBootScan

-   Spring Boot 漏洞扫描核心引擎
-   实现 Spring Boot 应用识别逻辑
-   执行敏感路径扫描和结果分析

#### 🌐 MyHttpHandler

-   HTTP 请求/响应处理器
-   实现`HttpHandler`接口
-   负责流量拦截和漏洞检测触发

#### 📋 MyTableModel

-   扫描结果数据模型
-   管理检测结果的存储和展示
-   支持结果排序和筛选功能

#### 📁 TableData

-   结果数据封装类
-   存储漏洞详细信息
-   包含 HTTP 请求/响应数据

## 📦 安装使用

### 环境要求

-   Java 17 或更高版本
-   Burp Suite Professional/Community Edition
-   Maven 3.6+

### 编译安装

```bash
# 克隆项目
git clone <repository-url>
cd SpringScan

# 编译打包
mvn clean compile package

# 生成的JAR文件
target/SpringScan-1.0-SNAPSHOT.jar
```

### 插件加载

1. 打开 Burp Suite
2. 进入 `Extensions` → `Installed`
3. 点击 `Add` 按钮
4. 选择编译好的 JAR 文件
5. 确认插件加载成功

## 🚀 使用指南

### 基础操作

1. **自动扫描模式**

    - 插件加载后自动开始监听 HTTP 流量
    - 当检测到 Spring Boot 应用时自动触发扫描
    - 无需手动配置，开箱即用

2. **手动测试**

    - 在 Proxy 或 Repeater 中发送目标请求
    - 插件自动识别 Spring Boot 应用特征
    - 触发完整的安全扫描流程

3. **结果查看**
    - 扫描结果实时显示在 SpringBoot-Scan 标签页
    - 点击记录查看详细的请求/响应信息
    - 发现的漏洞路径会特别标记

### 扫描流程

#### 第一阶段：Spring Boot 识别

-   分析 HTTP 响应头信息
-   检查 Server 字段和 X-Application-Context
-   识别 Spring Boot 特有的错误页面
-   确认目标为 Spring Boot 应用

#### 第二阶段：路径扫描

-   基于内置字典进行路径爆破
-   发送 GET 请求到潜在的敏感路径
-   分析响应状态码和内容长度
-   识别有效的敏感路径

#### 第三阶段：结果分析

-   过滤 404 和其他无效响应
-   分析响应内容判断敏感程度
-   生成详细的扫描报告
-   标记高风险发现项

## 🔄 检测原理

### Spring Boot 识别机制

```
HTTP响应分析 → 框架特征识别 → 版本信息提取 → 应用确认
```

### 路径扫描流程

```
字典加载 → 路径构造 → 批量请求 → 响应分析 → 结果过滤 → 漏洞确认
```

### 详细检测逻辑

1. **被动识别**

    - 监听正常 HTTP 流量
    - 分析响应头中的 Spring Boot 标识
    - 检查错误页面的框架信息

2. **主动扫描**

    - 构造常见 Actuator 端点路径
    - 发送 HTTP GET 请求进行探测
    - 分析响应确定端点有效性

3. **深度分析**
    - 对发现的有效端点进行详细分析
    - 提取可能的敏感信息
    - 评估安全风险等级

## 📝 检测结果

### 结果格式

扫描结果包含以下信息：

-   **记录 ID**：唯一标识符
-   **目标 URL**：发现漏洞的完整路径
-   **状态码**：HTTP 响应状态码
-   **响应长度**：响应内容大小
-   **漏洞类型**：发现的安全问题类型
-   **详细信息**：完整的 HTTP 请求/响应数据

### 常见发现项

-   **信息泄露**：/actuator/env 环境变量泄露
-   **健康状态**：/actuator/health 应用健康信息
-   **配置信息**：/actuator/configprops 配置属性
-   **系统指标**：/actuator/metrics 系统监控指标
-   **请求追踪**：/actuator/httptrace 请求追踪信息

**⚠️ 免责声明**：本工具仅用于合法的安全测试和研究目的。使用者需确保在获得适当授权的情况下使用，并对使用后果承担全部责任。请遵循相关法律法规和道德准则。
