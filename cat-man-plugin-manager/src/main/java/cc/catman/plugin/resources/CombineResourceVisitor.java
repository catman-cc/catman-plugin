package cc.catman.plugin.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;


/**
 * 组合式的资源浏览器,如果当前访问的资源没有被明确拒绝,那么将递归调用其他资源浏览器继续解析资源,并将其子资源传递回当前访问者继续处理.
 */
@Slf4j
public class CombineResourceVisitor extends FilterResourceVisitor{
    private CombineResourceBrowser browser;

    protected ResourceVisitor doVisitor;

    public CombineResourceVisitor(CombineResourceBrowser browser, ResourceVisitor doVisitor) {
        this.browser = browser;
        this.doVisitor = doVisitor;
    }

    @Override
    public boolean filter(Resource resource) {
        return false;
    }

    @Override
    protected boolean doVisitor(Resource resource) {
        return false;
    }

    @Override
    public boolean visitor(Resource resource) {
        log.trace("find resource file:{}",resource);
        // 做一个简单的代理操作,以便于合并多个资源浏览器进行统一的资源访问
        // 所有的资源访问器互相嵌套
        // 优先使用当前visitor访问资源,如果当前visitor没有决绝资源的访问,
        // 那么将传递给其他资源浏览器,解析出的新资源,继续传递给当前资源访问者
        if (!doVisitor.visitor(resource)){
            return false;
        }
        if (browser.support(resource)){
            ResourceBrowserResult br = browser.browser(resource, this);
            if (!br.isContinueVisitor()){
                return false;
            }
        }
        return true;
    }
}
