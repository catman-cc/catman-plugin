package cc.catman.plugin.classloader.strategy;

import cc.catman.plugin.classloader.handler.Payload;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassLoadingStrategyProcessor {

    @Getter
    protected  Map<String,IClassLoadingStrategy> strategies=createStrategies();

    private Map<String, IClassLoadingStrategy> createStrategies() {
        Map<String, IClassLoadingStrategy> strategies=new HashMap<>();
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

    public boolean loadClass(List<String> types, Payload payload){
        for (String type : types) {
           if (loadClass(type,payload)){
               return true;
           }
        }

        return false;
    }
}
