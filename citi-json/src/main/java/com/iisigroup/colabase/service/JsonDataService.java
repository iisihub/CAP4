package com.iisigroup.colabase.service;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.model.RequestContent;

import java.util.List;
import java.util.Map;

/**
 * Created by AndyChen on 2018/5/14.
 */
public interface JsonDataService {

    void setParamToJsonContent(RequestContent requestContent, String fieldName, String value);

    void cleanJsonObjectData(RequestContent reqInstance);

    void setDefaultValue(RequestContent reqInstance, Map<String, String> valueMap);

    void copyDefaultArrayObject(RequestContent reqInstance, List<String> allPathList);

    JsonObject removeUnnecessaryNode(RequestContent requestContent);

    JsonObject getJsonStr(RequestContent requestContent);

}
