package cc.catman.plugin.classloader.strategy;

import cc.catman.plugin.classloader.handler.Payload;
import cc.catman.plugin.options.PluginOptions;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

public class ClassLoadingStrategyProcessor {
    private PluginOptions options;



    @Getter
    protected  Map<String,IClassLoadingStrategy> strategies=createStrategies();

    public ClassLoadingStrategyProcessor(@NonNull PluginOptions options) {
        this.options = options;
    }
    private Map<String, IClassLoadingStrategy> createStrategies() {
        strategies.put(EClassLoadingStrategy.PARENT.name(),new ParentClassLoadingStrategy());
        strategies.put(EClassLoadingStrategy.SELF.name(),new SelfClassLoadingStrategy());
        strategies.put(EClassLoadingStrategy.DEPENDENCY.name(),new DependenciesClassLoadingStrategy());
        return strategies;
    }

    public ClassLoadingStrategyProcessor registryStrategy(String type,IClassLoadingStrategy strategy){
        this.strategies.put(type, strategy);
        return this;
    }

    public boolean loadClass(String type, Payload payload){
        IClassLoadingStrategy strategy=strategies.get(type);
        return strategy.load(payload);
    }

    public boolean loadClass( Payload payload){
        for (String type : payload.getOrderStrategies()) {
           if (loadClass(type,payload)){
               return true;
           }
        }

        return false;
    }
}
