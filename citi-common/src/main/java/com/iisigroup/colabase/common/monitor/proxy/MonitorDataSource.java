package com.iisigroup.colabase.common.monitor.proxy;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;


/**
 * @author AndyChen
 * @version <ul>
 *          <li>2019/4/9 AndyChen,new
 *          </ul>
 * @since 2019/4/9
 */
public class MonitorDataSource implements DataSource {

    private DataSource dataSource;

    public MonitorDataSource(DataSource originalDataSource){
        dataSource = originalDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        MonitorConnnection monitorConnnection = new MonitorConnnection();
        return monitorConnnection.bind(dataSource.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        MonitorConnnection monitorConnnection = new MonitorConnnection();
        return monitorConnnection.bind(dataSource.getConnection(username, password));
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }
}
