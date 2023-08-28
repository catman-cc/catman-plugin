package cc.catman.plugin.describe.enmu;

/**
 * 插件类型
 */
public enum EPluginKind {
    MAVEN,
    PACKAGE, // 包类型的插件,可以用于提供一组插件
    FILE,// 文件类型的插件,用于提供一个插件,但无法细分插件的具体格式,理论上该状态不应该存在,他最终会被转换为下面几个状态.
    DIR, // 在一个指定的目录下存放着一组插件
    JAR, // 一个jar包,存放着插件所需的所有内容,通常是一个FAT-JAR
    JAR_WITH_DIR,// jar包和目录共存,一般是一个SHIN-JAR配合着所需的依赖jar包共同存在.
    CLASSES_DIR, // 主要用于开发阶段,这里会直接去加载指定目录下的class文件
    // ========= 上面是比较常规的插件类型 ===============
    SOURCE_CODE,// 这是插件的 中间形态,后面会根据插件源码的类型,进一步交给编译工具去处理,比如:maven或者gradle,经过处理后,将会转换为上诉几种插件类型.

}
