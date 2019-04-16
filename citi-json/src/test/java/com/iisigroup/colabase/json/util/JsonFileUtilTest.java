package com.iisigroup.colabase.json.util;

import org.junit.Assert;
import org.junit.Test;

public class JsonFileUtilTest {

    @Test
    public void loadFileFromConfigPath() {
        String fileStr = JsonFileUtil.loadFileFromConfigPath("test.json");
        Assert.assertNotEquals("result should not be empty", "", fileStr);
    }
}