package com.iisigroup.colabase.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import com.iisigroup.colabase.service.AddressService;

public class AddressServiceImpl implements AddressService {
    private static Map<String, String> villages = new HashMap<String, String>();
    private static Map<String, String> roadce = new HashMap<String, String>();
    private static Map<String, String> countryce = new HashMap<String, String>();
    private static Connection conn = null;

    // data prepare 把從中華郵政下載的資料，insert 到 h2 in-memory db
    static {
        InputStream configFile = null;
        InputStream villageFile = null;
        InputStream countryceFile = null;
        InputStream roadceFile = null;
        InputStream zipCodeFile = null;
        BufferedReader br = null;
        PreparedStatement ps = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:mem:address", "sa", "");
            // conn =
            // DriverManager.getConnection("jdbc:h2:tcp://localhost:51043/E:\\@IISI\\@Project\\FCB\\eloan\\svn\\source\\poc\\h2db\\pocdb",
            // "sa", "");
            ps = conn.prepareStatement("CREATE TABLE ZIP3 (OID VARCHAR(32) PRIMARY KEY, ZIP CHAR(3), CITY NVARCHAR(3), DISTRICT NVARCHAR(3))");
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("CREATE TABLE ZIP5 (OID VARCHAR(32) PRIMARY KEY, ZIP CHAR(5), CITY NVARCHAR(3), DISTRICT NVARCHAR(3), ROAD NVARCHAR(60), ROAD2 NVARCHAR(60))");
            ps.execute();
            ps.close();
            // add application code here
            configFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            Properties p = new Properties();
            p.load(configFile);
            String villageFileName = p.getProperty("village.filename"); // 中華郵政下載的村里資料
            String villageEncoding = p.getProperty("village.encoding");
            villageFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(villageFileName);
            br = new BufferedReader(new InputStreamReader(villageFile, villageEncoding));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                String tw = st.nextToken().trim();
                String en = st.nextToken().trim();
                if (!tw.endsWith("巷")) { // 排除村里資料中以"巷"結尾的資料
                    villages.put(tw, en);
                }
            }
            // 鄉鎮市區中英對照
            String countryceFileName = p.getProperty("country_ce.filename"); // 中華郵政下載的村里資料
            String countryceEncoding = p.getProperty("country_ce.encoding");
            countryceFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(countryceFileName);
            br = new BufferedReader(new InputStreamReader(countryceFile, countryceEncoding));
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                String zip3 = st.nextToken();
                int last = line.indexOf("\"") >= 0 ? line.indexOf("\"") : line.lastIndexOf(",");
                String en = line.substring(last).replaceAll("\"", "");
                countryce.put(zip3, en);
            }
            // 路名中英對照
            String roadceFileName = p.getProperty("road_ce.filename"); // 中華郵政下載的村里資料
            String roadceEncoding = p.getProperty("road_ce.encoding");
            roadceFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(roadceFileName);
            br = new BufferedReader(new InputStreamReader(roadceFile, roadceEncoding));
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                if (st.countTokens() != 2) {
                    System.out.println(line);
                }
                String tw = st.nextToken().trim();
                String en = st.nextToken().trim();
                roadce.put(tw.replaceAll("１０", "十").replaceAll("１", "一").replaceAll("２", "二").replaceAll("３", "三").replaceAll("４", "四").replaceAll("５", "五").replaceAll("６", "六").replaceAll("７", "七")
                        .replaceAll("８", "八").replaceAll("９", "九").trim(), en); //TODO number formate issue
            }
            // 中華郵政下載3+2郵遞區號資料，但須手動刪除"路"之後的資料(ex.單、雙......)
            String zipCodeFileName = p.getProperty("zipcode.filename");
            String zipCodeEncoding = p.getProperty("zipcode.encoding");
            zipCodeFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(zipCodeFileName);
            br = new BufferedReader(new InputStreamReader(zipCodeFile, zipCodeEncoding));
            Set<String> zip3Set = new HashSet<>();
            while ((line = br.readLine()) != null) {
                String zip3 = line.substring(0, 3);
                String zip5 = line.substring(0, 5);
                String city = line.substring(5, 8);
                String country = line.substring(8, 11).trim();
                String road = line.substring(11);
                if (!zip3Set.contains(zip3)) {
                    ps = conn.prepareStatement("INSERT INTO ZIP3 (OID, ZIP, CITY, DISTRICT) VALUES (?, ?, ?, ?)");
                    ps.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
                    ps.setString(2, zip3);
                    ps.setString(3, city);
                    ps.setString(4, country);
                    ps.executeUpdate();
                    ps.close();
                    zip3Set.add(zip3);
                }
                ps = conn.prepareStatement("INSERT INTO ZIP5 (OID, ZIP, CITY, DISTRICT, ROAD, ROAD2) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
                ps.setString(2, zip5);
                ps.setString(3, city);
                ps.setString(4, country);
                ps.setString(5, road.trim());
                // 中華郵政下載3+2郵遞區號資料中，段是用全型數字，但身分證和一般證件是用國字，所以多放一欄是轉成國字的資料，也方便後續取"段"的資料
                ps.setString(6, road.replaceAll("１０", "十").replaceAll("１", "一").replaceAll("２", "二").replaceAll("３", "三").replaceAll("４", "四").replaceAll("５", "五").replaceAll("６", "六")
                        .replaceAll("７", "七").replaceAll("８", "八").replaceAll("９", "九").trim());
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) {
            System.out.println("?" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            if (configFile != null) {
                try {
                    configFile.close();
                } catch (IOException e) {
                }
            }
            if (villageFile != null) {
                try {
                    villageFile.close();
                } catch (IOException e) {
                }
            }
            if (zipCodeFile != null) {
                try {
                    zipCodeFile.close();
                } catch (IOException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e1) {
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see idv.lancelot.lambda.tool.service.AddressService#normalize(java.lang.
     * String)
     */
    @Override
    public Map<String, String> normalize(String address) throws Exception {
        Map<String, String> res = new HashMap<String, String>();
        String process = address;
        String zip;
        String zip3 = null;
        String zip5 = null;
        String city;
        String district;
        String road = null;
        String lane = null;
        String no = null;
        String floor = null;
        String village = null;
        String alley = null;
        String neighborhood = null;
        String section = null;
        String room = null;
        // zip code
        try {
            Integer.parseInt(address.substring(0, 5));
            zip3 = address.substring(0, 3);
            zip = zip5 = address.substring(0, 5);
        } catch (Exception e) {
            Integer.parseInt(address.substring(0, 3));
            zip = zip3 = address.substring(0, 3);
        }
        process = process.replace(zip, "").trim();
        List<Map<String, Object>> result = query(2, "SELECT CITY, DISTRICT FROM ZIP3 WHERE ZIP = ?", zip3);
        if (result.isEmpty()) {
            throw new Exception("city/district can't be found with zip " + zip);
        } else {
            Map<String, Object> data = result.get(0);
            city = (String) data.get("1");
            district = (String) data.get("2");
        }
        // city
        if (process.contains(city)) {
            process = process.replace(city, "").trim();
        } else {
            process = process.replace("台", "臺"); // 台 vs. 臺比對處理
            if (process.contains(city)) {
                process = process.replace(city, "").trim();
            } else {
                throw new Exception("city should be " + city + " with zip " + zip);
            }
        }
        // country
        if (process.contains(district)) {
            process = process.replace(district, "").trim();
        }
        // village
        for (Entry<String, String> m : villages.entrySet()) {
            String rec = m.getKey();
            if (process.contains(rec)) {
                village = rec;
                process = process.replace(rec, "").trim();
                res.put("village", village);
                break;
            }
        }
        // neighborhood 3+2資料中的路名有 "德鄰巷" 這種會導致混淆的得排除
        if (process.contains("鄰") && !process.contains("德鄰巷")) {
            if (village == null) {
                throw new Exception("neighborhood should come after village"); // 有鄰就應該有村里
            }
            neighborhood = process.substring(0, process.indexOf("鄰") + 1);
            process = process.replace(neighborhood, "").trim();
            res.put("neighborhood", neighborhood);
        }
        // road & check zip5
        result = query(3, "SELECT ZIP, ROAD, ROAD2 FROM ZIP5 WHERE CITY = ? AND DISTRICT = ?", new Object[] { city, district });
        if (result.isEmpty()) {
            throw new Exception("road can't be found with city/district " + city + "/" + district);
        } else {
            boolean found = false;
            for (Map<String, Object> rec : result) {
                String zipCol = (String) rec.get("1");
                String roadCol = (String) rec.get("2");
                String roadCol2 = (String) rec.get("3");
                if (process.contains(roadCol) || process.contains(roadCol2)) {
                    road = process.contains(roadCol) ? roadCol : roadCol2;
                    // 處理輸入地址有"段"、但比對 3+2 卻比到沒"段"的路名問題
                    if (process.contains("段") && !road.contains("段")) {
                        road = null;
                        continue;
                    }
                    found = true;
                    process = process.replace(road, "").trim();
                    if (zip5 == null) {
                        zip5 = zipCol;
                    }
                    if (road.contains("段")) {
                        // FIXME? 如果輸入資料是 "10段"，會有問題
                        section = road.substring(road.indexOf("段") - 1, road.indexOf("段") + 1);
                        res.put("section", section);
                    }
                    break;
                }
            }
            if (!found) {
                throw new Exception("road isn't correct with city/district " + city + "/" + district);
            } else {
                res.put("road", road);
            }
        }
        lane = processLane(process);
        if (lane != null) {
            process = process.replace(lane, "").trim();
            res.put("lane", lane);
        }
        alley = processAlley(process);
        if (alley != null) {
            process = process.replace(alley, "").trim();
            res.put("alley", alley);
        }
        no = processNo(process);
        if (no != null) {
            process = process.replace(no, "").trim();
            res.put("no", no);
        }
        floor = processFloor(process);
        if (floor != null) {
            process = process.replace(floor, "").trim();
            res.put("floor", floor);
        }
        room = processRoom(process);
        if (room != null) {
            process = process.replace(room, "").trim();
            res.put("room", room);
        }
        res.put("address", address);
        res.put("zip3", zip3);
        res.put("zip5", zip5);
        res.put("city", city);
        res.put("district", district);
        if (!"".equals(process.trim())) {
            res.put("other", process);
        }
        return res;
    }

    private String processLane(String process) {
        return processSinglePhrases(process, "巷");
    }

    private String processAlley(String process) {
        return processSinglePhrases(process, "弄");
    }

    private String processNo(String process) {
        String no = null;
        if (process.contains("號之")) {
            no = process.substring(0, process.indexOf("號之") + 3); // 假設"號之"後面接一位中文或一位全型
        } else {
            no = processSinglePhrases(process, "號");
        }
        return no;
    }

    private String processFloor(String process) {
        String floor = null;
        if (process.contains("樓之")) {
            floor = process.substring(0, process.indexOf("樓之") + 3); // 假設"樓之"後面接一位中文或一位全型
        } else {
            floor = processSinglePhrases(process, "樓");
        }
        return floor;
    }

    private String processRoom(String process) {
        return processSinglePhrases(process, "室");
    }

    private String processSinglePhrases(String process, String phrases) {
        String result = null;
        if (process.contains(phrases)) {
            result = process.substring(0, process.indexOf(phrases) + 1);
        }
        return result;
    }

    private List<Map<String, Object>> query(int columnCount, String sqlStr, Object... parameters) throws Exception {
        PreparedStatement smt = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            smt = conn.prepareStatement(sqlStr, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            for (int i = 1; i <= parameters.length; i++) {
                smt.setObject(i, parameters[i - 1]);
            }
            if (smt.execute()) {
                ResultSet rs = smt.getResultSet();
                rs.beforeFirst();
                while (rs.next()) {
                    Map<String, Object> rec = new HashMap<String, Object>();
                    for (int j = 0; j < columnCount; j++) {
                        rec.put(String.valueOf(j + 1), rs.getObject(j + 1));
                    }
                    result.add(rec);
                }
                rs.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (smt != null) {
                try {
                    smt.close();
                } catch (SQLException e) {
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, String> toEnglish(Map<String, String> normalized) throws Exception {
        Map<String, String> eAddress = new HashMap<String, String>();
        for (Entry<String, String> e : normalized.entrySet()) {
            String key = e.getKey();
            String eKey = "e" + key;
            String value = e.getValue();
            switch (key) {
            case "village":
                eAddress.put(eKey, villages.get(value));
                break;
            case "road":
                eAddress.put(eKey, roadce.get(value));
                break;
            case "lane":
                eAddress.put(eKey, "Ln. " + value.replace("巷", ""));
                break;
            case "alley":
                eAddress.put(eKey, "Aly. " + value.replace("弄", ""));
                break;
            case "no":
                String result = "";
                if (value.indexOf("之") > 0) {
                    String dash = value.substring(value.indexOf("之") + 1).replaceAll("一", "1").replaceAll("二", "2").replaceAll("三", "3").replaceAll("四", "4").replaceAll("五", "5").replaceAll("六", "6")
                            .replaceAll("七", "7").replaceAll("八", "8").replaceAll("九", "9").replaceAll("十", "10");
                    result = "No." + value.substring(0, value.indexOf("號")) + "-" + dash;
                } else {
                    result = "No." + value.substring(0, value.indexOf("號"));
                }
                eAddress.put(eKey, result);
                break;
            case "floor":
                result = "";
                if (value.indexOf("之") > 0) {
                    String dash = value.substring(value.indexOf("之") + 1).replaceAll("一", "1").replaceAll("二", "2").replaceAll("三", "3").replaceAll("四", "4").replaceAll("五", "5").replaceAll("六", "6")
                            .replaceAll("七", "7").replaceAll("八", "8").replaceAll("九", "9").replaceAll("十", "10");
                    result = value.substring(0, value.indexOf("樓")) + "F.-" + dash;
                } else {
                    result = value.substring(0, value.indexOf("樓")) + "F.";
                }
                eAddress.put(eKey, result);
                break;
            case "room":
                eAddress.put(eKey, "Rm. " + value.replace("室", ""));
                break;
            case "zip3":
                eAddress.put("city_dist", countryce.get(value));
            case "zip5":
            case "other":
                eAddress.put(eKey, value);
                break;
            case "neighborhood":
            case "address":
            case "city":
            case "district":
            case "section":
                // ignore
                break;
            }
            String[] ordinal = new String[] { "eroom", "efloor", "eno", "ealley", "elane", "eroad", "evillage", "city_dist" };
            StringBuilder eAddr = new StringBuilder();
            for (String k : ordinal) {
                if (eAddress.containsKey(k)) {
                    eAddr.append(eAddress.get(k)).append(", ");
                }
            }
            String tmp = eAddr.toString();
            eAddress.put("eAddress", tmp.substring(0, tmp.lastIndexOf(",")) + " " + eAddress.get("ezip3") + ", Taiwan (R.O.C.)");
        }
        return eAddress;
    }

}
