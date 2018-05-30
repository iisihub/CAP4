package com.iisigroup.colabase.util;

import org.junit.Assert;
import org.junit.Test;

public class NumberUtilTest {


    @Test
    public void get_first_numbers() {
        String test = NumberUtil.getFirstNumers("三元路二段149-2號");
        Assert.assertEquals("二", test);
    }

    @Test
    public void test_string_trans_to_assign_type() {
        String test = NumberUtil.formatStrNumberByType("中正南路二段149-2號", NumberUtil.Type.CHINESE);
        Assert.assertEquals("中正南路二段一四九-二號", test);
        String test1 = NumberUtil.formatStrNumberByType("中正南路二十段10-2號", NumberUtil.Type.CHINESE);
        Assert.assertEquals("中正南路二十段十-二號", test1);
    }

    @Test
    public void transNumberToAssignType() {
        String test = NumberUtil.transNumberToAssignType("194", NumberUtil.Type.CHINESE);
        Assert.assertEquals("一九四", test);

        String test1 = NumberUtil.transNumberToAssignType("80", NumberUtil.Type.CHINESE);
        Assert.assertEquals("八十", test1);

        String test2 = NumberUtil.transNumberToAssignType("四十三", NumberUtil.Type.HALF);
        Assert.assertEquals("43", test2);

        String test3 = NumberUtil.transNumberToAssignType("六十", NumberUtil.Type.FULL);
        Assert.assertEquals("６０", test3);

        String test4 = NumberUtil.transNumberToAssignType("804", NumberUtil.Type.CHINESE);
        Assert.assertEquals("八零四", test4);

        String test5 = NumberUtil.transNumberToAssignType("85", NumberUtil.Type.CHINESE);
        Assert.assertEquals("八十五", test5);

        String test6 = NumberUtil.transNumberToAssignType("098", NumberUtil.Type.CHINESE);
        Assert.assertEquals("零九八", test6);

        String test7 = NumberUtil.transNumberToAssignType("10", NumberUtil.Type.CHINESE);
        Assert.assertEquals("十", test7);

        String test8 = NumberUtil.transNumberToAssignType("九0三", NumberUtil.Type.HALF);
        Assert.assertEquals("903", test8);

    }

    @Test
    public void transStrToNumber() {
        String ten = NumberUtil.transStrToNumber("十");
        Assert.assertEquals("10", ten);

        String sixty = NumberUtil.transStrToNumber("六十");
        Assert.assertEquals("60", sixty);

        String eightyThree = NumberUtil.transStrToNumber("八十三");
        Assert.assertEquals("83", eightyThree);

        String fourSevenTwo = NumberUtil.transStrToNumber("四七二");
        Assert.assertEquals("472", fourSevenTwo);

        String eightFourSevenTwo = NumberUtil.transStrToNumber("八四七二");
        Assert.assertEquals("8472", eightFourSevenTwo);

        String nine = NumberUtil.transStrToNumber("九");
        Assert.assertEquals("9", nine);

        String test = NumberUtil.transStrToNumber("九零零三");
        Assert.assertEquals("9003", test);

        String test1 = NumberUtil.transStrToNumber("零九零");
        Assert.assertEquals("090", test1);
    }

}