package cc.catman.plugin.extensionPoint;

import lombok.SneakyThrows;

public class DefaultExtensionPointInstanceFactory implements IExtensionPointInstanceFactory{
    protected DefaultExtensionPointManager extensionPointManager;

    public DefaultExtensionPointInstanceFactory(DefaultExtensionPointManager extensionPointManager) {
        this.extensionPointManager = extensionPointManager;
    }

    @Override
    @SneakyThrows
    public <T> T newInstance(Class<T> clazz)  {
        return  clazz.newInstance();
    }
}
