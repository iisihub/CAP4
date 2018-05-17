package com.iisigroup.colabase.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.iisigroup.colabase.annotation.JsonTemp;
import com.iisigroup.colabase.model.JsonAbstract;
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

    static <T extends JsonAbstract> T getInstance(Class<T> requestClass, Object... objects) throws
            NoSuchFieldException, IllegalAccessException{
        T instance = getInstance(requestClass);
        for (Object object : objects) {
            Field field = getSameClassField(instance.getClass(), object.getClass());
            if(field == null)
                continue;
            field.setAccessible(true);
            field.set(instance, object);
        }
        return instance;
    }

    private static <T extends JsonAbstract> Field getSameClassField(Class<T> mainClass, Class<?> setClass) {
        if(mainClass == JsonAbstract.class)
            return null;
        Field[] declaredFields = mainClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (setClass == field.getType()) {
                return field;
            } else {
                return getSameClassField(mainClass, setClass.getSuperclass());
            }
        }
        return getSameClassField((Class<T>) mainClass.getSuperclass(), setClass);
    }

    static <T extends JsonAbstract> T getInstance(Class<T> requestClass) throws NoSuchFieldException,
            IllegalAccessException {
        if(!checkJsonTemp(requestClass)) {
            throw new IllegalStateException("please check Request model defined jsonTemp with annotation @JsonTemp");
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
        }
        if ("getJsonString".equals(methodName)) {
            return JsonFactory.processNoSendField((JsonAbstract) object);
        }
        return methodProxy.invokeSuper(object, args);
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
