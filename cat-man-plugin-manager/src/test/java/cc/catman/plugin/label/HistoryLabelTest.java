package cc.catman.plugin.label;

import cc.catman.plugin.core.label.HistoryLabel;
import cc.catman.plugin.core.label.Labels;
import junit.framework.TestCase;


public class HistoryLabelTest extends TestCase {

    public void test(){
        Labels l= HistoryLabel.wrapper(Labels.empty());
        l.add("a","1");
        l.add("a","2");
        l.add("a","3");
        l.add("a","4");
        l.add("b","!23");
        l.rm("b","2!23");
        l.rm("b","2!23");
        System.out.println(l.toString());
    }
}