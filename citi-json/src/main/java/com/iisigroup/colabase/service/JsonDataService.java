package com.iisigroup.colabase.service;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.model.JsonAbstract;

import java.util.List;
import java.util.Map;

/**
 * Created by AndyChen on 2018/5/14.
 */
public interface JsonDataService {

    /**
     * 將指定的值置入到指定的Json物件中
     * @param requestContent 封裝JsonObject的物件
     * @param fieldName json key
     * @param value json value
     */
    void setParamToJsonContent(JsonAbstract requestContent, String fieldName, String value);

    /**
     * 清空JsonObject內既有的值，保持個元素都是空值
     * 同時獲取各屬性路徑
     * @param reqInstance instance
     * @param allPathList store all attribute path
     */
    void cleanJsonObjectDataAndGetAllPath(JsonAbstract reqInstance, List<String> allPathList);


    /**
     * 針對傳入的JsonObject包裝類，將傳入的值對進行匹配，若目前JsonObject對應的值為空時，進行填值。
     * @param reqInstance 封裝JsonObject的物件
     * @param valueMap 元素值對
     */
    void setDefaultValue(JsonAbstract reqInstance, Map<String, String> valueMap);

    /**
     * set default array copy to map
     * @param reqInstance 封裝JsonObject的物件
     * @param allPathList
     */
    void copyDefaultArrayObject(JsonAbstract reqInstance, List<String> allPathList);

    /**
     * 根據提供的物件，取出noSendList，並移除該list所指定的路徑其值為空的元素
     * @param requestContent instance
     */
    JsonObject removeUnnecessaryNode(JsonAbstract requestContent);

}
