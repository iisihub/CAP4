package com.iisigroup.colabase.common.jsp;

import com.iisigroup.cap.utils.CapString;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2018/11/8 AndyChen,new
 * </ul>
 * @since 2018/11/8
 */
public class ColaSysDbAndConfigProps extends COLASystemProperties implements InitializingBean {

    private Properties properties = new Properties();

    public ColaSysDbAndConfigProps() {
    }

    public ColaSysDbAndConfigProps(Properties properties) throws IOException {
        this.properties = properties;
        Enumeration<?> names = properties.propertyNames();
        while (names.hasMoreElements()) {
            String name = String.valueOf(names.nextElement());
            String property = properties.getProperty(name);
            if(CapString.isEmpty(property))
                property = "";
            this.put(name, property);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
