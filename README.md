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

## 开发相关
> 插件系统的加载机制参考了maven的插件加载机制和Annotation Processor的加载机制,并且在此基础上进行了扩展.

当前插件系统,将插件的加载过程拆分为多个生命周期,用户可以根据自己的需要为插件动态注入新的生命周期,目前内置的生命周期有:
- UNKNOWN: 未知阶段,无法确认插件处于何种状态
- ANALYZE: 分析阶段,插件处于此阶段时,插件系统会在有限的信息内对插件进行分析,判断插件的打包类型
- ACHIEVE: 获取阶段,插件处于此阶段时,插件系统会尝试获取插件的资源
- COMPILE: 编译阶段,插件处于此阶段时,插件系统会尝试编译插件,生成编译产物,主要是class文件及其静态资源,对于已获取最终资源的插件,此阶段会被跳过
- BUILD: 构建阶段,插件处于此阶段时,插件系统会尝试基于编译产物构建插件,并尝试生成jar,war,zip,tar等文件,对于已获取最终资源的插件,此阶段会被跳过
- SEARCH: 搜索阶段,插件处于此阶段时,插件系统会尝试搜索插件的描述信息,并将扩展点的信息加载到插件系统中
- PARSE: 解析阶段,插件处于此阶段时,插件系统会尝试解析插件的描述信息,并将插件的描述信息加载到插件系统中
- VERIFY: 验证阶段,插件处于此阶段时,插件系统会尝试验证插件的描述信息,并将插件的描述信息加载到插件系统中
- PRE_LOAD: 插件加载前的准备工作,比如,获取第三方依赖资源,初始化插件的运行环境等
- LOAD: 加载阶段,插件处于此阶段时,插件系统会尝试加载插件,并将其注册到对应的插件管理器中.
- AFTER_LOAD: 插件加载后的工作,比如,初始化插件的运行环境,启动插件的运行环境等
- FINISHED: 插件加载完成

插件系统的加载过程是一个有限状态机,每个插件都有自己的状态,并且可以根据自己的状态进行状态转移,插件系统会根据插件的状态进行加载,并且在加载过程中
,会触发一系列的事件,用户可以监听这些事件,并且根据自己的需要进行处理.

同时,参考AnnotationProcessor的加载机制,插件系统将使用循环模型解析插件,允许插件在解析时,动态调整自己接下来要触发的生命周期.

### 如何动态注入生命周期
```java
public class MyPluginLifecycleInjector implements IPluginLifecycleInjector {
    @Override
    public IInjectPluginLifecycle inject(IParsingProcessProcessor processor, IPluginManager pluginManager); {
        // 注入一个新的生命周期
       return new IInjectPluginLifecycle() {
         @Override
         public void injectLifecycle(){
         
         }
         @Override
         public IPluginParserInfoHandler providerHandler(){
             return null;
         }

       };
    }
}
``` 
然后将其注入到插件系统中即可:
```java
PluginConfiguration pluginConfiguration = new PluginConfiguration();
pluginConfiguration.getParsingProcessProcessorFactory()
  .registerLifecycleInjector(new IPluginLifecycleInjector() {
  @Override
  public IInjectPluginLifecycle inject(IParsingProcessProcessor processor, IPluginManager pluginManager) {
    return new MyPluginLifecycleInjector();
  }
});
```

