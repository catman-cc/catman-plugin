package cc.catman.plugin;

import cc.catman.plugin.common.GAV;
import junit.framework.TestCase;

public class GAVTest extends TestCase {

    public void tests(){
        GAV g1 = GAV.builder().group("jpanda").name("jpanda").version("version").build();
        GAV g2 = GAV.builder().group("jpanda").name("jpanda").version("version").build();
        GAV g3 = GAV.builder().group("jpanda").name("jpanda").version("version").build();
        assert g1.equals(g2);
    }
}