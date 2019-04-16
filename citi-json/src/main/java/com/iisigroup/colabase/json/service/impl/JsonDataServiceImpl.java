package com.iisigroup.colabase.json.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.iisigroup.colabase.json.model.JsonAbstract;
import com.iisigroup.colabase.json.annotation.ApiRequest;
import com.iisigroup.colabase.json.service.JsonDataService;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/14 AndyChen,new
 *          </ul>
 * @since 2018/5/14
 */
public class JsonDataServiceImpl implements JsonDataService {

    private static final String ARRAY_MARK = "[]";
    private static final String LAST_PROCESS_KEY = "lastProcess";
    private static final String ARRAY_MAP_KEY = "arrayMap";
    private static final String NO_SEND_LIST_KEY = "noSendList";
    private static final String PRIMARY_CLEAN_LIST = "primaryCleanList";
    private static final String ALL_PATH_MAP = "allPathMap";
    private static final String PATH_SPLIT_MARK = "\\.";

    @Override
    public void setParamToJsonContent(JsonAbstract requestContent, String fieldName, String value) {
        Map<String, Object> arrayMap;
        Map<String, String> allPathMap;
        try {
            arrayMap =  (Map<String, Object>) getFieldObject(requestContent, ARRAY_MAP_KEY);
            allPathMap =  (Map<String, String>) getFieldObject(requestContent, ALL_PATH_MAP);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("can not found arrayMap in requestContent");
        }
        JsonObject requestJson = requestContent.getRequestContent();
        String path = allPathMap.get(fieldName);
        if (path == null)
            return;
        String[] paths = path.split(PATH_SPLIT_MARK);
        this.countArray(path, arrayMap);
        JsonObject jsonElement = this.getJsonElement(requestJson, path, arrayMap);
        jsonElement.addProperty(paths[paths.length - 1], value);

    }

    private Object getFieldObject(Object requestObj, String fieldName) throws NoSuchFieldException,
        IllegalAccessException {
        Class superclass = JsonAbstract.class;
        Field reqContentField = superclass.getDeclaredField(fieldName);
        reqContentField.setAccessible(true);
        return reqContentField.get(requestObj);
    }

    /**
     * count set field times
     * 當array路徑次數加一時，底下的array節點必須歸零次數，避免產生多於空元素
     * @param path path chain
     * @param arrayMap count each setting method times
     */
    private void countArray(String path, Map<String, Object> arrayMap) {
        arrayMap.put(LAST_PROCESS_KEY, path);
        Object object = arrayMap.get(path);
        if(object == null) {
            arrayMap.put(path, 0);
        } else {
            arrayMap.put(path, (Integer)object + 1);
            //上層array擴增，底下所有的array計數要歸零
            String parentArrayName = path.substring(0, path.lastIndexOf('.'));
            parentArrayName = parentArrayName.replaceAll("\\[\\]", "\\\\[\\\\]"); //for next regExp string
            for (Map.Entry<String, Object> entry : arrayMap.entrySet()) {
                String regExpStr = parentArrayName + "\\..*\\[\\]\\..*";
                Pattern pattern = Pattern.compile(regExpStr);
                Matcher matcher = pattern.matcher(entry.getKey());
                if (matcher.matches()) {
                    try {
                        Integer.parseInt(String.valueOf(entry.getValue()));
                        arrayMap.put(entry.getKey(), null);
                    } catch (NumberFormatException e) {
                        //do nothing
                    }
                }
            }
        }
    }

