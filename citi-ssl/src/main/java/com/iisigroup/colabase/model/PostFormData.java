package com.iisigroup.colabase.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2018/9/27 AndyChen,new
 * </ul>
 * @since 2018/9/27
 */
public abstract class PostFormData extends RequestContent {

    public PostFormData() {
        super();
        Map<String, List<String>> requestHeaders = getRequestHeaders();
        //加入post form要用的header
        ArrayList<String> list = new ArrayList<>();
        list.add("application/x-www-form-urlencoded");
        requestHeaders.put("Content-Type", list);
    }

    private Map<String, String> dataMap = new LinkedHashMap<>();

    public void putData(String key, String value) {
        dataMap.put(key, value);
    }

    public String getData(String key) {
        return dataMap.get(key);
    }

    public void removeData(String key) {
        dataMap.remove(key);
    }

    public Map<String, String> getDataMap() {
        return this.dataMap;
    }

}
