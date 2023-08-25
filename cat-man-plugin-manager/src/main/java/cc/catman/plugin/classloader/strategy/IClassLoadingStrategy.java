package cc.catman.plugin.classloader.strategy;

import cc.catman.plugin.classloader.handler.Payload;

/**
 * 类加载策略,用户可以主动重写具体的某一个类加载策略,比如,不允许向父容器加载特定的类定义,(当然,这种处理IClassLoaderHandler可能会更合适一些)
 * 甚至直接重写ClassLoadingStrategyProcessor,全面的对类加载过程进行限制.
 */
public interface IClassLoadingStrategy {

    boolean load(Payload payload);
}
