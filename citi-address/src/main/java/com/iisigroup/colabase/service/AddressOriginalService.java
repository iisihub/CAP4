package com.iisigroup.colabase.service;

import java.util.Map;

public interface AddressOriginalService {
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
    Map<String, String> normalize(String address) throws Exception;

    /**
     * 傳入正規化後的地址，轉為英文
     * 
     * @param normalized
     * @return
     * @throws Exception
     */
    Map<String, String> toEnglish(Map<String, String> normalized) throws Exception;
}
