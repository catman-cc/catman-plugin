package cc.catman.plugin.extensionPoint;

/**
 * 扩展点验证器,判断一个类型是否符合预期所需的类型
 *
 * 取名叫做Matcher其实并不合适,因为理论上IExtensionPointMatcher是可以操作返回的类型定义的
 */
public interface IExtensionPointMatcher {
    Class<?> match(Class<?> clazz,Class<?>wantType);
}
