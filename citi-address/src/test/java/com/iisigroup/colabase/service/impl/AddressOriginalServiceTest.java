package com.iisigroup.colabase.service.impl;

import com.iisigroup.colabase.model.Address;
import com.iisigroup.colabase.service.AddressOriginalService;
import com.iisigroup.colabase.service.AddressService;
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
    public void testAddress() throws Exception {

//        String testAddress = "241新北市三重區中正南路149-2號9樓";
        String testAddress = "100臺北市中正區中華路１段149-2號9樓";
        Map<String, String> normalize = addressService.normalize(testAddress);
        String s = "";

    }

}