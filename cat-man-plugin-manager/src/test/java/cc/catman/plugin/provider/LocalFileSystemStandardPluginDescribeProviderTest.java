package cc.catman.plugin.provider;

import junit.framework.TestCase;

import java.util.Collections;

public class LocalFileSystemStandardPluginDescribeProviderTest extends TestCase {
    public void test(){
        LocalFileSystemPluginDescribeProvider localFileSystemPluginDescribeProvider=LocalFileSystemPluginDescribeProvider
                .builder()
                .dirs(Collections.singletonList(System.getProperty("user.dir")))
                .build();
        localFileSystemPluginDescribeProvider.provider().forEach(pd->{
            System.out.println(pd);
        });
    }
}