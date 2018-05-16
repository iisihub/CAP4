package com.iisigroup.colabase.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iisigroup.colabase.annotation.ApiRequest;
import com.iisigroup.colabase.annotation.JsonTemp;
import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.service.JsonDataService;
import com.iisigroup.colabase.service.impl.JsonDataServiceImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.objects.NativeString.substring;

/**
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
public class RequestFactory {

    private static final String REQUEST_CONTENT_KEY = "requestContent";
    private static final String NO_SEND_LIST_KEY = "noSendList";
    private static final String ALL_PATH_LIST_KEY = "allPathList";

    static {
        jsonDataService = new JsonDataServiceImpl();
    }

    private static final JsonDataService jsonDataService;

    private RequestFactory() {

    }

    public static <T extends RequestContent> T getInstance(Class<T> requestClass) {
        T instance;
        try {
            instance = RequestProxy.getInstance(requestClass);
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
    static <T extends RequestContent> void initJsonObject(Class<T> requestClass, T instance) throws NoSuchFieldException, IllegalAccessException {
        Field jsonObjField = RequestContent.class.getDeclaredField(REQUEST_CONTENT_KEY);
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
            throw new IllegalStateException("please check Request model defined jsonTemp with annotation @JsonTemp");
        jsonStrField.setAccessible(true);
        Object jsonStr = jsonStrField.get(instance);
        Gson gson = new Gson();
        //init jsonObject
        JsonObject jsonObject = gson.fromJson(String.valueOf(jsonStr), JsonObject.class);
        jsonObjField.set(instance, jsonObject);

        Map<String, String> valueMap = new HashMap<>();
        List<String> noSendList = getListToInstance(instance);
        List<String> allPathList = new ArrayList<>();
        setApiRequstInfo(valueMap, noSendList, allPathList, instance.getClass());

        // clean current jsonObject
        jsonDataService.cleanJsonObjectData(instance);
        // set default value to it
        jsonDataService.setDefaultValue(instance, valueMap);
        //TODO set default array copy to map
        jsonDataService.copyDefaultArrayObject(instance, allPathList);

    }

    private static <T extends RequestContent> List<String> getListToInstance(T instance) {
        try {
            Field noSendList = RequestContent.class.getDeclaredField(NO_SEND_LIST_KEY);
            noSendList.setAccessible(true);
            return (List<String>) noSendList.get(instance);
        } catch (NoSuchFieldException |IllegalAccessException e) {
            throw new IllegalArgumentException("instance provide did not have list field");
        }
    }

    private static void setApiRequstInfo(Map<String, String> valueMap, List<String> noVnoSLinst, List<String> allPathList, Class<?> tClass) {
        if(tClass == RequestContent.class)
            return;

        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            ApiRequest annotation = field.getAnnotation(ApiRequest.class);
            if (annotation == null) {
                continue;
            }
            String path = annotation.path();
            if("".equals(path))
                throw new IllegalStateException("path could not be empty in annotation, issue field: " + field.getName());
            allPathList.add(path);
            String defValue = annotation.defaultValue();
            boolean isNoSend = annotation.noValueNoSend();
            valueMap.put(field.getName(), defValue);
            if(isNoSend)
                noVnoSLinst.add(path);
        }
        setApiRequstInfo(valueMap, noVnoSLinst, allPathList, tClass.getSuperclass());
    }


    static void setValueToJsonContent(Object object, Method method, Object[] args) {
        String value = String.valueOf(args.length > 0 ? args[0] : "");
        String methodName = method.getName();
        String fieldName = methodNameToFieldName(methodName);
        jsonDataService.setParamToJsonContent((RequestContent) object, fieldName, value);
    }

    public static Object getFieldObject(Object requestObj, String fieldName) throws NoSuchFieldException,
            IllegalAccessException {
        Class superclass = RequestContent.class;
            Field reqContentField = superclass.getDeclaredField(fieldName);
            reqContentField.setAccessible(true);
            return reqContentField.get(requestObj);
    }

    private static String methodNameToFieldName(String methodName) {
        String result = "";
        result += methodName.substring(3, 4).toLowerCase();
        result += methodName.substring(4);
        return result;
    }
}
