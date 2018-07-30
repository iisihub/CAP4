package com.iisigroup.colabase.demo.address;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 假裝從DB取出相關路名，實際專案需自行實作DB表及取路名集合的service
 * @author AndyChen
 * @version <ul>
 *          <li>2018/4/30 AndyChen,new
 *          </ul>
 * @since 2018/4/30
 */
@Service
public class FakeRoadService {

    /**
     * get Dummy road list insteads of from DB
     * @return
     */
    public List<String> getDummyRoadList() {
        List<String> roadList = Arrays.asList(new String[] {"工商路" , "中直路" , "中直路崩山巷" , "中興路１段" , "中興路２段" , "中興路３段" , "中興路４段" , "五工一路, 五工二路" , "五工三路" , "五工五路" , "五工六路" , "五工路" , "五林路" , "五福路" , "五權七路" , "五權二路, 五權八路" , "五權三路" , "五權五路" , "五權六路" , "五權路" , "六合街" , "天乙路" , "水碓一路" , "水碓七路", "水碓九路" , "水碓二路" , "水碓三路" , "水碓五路" , "水碓六路" , "水碓路" , "四維路" , "外寮路" , "民義路１段, 民義路２段" , "民義路３段" , "成洲一路" , "成洲七路" , "成洲二路" , "成洲八路" , "成洲三路" , "成洲五路, 成洲六路" , "成泰路１段" , "成泰路２段" , "成泰路３段" , "成泰路４段" , "自強路" , "西雲路" , "孝義路" , "更洲路", "明德路" , "芳洲一路" , "芳洲七路" , "芳洲九路" , "芳洲二路" , "芳洲八路" , "芳洲十路" , "芳洲三路" , "芳洲五路" , "芳洲六路" , "洲子路" , "洲后路, 洲新路" , "凌雲路１段" , "凌雲路２段" , "凌雲路３段" , "凌雲路３段田埔巷" , "國道路３段" , "御史路" , "御成路" , "疏洪北路" , "疏洪東路" , "登林路, 登林路貿商巷" , "新五路２段" , "新五路３段" , "新城一路" , "新城二路" , "新城八路" , "新城三路" , "新城五路" , "新城六路" , "蓬萊路" , "壟勾路"});
        return roadList;
    }
}
