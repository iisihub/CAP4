package com.iisigroup.colabase.common.monitor.proxy;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jndi.JndiObjectFactoryBean;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2019/4/9 AndyChen,new
 *          </ul>
 * @since 2019/4/9
 */
public class MonitorJndiFactoryBean extends JndiObjectFactoryBean {

    @Override
    protected Object lookup() throws NamingException {
        return new MonitorDataSource((DataSource) super.lookup());
    }
}
