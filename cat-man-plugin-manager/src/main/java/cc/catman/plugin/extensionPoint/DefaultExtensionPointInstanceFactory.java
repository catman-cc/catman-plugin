package cc.catman.plugin.extensionPoint;

import lombok.SneakyThrows;

public class DefaultExtensionPointInstanceFactory implements IExtensionPointInstanceFactory{
    protected IExtensionPointManager extensionPointManager;

    public DefaultExtensionPointInstanceFactory(IExtensionPointManager extensionPointManager) {
        this.extensionPointManager = extensionPointManager;
    }

    @Override
    @SneakyThrows
    public <T> T newInstance(Class<T> clazz)  {
        return  clazz.newInstance();
    }
}
