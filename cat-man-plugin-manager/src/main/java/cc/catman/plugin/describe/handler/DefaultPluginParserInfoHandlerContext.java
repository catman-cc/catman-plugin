package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultPluginParserInfoHandlerContext implements IPluginParserInfoHandlerContext{

    protected  List<IPluginParserInfoHandler> handlers=createParserInfoHandlers();

    private List<IPluginParserInfoHandler> createParserInfoHandlers() {
        return Arrays.asList(
                new DirPluginParserInfoHandler(),
                new ClassDirPluginParserInfoHandler(),
                new JarPluginParserInfoHandler()
                );
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        ArrayList<PluginParseInfo> successParsed=new ArrayList<>();
        AtomicInteger counter=new AtomicInteger(0);

        for (IPluginParserInfoHandler handler : this.handlers) {
            if(handler.support(parseInfo)){
                counter.incrementAndGet();
                List<PluginParseInfo> ppis=handler.handler(parseInfo);
                ppis.stream().forEach(ppi->{
                    if (EPluginParserStatus.SUCCESS.equals(ppi.getStatus())){
                        // 插件解析成功,无需继续解析,进入下一阶段
                        successParsed.add(ppi);
                        return;
                    } else if (EPluginParserStatus.FAIL.equals(ppi.getStatus())) {
                        // 插件解析失败
                        return;
                    }
//                    这里需要重新解析
                   successParsed.addAll(this.handler(ppi));
                });
                // 如果PluginParseInfo的状态需要继续处理,那就重新传递给handler方法
                // 如果PluginParseInfo已经被完全解析完毕或者解析失败,走解析失败流程
            }
        }
        // 这里需要处理,如果一个parseinfo没有匹配到处理器
        if (counter.get()==0){
            // TODO 没有找到对应的处理器
        }
        return successParsed;
    }
}
