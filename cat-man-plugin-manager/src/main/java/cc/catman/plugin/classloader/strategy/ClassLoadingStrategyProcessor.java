package cc.catman.plugin.classloader.strategy;

import cc.catman.plugin.classloader.handler.Payload;
import cc.catman.plugin.options.PluginOptions;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

public class ClassLoadingStrategyProcessor {



    @Getter
    protected  Map<String,IClassLoadingStrategy> strategies=createStrategies();

    public ClassLoadingStrategyProcessor() {
    }
    private Map<String, IClassLoadingStrategy> createStrategies() {
        Map<String, IClassLoadingStrategy> strategyMap=new HashMap<>();
        strategyMap.put(EClassLoadingStrategy.PARENT.name(),new ParentClassLoadingStrategy());
        strategyMap.put(EClassLoadingStrategy.SELF.name(),new SelfClassLoadingStrategy());
        strategyMap.put(EClassLoadingStrategy.DEPENDENCY.name(),new DependenciesClassLoadingStrategy());
        return strategyMap;
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
