package com.iisigroup.colabase.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.iisigroup.colabase.util.AddressSplitterUtil;
import org.junit.Test;

/**
 * Created by jackblackevo on 2017/4/26.
 */
public class AddressSplitterServiceTest {

  @Test
  public void splitAddress() {
//    String zip = "24889";
//    String city = "新北市";
//    String district = "五股區";
//    String otherAddress = "騙子路224巷5弄118號8F五十室";
//    String otherAddress = "五工三路2段224巷5弄118號之98";
//    String otherAddress = "平安里33鄰五工三路１段224巷5弄118號8F之3五十室";
    String road = "五工三路二十三段";
    String otherAddress = "平安里33鄰"+road+"224巷5弄118號8F之3五十室";
//    String otherAddress = "五工三路2段224巷5弄118之3號8F之3五十室";
//    String otherAddress = "工商路二段224巷5弄118號8F五十室";
    List<String> roadList = Arrays.asList(new String[] {"五工三路2段","五工三路二十三段","五工三路１段", "工商路二段" , "中直路" , "中直路崩山巷" ,
            "中興路１段" , "中興路２段" , "中興路３段" , "中興路４段" , "五工一路, 五工二路" , "五工三路" , "五工五路" , "五工六路" , "五工路" , "五林路" , "五福路" , "五權七路" , "五權二路, 五權八路" , "五權三路" , "五權五路" , "五權六路" , "五權路" , "六合街" , "天乙路" , "水碓一路" , "水碓七路", "水碓九路" , "水碓二路" , "水碓三路" , "水碓五路" , "水碓六路" , "水碓路" , "四維路" , "外寮路" , "民義路１段, 民義路２段" , "民義路３段" , "成洲一路" , "成洲七路" , "成洲二路" , "成洲八路" , "成洲三路" , "成洲五路, 成洲六路" , "成泰路１段" , "成泰路２段" , "成泰路３段" , "成泰路４段" , "自強路" , "西雲路" , "孝義路" , "更洲路", "明德路" , "芳洲一路" , "芳洲七路" , "芳洲九路" , "芳洲二路" , "芳洲八路" , "芳洲十路" , "芳洲三路" , "芳洲五路" , "芳洲六路" , "洲子路" , "洲后路, 洲新路" , "凌雲路１段" , "凌雲路２段" , "凌雲路３段" , "凌雲路３段田埔巷" , "國道路３段" , "御史路" , "御成路" , "疏洪北路" , "疏洪東路" , "登林路, 登林路貿商巷" , "新五路２段" , "新五路３段" , "新城一路" , "新城二路" , "新城八路" , "新城三路" , "新城五路" , "新城六路" , "蓬萊路" , "壟勾路"});

    Map<String, String> addressMap = AddressSplitterUtil.splitAddress(roadList, otherAddress);
    assertEquals("118", addressMap.get("number"));
    assertEquals(road, addressMap.get("road"));
    assertEquals("23", addressMap.get("section"));
    assertEquals("33", addressMap.get("neighborhood"));
    assertEquals("5", addressMap.get("alley"));
    assertEquals("8-3", addressMap.get("floor"));
    assertEquals("平安里", addressMap.get("village"));
    assertEquals("224", addressMap.get("lane"));
    assertEquals("五十", addressMap.get("room"));
  }

}
