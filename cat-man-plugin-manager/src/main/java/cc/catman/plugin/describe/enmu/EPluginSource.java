package cc.catman.plugin.describe.enmu;

/**
 * 插件的来源,不同的来源可能对应着不同的处理方式,此处并不是限制了source的类型,而是展示内置的source类型
 * 比如:
 *    GIT,MYSQL,
 *    - GIT: 需要先获取代码,得到source_code类型的插件,然后交给其他插件处理.
 *    - MYSQL: 直接生成MYSQL对应的ClassLoader即可.
 */
public enum EPluginSource {
    LOCAL, // 插件来源于本地
    GIT, // 插件需要使用GIT协议获取
    MYSQL, // 插件来源于数据库
}
