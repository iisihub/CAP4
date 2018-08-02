package com.iisigroup.colabase.service.impl;

import com.iisigroup.colabase.dao.ZipCodeDao;
import com.iisigroup.colabase.model.Address;
import com.iisigroup.colabase.model.ZipCode;
import com.iisigroup.colabase.service.AddressService;
import com.iisigroup.colabase.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2018/6/4 AndyChen,new
 * </ul>
 * @since 2018/6/4
 */
@Service
public class AddressServiceImpl extends AddressOriginalServiceImpl implements AddressService {

    private static Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private boolean hasInit = false;
    private long lastInit;

    @Autowired
    private ZipCodeDao zipCodeDao;

    @Override
    protected List<Map<String, Object>> query(int columnCount, String sqlStr, Object... parameters) throws Exception {
        long diffADay = 24 * 60 * 60 * 1000;
        long today = new Date().getTime();
        if(!hasInit || (today - lastInit > diffADay)) {
            this.cleanH2Db();
            this.putZipCodeDataToH2();
            lastInit = today;
            hasInit = true;
        }
        return super.query(columnCount, sqlStr, parameters);
    }

    @Override
    public Address normalizeAddress(String address) throws Exception {
        Map<String, String> resultMap = super.normalize(address);
        Address addressModel = new Address();
        addressModel.setNo(resultMap.get("no"));
        addressModel.setAddress(resultMap.get("address"));
        addressModel.setRoad(resultMap.get("road"));
        addressModel.setCity(resultMap.get("city"));
        addressModel.setDistrict(resultMap.get("district"));
        addressModel.setSection(resultMap.get("section"));
        addressModel.setZip3(resultMap.get("zip3"));
        addressModel.setFloor(resultMap.get("floor"));
        addressModel.setZip5(resultMap.get("zip5"));
        addressModel.setLane(resultMap.get("lane"));
        addressModel.setAlley(resultMap.get("alley"));
        addressModel.setRoom(resultMap.get("room"));
        addressModel.setVillage(resultMap.get("village"));
        addressModel.setNeighborhood(resultMap.get("neighborhood"));
        return addressModel;
    }

    private void putZipCodeDataToH2() {
        PreparedStatement ps = null;
        String zip3 = null;
        String zip5= null;
        String city= null;
        String country= null;
        String road= null;
        try {
            List<ZipCode> allZipCodes = zipCodeDao.findAll();
            Set<String> zip3Set = new HashSet<>();
            for(ZipCode zipCode : allZipCodes) {
                zip3 = zipCode.getZipCode().substring(0, 3);
                zip5 = zipCode.getZipCode();
                city = zipCode.getCounty();
                country = zipCode.getDistrict();
                road = zipCode.getRoad();
                this.putDataToH2( zip3,  zip5,  city,  country,  road, zip3Set);
                if(city.contains("台")) {
                    city = city.replaceAll("台", "臺");
                    this.putDataToH2( zip3,  zip5,  city,  country,  road, zip3Set);
                } else if(city.contains("臺")){
                    city = city.replaceAll("臺", "台");
                    this.putDataToH2( zip3,  zip5,  city,  country,  road, zip3Set);
                }
            }
        } catch (Exception e) {
            logger.error("[setZipCodeDataToH2] fail to put data from MsSQL to H2, info: " + e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e1) {
                }
            }
        }

    }
    
    private void putDataToH2(String zip3, String zip5, String city, String country, String road, Set<String> zip3Set) {
        PreparedStatement ps = null;
        try {
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
            ps.setString(6, NumberUtil.formatStrNumberByType(road.trim(), NumberUtil.Type.CHINESE));
            // ps.setString(6, road.replaceAll("１０", "十").replaceAll("１", "一").replaceAll("２", "二").replaceAll("３", "三").replaceAll("４", "四").replaceAll("５", "五").replaceAll("６", "六")
            // .replaceAll("７", "七").replaceAll("８", "八").replaceAll("９", "九").trim());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            logger.error("[setZipCodeDataToH2] fail to put data from MsSQL to H2, info: " + e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e1) {
                }
            }
        }
    }

    private void cleanH2Db() {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DROP TABLE ZIP3");
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("DROP TABLE ZIP5");
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("CREATE TABLE ZIP3 (OID VARCHAR(32) PRIMARY KEY, ZIP CHAR(3), CITY NVARCHAR(3), DISTRICT NVARCHAR(4))");
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("CREATE TABLE ZIP5 (OID VARCHAR(32) PRIMARY KEY, ZIP CHAR(5), CITY NVARCHAR(3), DISTRICT NVARCHAR(4), ROAD NVARCHAR(60), ROAD2 NVARCHAR(60))");
            ps.execute();
            ps.close();
        } catch (Exception e) {
            logger.error("[cleanH2Db] fail to clean H2 table, info: " + e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e1) {
                }
            }
        }
    }
}
