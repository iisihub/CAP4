package com.iisigroup.colabase.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonFileUtilTest {

    @Test
    public void loadFileFromConfigPath() {
        String fileStr = JsonFileUtil.loadFileFromConfigPath("test.json");
        Assert.assertNotEquals("result should not be empty", "", fileStr);
    }
}