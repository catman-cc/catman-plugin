package cc.catman.plugin.runtime;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.extensionPoint.IExtensionPointManagerFactory;

public class DefaultPluginInstanceFactory implements IPluginInstanceFactory{
    @Override
    public IPluginInstance create(IPluginManager pluginManager, PluginParseInfo pluginParseInfo) {
        DefaultPluginInstance pluginInstance=new DefaultPluginInstance(pluginManager,pluginParseInfo);
        // 设置类加载器,从描述信息中获取所有的扩展点数据,
        // 初始化扩展点管理器
        IExtensionPointManagerFactory extensionPointManagerFactory=pluginManager.getPluginConfiguration().getExtensionPointManagerFactory();
        IExtensionPointManager extensionPointManager=extensionPointManagerFactory.create(pluginInstance);
        if (pluginInstance.getExtensionPointManager()==null){
            pluginInstance.setExtensionPointManager(extensionPointManager);
        }
        return pluginInstance;
    }
}
