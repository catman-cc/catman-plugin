package cc.catman.plugin.common;

public class Constants {

    public static final String LABEL_PREFIX="cc.catman.plugin/";
    /**
     * 插件文件名称,不包含后缀
     */
    public static final String PLUGIN_DESCRIBE_FILE_NAME="cat-man-plugin";

    /**
     * 基于MAVEN插件创建的普通依赖项列表
     */
    public static final String PLUGIN_MAVEN_NORMAL_DEPENDENCIES_FILE_NAME="cat-man-plugin-maven.deps";

    /**
     * 基于maven的普通依赖pom文件
     */
    public static final String PLUGIN_MAVEN_NORMAL_DEPENDENCIES_POM_FILE_NAME="cat-man-plugin-maven.pom";

    public static final String LOCAL_CACHE_FILE_NAME="cat-man-plugin.cache";

    public static final String DEFAULT_NORMAL_DEPENDENCIES_LIBS_DIR="libs";

    public static final String[] JDK_EXCLUDED_PACKAGES = new String[] { "apple.",
            "com.apple", "java.", "javax.", "jdk", "sun.",
            "com.sun",
            "oracle.",
            "javassist.", "org.aspectj.", "net.sf.cglib." };
}
