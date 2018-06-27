/**
 *
 */
package com.iisigroup.colabase.webatm.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author TimChiang
 * @since
 *        <li>2015/10/22,TimChiang,new
 */
public class AWATMSystemDBConfig extends PropertyPlaceholderConfigurer implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(AWATMSystemDBConfig.class);

    private static final String DEFAULT_DATASOURCENAME = "cc-db";
    private static final String DEFAULT_DBTABLENAME = "AWATM_CFG_SYSPARM";
    private static final String DEFAULT_DBKEYCOLUMNNAME = "parmId";
    private static final String DEFAULT_DBVALUECOLUMNNAME = "parmValue";
    String dataSourceName;
    String dbTableName;
    String dbKeyColumnName;
    String dbValueColumnName;

    private Properties dbProps = new Properties();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        DataSource dataSource = (DataSource) beanFactory.getBean(getDataSourceName());
        // DbProperties dbProps = new DbProperties(dataSource);

        // dbProps.put("app.version", "v3");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Map<String, Object>> l = jdbcTemplate.queryForList("select " + DEFAULT_DBKEYCOLUMNNAME + ", " + DEFAULT_DBVALUECOLUMNNAME + " from " + DEFAULT_DBTABLENAME);
        for (Map<String, Object> m : l) {
            logger.debug("Loading from DB: [{}:{}]", m.get("parmId"), m.get("parmValue"));
            dbProps.put((m.get("parmId")).toString(), (m.get("parmValue")).toString());
        }
        setProperties(dbProps);
        super.postProcessBeanFactory(beanFactory);
    }

    public String getDataSourceName() {
        return dataSourceName == null ? DEFAULT_DATASOURCENAME : dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    // private Properties properties = new Properties();

    public AWATMSystemDBConfig() {
    }

    public AWATMSystemDBConfig(Properties properties) throws IOException {
        this.dbProps = properties;
    }

    public String getProperty(String key) {
        return dbProps.getProperty(key);
    }

    public String getProperty(String key, String defValue) {
        return dbProps.getProperty(key, defValue);
    }// ;

    public boolean containsKey(String key) {
        return dbProps.containsKey(key);
    }// ;

    public Properties getProperties() {
        return dbProps;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // do nothing
    }
}
