package com.iisigroup.colabase.json.demo.json.model;

import com.iisigroup.colabase.json.annotation.ApiRequest;
import com.iisigroup.colabase.json.annotation.JsonTemp;
import com.iisigroup.colabase.json.model.JsonAbstract;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2018/5/25 AndyChen,new
 * </ul>
 * @since 2018/5/25
 */
public class DemoModel extends JsonAbstract {

    @ApiRequest(path = "personDetails.parent.name.fullName")
    private String parentName;

    @ApiRequest(path = "personDetails.name")
    private String name;
    @ApiRequest(path = "personDetails.sex")
    private String sex;

    @ApiRequest(path = "phone[].number")
    private String phoneNumber;
    @ApiRequest(path = "phone[].area")
    private String phoneArea;

    @JsonTemp
    private String jsonTemp =
                    "{\n" +
                    "    \"personDetails\": {\n" +
                    "        \"parent\": {\n" +
                    "            \"name\": {\n" +
                    "                \"fullName\": \"Alex\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        \"name\": \"July\",\n" +
                    "        \"sex\": \"female\"\n" +
                    "    },\n" +
                    "    \"phone\": [{\n" +
                    "        \"number\": \"0999999999\",\n" +
                    "        \"area\": \"886\"\n" +
                    "    }]\n" +
                    "}";

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneArea() {
        return phoneArea;
    }

    public void setPhoneArea(String phoneArea) {
        this.phoneArea = phoneArea;
    }
}
