package com.iisigroup.colabase.json.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iisigroup.colabase.json.annotation.ApiRequest;
import com.iisigroup.colabase.json.annotation.JsonTemp;
import com.iisigroup.colabase.json.model.JsonAbstract;
import com.iisigroup.colabase.json.service.JsonDataService;
import com.iisigroup.colabase.json.service.impl.JsonDataServiceImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


/**
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
public class JsonFactory {

    private static final String REQUEST_CONTENT_KEY = "requestContent";
    private static final String NO_SEND_LIST_KEY = "noSendList";
    private static final String PRIMARY_CLEAN_LIST = "primaryCleanList";
    private static final String ALL_PATH_MAP = "allPathMap";

    private static final JsonDataService jsonDataService;

    static {
        jsonDataService = new JsonDataServiceImpl();
    }

    private JsonFactory() {}

    /**
     * 產生JsonObject包裝物件的主要方法，傳入自定義ModelClass產生出配置好的JsonObject包裝物件
     * @param requestClass 一般Model物件class
     * @param <T>
     * @return JsonObject包裝物件
     */
    public static <T extends JsonAbstract> T getInstance(Class<T> requestClass) {
        T instance;
        try {
            instance = JsonProxy.getInstance(requestClass);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("init instance exception. warp exception: " + e.getClass());
        }
        return instance;
    }

    /**
     * 產生JsonObject包裝物件的主要方法，傳入自定義ModelClass產生出配置好的JsonObject包裝物件
     * @param requestClass 一般Model物件class
     * @param objects 於Model物件中需要額外注入的Object (@Autowired)
     * @param <T>
     * @return JsonObject包裝物件
     */
    public static <T extends JsonAbstract> T getInstance(Class<T> requestClass, Object... objects) {
        T instance;
        try {
            instance = JsonProxy.getInstance(requestClass, objects);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("init instance exception. warp exception: " + e.getClass());
        }
        return instance;
    }

    /**
     * process to init jsonObject then clean, set default value and copy default array
     * @param requestClass original class from user
     * @param instance after proxy object
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    static <T extends JsonAbstract> void initJsonObject(Class<T> requestClass, T instance) throws NoSuchFieldException, IllegalAccessException {
        Field jsonObjField = JsonAbstract.class.getDeclaredField(REQUEST_CONTENT_KEY);
        jsonObjField.setAccessible(true);
        Field jsonStrField = null;
        for (Field field : requestClass.getDeclaredFields()) {
            JsonTemp annotation = field.getAnnotation(JsonTemp.class);
            if (annotation != null) {
                jsonStrField = field;
                break;
            }
        }
        if (jsonStrField == null)
            throw new IllegalStateException("please check ApiRequest model defined jsonTemp with annotation @JsonTemp");
        jsonStrField.setAccessible(true);
        //load from config
        Object jsonStr = getJsonStrFromField(jsonStrField, instance);
        Gson gson = new Gson();
        //init jsonObject
        JsonObject jsonObject = gson.fromJson(String.valueOf(jsonStr), JsonObject.class);
        jsonObjField.set(instance, jsonObject);

        // clean jsonObject original value and get all json attribute paths
        List<String> allPathList = new ArrayList<>();
        jsonDataService.cleanJsonObjectDataAndGetAllPath(instance, allPathList);

        Map<String, String> valueMap = new HashMap<>();
        List<String> noSendList = (List<String>)getObjectFromInstance(instance, NO_SEND_LIST_KEY);
        List<String> primaryCleanList = (List<String>)getObjectFromInstance(instance, PRIMARY_CLEAN_LIST);
        Map<String, String> allPathMap = (Map<String, String>) getObjectFromInstance(instance, ALL_PATH_MAP);

        // get some info when field had @ApiRequest
        List<String> allPathListForMapping = new ArrayList<>(allPathList);
        setApiRequstInfo(valueMap, noSendList, allPathListForMapping, primaryCleanList, allPathMap, instance.getClass());
        // set default value to it
        jsonDataService.setDefaultValue(instance, valueMap);
        // set default array copy to map
        jsonDataService.copyDefaultArrayObject(instance, allPathList);

    }

    private static String getJsonStrFromField(Field field, Object instance) throws IllegalAccessException {
        JsonTemp annotation = field.getAnnotation(JsonTemp.class);
        String location = annotation.location();
        if("".equals(location)) {
            return (String) field.get(instance);
        }
        return JsonFileUtil.loadFileFromConfigPath(location);
    }


    private static <T extends JsonAbstract> Object getObjectFromInstance(T instance, String fieldName) {
        try {
            Field objectField = JsonAbstract.class.getDeclaredField(fieldName);
            objectField.setAccessible(true);
            return objectField.get(instance);
        } catch (NoSuchFieldException |IllegalAccessException e) {
            throw new IllegalArgumentException("instance provide did not have list field");
        }
    }

    private static void setApiRequstInfo(Map<String, String> valueMap, List<String> noVnoSLinst, List<String> allPathList, List<String> primaryCleanList, Map<String, String> allPathMap, Class<?> tClass) {
        if(tClass == JsonAbstract.class)
            return;

        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            ApiRequest annotation = field.getAnnotation(ApiRequest.class);
            if (annotation == null) {
                continue;
            }
            mappingFieldPath(field, allPathList, allPathMap);
            String path = allPathMap.get(field.getName());
            if("".equals(path))
                throw new IllegalStateException("path could not be empty in annotation, issue field: " + field.getName());
            String defValue = annotation.defaultValue();
            boolean isNoSend = annotation.noValueNoSend();
            boolean isPrimaryClean = annotation.primaryEmptyClean();
            valueMap.put(field.getName(), defValue);
            if(isNoSend)
                noVnoSLinst.add(path);
            if(isPrimaryClean)
                primaryCleanList.add(path);
        }
        setApiRequstInfo(valueMap, noVnoSLinst, allPathList, primaryCleanList, allPathMap, tClass.getSuperclass());
    }

    /**
     * 針對之前存的all path list去mapping 找出該屬性是要用哪個path
     * @param field model had @ApiRequest field
     * @param allPathList all json attr path
     * @param allPathMap mapping map for each field
     */
    private static void mappingFieldPath(Field field, List<String> allPathList, Map<String, String> allPathMap) {
        ApiRequest annotation = field.getAnnotation(ApiRequest.class);
        String fieldName = field.getName();
        String fieldPath = annotation.path();
        String keyword;
        if ("".equals(fieldPath)) {
            keyword = fieldName;
        } else {
            keyword = fieldPath;
        }
        Iterator<String> iterator = allPathList.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (keyword.equals(path)) {
                if (allPathMap.get(fieldName) != null)
                    throw new IllegalStateException("json object model field name: " + fieldName + " duplicate, may cause some issue. ");
                allPathMap.put(fieldName, path);
                allPathList.remove(path);
                return;
            } else {
                String pathCheck = path.substring(path.lastIndexOf(".") + 1);
                if (keyword.equals(pathCheck)) {
                    allPathMap.put(fieldName, path);
                    allPathList.remove(path);
                    return;
                }
            }
        }

        if (allPathMap.get(fieldName) == null)
            throw new IllegalStateException("field name: " + fieldName + " ,had found no path for map.");
    }


    static void setValueToJsonContent(Object object, Method method, Object[] args) {
        String value = String.valueOf(args.length > 0 ? args[0] : "");
        String methodName = method.getName();
        String fieldName = methodNameToFieldName(methodName);
        jsonDataService.setParamToJsonContent((JsonAbstract) object, fieldName, value);
    }

    private static String methodNameToFieldName(String methodName) {
        String result = "";
        result += methodName.substring(3, 4).toLowerCase();
        result += methodName.substring(4);
        return result;
    }

    static String processNoSendField(JsonAbstract instance) {
        return jsonDataService.removeUnnecessaryNode(instance).toString();
    }
}
