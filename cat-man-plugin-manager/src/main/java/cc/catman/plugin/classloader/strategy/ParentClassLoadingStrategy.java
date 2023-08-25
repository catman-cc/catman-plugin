package cc.catman.plugin.classloader.strategy;

import cc.catman.plugin.classloader.handler.Payload;

public class ParentClassLoadingStrategy implements IClassLoadingStrategy{
    @Override
    public boolean load(Payload payload) {
       ClassLoader parent= payload.getClassLoader().getParent();
        try {
           Class<?> clazz=parent.loadClass(payload.getClassName());
           payload.setClazz(clazz);
        }catch (ClassNotFoundException e) {
            return  false;
        }
        return true;
    }
}
