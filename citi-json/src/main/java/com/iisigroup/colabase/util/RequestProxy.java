package com.iisigroup.colabase.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.iisigroup.colabase.annotation.JsonTemp;
import com.iisigroup.colabase.model.RequestAbstract;
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
class RequestProxy implements MethodInterceptor {

    static {
        proxy = new RequestProxy();
    }

    private static final RequestProxy proxy;

    private RequestProxy() {

    }

    static <T extends RequestContent> T getInstance(Class<T> requestClass) throws NoSuchFieldException,
            IllegalAccessException {
        if(!checkJsonTemp(requestClass)) {
            throw new IllegalStateException("please check Request model defined jsonTemp with annotation @JsonTemp");
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(requestClass);
        enhancer.setCallback(proxy);
        T result = (T) enhancer.create();
        RequestFactory.initJsonObject(requestClass, result);
        return result;
    }


    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        String methodName = method.getName();
        if(methodName.contains("set")) {
            RequestFactory.setValueToJsonContent(object, method, args);
        }
        if ("getJsonString".equals(methodName)) {
            return RequestFactory.processNoSendField((RequestAbstract) object);
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
