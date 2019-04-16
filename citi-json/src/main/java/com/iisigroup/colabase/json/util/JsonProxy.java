package com.iisigroup.colabase.json.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;

import com.iisigroup.colabase.json.annotation.JsonTemp;
import com.iisigroup.colabase.json.model.JsonAbstract;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
class JsonProxy implements MethodInterceptor {

    static {
        proxy = new JsonProxy();
    }

    private static final JsonProxy proxy;

    private JsonProxy() {

    }

    static <T extends JsonAbstract> T getInstance(Class<T> requestClass, Object... objects) throws NoSuchFieldException, IllegalAccessException{
        T instance = getInstance(requestClass);
        for (Object object : objects) {
            Field field = getSameClassField(instance.getClass(), object.getClass());
            if(field == null) {
                throw new IllegalArgumentException("argument object: " + object.getClass() + ", can not found field to set");
            }
            field.setAccessible(true);
            field.set(instance, object);
        }
        return instance;
    }

    private static <T extends JsonAbstract> Field getSameClassField(Class<T> mainClass, Class<?> setClass) {
        if(mainClass == JsonAbstract.class || setClass == Object.class)
            return null;
        Field[] declaredFields = mainClass.getDeclaredFields();
        for (Field field : declaredFields) {
            Autowired annotation = field.getAnnotation(Autowired.class);
            if(annotation == null)
                continue;
            Class<?> fieldType = field.getType();
            if (fieldType == setClass) { //check self class with setClass
                return field;
            } else {
                // check setClass's interfaces
                Class<?>[] interfaces = setClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    if(fieldType.isAssignableFrom(anInterface)) {
                        return field;
                    }
                }
                // check setClass father class
                Field classField = getSameClassField(mainClass, setClass.getSuperclass());
                if(classField != null)
                    return classField;
            }
        }
        return getSameClassField((Class<T>) mainClass.getSuperclass(), setClass);
    }

    static <T extends JsonAbstract> T getInstance(Class<T> requestClass) throws NoSuchFieldException,
            IllegalAccessException {
        if(!checkJsonTemp(requestClass)) {
            throw new IllegalStateException("please check ApiRequest model defined jsonTemp with annotation @JsonTemp");
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(requestClass);
        enhancer.setCallback(proxy);
        T result = (T) enhancer.create();
        JsonFactory.initJsonObject(requestClass, result);
        return result;
    }


    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        String methodName = method.getName();
        if(methodName.contains("set")) {
            JsonFactory.setValueToJsonContent(object, method, args);
            this.removeCache(object);
        }
        if ("getJsonString".equals(methodName)) {
            return this.readCache(object);
        }
        return methodProxy.invokeSuper(object, args);
    }

    private String readCache(Object instance) {
        try {
            Field field = JsonAbstract.class.getDeclaredField("jsonStrCache");
            field.setAccessible(true);
            String value = (String)field.get(instance);
            if("".equals(value)) {
                String jsonStr = JsonFactory.processNoSendField((JsonAbstract) instance);
                field.set(instance, jsonStr);
                return jsonStr;
            } else {
                return value;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return JsonFactory.processNoSendField((JsonAbstract) instance);
        }
    }

    private void removeCache(Object instance) {
        try {
            Field field = JsonAbstract.class.getDeclaredField("jsonStrCache");
            field.setAccessible(true);
            field.set(instance, "");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // do notthing
        }
    }

    private static <T> boolean checkJsonTemp(Class<T> objClass) {
        for (Field field : objClass.getDeclaredFields()) {
            JsonTemp annotation = field.getAnnotation(JsonTemp.class);
            if(annotation != null)
                return true;
        }
        return false;
    }


}
