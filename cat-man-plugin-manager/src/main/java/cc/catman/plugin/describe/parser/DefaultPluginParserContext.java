package cc.catman.plugin.describe.parser;


import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultPluginParserContext implements IPluginParserContext{

    protected List<IPluginDescribeParser> parsers=createParsers();

    private List<IPluginDescribeParser> createParsers()
    {
        List<IPluginDescribeParser> parsers=new ArrayList<>();
        parsers.add(new JsonJacksonPluginDescribeParser());
        return parsers;
    }

    @Override
    public List<PluginParseInfo> parser(StandardPluginDescribe standardPluginDescribe) {
        for (IPluginDescribeParser parser : parsers) {
            if (parser.supports(standardPluginDescribe)){
                PluginParseInfo parseInfo=parser.wrapper(standardPluginDescribe);
                parseInfo.setStatus(EPluginParserStatus.WAIT_PARSE);
                parseInfo.setPluginParserContext(this);
              return Collections.singletonList(parseInfo);
            }
        }
        // TODO 这里需要特殊处理,上报异常,而不是中断操作,最后又事件总线统一处理异常问题.
//       throw new ClassLoadRuntimeException("Unable to parse the characteristic plugin description information:"+pluginDescribe.getResource().getFilename());
        return Collections.emptyList();
    }

    @Override
    public IPluginParserContext add(IPluginDescribeParser parser) {
        this.parsers.add(parser);
        return this;
    }
}
