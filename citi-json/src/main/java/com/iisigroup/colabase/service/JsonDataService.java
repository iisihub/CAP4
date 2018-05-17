package com.iisigroup.colabase.service;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.model.RequestAbstract;

import java.util.List;
import java.util.Map;

/**
 * Created by AndyChen on 2018/5/14.
 */
public interface JsonDataService {

    void setParamToJsonContent(RequestAbstract requestContent, String fieldName, String value);

    void cleanJsonObjectData(RequestAbstract reqInstance);

    void setDefaultValue(RequestAbstract reqInstance, Map<String, String> valueMap);

    void copyDefaultArrayObject(RequestAbstract reqInstance, List<String> allPathList);

    JsonObject removeUnnecessaryNode(RequestAbstract requestContent);

    JsonObject getJsonStr(RequestAbstract requestContent);

}
