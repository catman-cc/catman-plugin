package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.handler.jar.JarPluginParseInfo;
import junit.framework.TestCase;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.core.io.ClassPathResource;

public class JsonJacksonPluginDescribeParserTest extends TestCase {

    public void testUnknownProperties(){
        StandardPluginDescribe standardPluginDescribe=new StandardPluginDescribe();
        standardPluginDescribe.setDescribeResource(new ClassPathResource("cat-man-plugin.json"));
        JsonJacksonPluginDescribeParser parser=new JsonJacksonPluginDescribeParser();
        PluginParseInfo pluginParseInfo = parser.wrapper(standardPluginDescribe);
        System.out.println(pluginParseInfo);
        BeanCopier beanCopier = BeanCopier.create(PluginParseInfo.class, JarPluginParseInfo.class, false);
        JarPluginParseInfo j=new JarPluginParseInfo();
        beanCopier.copy(pluginParseInfo,j,null );
        System.out.println(j);
    }
}