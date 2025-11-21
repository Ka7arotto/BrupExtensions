# XssScan - XSS 漏洞扫描插件

![Java](https://img.shields.io/badge/Java-17-orange)
![Burp Suite](https://img.shields.io/badge/Burp%20Suite-Professional%2FCommunity-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 项目简介

XssScan 是一个基于 Burp Suite Montoya API 开发的专业 XSS（跨站脚本）漏洞扫描插件，专注于检测存储型 XSS 漏洞。该插件通过智能拦截 HTTP 流量，自动注入测试标记并分析响应内容来识别潜在的 XSS 安全漏洞。

## ✨ 核心功能

### 🔍 漏洞检测

-   **存储型 XSS 检测**：专门针对存储型 XSS 漏洞进行深度扫描
-   **智能参数提取**：自动识别 URL 参数、Body 参数和 JSON 参数
-   **动态标记注入**：使用时间戳生成唯一测试标记避免误报
-   **多参数类型支持**：支持 GET/POST/JSON 等多种参数格式

### 🎯 流量过滤

-   **来源过滤**：仅处理来自 Proxy 和 Repeater 的请求
-   **Content-Type 检测**：智能识别可能存在 XSS 的响应类型
    -   `text/html`
    -   `text/javascript`
    -   `application/javascript`

### 📊 结果管理

-   **实时结果显示**：扫描结果实时展示在专用标签页
-   **详细信息查看**：点击结果查看完整的请求/响应详情
-   **漏洞高亮显示**：发现的 XSS 漏洞在表格中特殊标记
-   **可视化界面**：直观的 GUI 界面便于操作和结果分析

## 🛠 技术架构

### 核心技术栈

-   **开发语言**：Java 17
-   **API 框架**：Burp Suite Montoya API
-   **构建工具**：Maven
-   **UI 框架**：Java Swing

### 关键组件

#### 🔧 MyHttpHandler

-   实现`HttpHandler`接口
-   负责 HTTP 请求/响应拦截和处理
-   执行 XSS 检测逻辑

#### 📋 MyTableModel

-   继承`AbstractTableModel`
-   管理扫描结果数据
-   支持表格排序和显示

#### 🗂 HashMapRequest

-   管理 XSS 测试标记与请求映射
-   确保测试标记唯一性
-   追踪参数测试状态

#### 🔍 MyFilterRequest

-   请求来源过滤器
-   判断请求是否来自 Proxy 或 Repeater
-   提高扫描精准度

## 📦 安装使用

### 环境要求

-   Java 17 或更高版本
-   Burp Suite Professional/Community Edition
-   Maven 3.6+

### 编译安装

```bash
# 克隆项目
git clone <repository-url>
cd XssScan

# 编译打包
mvn clean compile package

# 生成的JAR文件
target/XssScan-1.0-SNAPSHOT.jar
```

### 插件加载

1. 打开 Burp Suite
2. 进入 `Extensions` → `Installed`
3. 点击 `Add` 按钮
4. 选择编译好的 JAR 文件
5. 确认插件加载成功

## 🚀 使用指南

### 基础操作

1. **启动扫描**

    - 在 XSS-Scan 标签页中点击"开始扫描"按钮
    - 插件开始监听 HTTP 流量

2. **执行测试**

    - 在 Proxy 或 Repeater 中发送目标请求
    - 插件自动注入 XSS 测试标记
    - 分析响应内容识别漏洞

3. **查看结果**
    - 扫描结果实时显示在结果表格中
    - 点击记录查看详细的请求/响应信息
    - XSS 漏洞会在表格中特殊标记

### 高级配置

#### 控制面板功能

-   **扫描开关**：启用/禁用 XSS 扫描功能
-   **参数配置**：自定义扫描参数和载荷
-   **过滤设置**：配置流量过滤规则

## 🔄 工作流程

```
HTTP响应拦截 → 流量过滤 → 参数提取 → 标记注入 → 请求发送 → 响应分析 → 结果展示
```

### 详细流程

1. **初始化阶段**

    - 注册 HTTP 处理器
    - 创建用户界面
    - 初始化数据结构

2. **请求拦截**

    - 监听所有 HTTP 响应
    - 过滤非目标请求
    - 检查 Content-Type

3. **参数处理**

    - 提取 URL/Body/JSON 参数
    - 生成唯一测试标记
    - 构造测试请求

4. **漏洞检测**

    - 发送带标记的测试请求
    - 分析响应内容
    - 识别 XSS 漏洞

5. **结果管理**
    - 记录扫描结果
    - 更新 UI 显示
    - 提供详细信息

## 📝 输出格式

扫描结果包含以下信息：

-   **ID**：唯一标识符
-   **URL**：目标 URL 地址
-   **状态码**：HTTP 响应状态
-   **XSS ID**：检测到的 XSS 标记
-   **参数名**：存在漏洞的参数
-   **详情**：完整的请求/响应信息

## ⚡ 性能特点

-   **低误报率**：使用时间戳生成唯一标记
-   **高效过滤**：多层过滤机制减少无效扫描
-   **实时处理**：流量拦截实时分析
-   **资源优化**：合理的内存和 CPU 使用

**⚠️ 免责声明**：此工具仅用于合法的安全测试目的。使用者需确保在获得适当授权的情况下使用，并对使用后果承担全部责任。
