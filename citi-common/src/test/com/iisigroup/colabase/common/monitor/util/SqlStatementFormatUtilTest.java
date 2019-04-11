package com.iisigroup.colabase.common.monitor.util;

import org.junit.Assert;
import org.junit.Test;


public class SqlStatementFormatUtilTest {

    @Test
    public void addNoLockStatement_simple() {
        String origin = "select * from XXXX a where a.name = '1234'";
        System.out.println("origin: " + origin);
        String result = SqlStatementFormatUtil.addNoLockStatement(origin).toLowerCase();
        System.out.println("result: " + result);
        int firstIndex = result.indexOf("nolock");
        int lastIndex = result.lastIndexOf("nolock");
        Assert.assertEquals(firstIndex, lastIndex);
    }

    @Test
    public void addNoLockStatement_complex() {
        String origin = "select * from XXXX a where a.name = (select aaa from YYYY where 'a' = 'b')";
        System.out.println("origin: " + origin);
        String result = SqlStatementFormatUtil.addNoLockStatement(origin).toLowerCase();
        System.out.println("result: " + result);
        int firstIndex = result.indexOf("nolock");
        int lastIndex = result.lastIndexOf("nolock");
        Assert.assertNotEquals(firstIndex, lastIndex); // 2個以上
    }


}