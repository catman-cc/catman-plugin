package cc.catman.plugin.core.label;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用于为资源添加标签,value设计为集合的原因是为了提供更多的使用场景
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Label implements ILabelAbility{

    @Getter
    private Labels labels;

     private String name;

     private List<String> value;

    public static Label create(String name, String value) {

        return Label.builder()
                .name(name)
                .value(toList(value))
                .build();
    }

    public static Label create(String name, Object value) {
        return Label.builder()
                .name(name)
                .value(toList(value.toString()))
                .build();
    }

    public static Label create(String name, String... value) {
        return Label.builder()
                .name(name)
                .value(toList(value))
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
                .value(toList(e.getValue()))
                .build()).collect(Collectors.toList());
    }

    public static List<Label> fromMap(Map<String, Collection<String>> map) {
        return map.entrySet().stream().map(e -> Label.builder()
                .name(e.getKey())
                .value(new ArrayList<>(e.getValue()))
                .build()).collect(Collectors.toList());
    }

    public static List<Label> fromMapArray(Map<String, String[]> map) {
        return map.entrySet().stream().map(e -> Label.builder()
                .name(e.getKey())
                .value(toList(e.getValue()))
                .build()).collect(Collectors.toList());
    }

    public Label(String name) {
        this(name,Collections.emptyList());
    }

    public Label(String name,String ...value){
        this(name,Arrays.asList(value));
    }

    public Label(String name,Number ...vs){
        this(name, Arrays.stream(vs).map(Number::toString).collect(Collectors.toList()));
    }

    public Label(@NonNull String name, Collection<String> value){
        this.name=name;
        this.value=new ArrayList<>(value);
    }
    public Label add(Number ...ns){
        return add(Arrays.stream(ns).map(Number::toString).collect(Collectors.toList()));
    }

    public Label add(String ...vs){
        for (String v : vs) {
            if (notContain(v)){
                this.value.add(v);
            }
        }
        return this;
    }
    public Label add(Collection<String> vs){
        return this.add(vs.toArray(new String[]{}));
    }

    public  boolean replace(String oldValue,String newValue) {
        return replace(oldValue,newValue,false);
    }

    public boolean replace(String oldValue,String newValue,boolean check){
        boolean removed=this.value.remove(oldValue);
        if (check&&!removed){
            return false;
        }
        this.value.add(newValue);
        return removed;
    }

    // 不验证
    public void replace(Map<String,String> values){
        for (Map.Entry<String, String> entry : values.entrySet()) {
            this.replace(entry.getKey(),entry.getValue());
        }
    }

    public boolean remove(String ...v) {
       return remove(Arrays.asList(v));
    }

    public boolean remove(Predicate<String> filter) {
        return getValue().removeIf(filter);
    }

    public boolean remove(Collection<String> toDel){
        return getValue().removeAll(toDel);
    }


    public boolean existAndRemove(String v) {
        return getValue().remove(v);
    }

    public <T> T read(Function<Label,T> covert){
        return covert.apply(this);
    }

    public boolean empty() {
        return getValue().isEmpty();
    }

    public boolean notEmpty() {
        return !empty();
    }

    public boolean checkLength(int l){
        return getValue().size()==l;
    }
    public boolean one() {
        return checkLength(1);
    }

    public boolean multi() {
        return getValue().size() > 1;
    }

    /**
     * 是否包含指定的元素
     *
     * @param v 元素
     */
    public boolean contain(String v) {
        return getValue().contains(v);
    }

    public boolean contain(Collection<String> vs){
        return new HashSet<>(getValue()).containsAll(vs);
    }

    public boolean notContain(String v){
        return !contain(v);
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

    public boolean isTrue(){
        if (!one()) {
            return false;
        }
        String s = getValue().get(0);
        String ls = s.trim().toLowerCase();

        return ls.equals("true")||ls.equals("t")||ls.equals("1");
    }
    protected double diff(String value, Number target) {
        return new BigDecimal(value).subtract(BigDecimal.valueOf(target.doubleValue())).doubleValue();
    }

    protected static List<String> toList(String value){
        return toList(new String[]{value});
    }
    protected static List<String> toList(String[]value){
        return new ArrayList<>(Arrays.asList(value));
    }
}
