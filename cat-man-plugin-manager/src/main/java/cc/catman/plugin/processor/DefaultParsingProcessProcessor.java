package cc.catman.plugin.processor;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.label.HistoryLabel;
import cc.catman.plugin.core.label.Labels;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.enums.EPluginParserStatus;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.enums.EParseProcessLabel;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.handlers.PluginParseErrorHandler;
import cc.catman.plugin.handlers.PluginParseInfoHelper;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.runtime.IPluginManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

@Builder
@Slf4j
@AllArgsConstructor()
public class DefaultParsingProcessProcessor implements IParsingProcessProcessor {
    /**
     * 是否为Label添加History功能
     */
    @Builder.Default
    private boolean historyLabel = false;
    /**
     * 轮次编号
     */
    @Builder.Default
    private int roundNumber = 0;

    @Builder.Default
    private PluginParseErrorHandler errorHandler = (exception, processor) -> false;

    /**
     * 为每一个描述信息都写入一个唯一的id标记
     */
    @Builder.Default
    private IdGenerator idGenerator = new AlternativeJdkIdGenerator();

    @Getter
    private PluginParseInfoHelper pluginParseInfoHelper;

    @Getter
    protected IPluginManager ownerPluginManager;

    @Getter
    @Builder.Default
    protected Map<Integer,List<IPluginInstance>> roundPluginInstances=new HashMap<>();

    /**
     * 插件的生命周期
     */
    @Builder.Default
    private List<String> lifeCycles = new ArrayList<>(
            Arrays
                    .stream(ELifeCycle.values())
                    .map(ELifeCycle::name)
                    .collect(Collectors.toList())
    );
    @Builder.Default
    private List<IPluginParserInfoHandler> parserInfoHandlerList = new ArrayList<>();

    /**
     * 不同生命周期下对应的插件解析器
     */
    @Builder.Default
    private Map<String, List<IPluginParserInfoHandler>> lifeCyclePluginParsers = new HashMap<>();

    /**
     * 结束的插件描述信息
     */
    @Builder.Default
    private List<PluginParseInfo> finished = new ArrayList<>();

    /**
     * 本轮次需要处理的插件信息
     */
    @Builder.Default
    private List<PluginParseInfo> currentRound = new ArrayList<>();

    /**
     * 下一轮次需要处理的插件信息
     */
    @Builder.Default
    private List<PluginParseInfo> nextRound = new ArrayList<>();

    public DefaultParsingProcessProcessor init(){
        ArrayList<IPluginParserInfoHandler> copy = new ArrayList<>(this.parserInfoHandlerList);
        this.parserInfoHandlerList.clear();
        copy.forEach(this::add);
        return this;
    }

