package com.iisigroup.colabase.json.util;

import org.junit.Assert;
import org.junit.Test;

import com.iisigroup.colabase.json.model.test.TestRequestDetail;

/**
 * Created by AndyChen on 2018/5/15.
 */
public class JsonFactoryTest {

    @Test
    public void getInstance() throws Exception {
        TestRequestDetail instance = JsonFactory.getInstance(TestRequestDetail.class);
        Assert.assertNotNull(instance);
    }
}