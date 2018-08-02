package com.iisigroup.colabase.service;

import com.iisigroup.colabase.model.Address;

import java.util.Map;

public interface AddressService {
    /**
     * <pre>
     * 地址正規化，處理按照固定順序的地址資料。
     * 3 or 5 碼 zip code + 縣市 + 鄉鎮市區 + (村里) + (鄰) + 路段 + 巷 + 弄 + 號 + 樓 + 室
     * </pre>
     *
     * @param address
     * @return
     * @throws Exception
     */
    Address normalizeAddress(String address) throws Exception;

}
