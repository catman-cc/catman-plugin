package cc.catman.plugin.resources;

import org.springframework.core.io.Resource;

public interface ResourceVisitor {

    /**
     *  访问资源
     * @param resource 资源浏览器传递过来的资源对象
     * @return 告知browser是否继续访问剩下的资源
     */
    boolean visitor(Resource resource);
}
