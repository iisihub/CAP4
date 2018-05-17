package com.iisigroup.colabase.service;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.model.JsonAbstract;

import java.util.List;
import java.util.Map;

/**
 * Created by AndyChen on 2018/5/14.
 */
public interface JsonDataService {

    void setParamToJsonContent(JsonAbstract requestContent, String fieldName, String value);

    void cleanJsonObjectData(JsonAbstract reqInstance);

    void setDefaultValue(JsonAbstract reqInstance, Map<String, String> valueMap);

    void copyDefaultArrayObject(JsonAbstract reqInstance, List<String> allPathList);

    JsonObject removeUnnecessaryNode(JsonAbstract requestContent);

    JsonObject getJsonStr(JsonAbstract requestContent);

}