    /**
     * 經由每次遞迴最後取出路徑chain中最後一個節點JsonObject物件. ex: person.company.name -> 取出company的jsonObject
     * 基線為路徑chain只有一個節點(表示為最後)
     * @param jsonObject 父節點
     * @param eleNameChain 路徑節點
     * @param arrayMap 紀錄設值次數map
     * @return 包含最後一個節點的jsonObject
     */
    private JsonObject getJsonElement(JsonObject jsonObject, String eleNameChain, Map<String, Object> arrayMap) {
        String[] paths = eleNameChain.split(PATH_SPLIT_MARK);
        String path = paths[0];
        if(path.contains(ARRAY_MARK) && paths.length > 1) {
            path = path.replace(ARRAY_MARK, "");
            String newPath = eleNameChain.substring(eleNameChain.indexOf(ARRAY_MARK) + 3);
            jsonObject = this.getJsonArrayElement(jsonObject, path, arrayMap);
            return this.getJsonElement(jsonObject, newPath, arrayMap);
        }
        JsonElement jsonElement = jsonObject.get(path);
        if (jsonElement == null) { //check path是否有效
            throw new IllegalArgumentException("can not found element by path: " + path);
        }
        if(jsonElement instanceof JsonArray) {
            throw new IllegalStateException("eleNameChain: " + eleNameChain + " is wrong! Can not get JsonObject.");
        }
        if(paths.length == 1) {
            return jsonObject;
        }
        String newPath = eleNameChain.substring(eleNameChain.indexOf('.') + 1);
        return this.getJsonElement((JsonObject) jsonElement, newPath, arrayMap);
    }

    /**
     * 取出Array裡的JsonObject，並判斷是否要新增一個JsonObject in array
     * @param jsonObject 當前JsonObject
     * @param arrayName array name in jsonObject
     * @param arrayMap 紀錄設值次數map
     * @return 所要用的JsonObject
     */
    private JsonObject getJsonArrayElement(JsonObject jsonObject, String arrayName, Map<String, Object> arrayMap) {
        JsonArray jsonArray = jsonObject.getAsJsonArray(arrayName);
        if(jsonArray == null)
            throw new IllegalArgumentException("can not found any json array by arrayName: " + arrayName);

        String lastProcess = (String)arrayMap.get(LAST_PROCESS_KEY);
        Integer count = (Integer) arrayMap.get(lastProcess);
        if(count == null) {
            count = 0;
        }
        String[] paths = lastProcess.split(PATH_SPLIT_MARK);

        //目標array
        String markPath = paths[paths.length - 2].replace(ARRAY_MARK, "");
        if(arrayName.equals(markPath)) {
            String arrayKey = arrayName + ARRAY_MARK;
            if(count == 0) {
                return (JsonObject) jsonArray.get(0);
            } else {
                if(jsonArray.size() - 1 == count)
                    return (JsonObject) jsonArray.get(count);
                JsonObject temp = (JsonObject) arrayMap.get(arrayKey);
                JsonElement element = (JsonElement) deepCopy(temp);
                jsonArray.add(element);
                return (JsonObject) element;
            }
        } else {
            //中途array
            return (JsonObject)jsonArray.get(jsonArray.size() - 1);
        }
    }

