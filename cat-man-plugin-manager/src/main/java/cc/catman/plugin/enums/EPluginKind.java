package cc.catman.plugin.enums;

/**
 * 插件类型,目前系统只认这四种插件,这四种插件都会在
 */
public enum EPluginKind {
    MAVEN,
    PACKAGE, // 包类型的插件,可以用于提供一组插件
    DIR, // 在一个指定的目录下存放着一组插件
    JAR, // 一个jar包,存放着插件所需的所有内容,通常是一个FAT-JAR
    CLASSES_DIR, // 主要用于开发阶段,这里会直接去加载指定目录下的class文件
}
