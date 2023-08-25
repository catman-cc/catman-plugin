package cat.man.plugin.example.api;

/**
 * 扩展点不需要额外的配置.
 * 当然,也可以通过实现{@link cc.catman.plugin.core.IExtensionPoint}接口定义,
 * 来**强制**实现类响应插件系统的各项回调
 */
public interface NameService {

    String echo(String content);
}
