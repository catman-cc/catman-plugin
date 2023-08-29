package cc.catman.plugin.describe.resources;

import org.springframework.core.io.Resource;

/**
 * 资源浏览器
 */
public interface IResourceBrowser {
    /**
     * 是否支持浏览指定类型的资源
     * @param resource 需要浏览的资源
     */
    boolean support(Resource resource);

    /**
     *  将所有的资源传递给visitor,visitor可以根据自己的需要进行处理
     * @param visitor 资源浏览这
     * @return 虽然支持访问该资源,但是未能处理该资源,所以告知调用方,浏览失败了
     */
    ResourceBrowserResult browser(Resource resource,ResourceVisitor visitor);
}
