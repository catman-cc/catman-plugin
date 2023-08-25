package cc.catman.plugin.extensionPoint;

public class DefaultExtensionPointMatcher implements IExtensionPointMatcher{
    @Override
    public Class<?> match(Class<?> clazz, Class<?> wantType) {
        return clazz.isAssignableFrom(wantType)?clazz:null;
    }
}
