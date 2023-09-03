package cc.catman.plugin.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dynamic implements Map<String,Object> {
    private final HashMap<String,Object> __trustee=new HashMap<>();

    @Override
    public int size() {
        return __trustee.size();
    }

    @Override
    public boolean isEmpty() {
        return __trustee.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return __trustee.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return __trustee.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return __trustee.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return __trustee.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return __trustee.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        __trustee.putAll(m);
    }

    @Override
    public void clear() {
        __trustee.clear();
    }

    @Override
    public Set<String> keySet() {
        return __trustee.keySet();
    }

    @Override
    public Collection<Object> values() {
        return __trustee.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return __trustee.entrySet();
    }
}
