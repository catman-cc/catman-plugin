package cc.catman.plugin.classloader.handler;

import java.util.List;
import java.util.Optional;

/**
 * 根据插件配置重写类加载策略
 */
public class ClassLoadingStrategySortedHandlerChain implements IClassLoaderHandler {
    @Override
    public Payload before(Payload payload) {
        List<String> orderStrategies = Optional.ofNullable(payload.getPluginInstance().getPluginOptions().getSpecifiedOrderlyClassLoadingStrategy())
                .filter(map -> map.containsKey(payload.getClassName()))
                .map(map -> map.get(payload.getClassName())).orElseGet(() -> Optional.ofNullable(
                                payload.getPluginInstance().getPluginOptions().getOrderlyClassLoadingStrategy())
                        .orElse(payload.getOrderStrategies()));
        payload.setOrderStrategies(orderStrategies);
        return payload;
    }
}
