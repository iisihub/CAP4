package com.iisigroup.colabase.service.impl;

import com.iisigroup.colabase.model.Address;
import com.iisigroup.colabase.service.AddressOriginalService;
import com.iisigroup.colabase.service.AddressService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by VALLA on 2018/5/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/test_applicationContext.xml"})
public class AddressOriginalServiceTest {

    @Autowired
    private AddressService addressOriginalService;

    @Test
    public void test_normal_address() throws Exception {
        String testAddress = "100臺北市中正區仁愛路１段149-2號9樓";
        Address address = addressOriginalService.normalizeAddress(testAddress);
        Assert.assertNotNull(address);
    }

    @Test
    public void test_normal_address_with_village() throws Exception {
        String testAddress = "100臺北市中正區丁台里36鄰仁愛路１段149-2號9樓";
        Address address = addressOriginalService.normalizeAddress(testAddress);
        Assert.assertNotNull(address);
    }

}