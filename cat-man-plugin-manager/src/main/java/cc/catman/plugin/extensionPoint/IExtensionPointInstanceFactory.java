package cc.catman.plugin.extensionPoint;

/**
 * 扩展点实例化工程
 */
public interface IExtensionPointInstanceFactory {

    <T> T newInstance(Class<T> clazz);
}
