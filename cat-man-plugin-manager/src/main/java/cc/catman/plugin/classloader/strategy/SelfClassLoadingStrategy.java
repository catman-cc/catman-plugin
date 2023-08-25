package cc.catman.plugin.classloader.strategy;

import cc.catman.plugin.classloader.exceptions.ClassLoadRuntimeException;
import cc.catman.plugin.classloader.handler.Payload;

public class SelfClassLoadingStrategy implements IClassLoadingStrategy{
    @Override
    public boolean load(Payload payload) {
        try {
            Class<?> clazz=payload.getSelfLoadFunction().apply(payload.getClassName());
            payload.setClazz(clazz);
        }catch (ClassLoadRuntimeException e){
            return false;
        }
        return true;
    }
}
