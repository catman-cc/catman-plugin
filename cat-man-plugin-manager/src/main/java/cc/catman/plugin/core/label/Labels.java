package cc.catman.plugin.core.label;


import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 标签组
 */
public class Labels implements ILabelAbility{

    /**
     * labels也可以有元数据
     */
    @Getter
    @Setter
    private Labels labels;

    @Getter
    @Setter
    private Map<String, Label> items;


    public static Labels empty() {
        return new Labels();
    }

    public static Labels of(Label label) {
        Labels labels = new Labels();
        labels.add(label);
        return labels;
    }

    public static Labels of(Collection<Label> ls) {
        Labels labels = new Labels();
        for (Label l : ls) {
            labels.add(l);
        }
        return labels;
    }

    protected Labels() {
        this.items = new HashMap<>();
    }

    public Labels(Labels labels) {
        this();
        this.labels = labels;
    }

    public Labels(Map<String,Label> labels) {
        this.items=labels;
    }

    public Labels(Labels labels, Map<String, Label> items) {
        this.labels = labels;
        this.items = items;
    }

    public Label add(String name, String value) {
        return add(Label.create(name, value));
    }

    public Label add(String name, Number value) {
        return add(Label.create(name, Double.toString(value.doubleValue())));
    }

    public Label add(String name, Object value) {
        //  maybe 转换成json.但是感觉把label搞得太复杂了
        return add(Label.create(name, value.toString()));
    }

    public Label add(Label label) {
        Label l = items.computeIfAbsent(label.getName(), (k) -> label);
        return l.add(label.getValue());
    }
    public Label replace(String name,String value){
        return replace(Label.create(name,value));
    }
    public Label replace(String name,String ...value){
        return replace(Label.create(name,value));
    }
    public Label replace(Label label){
        rm(label.getName());
       return add(label);
    }


    public boolean eq(String name, String value) {
        return find(name).map(label -> label.eq(value)).orElse(false);
    }

    public boolean eq(String name, Number value) {
        return find(name).map(label -> label.eq(value)).orElse(false);
    }

    public boolean gt(String name, Number value) {
        return find(name).map(label -> label.gt(value)).orElse(false);
    }

    public boolean ge(String name, Number value) {
        return find(name).map(label -> label.ge(value)).orElse(false);
    }

    public boolean lt(String name, Number value) {
        return find(name).map(label -> label.lt(value)).orElse(false);
    }

    public boolean le(String name, Number value) {
        return find(name).map(label -> label.le(value)).orElse(false);
    }

    public boolean sum(String name, Number value) {
        return find(name).map(label -> label.sum(value)).orElseGet(() -> {
            add(name, value);
            return true;
        });
    }

    public boolean sub(String name, Number value) {
        return find(name).map(label -> label.sub(value)).orElseGet(() -> {
            add(name, -value.doubleValue());
            return true;
        });
    }


    public boolean contain(String... name) {
        return contain(Arrays.asList(name));
    }

    public boolean contain(Collection<String> names) {
        return names.stream().allMatch(this::exist);
    }

    public boolean contain(String name,String value){
        return find(name,value).isPresent();
    }

    public boolean contain(String name,Collection<String> vs){
        return find(name).map(l->l.contain(vs)).orElse(false);
    }
    public boolean exist(String name) {
        return find(name).isPresent();
    }

    public boolean noExist(String name) {
        return !exist(name);
    }

    public boolean hasValue(String name) {
        return find(name).map(Label::notEmpty).orElse(false);
    }

    public boolean exist(String name, String value) {
        return find(name, value).isPresent();
    }

    public boolean notExistLabelOrLabelHasValue(String name, String value) {
        return StringUtils.hasText(value)&&find(name).map(l -> l.contain(value)).orElse(true);
    }

    public boolean notExistLabelOrLabelHasAnyValue(String name, String ... value) {
        return notExistLabelOrLabelHasAnyValue(name,Arrays.asList(value));
    }

    public boolean notExistLabelOrLabelHasAnyValue(String name, Collection<String> values) {
       return find(name).map(l-> CollectionUtils.isEmpty(values)||l.anyMatch(values::contains)).orElse(true);
    }
    public Optional<Label> find(String name) {
        return Optional.ofNullable(this.items.get(name));
    }

    public Optional<Label> find(String name, String value) {
        return find(name).filter(l -> l.contain(value));
    }

    public void find(String name, Consumer<Label> consumer) {
        find(name).ifPresent(consumer);
    }
    public List<Label> prefix(String prefix){
        return this.items.entrySet()
                .stream()
                .filter(e->e.getKey().startsWith(prefix))
                .map(Map.Entry::getValue).collect(Collectors.toList());
    }
    public boolean hasPrefix(String prefix){
        return prefix(prefix).size()>0;
    }
    public Optional<Label> rmLabel(String name) {
        return Optional.ofNullable(items.remove(name));
    }

    public Optional<List<Label>> rmLabel(Collection<String> names) {
        return Optional.of(
                names.stream()
                        .map(this::rmLabel)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
    }

    public Optional<Label> rm(String name, String value) {
        return find(name).filter(l -> {
            l.remove(value);
            return true;
        });
    }

    public Optional<Label> rm(String name, String... value) {
        return rm(name,Arrays.asList(value));
    }

    public Optional<Label> rm(String name, Collection<String> value) {
        return find(name).filter(l -> {
            l.remove(value);
            return true;
        });
    }

    public boolean rm(String name) {
        return Optional.ofNullable(this.items.remove(name)).isPresent();

    }

    public Optional<Label> rm(Label label) {
        return rm(label.getName(), label.getValue());
    }
}
