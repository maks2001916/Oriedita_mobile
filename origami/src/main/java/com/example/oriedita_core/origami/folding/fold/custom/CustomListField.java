package com.example.oriedita_core.origami.folding.fold.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomListField<T, V extends Enum<V>> {
    private final Class<V> names;
    private final String namespace;
    private final Adapter<Map<V, Object>, T> factory;

    public CustomListField(String namespace, Class<V> names, Adapter<Map<V, Object>, T> factory) {
        this.names = names;
        this.namespace = namespace;
        this.factory = factory;
    }

    public List<T> getValue(Map<String, Object> customMap) {
        Map<V, List<?>> vals = new HashMap();
        Enum[] var3 = this.names.getEnumConstants();
        int var4 = var3.length;

        int i;
        for(i = 0; i < var4; ++i) {
            V name = (V) var3[i];
            vals.put(name, (List)customMap.get(this.getKey(name)));
        }

        int size = 0;
        Iterator var13 = vals.values().iterator();

        while(var13.hasNext()) {
            List<?> val = (List)var13.next();
            if (val != null && val.size() > size) {
                size = val.size();
            }
        }

        List<T> out = new ArrayList();

        for(i = 0; i < size; ++i) {
            Map<V, Object> constructorMap = new HashMap();
            Iterator var7 = vals.keySet().iterator();

            while(var7.hasNext()) {
                V v = (V) var7.next();
                List<?> val = vals.get(v);
                if (val != null) {
                    Object value = vals.get(v).size() > i ? ((List)vals.get(v)).get(i) : null;
                    constructorMap.put(v, value);
                }
            }

            T apply = null;

            try {
                apply = this.factory.convert(constructorMap, (T) null);
            } catch (Exception var11) {
            }

            if (apply != null) {
                out.add(apply);
            }
        }

        return out;
    }

    private String getKey(V name) {
        return this.namespace + ":" + name;
    }

    public void setValue(Map<String, Object> customMap, List<T> val) {
        Map<String, List<Object>> tempMap = new HashMap();
        Enum[] var4 = this.names.getEnumConstants();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            V v = (V) var4[var6];
            tempMap.put(this.getKey(v), new ArrayList());
        }

        Iterator var9 = val.iterator();

        while(var9.hasNext()) {
            T t = (T) var9.next();
            Map<V, Object> map = (Map)this.factory.convertBack(t, new HashMap());
            Iterator var13 = map.keySet().iterator();

            while(var13.hasNext()) {
                V v = (V) var13.next();
                tempMap.get(this.getKey(v)).add(map.get(v));
            }
        }

        var9 = tempMap.entrySet().iterator();

        while(var9.hasNext()) {
            Map.Entry<String, List<Object>> entry = (Map.Entry)var9.next();
            if (entry.getValue().size() > 0) {
                customMap.put(entry.getKey(), entry.getValue());
            } else {
                customMap.remove(entry.getKey());
            }
        }

    }
}

