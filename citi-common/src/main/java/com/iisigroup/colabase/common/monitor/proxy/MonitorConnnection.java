package com.iisigroup.colabase.common.monitor.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;

import com.iisigroup.colabase.common.monitor.util.SqlStatementFormatUtil;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.colabase.common.jsp.ColaSysDbAndConfigProps;


/**
 * @author AndyChen
 * @version <ul>
 *          <li>2019/4/9 AndyChen,new
 *          </ul>
 * @since 2019/4/9
 */
public class MonitorConnnection implements InvocationHandler {

    private Connection connection;
    private ColaSysDbAndConfigProps sysProp;
    private String[] withoutNoLockTableNames;


    public MonitorConnnection() {
    }

    public Connection bind(Connection connection) {
        this.connection = connection;
        Connection instance;
        try {
            instance = (Connection) Proxy.newProxyInstance(connection.getClass().getClassLoader(), connection.getClass().getInterfaces(), this);
        } catch (ClassCastException e) {
            //應為container實作的class not implements Connection,而是繼承基類，改取父類interfaces
            instance = (Connection) Proxy.newProxyInstance(connection.getClass().getClassLoader(), connection.getClass().getSuperclass().getInterfaces(), this);
        }
        return instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.setWithoutNoLockNames();
        String methodName = method.getName();
        String[] targetNames = {"prepareStatement"}; //目前執行SQL的method names
        if (Arrays.asList(targetNames).contains(methodName) && args != null) {
            Object[] executeArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                String sqlStr = args[i].toString().toLowerCase().trim();
                boolean needNoLock = true;
                if (withoutNoLockTableNames != null) {
                    for (String withoutNoLockTableName : withoutNoLockTableNames) {
                        if(sqlStr.contains(withoutNoLockTableName.toLowerCase())) {
                            needNoLock = false;
                            break;
                        }
                    }
                }
                if(needNoLock) {
                    executeArgs[i] = SqlStatementFormatUtil.addNoLockStatement(sqlStr);
                } else {
                    executeArgs[i] = sqlStr;
                }
            }
            return method.invoke(connection, executeArgs);
        } else {
            return method.invoke(connection, args);
        }
    }

    /**
     * 因為目前沒有辦法將sysProp在一開始傳入。
     * 所以排除用noLock的table，會在起完server後(all spring beans had initialed)才會生效。
     */
    private void setWithoutNoLockNames() {
        this.setSysProp();
        if(this.withoutNoLockTableNames != null || sysProp == null)
            return;
        String dbNames = sysProp.get("db.without.nolock.tables");
        if(dbNames != null && dbNames.length() > 0) {
            this.withoutNoLockTableNames =  dbNames.split(",");
        }
    }

    private void setSysProp() {
        if(sysProp == null) {
            try {
                sysProp = (ColaSysDbAndConfigProps) CapAppContext.getApplicationContext().getBean("sysProp");
            } catch (Exception e) {
            }
        }
    }
}
