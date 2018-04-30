package com.iisigroup.colabase.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressSplitterUtil {
  private static String village = "";

  private static AddressSplitterUtil instance;

  private AddressSplitterUtil() {

  }

  static {
    instance = new AddressSplitterUtil();
  }

  public static Map<String, String> splitAddress(List<String> roadList, String otherAddress) {
    Map<String, String> result = new HashMap<>();

    ArrayList<String> lastAddress = new ArrayList<>();
    ArrayList<Integer> roadLocation = new ArrayList<>();

    Boolean roadFlag = true;
    Boolean NEIGHBORHOODflag = true;

    String Address_A1 = otherAddress;
    String Address_A2 = "";
    String Address_A3 = "";
    String Address_A4 = "";

    //判斷 roadFunction 是否存在 (otherAddress)

    roadLocation = instance.roadFunction(otherAddress, roadList);

    if (roadLocation.size() == 0) {

      result.put("road", "");
      result.put("section", "");
      roadFlag = false;
      Address_A2 = Address_A1;

    } else {

      Address_A2 = Address_A1.replaceFirst(roadList.get(roadLocation.get(0)), "");

      if (roadList.get(roadLocation.get(0)).contains("段")) {
        String Section_String = instance.getSection(roadList.get(roadLocation.get(0)));
        if (!Section_String.equals("")) {

          String Road_String = roadList.get(roadLocation.get(0)).split(Section_String + "段")[0];

          result.put("road", Road_String);
          result.put("section", Section_String);

        } else {

          result.put("road", roadList.get(roadLocation.get(0)));
          result.put("section", "");

        }

      } else {

        result.put("road", roadList.get(roadLocation.get(0)));
        result.put("section", "");

      }
    }

    //判斷鄰
    if (roadFlag) {

      if (Address_A2.contains("鄰")) {

        String neighborhood_String = instance.getKeyValue(Address_A2, "鄰");

        Address_A3 = Address_A2.replaceFirst(neighborhood_String + "鄰", "");

        if (!neighborhood_String.equals("")) {

          result.put("neighborhood", neighborhood_String);

        } else {

          result.put("neighborhood", "");
          NEIGHBORHOODflag = false;

        }
      } else {

        Address_A3 = Address_A2;

      }

      lastAddress = instance.splitLAString(Address_A3);

      result.put("lane", lastAddress.get(0));
      result.put("alley", lastAddress.get(1));
      result.put("number", lastAddress.get(2));
      result.put("floor", lastAddress.get(3));
      result.put("room", lastAddress.get(4));

      //加入村里

      if (village.equals("")) {

        result.put("village", "");
        village = "";
      } else {

        result.put("village", village.trim());
        village = "";

      }

    } else {

      result.put("village", "");
      result.put("road", "");
      result.put("section", "");
      result.put("neighborhood", "");
      result.put("lane", "");
      result.put("alley", "");
      result.put("number", "");
      result.put("floor", "");
      result.put("room", "");

    }

    return result;


  }

  private ArrayList<Integer> roadFunction(String otherAddress, List<String> roadList) {
    ArrayList<Integer> roadLocation = new ArrayList<>();
    ArrayList<Integer> tempStoreListROAD = new ArrayList<>();
    Integer lengthCounterROAD = 0;

    //段前數字為數字

    for (int t = 0; t < roadList.size(); t++) {
      String roadName = roadList.get(t);

      if (otherAddress.contains(roadName)) {
        if (roadName.length() > lengthCounterROAD) {
          lengthCounterROAD = roadName.length();
          tempStoreListROAD.clear();
          tempStoreListROAD.add(t);

        } else if (roadName.length() == lengthCounterROAD) {
          tempStoreListROAD.add(t);

        } else if (roadName.length() < lengthCounterROAD) {

          //do nothing

        }
      }
    }

    roadLocation.addAll(tempStoreListROAD);


    return roadLocation;
  }


  private ArrayList<String> splitLAString(String LAString) {

    ArrayList<String> LAStringList = new ArrayList<String>();

    String DF1 = "";
    String DF2 = "";
    String DF3 = "";
    String DF4 = "";
    String DF5 = "";


    String LAString2 = "";
    String LAString3 = "";
    String LAString4 = "";
    String LAString5 = "";
    String LAString6 = "";

    //找出巷的資料

    if (LAString.indexOf("巷") != -1) {

      DF1 = getKeyValue(LAString, "巷");
      LAStringList.add(DF1);

      LAString2 = LAString.replaceFirst(DF1 + "巷", "");

    } else {

      LAStringList.add("");

      LAString2 = LAString;

    }

    //找出弄的資料

    if (LAString2.indexOf("弄") != -1) {

      DF2 = getKeyValue(LAString2, "弄");

      LAStringList.add(DF2);

      LAString3 = LAString2.replaceFirst(DF2 + "弄", "");

    } else {

      LAStringList.add("");

      LAString3 = LAString2;

    }

    //找出號的資料

    if (LAString3.indexOf("號") != -1) {

      DF3 = getKeyValue(LAString3, "號");
      LAStringList.add(DF3);

      LAString4 = LAString3;

      String SubString = "";

      if (!LAString4.equals("")) {
        if (LAString4.contains("號之")) {
          SubString = getKeyValueRevert(LAString4, "號之", "號");

          if (!SubString.equals("")) {

            LAStringList.set((LAStringList.size() - 1), (DF3 + "-" + SubString));
            LAString4 = LAString4.replaceFirst(DF3 + "號之" + SubString, "");


          }
        } else if (LAString4.contains("號-")) {
          SubString = getKeyValueRevert(LAString4, "號-", "號");

          if (!SubString.equals("")) {

            LAStringList.set((LAStringList.size() - 1), (DF3 + "-" + SubString));
            LAString4 = LAString4.replaceFirst(DF3 + "號-" + SubString, "");


          }
        } else {

          LAString4 = LAString4.replaceFirst(DF3 + "號", "");

        }
      }
    } else {

      LAStringList.add("");

      LAString4 = LAString3;

    }

    //找出樓的資料

    if (LAString4.indexOf("樓") != -1) {

      DF4 = getKeyValue(LAString4, "樓");
      LAStringList.add(DF4);

      LAString5 = LAString4;
      String SFString = "";

      if (!LAString5.equals("")) {
        if (LAString5.contains("樓之")) {
          SFString = getKeyValueRevert(LAString5, "樓之", "樓");

          if (!SFString.equals("")) {

            LAStringList.set((LAStringList.size() - 1), (DF4 + "-" + SFString));
            LAString5 = LAString5.replaceFirst(DF4 + "樓之" + SFString, "");


          }
        } else if (LAString5.contains("樓-")) {
          SFString = getKeyValueRevert(LAString5, "樓-", "樓");

          if (!SFString.equals("")) {

            LAStringList.set((LAStringList.size() - 1), (DF4 + "-" + SFString));
            LAString5 = LAString5.replaceFirst(DF4 + "樓-" + SFString, "");


          }
        } else {

          LAString5 = LAString5.replaceFirst(DF4 + "樓", "");

        }

      }

    } else if (LAString4.indexOf("F") != -1) {

      DF4 = getKeyValue(LAString4, "F");
      LAStringList.add(DF4);

      LAString5 = LAString4;

      String SFString = "";

      if (!LAString5.equals("")) {
        if (LAString5.contains("F之")) {
          SFString = getKeyValueRevert(LAString5, "F之", "F");

          if (!SFString.equals("")) {

            LAStringList.set((LAStringList.size() - 1), (DF4 + "-" + SFString));
            LAString5 = LAString5.replaceFirst(DF4 + "F之" + SFString, "");


          }
        } else if (LAString5.contains("F-")) {
          SFString = getKeyValueRevert(LAString5, "F-", "F");

          if (!SFString.equals("")) {

            LAStringList.set((LAStringList.size() - 1), (DF4 + "-" + SFString));

            LAString5 = LAString5.replaceFirst(DF4 + "F-" + SFString, "");

          }
        } else {
          LAString5 = LAString5.replaceFirst(DF4 + "F", "");
        }

      }
    } else {

      LAStringList.add("");

      LAString5 = LAString4;

    }

    //找出室的資料
    if (LAString5.indexOf("室") != -1) {

      DF5 = getKeyValue(LAString5, "室");
      LAStringList.add(DF5);

      LAString6 = LAString5.replaceFirst(DF5 + "室", "");

    } else {

      LAStringList.add("");

      LAString6 = LAString5;

    }

    village = LAString6;

    return LAStringList;

  }

  private boolean isNumeric(String str) {

    Pattern pattern = Pattern.compile("[0-9]*");

    Matcher isNum = pattern.matcher(str);

    if (!isNum.matches()) {

      return false;

    } else {


      return true;

    }

  }

  private String getKeyValue(String Address, String KeyWord) {

    String TempString = "";
    Boolean Startflag = false;

    for (int r = Address.length(); r > 0; r--) {

      char c = Address.charAt(r - 1);

      if (Startflag) {

        if (isNumeric(String.valueOf(c)) | String.valueOf(c).equals("-")) {

          TempString = String.valueOf(c) + TempString;

        } else {
          Startflag = false;
        }

      }

      if (String.valueOf(c).equals(KeyWord)) {

        Startflag = true;

      }

    }

    return TempString;


  }

  private String getKeyValueRevert(String Address, String KeyWord, String KeyWord2) {

    String TempString = "";
    Boolean StopTag = true;

    for (int r = Address.length(); r > 0; r--) {

      if (StopTag) {

        char c = Address.charAt(r - 1);

        if ((KeyWord2 + String.valueOf(c)).equals(KeyWord)) {

          TempString = reSearchFunction(Address, r - 1);

          StopTag = false;
        }
      }

    }

    return TempString;


  }

  private String reSearchFunction(String Address, Integer c) { //20170420

    Boolean Startflag = true;
    String TMString = "";

    for (int r = c + 1; r < Address.length(); r++) {

      char cat = Address.charAt(r);

      if (Startflag) {

        if (isNumeric(String.valueOf(cat)) | String.valueOf(cat).equals("-")) {

          TMString = TMString + String.valueOf(cat);

        } else {

          Startflag = false;

        }

      }

    }

    return TMString;

  }

  private String getSection(String Address) {

    String tempnumber = "";
    Boolean Startflag = true;

    for (int r = Address.length(); r > 0; r--) {

      char c = Address.charAt(r - 1);

      if (Startflag) {
        if (String.valueOf(c).equals("０") | String.valueOf(c).equals("１") | String.valueOf(c).equals("２") | String.valueOf(c).equals("３") | String.valueOf(c).equals("４") | String.valueOf(c).equals("５") | String.valueOf(c).equals("６") | String.valueOf(c).equals("７") | String.valueOf(c).equals("８") | String.valueOf(c).equals("９")) {

          tempnumber = String.valueOf(c) + tempnumber;

        } else {

          Startflag = false;
        }
      }

      if (String.valueOf(c).equals("段")) {

        Startflag = true;

      }

    }

    return tempnumber;

  }
}
