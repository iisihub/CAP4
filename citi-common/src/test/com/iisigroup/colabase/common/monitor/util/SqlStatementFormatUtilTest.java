package com.iisigroup.colabase.common.monitor.util;

import org.junit.Assert;
import org.junit.Test;


public class SqlStatementFormatUtilTest {

    @Test
    public void addNoLockStatement_simple() {
        String origin = "SELECT * \n\t\t\t\tFROM \n\t\t\t\tCLM_TRANSLOG AS tb1 \n\t\t\t\t\n\t\t\t\tWHERE  tb1.APPL_TIME BETWEEN ? AND ?\n\t\t\t\tORDER BY tb1.APPL_TIME;";
        origin = origin.toLowerCase();
        System.out.println("origin: " + origin);
        String result = SqlStatementFormatUtil.addNoLockStatement(origin).toLowerCase();
        System.out.println("result: " + result);
        int firstIndex = result.indexOf("nolock");
        int lastIndex = result.lastIndexOf("nolock");
        Assert.assertEquals(firstIndex, lastIndex);
        Assert.assertTrue(firstIndex > 0);
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
        Assert.assertTrue(firstIndex > 0 && lastIndex > 0);
    }


}