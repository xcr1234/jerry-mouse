package com.jerry.mouse.util;

import java.io.Serializable;
import java.util.*;

public class CaseInsensitiveMap<V> implements Map<String,V>,Serializable {

    private static final long serialVersionUID = -8269094973183168477L;
    private Map<StringIgnoreCase,V> map = new HashMap<StringIgnoreCase, V>();


    private static class StringIgnoreCase implements Serializable{

        private static final long serialVersionUID = -5199433063967504563L;
        private String value;

        StringIgnoreCase(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value == null ? 0 : value.toLowerCase().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null){
                return false;
            }
            if(!(obj instanceof StringIgnoreCase)){
                return false;
            }
            StringIgnoreCase stringIgnoreCase = (StringIgnoreCase)obj;
            return value == null ? stringIgnoreCase.value == null : value.equalsIgnoreCase(stringIgnoreCase.value);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(new StringIgnoreCase(key.toString()));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(new StringIgnoreCase(key.toString()));
    }

    @Override
    public V put(String key, V value) {
        return map.put(new StringIgnoreCase(key),value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(new StringIgnoreCase(key.toString()));
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for(Entry<? extends String,? extends V> entry : m.entrySet()){
            String key = entry.getKey();
            V value = entry.getValue();
            map.put(new StringIgnoreCase(key),value);
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return new KeySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet();
    }

    class EntrySet extends AbstractSet<Entry<String,V>>{

        @Override
        public Iterator<Entry<String, V>> iterator() {
            return new Iterator<Entry<String, V>>() {

                private Iterator<Entry<StringIgnoreCase,V>> iterator = map.entrySet().iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Entry<String, V> next() {
                    Entry<StringIgnoreCase,V> entry = iterator.next();
                    return new AbstractMap.SimpleEntry<String, V>(entry.getKey().value,entry.getValue());
                }

                @Override
                public void remove() {
                    iterator.remove();
                }
            };
        }

        @Override
        public int size() {
            return map.size();
        }
    }

    class KeySet extends AbstractSet<String>{

        @Override
        public boolean contains(Object o) {
            return map.containsKey(new StringIgnoreCase(o.toString()));
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {

                private Iterator<Entry<StringIgnoreCase,V>> iterator = map.entrySet().iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public String next() {
                    return iterator.next().getKey().value;
                }

                @Override
                public void remove() {
                    iterator.remove();
                }
            };
        }

        @Override
        public int size() {
            return map.size();
        }
    }
}
