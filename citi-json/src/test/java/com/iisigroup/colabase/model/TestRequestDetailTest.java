package com.iisigroup.colabase.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iisigroup.colabase.model.test.TestRequestDetail;
import com.iisigroup.colabase.service.test.TestService;
import com.iisigroup.colabase.service.test.TestService2;
import com.iisigroup.colabase.service.test.TestServiceImpl;
import com.iisigroup.colabase.service.test.TestServiceImpl2;
import com.iisigroup.colabase.util.JsonFactory;

import java.lang.reflect.Field;

/**
 * Created by AndyChen on 2018/5/15.
 */
public class TestRequestDetailTest {

    private static TestRequestDetail requestContent;

    private final String PARENT_NODE_NAME = "documentDetails";
    private final String TEST_NODE_NAME = "documentIdType";
    private final String TEST_VALUE = "TestType";
    private final String DEFAULT_VALUE = "MyDefaultV";

    private final String number1 = "0987888777";
    private final String nation1 = "Taiwan";
    private final String code1 = "06";
    private final String text1 = "tainan";

    private final String number2 = "0955555666";
    private final String nation2 = "USA";
    private final String code2 = "07";
    private final String text2 = "taipei";

    private final String numberNode = "number";
    private final String nationNode = "nation";
    private final String codeNode = "code";
    private final String textNode = "text";


    /**
     * test json temp follow on TestRequestDetail.java
     */
    private String jsonTempStr =
            "{\n" +
                    "    \"documentDetails\": {\n" +
                    "        \"documentIdType\": \"xxx\",\n" +
                    "        \"documentFormat\": \"ooo\"\n" +
                    "    },\n" +
                    "    \"controlFlowId\": \"123\",\n" +
                    "    \"phone\": [{\n" +
                    "        \"number\":\"ok\",\n" +
                    "        \"nation\":\"shi\",\n" +
                    "        \"area\" : [{ " +
                    "   \"code\" : \"hoho\",\n" +
                    "   \"text\" : \"kkk\"\n" +
                    "}]\n" +
                    "    }]\n" +
                    "}";

    @BeforeClass
    public static void setEnv() {
        
    }

    @Before
    public void setUp() {
        requestContent = JsonFactory.getInstance(TestRequestDetail.class);
    }

    @Test
    public void setFieldTest() throws Exception {
        requestContent.setDocumentIdType(TEST_VALUE);
        String testValue = getTestValue();
        Assert.assertEquals(TEST_VALUE, testValue);
    }

    private String getTestValue() {
        JsonObject parentEle = (JsonObject) requestContent.getRequestContent().get(PARENT_NODE_NAME);
        return parentEle.get(TEST_NODE_NAME).getAsString();
    }

    @Test
    public void setArrayFieldTest() throws Exception {
        requestContent.setControlFlowId("control");
        requestContent.setDocumentIdType("new type");

        requestContent.setCode(code1);
        requestContent.setNumber(number1);
        requestContent.setNation(nation1);
        // requestContent.setCode(code1);
        requestContent.setText(text1);

        requestContent.setNumber(number2);
        requestContent.setNation(nation2);
        requestContent.setCode(code2);
        requestContent.setText(text2);

        JsonArray phoneEles = requestContent.getRequestContent().getAsJsonArray("phone");
        int arraySize = phoneEles.size();
        Assert.assertEquals(2, arraySize);

        validateArrayValue(phoneEles, 0, number1, nation1, code1, text1);
        validateArrayValue(phoneEles, 1, number2, nation2, code2, text2);
    }

    public void validateArrayValue(JsonArray phoneEles, int index, String number, String nation, String code, String text) {
        JsonObject firstPhoneEle = (JsonObject) phoneEles.get(index);
        JsonElement numberValue1 = firstPhoneEle.get(numberNode);
        JsonElement nationValue1 = firstPhoneEle.get(nationNode);

        Assert.assertEquals(number, numberValue1.getAsString());
        Assert.assertEquals(nation, nationValue1.getAsString());

        JsonArray areas = firstPhoneEle.getAsJsonArray("area");
        Assert.assertEquals(1, areas.size());

        JsonObject areaObj = (JsonObject) areas.get(0);
        JsonElement codeValue = areaObj.get(codeNode);
        JsonElement textValue = areaObj.get(textNode);

        Assert.assertEquals(code, codeValue.getAsString());
        Assert.assertEquals(text, textValue.getAsString());

    }

    @Test
    public void test_no_send_list() throws Exception {
        String standStr = requestContent.getRequestContent().toString();
        String jsonString = requestContent.getJsonString();
        Assert.assertNotEquals(standStr, jsonString);
    }

    @Test
    public void test_afterProgress() throws Exception {
        TestService testService = new TestServiceImpl();
        TestService2 testService2 = new TestServiceImpl2();
        requestContent = JsonFactory.getInstance(TestRequestDetail.class, testService, testService2);
        ResponseContent response = new ResponseContent();
        requestContent.afterSendRequest(response);
    }

    @Test
    public void test_chache_string() throws Exception {
        Field field = JsonAbstract.class.getDeclaredField("jsonStrCache");
        field.setAccessible(true);
        String value = (String) field.get(requestContent);
        Assert.assertEquals("", value);

        requestContent.getJsonString();
        value = (String) field.get(requestContent);
        Assert.assertNotEquals("", value);

        requestContent.getJsonString();
        requestContent.setNation("Taiwan");
        value = (String) field.get(requestContent);
        Assert.assertEquals("", value);

    }
}