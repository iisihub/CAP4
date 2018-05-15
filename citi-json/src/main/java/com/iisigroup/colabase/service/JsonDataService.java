package com.iisigroup.colabase.service;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.model.RequestContent;

import java.util.Map;

/**
 * Created by VALLA on 2018/5/14.
 */
public interface JsonDataService {

    <T extends RequestContent> void setParamToJsonContent(T requestContent, String fieldName, String value);

    <T extends RequestContent> void cleanJsonObjectData(T reqInstance);

    <T extends RequestContent> void setDefaultValue(T reqInstance, Map<String, String> noSendList);

    <T extends RequestContent> void removeUnnecessaryNode(T requestContent);

    JsonObject getJsonStr(RequestContent requestContent);

}
