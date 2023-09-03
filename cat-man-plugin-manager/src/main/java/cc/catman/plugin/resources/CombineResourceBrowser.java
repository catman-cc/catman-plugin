package cc.catman.plugin.resources;

import lombok.Getter;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * 合并资源浏览器,访问者可以直接访问聚合后的浏览器
 */
public class CombineResourceBrowser implements IResourceBrowser{
    @Getter
    private List<IResourceBrowser> browsers;

    public CombineResourceBrowser() {
        this(new ArrayList<>());
    }

    public CombineResourceBrowser addResourceBrowser(IResourceBrowser browser){
        this.browsers.add(browser);
        return this;
    }

    public CombineResourceBrowser(List<IResourceBrowser> browsers) {
        this.browsers = browsers;
    }

    @Override
    public boolean support(Resource resource) {
        return browsers.stream().anyMatch(browser->browser.support(resource));
    }

    @Override
    public ResourceBrowserResult browser(Resource resource, ResourceVisitor visitor) {
        for (IResourceBrowser browser : browsers) {
           if (browser.support(resource)){
               ResourceBrowserResult r=browser.browser(resource,visitor);
               if (r.isHandled()|| !r.isContinueVisitor()){
                   return  r;
               }
           }
        }
        return ResourceBrowserResult.of(false,true);
    }
}
