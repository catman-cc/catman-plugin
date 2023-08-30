package cc.catman.plugin.label;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用于为资源添加标签,value设计为集合的原因是为了提供更多的使用场景
 */
@Data
@Builder
public class Label {
    @NonNull
    final private String name;
    @NonNull
    final private List<String> value;

    public static Label create(String name, String value) {
        return Label.builder()
                .name(name)
                .value(Collections.singletonList(value))
                .build();
    }

    public static Label create(String name, String... value) {
        return Label.builder()
                .name(name)
                .value(Arrays.asList(value))
                .build();
    }

    public static Label create(String name, List<String> value) {
        return Label.builder()
                .name(name)
                .value(value)
                .build();
    }

    public static List<Label> create(Map<String, String> map) {
        return map.entrySet().stream().map(e -> Label.builder()
                .name(e.getKey())
                .value(Collections.singletonList(e.getValue()))
                .build()).collect(Collectors.toList());
    }

    public static List<Label> fromMap(Map<String, Collection<String>> map) {
        return map.entrySet().stream().map(e -> Label.builder()
                .name(e.getKey())
                .value(new ArrayList<>(e.getValue()))
                .build()).collect(Collectors.toList());
    }

    public static List<Label> fromArray(Map<String, String[]> map) {
        return map.entrySet().stream().map(e -> Label.builder()
                .name(e.getKey())
                .value(Arrays.asList(e.getValue()))
                .build()).collect(Collectors.toList());
    }

    public boolean none() {
        return getValue().isEmpty();
    }

    public boolean notEmpty() {
        return !none();
    }

    public boolean one() {
        return 1 == getValue().size();
    }

    public boolean multi() {
        return getValue().size() > 1;
    }

    public boolean existAndRemove(String v) {
        return getValue().remove(v);
    }

    /**
     * 是否包含指定的元素
     *
     * @param v 元素
     */
    public boolean contain(String v) {
        return getValue().contains(v);
    }

    public boolean exist(String v) {
        return contain(v);
    }

    public boolean anyMatch(Predicate<String> filter) {
        return getValue().stream().anyMatch(filter);
    }

    public boolean anyMatch(String regex) {
        Pattern reg = Pattern.compile(regex);
        return anyMatch(v -> reg.matcher(v).matches());
    }

    public Label rm(String v) {
        getValue().remove(v);
        return this;
    }

    public Label rm(Predicate<String> filter) {
        getValue().removeIf(filter);
        return this;
    }


    public boolean eq(String v) {
        return one() && contain(v);
    }

    public boolean eq(Number v) {
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        return diff(s, v) == 0;
    }

    public boolean gt(Number v) {
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        return diff(s, v) > 0;
    }

    public boolean ge(Number v) {
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        return diff(s, v) >= 0;
    }

    public boolean lt(Number v) {
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        return diff(s, v) < 0;
    }

    public boolean le(Number v) {
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        return diff(s, v) <= 0;
    }

    public boolean sum(Number v){
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        value.clear();
        value.add(new BigDecimal(s).add(BigDecimal.valueOf(v.doubleValue())).toString());
        return true;
    }

    public boolean sub(Number v){
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        value.clear();
        value.add(new BigDecimal(s).subtract(BigDecimal.valueOf(v.doubleValue())).toString());
        return true;
    }
    protected double diff(String value, Number target) {
        return new BigDecimal(value).subtract(BigDecimal.valueOf(target.doubleValue())).doubleValue();
    }
}
