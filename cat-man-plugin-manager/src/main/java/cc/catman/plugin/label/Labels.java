package cc.catman.plugin.label;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 标签组
 */
public class Labels {

    final private List<Label> items;

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
        this.items = new ArrayList<>();
    }

    public Label add(String name, String value) {
        return add(Label.create(name, value));
    }
    public Label add(String name, Number value) {
        return add(Label.create(name, Double.toString(value.doubleValue())));
    }

    public Label add(String name, Object value) {
        // TODO maybe 转换成json.但是感觉把label搞得太复杂了
        return add(Label.create(name, value.toString()));
    }
    public Label add(Label label) {
        return pop(label.getName())
                .map(existLabel -> {
                    List<String> ev = existLabel.getValue();
                    label.getValue().forEach(v -> {
                        if (!ev.contains(v)) {
                            ev.add(v);
                        }
                    });
                    return existLabel;
                }).orElseGet(() -> {
                    items.add(label);
                    return label;
                });
    }

    public boolean eq(String name, String value) {
        return pop(name).map(label -> label.eq(value)).orElse(false);
    }

    public boolean eq(String name, Number value) {
        return pop(name).map(label -> label.eq(value)).orElse(false);
    }

    public boolean gt(String name, Number value) {
        return pop(name).map(label -> label.gt(value)).orElse(false);
    }

    public boolean ge(String name, Number value) {
        return pop(name).map(label -> label.ge(value)).orElse(false);
    }

    public boolean lt(String name, Number value) {
        return pop(name).map(label -> label.lt(value)).orElse(false);
    }

    public boolean le(String name, Number value) {
        return pop(name).map(label -> label.le(value)).orElse(false);
    }

    public boolean sum(String name,Number value){
        return  pop(name).map(label -> label.sum(value)).orElseGet(()->{
            add(name,value);
            return true;
        });
    }
    public boolean sub(String name,Number value){
        return  pop(name).map(label -> label.sub(value)).orElseGet(()->{
            add(name,-value.doubleValue());
            return true;
        });
    }



    public boolean exist(String name) {
        return items.stream().anyMatch(i -> i.getName().equals(name));
    }

    public boolean hasValue(String name) {
        return items.stream().anyMatch(i -> i.getName().equals(name) && i.notEmpty());
    }

    public boolean exist(String name, String value) {
        return items.stream().anyMatch(i -> i.getName().equals(name) && i.exist(value));
    }

    public boolean notExistLabelOrLabelHasValue(String name, String value) {
        return items.stream().filter(i -> i.getName().equals(name)).findFirst()
                .map(l -> l.exist(value))
                .orElse(true);
    }

    public Optional<Label> pop(String name) {
        return items.stream()
                .filter(i ->
                        i.getName().equals(name)
                )
                .findFirst();
    }

    public Optional<Label> pop(String name, String value) {
        return items.stream().filter(i -> i.getName().equals(name) && i.exist(value)).findFirst();
    }

    public Optional<Label> rm(String name, String value) {
        return items.stream().filter(i -> i.getName().equals(name) && i.exist(value)).findFirst();
    }

    public boolean rm(String name) {
        return items.stream().filter(i -> i.getName().equals(name))
                .findFirst().map(items::remove).orElse(false);

    }

    public boolean rm(Label label) {
        return items.stream().filter(i -> i.getName().equals(label.getName()))
                .findFirst()
                .map(l -> l.getValue().removeAll(label.getValue())).orElse(false);
    }


}
