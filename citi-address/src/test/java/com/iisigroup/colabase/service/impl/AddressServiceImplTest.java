package com.iisigroup.colabase.service.impl;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by VALLA on 2018/5/7.
 */
public class AddressServiceImplTest {




    @Test
    public void testAddress() throws Exception {
        AddressServiceImpl addressService = new AddressServiceImpl();
        String testAddress = "241新北市三重區中正南路149-2號9樓";
        Map<String, String> normalize = addressService.normalize(testAddress);
        String s = "";

    }

}