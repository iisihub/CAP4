package com.iisigroup.colabase.json.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/4/9 AndyChen,new
 *          </ul>
 * @since 2018/4/9
 */
public abstract class JsonAbstract implements Json {

    /**
     * 請求內容JsonObject型態表示
     */
    private JsonObject requestContent;

    /**  !!! do not use this map yourself */
    private Map<String, Object> arrayMap = new HashMap<>();

    /**  !!! do not use this map yourself */
    private Map<String, Object> allPathMap = new HashMap<>();

    /**  !!! do not use this list yourself */
    private List<String> noSendList = new ArrayList<>();

    /**  !!! do not use this list yourself */
    private List<String> primaryCleanList = new ArrayList<>();

    /** !!! do not use this string yourself */
    private String jsonStrCache = "";

    /**
     * !!! 取json字串時使用本方法
     * 實作no value no send的功能
     * @return 去掉no value no send 的字串
     */
    @Override
    public String getJsonString() {
        // this return will never reach cause proxy
        throw new IllegalStateException("something wrong, proxy did not catch this method.");
    }

    @Override
    public JsonObject getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(JsonObject requestContent) {
        this.requestContent = requestContent;
    }

    @Override
    public String toString() {
        return "arrayMap: " + arrayMap.toString() + "\n" +
                "noSendList: " + noSendList.toString() + "\n" +
                "primaryCleanList: " + primaryCleanList.toString() + "\n" +
                "jsonStrCache: " + jsonStrCache + "\n";
    }
}