    public List<IPluginInstance> process() {

       try {
         while (currentRound.size()>0){
             log.debug(" start process,round:[{}],rounds size:{},finished size:{}",  roundNumber+1, currentRound.size(), finished.size());
             preProcess();
             doProcess();
             postProcess();
         }
       }
        catch (RuntimeException runtimeException){
            // 如果解析时发生异常,将其发送给移除处理器,并由异常处理器来决定是否继续循环
            if (!errorHandler.handle(runtimeException, this)) {
                log.warn("an exception occurred while processing",  runtimeException);
            }
        }
       return roundPluginInstances.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    protected void preProcess() {
        // 自增序列号
        roundNumber++;
        // 添加基础信息
        for (PluginParseInfo parseInfo : this.currentRound) {
            // 处理标签
            handlePluginLabels(parseInfo);
            // 如果标签启用了历史记录功能,为其创建代理
            if (historyLabel) {
                if (!HistoryLabel.isHistoryLabel(parseInfo)) {
                    parseInfo.setLabels(HistoryLabel.wrapper(parseInfo.getLabels()));
                }
            }
        }
    }

    protected void handlePluginLabels(PluginParseInfo parseInfo) {
        Labels labels = parseInfo.getLabels();
        if (labels.noExist(EParseProcessLabel.IN_PROCESS_ID.label())) {
            labels.add(EParseProcessLabel.IN_PROCESS_ID.label(), idGenerator.generateId());
        }

        if (labels.noExist(EParseProcessLabel.ROUND_CREATE.v())) {
            labels.add(EParseProcessLabel.ROUND_CREATE.v(), roundNumber);
        }
    }

    protected void postProcess() {
        // 执行完成,处理后续流程,比如
        // 用户标记了描述信息的状态为完成.但是没有调用对应的方法
        // 处理遗漏掉的插件信息
        for (PluginParseInfo parseInfo : this.currentRound) {
            if (ELifeCycle.FINISHED.equals(ELifeCycle.valueOf(parseInfo.getLifeCycle()))) {
                handlerFinishedPluginParseInfo(parseInfo);
            }else {
                handlerMissingPluginParseInfo(parseInfo);
            }
        }
        // 处理完成,开始迁移数据
        this.currentRound.clear();
        if (this.nextRound != null) {
            this.currentRound.addAll(this.nextRound);
            this.nextRound.clear();
        }
    }

    protected void resetLifeCycle(PluginParseInfo parseInfo) {
        if (ELifeCycle.FINISHED.equals(ELifeCycle.valueOf(parseInfo.getLifeCycle()))){
            // 已完成的将被忽略
            return;
        }
        // 当前阶段还有没执行完的任务
        if (parseInfo.getTasks(parseInfo.getLifeCycle()).size()>0){
            return;
        }
        if (Optional.ofNullable(parseInfo.getNextLifCycle().peek()).isPresent()) {
            parseInfo.setLifeCycle(parseInfo.getNextLifCycle().poll());
        }else if (parseInfo.isLifeCycleContinue()){
            int i = lifeCycles.indexOf(parseInfo.getLifeCycle());
            if (i==-1){
                throw new RuntimeException("The life cycle value:["+parseInfo.getLifeCycle()+"] of a plug-n :["+parseInfo.toGAV().toString()+"] cannot be recognized.");
            }
            if (i<lifeCycles.size()){
                parseInfo.setLifeCycle(lifeCycles.get(i+1));
            }
        }
        }


    void doProcess() {
        for (PluginParseInfo ppi : currentRound) {
            Optional<List<IPluginParserInfoHandler>> handlers =
                    Optional.ofNullable(this.lifeCyclePluginParsers.get(ppi.getLifeCycle()))
                    .map(ps -> ps.stream().filter(p -> p.support(ppi))
                            .collect(Collectors.toList()));
            if (handlers.isPresent()&&handlers.get().size()>0) {
                List<IPluginParserInfoHandler> pihs = handlers.get();
                for (IPluginParserInfoHandler pih : pihs) {
                    log.debug("round:[{}],in [{}] lifeCycle, parser handler named:{},start handler:{}",roundNumber,ppi.getLifeCycle(),pih.getClass().getSimpleName(), ppi.toGAV());
                        if (!pih.handler(this, ppi)) {
                            // 终止后续处理器的处理
                            log.debug("[{}] send break signal...",pih.getClass().getSimpleName());
                            break;
                        }
                }
            } else {

                handlerNoParserFound(ppi);
            }
        }
    }

    public void finish(PluginParseInfo parseInfo) {
        if (!finished.contains(parseInfo)) {
            finished.add(parseInfo);
        }
        Labels labels = parseInfo.getLabels();
        labels.replace(EParseProcessLabel.ROUND_END.label(), String.valueOf(roundNumber));
        parseInfo.setLifeCycle(ELifeCycle.FINISHED.name());
    }

    /**
     * 派生了新的插件描述信息,将会自动加入到下一轮次
     *
     * @param parseInfo 新的插件描述信息
     */
    public void next(PluginParseInfo parseInfo) {
        // 判断是否是新增的插件描述,为其添加标签
        Labels labels = parseInfo.getLabels();
        // 生成的阶段
        if (labels.noExist(EParseProcessLabel.ROUND_CREATE.label())) {
            labels.add(EParseProcessLabel.ROUND_CREATE.label(), currentRound);
        }
        // 加入到下一阶段
        nextRound.add(parseInfo);
    }

    public PluginParseInfo createNext(PluginParseInfo from,PluginParseInfo parseInfo){

        getPluginParseInfoHelper().copyLabels(from,parseInfo);
        // 记录是由那个组件信息转换过来的
        parseInfo.setFrom(from);

        parseInfo.setStatus(EPluginParserStatus.PROCESSING);
        parseInfo.setClassLoaderConfiguration(from.getClassLoaderConfiguration());
        parseInfo.setPluginInstance(from.getPluginInstance());

        parseInfo.setAfterHandlers(from.getAfterHandlers());
        parseInfo.setAfterParsers(from.getAfterParsers());

        parseInfo.setDescribeResource(from.getDescribeResource());

        parseInfo.setRelativePath(from.getRelativePath());
        parseInfo.setBaseDir(from.getBaseDir());

        parseInfo.setNormalDependencyType(from.getNormalDependencyType());
        parseInfo.setNormalDependencyLibrariesDescrbieResource(from.getNormalDependencyLibrariesDescrbieResource());
        parseInfo.setNormalDependencyLibraries(from.getNormalDependencyLibraries());
        parseInfo.setNormalClassLoadingStrategy(from.getNormalClassLoadingStrategy());

        parseInfo.setNextLifCycle(from.getNextLifCycle());
        parseInfo.setLifeCycleTasks(from.getLifeCycleTasks());
        // 填充标签
        parseInfo.getLabels().add(EDescribeLabel.decorate("old-property/base-dir"),from.getBaseDir());
        next(parseInfo);
        return parseInfo;
    }

    public PluginParseInfo createNext(PluginParseInfo from){
        PluginParseInfoHelper pluginParseInfoHelper = getPluginParseInfoHelper();
        PluginParseInfo n = pluginParseInfoHelper.create();
        createNext(from,n);
        return n;
    }

    protected void handlerNoParserFound(PluginParseInfo parseInfo) {
        // 处理没有找到解析器的插件信息
        log.warn("round:[{}]-[{}],can not found parser handler for: [{}]",roundNumber, parseInfo.getLifeCycle(), parseInfo.toGAV());
        log.trace("plugin parse info details :{}", parseInfo);
        // 如果一个解析器在整个过程中都没有被处理过,那么将会被加入到noParserFound
    }

    /**
     * 获取在本轮次被遗漏掉的插件信息
     */
    protected List<PluginParseInfo> findMissingPluginParseInfo() {
        return currentRound.stream()
                .filter(ppi -> !(finished.contains(ppi) || nextRound.contains(ppi))).collect(Collectors.toList());
    }


    protected void handlerFinishedPluginParseInfo(PluginParseInfo parseInfo) {
        finish(parseInfo);
    }

    protected void handlerMissingPluginParseInfo(PluginParseInfo parseInfo) {
        // 调整生命周期,并添加到下一阶段
        resetLifeCycle(parseInfo);
        if (!nextRound.contains(parseInfo)){
            next(parseInfo);
        }
    }

    @Override
    public IParsingProcessProcessor add(IPluginParserInfoHandler parserInfoHandler) {
        this.parserInfoHandlerList.add(parserInfoHandler);
        parserInfoHandler.lifeCycles().forEach(l -> {
            this.lifeCyclePluginParsers
                    .computeIfAbsent(l, (k) -> new ArrayList<>())
                    .add(parserInfoHandler);
        });
        return this;
    }

    @Override
    public boolean beforeLifeCycles(String lifeCycle, String before) {
        int index = this.lifeCycles.indexOf(before);
        if (index == -1) {
            return false;
        }
        this.lifeCycles.add(index, before);
        onAddLifeCycle(lifeCycle);
        return true;
    }

    @Override
    public boolean afterLifeCycles(String lifeCycle, String after) {
        int index = this.lifeCycles.indexOf(after);
        if (index == -1) {
            return false;
        }
        this.lifeCycles.add(++index, after);
        onAddLifeCycle(lifeCycle);
        return true;
    }

    protected void onAddLifeCycle(String lifeCycle) {
        this.lifeCyclePluginParsers.put(
                lifeCycle
                , new ArrayList<>(
                        this.parserInfoHandlerList
                                .stream()
                                .filter(pih -> pih.lifeCycles().contains(lifeCycle))
                                .collect(Collectors.toList()
                                )
                ));
    }

   public IPluginInstance registryPluginInstance(PluginParseInfo parseInfo){
       IPluginInstance instance = getOwnerPluginManager().registryPluginInstance(parseInfo);
       this.roundPluginInstances.computeIfAbsent(roundNumber,k->new ArrayList<>())
               .add(instance);
       return instance;
   }
}
