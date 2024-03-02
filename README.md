# A highly configurable java plugin system

This repository contains the basic part of the plugin system, and the part about express integration with spring 
framework is under development.

**Development work is not yet complete.**

## 特性
- [x] 多层级的插件管理支持
  - 扁平化/树状插件体系
  - 支持插件之间互相依赖
  - 支持插件依赖于普通jar包
- [x] 灵活的扩展点加载策略
- [x] 灵活的类加载策略
  - 限制对指定包/类的访问
  - 限制或指定类加载的来源
- [x] 插件市场支持
  - 支持以maven仓库作为插件市场
  - 支持启用本地插件缓存
- [x] 强大的事件体系
  - 支持监听插件的变更
  - 支持读取扩展点的变更
  - 允许用户监听扩展点
- [x] 高度可定制化的插件行为
  - 允许为特定插件独立配置扩展点行为
  - 允许独立配置插件的行为
- [x] 高度可扩展性
  - 允许用户自定义提供插件描叙信息
  - 允许用户在插件加载过程中嵌入自己的数据
  - [ ] 允许用户为特定插件提供独立的解析加载流程
- [x] 强大的标签体系

## 快速开始
[如何使用.md](./documents/如何使用.md)