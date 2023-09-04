package cc.catman.plugin.common;

import cc.catman.plugin.handlers.search.FinderPluginDescribeParserInfoHandler;
import junit.framework.TestCase;

public class ClassHelperTest extends TestCase {

    public void testDoWithSuper() {
        ClassHelper.doWithSuper(FinderPluginDescribeParserInfoHandler.class,(c)->{
            System.out.println(c.getName());
        });
    }
}