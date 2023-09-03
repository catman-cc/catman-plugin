package cc.catman.plugin.describe.resources;

import cc.catman.plugin.resources.*;
import junit.framework.TestCase;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Objects;

public class CombineResourceBrowserTest extends TestCase {

    public void testBrowser() {
        // /Users/jpanda/work/temp/maven/com/google/guava
        CombineResourceBrowser combineResourceBrowser=new CombineResourceBrowser()
                .addResourceBrowser(new JarResourceBrowser())
                .addResourceBrowser(new DirResourceBrowser());

        CombineResourceVisitor visitor=new CombineResourceVisitor(combineResourceBrowser, new ResourceVisitor() {
            @Override
            public boolean visitor(Resource resource) {
                System.out.println(resource.getFilename());
                if (Objects.equals(resource.getFilename(), "NOTICE.txt")){
                    return false;
                }
                return true;
            }
        });

        visitor.visitor(new FileSystemResource("/Users/jpanda/work/temp/maven/com/google/guava"));

    }
}