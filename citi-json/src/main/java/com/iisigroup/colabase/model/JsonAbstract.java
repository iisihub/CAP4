package com.iisigroup.colabase.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**  !!! do not use this list yourself */
    private List<String> noSendList = new ArrayList<>();

    /**
     * !!! 取json字串時使用本方法
     * 實作no value no send的功能
     * @return 去掉no value no send 的字串
     */
    @Override
    public String getJsonString() {
        return requestContent.toString();
    }

    @Override
    public JsonObject getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(JsonObject requestContent) {
        this.requestContent = requestContent;
    }
}