    private Object deepCopy(Object jsonElement) {
        if(jsonElement instanceof JsonPrimitive) {
            String value = ((JsonPrimitive) jsonElement).getAsString();
            return new JsonPrimitive(value);
        }
        if(jsonElement instanceof JsonArray) {
            JsonArray jsonArray = new JsonArray();
            for (JsonElement element : (JsonArray)jsonElement) {
                jsonArray.add((JsonElement) deepCopy(element));
            }
            return jsonArray;
        }
        JsonObject jsonObject = (JsonObject)jsonElement;
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            result.add(entry.getKey(), (JsonElement) deepCopy(entry.getValue()));
        }
        return result;
    }

    /**
     * get annotation mark on field, will lookup extends chain while to JsonAbstract.class(father class)
     * @param tClass target class
     * @param fieldName field name defined in child class
     * @return ApiRequest annotation
     */
    private <T extends JsonAbstract> ApiRequest getFieldAnnotation(Class<T> tClass, String fieldName) {
        Field field;
        try {
            field = tClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if(JsonAbstract.class == tClass)
                throw new IllegalArgumentException("any class extends JsonAbstract, not have this fieldName: " + fieldName);
            return getFieldAnnotation((Class<T>) tClass.getSuperclass(), fieldName);
        }
        return field.getAnnotation(ApiRequest.class);
    }


    @Override
    public void cleanJsonObjectDataAndGetAllPath(JsonAbstract reqInstance, List<String> allPathList) {
        this.cleanJsonObjectAndGetAllPath(reqInstance.getRequestContent(), allPathList, "");
    }

    private void cleanJsonObjectAndGetAllPath(JsonObject jsonObject, List<String> allPath, String lastPath) {
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            JsonElement value = entry.getValue();
            if(value instanceof JsonPrimitive) {
                jsonObject.add(entry.getKey(), new JsonPrimitive(""));
                allPath.add("".equals(lastPath) ? "" + entry.getKey() : lastPath + "." + entry.getKey());
            } else if(value instanceof JsonArray){
                for (JsonElement jsonElement : ((JsonArray) value)) {
                    if (jsonElement.isJsonNull()) {
                        throw new IllegalStateException("please check your jsonTemp string, can not clean origin value: " + value);
                    }
                    String newLastPath = "".equals(lastPath) ? ("" + entry.getKey() + "[]") : (lastPath + "." + entry.getKey() + "[]" );
                    this.cleanJsonObjectAndGetAllPath((JsonObject) jsonElement, allPath, newLastPath);
                }
            } else {
                String newLastPath = "".equals(lastPath) ? "" + entry.getKey() : lastPath + "." + entry.getKey();
                this.cleanJsonObjectAndGetAllPath((JsonObject) value, allPath, newLastPath);
            }
        }
    }

    @Override
    public void setDefaultValue(JsonAbstract reqInstance, Map<String, String> valueMap) {
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String value = entry.getValue().trim();
            if("".equals(value))
                continue;
            this.setParamToJsonContent(reqInstance, entry.getKey(), value);
        }
        //must clean arrayMap to empty
        Map<String, Object> arrayMap = getArrayMap(reqInstance);
        arrayMap.clear();
    }

    private Map<String, Object> getArrayMap(JsonAbstract instance) {
        try {
            Field field = JsonAbstract.class.getDeclaredField(ARRAY_MAP_KEY);
            field.setAccessible(true);
            return  (Map<String, Object>) field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("can not found array map while set default value");
        }
    }


    @Override
    public void copyDefaultArrayObject(JsonAbstract reqInstance, List<String> allPathList) {
        JsonObject requestContent = reqInstance.getRequestContent();

        Map<String, Object> arrayMap = this.getArrayMap(reqInstance);
        for (String path : allPathList) {
            if(!path.contains(ARRAY_MARK))
                continue;
            String[] paths = path.split(PATH_SPLIT_MARK);
            arrayMap.put(LAST_PROCESS_KEY, path);
            JsonObject jsonElement = this.getJsonElement(requestContent, path, arrayMap);
            Object copy = this.deepCopy(jsonElement);
            String arrayName = paths[paths.length - 2];
            arrayMap.put(arrayName, copy);
        }
        arrayMap.remove(LAST_PROCESS_KEY);
    }



    @Override
    public JsonObject removeUnnecessaryNode(JsonAbstract requestContent) {
        List<String> noSendList;
        List<String> primaryCleanList;
        try {
            Field noSendListField = JsonAbstract.class.getDeclaredField(NO_SEND_LIST_KEY);
            noSendListField.setAccessible(true);
            noSendList = (List<String>) noSendListField.get(requestContent);
            Field primaryCleanField = JsonAbstract.class.getDeclaredField(PRIMARY_CLEAN_LIST);
            primaryCleanField.setAccessible(true);
            primaryCleanList = (List<String>) primaryCleanField.get(requestContent);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("instance can not found field: noSendList object");
        }
        JsonObject jsonObject = (JsonObject)deepCopy(requestContent.getRequestContent());
        for (String path : noSendList) {
            this.cleanTargetJson(path, jsonObject);
        }
        for (String path : primaryCleanList) {
            this.cleanTargetJson(path, jsonObject, true);
        }
        //清除所有empty jsonObject
        this.cleanEmptyJsonObject(jsonObject);
        return jsonObject;
    }


    private void cleanTargetJson(String pathChain, JsonObject jsonObject) {
        this.cleanTargetJson(pathChain, jsonObject, false);
    }

    /**
     * 根據指定路徑去找出元素，並移除該值為空的元素
     * 若為主要必要元素，則同時清除同階層元素
     * @param pathChain path
     * @param jsonObject jsonObject
     * @param isPrimaryClean 是否為主要必要元素
     */
    private void cleanTargetJson(String pathChain, JsonObject jsonObject, boolean isPrimaryClean) {
        String[] paths = pathChain.split(PATH_SPLIT_MARK);
        String targetKey = paths[paths.length - 1];
        if(pathChain.contains(ARRAY_MARK)) {
            // Array 處理
            this.cleanArrayJson(pathChain, targetKey, jsonObject, isPrimaryClean);
        } else {
            HashMap<String, Object> map = new HashMap<>();
            JsonObject jsonElement = this.getJsonElement(jsonObject, pathChain, map);
            JsonElement checkEle = jsonElement.get(targetKey);
            if (checkEle != null && !"".equals(checkEle.getAsString()))
                return;
            if(isPrimaryClean) {
                removeAllSameLevelEle(jsonElement);
            } else {
                jsonElement.remove(targetKey);
            }
        }
    }

    private void cleanArrayJson(String pathChain, String targetKey, JsonObject jsonObject, boolean isPrimaryClean) {
        JsonArray jsonArray = new JsonArray();
        getTargetJsonArray(pathChain, jsonObject, jsonArray);
        for (JsonElement jsonElement : jsonArray) {
            JsonElement checkEle = ((JsonObject) jsonElement).get(targetKey);
            if (checkEle != null && !"".equals(checkEle.getAsString())) {
                continue;
            }
            if(isPrimaryClean) {
                removeAllSameLevelEle(jsonElement);
            } else {
                ((JsonObject)jsonElement).remove(targetKey);
            }
        }

    }

    private void removeAllSameLevelEle(JsonElement jsonElement) {
        Iterator<Map.Entry<String, JsonElement>> iterator = ((JsonObject) jsonElement).entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next(); //for moving index
            iterator.remove();
        }
    }

    /**
     * 取得指定路徑的所有JsonArray中的JsonObject
     * ex: phone[{name:"Ted"}, {name:"Mary"}] -> {name:"Ted"}, {name:"Mary"}
     * @param pathChain path
     * @param jsonObject jsonObject
     * @param jsonArray 儲存找到的JsonObject
     */
    private void getTargetJsonArray(String pathChain, JsonObject jsonObject, JsonArray jsonArray) {
        String[] paths = pathChain.split(PATH_SPLIT_MARK);

        if(paths[0].contains(ARRAY_MARK)) {
            String path = paths[0].replace(ARRAY_MARK, "");
            JsonArray elements = jsonObject.getAsJsonArray(path);
            if(paths.length == 2) {
                jsonArray.addAll(elements);
                return;
            }
            String newPath = pathChain.substring(pathChain.indexOf(ARRAY_MARK) + 3);
            for (JsonElement element : elements) {
                getTargetJsonArray(newPath, (JsonObject)element, jsonArray);
            }
        } else { //primitive type
            String newPath = pathChain.substring(pathChain.indexOf('.') + 1);
            JsonObject jsonElement = (JsonObject) jsonObject.get(paths[0]);
            getTargetJsonArray(newPath, jsonElement, jsonArray);
        }
    }

    /**
     * 掃描json結構，移除為空的元素
     * @param jsonElement suggest start from JsonObject
     */
    private void cleanEmptyJsonObject(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive())
            return;
        JsonObject jsonObject = (JsonObject) jsonElement;
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive())
                continue;
            if (value.isJsonArray()) { // JsonArray process
                JsonArray array = ((JsonArray) value);
                for (int i = 0; i < array.size(); i++) {
                    // 檢查jsonArray 內的元素，如果是jsonObject，則檢查是否為{}元素
                    JsonElement jsonEle = array.get(i);
                    if (jsonEle.isJsonObject() && ((JsonObject) jsonEle).entrySet().isEmpty()) {
                        array.remove(i);
                        i -= 1;
                        continue;
                    }
                    cleanEmptyJsonObject(jsonEle);
                }
                // 最後如果array變為空則把自己再刪掉
                if (array.size() == 0) {
                    iterator.remove();
                }
                continue;
            }
            int size = ((JsonObject) value).entrySet().size(); //檢查JsonObject內有無元素
            if (size > 0) {
                cleanEmptyJsonObject(value); //嘗試清除內部元素
                size = ((JsonObject) value).entrySet().size();
                if(size > 0) //清除完要再檢查自己是否為空
                    continue;
            }
            iterator.remove();
        }
    }
}
