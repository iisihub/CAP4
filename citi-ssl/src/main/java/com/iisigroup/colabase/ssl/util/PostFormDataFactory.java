package com.iisigroup.colabase.ssl.util;

import com.iisigroup.colabase.ssl.model.PostFormData;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2018/9/28 AndyChen,new
 * </ul>
 * @since 2018/9/28
 */
public class PostFormDataFactory {

    private static PostFormDataFactory factory;

    static {
        factory = new PostFormDataFactory();
    }

    private PostFormDataFactory() {

    }


    /**
     * 取得繼承PostFormData實體，並且內部會掛上特定JsonProxy去改變JsonString
     * @param tClass 繼承PostFormData的class
     * @return 繼承PostFormData實體
     */
    public static <T extends PostFormData> T getInstance(Class<T> tClass) {
        return getInstance(tClass, new Object());
    }

    /**
     *
     * 取得繼承PostFormData實體，並且內部會掛上特定JsonProxy去改變JsonString
     * @param tClass 繼承PostFormData的class
     * @param objects 要產出的class中有標註@Autowired的field實體物件
     * @return 繼承PostFormData實體
     */
    public static <T extends PostFormData> T getInstance(Class<T> tClass, Object... objects) {
        T instance;
        try {
            Constructor<T> constructor = tClass.getConstructor();
            instance = constructor.newInstance();
            factory.setField(tClass, instance, objects);
        } catch (Exception e) {
            throw new IllegalStateException("Something going wrong! NOT able to create instance, e: " + e);
        }
        return instance;
    }


    private void setField(Class<?> mainClass, Object instance, Object... setObjects) throws IllegalAccessException {
        if(mainClass == Object.class)
            return;
        Field[] fields = mainClass.getDeclaredFields();
        for (Field field : fields) {
            Autowired annotation = field.getAnnotation(Autowired.class);
            boolean found = false;
            if(annotation != null) {
                for (int i = 0; !found && i < setObjects.length; i++) {
                    Class<?> fieldClass = field.getType();
                    Object setObj = setObjects[i];

                    if(fieldClass.isAssignableFrom(setObj.getClass())){
                        field.setAccessible(true);
                        field.set(instance, setObj);
                        found = true;
                    }
                }
                if(!found)
                    throw new IllegalArgumentException(field.getName() + " not found any obj for assign!");
            }
        }
        setField(mainClass.getSuperclass(), instance, setObjects);
    }
}
