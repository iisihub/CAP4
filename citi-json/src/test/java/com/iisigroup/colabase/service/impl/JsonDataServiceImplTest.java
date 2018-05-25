package com.iisigroup.colabase.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iisigroup.colabase.model.JsonAbstract;
import com.iisigroup.colabase.model.test.TestRequestDetail;
import com.iisigroup.colabase.service.JsonDataService;
import com.iisigroup.colabase.util.JsonFactory;

/**
 * Created by AndyChen on 2018/5/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonDataServiceImplTest {

    private static JsonAbstract requestContent;

    private static JsonDataService jsonDataService;
    private final String PARENT_NODE_NAME = "documentDetails";
    private final String TEST_NODE_NAME = "documentIdType";
    private final String TEST_VALUE = "TestType";
    private final String DEFAULT_VALUE = "MyDefaultV";
    private final String MODEL_TEST_FIELD = "documentIdType";

    private final String numberNode = "number";
    private final String nationNode = "nation";
    private final String codeNode = "code";
    private final String textNode = "text";
    private final String phoneNode = "phone";

    final String TEST_ARRAY_NODE_NAME = "number";
    final String TEST_VALUE1 = "0999999999";
    final String TEST_VALUE2 = "0988888888";



    @BeforeClass
    public static void setEnv() throws Exception {
        requestContent = JsonFactory.getInstance(TestRequestDetail.class);
        jsonDataService = new JsonDataServiceImpl();
    }

    @Before
    public void setUp() throws Exception {
        requestContent = JsonFactory.getInstance(TestRequestDetail.class);
    }

    @Test
    public void setParamToJsonContent() throws Exception {
        jsonDataService.setParamToJsonContent(requestContent, TEST_NODE_NAME, TEST_VALUE);
        String value = getTestValue();
        Assert.assertEquals(TEST_VALUE, value);
    }

    private String getTestValue() {
        JsonObject parentEle = (JsonObject) requestContent.getRequestContent().get(PARENT_NODE_NAME);
        return parentEle.get(TEST_NODE_NAME).getAsString();
    }

    @Test
    public void setParamToJsonContent_JsonArray_test() throws Exception {

        jsonDataService.setParamToJsonContent(requestContent, "code", "nouse");
        jsonDataService.setParamToJsonContent(requestContent, TEST_ARRAY_NODE_NAME, TEST_VALUE1);
        jsonDataService.setParamToJsonContent(requestContent, TEST_ARRAY_NODE_NAME, TEST_VALUE2);
        String arrayValue1 = getArrayValue(0);
        String arrayValue2 = getArrayValue(1);
        Assert.assertEquals(TEST_VALUE1, arrayValue1);
        Assert.assertEquals(TEST_VALUE2, arrayValue2);
    }


    public String getArrayValue(int index) {
        JsonArray phoneEles = requestContent.getRequestContent().getAsJsonArray("phone");
        JsonObject firstPhoneEle = (JsonObject) phoneEles.get(index);
        JsonElement numberValue1 =  firstPhoneEle.get(numberNode);
//        JsonElement nationValue1 =  firstPhoneEle.get(nationNode);

        return numberValue1.getAsString();
    }

    @Test
    public void cleanJsonObjectData() throws Exception {
        jsonDataService.cleanJsonObjectData(requestContent);
        String value = getTestValue();
        Assert.assertEquals("", value);
    }



    @Test
    public void setDefaultValue() throws Exception {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(MODEL_TEST_FIELD, DEFAULT_VALUE);
        jsonDataService.setDefaultValue(requestContent, dataMap);
        String testValue = getTestValue();
        Assert.assertEquals(DEFAULT_VALUE, testValue);
    }

    @Test
    public void removeUnnecessaryNode() throws Exception {
        jsonDataService.setParamToJsonContent(requestContent, "otherCode", "nouse");
        jsonDataService.setParamToJsonContent(requestContent, TEST_ARRAY_NODE_NAME, TEST_VALUE1);
        jsonDataService.setParamToJsonContent(requestContent, TEST_ARRAY_NODE_NAME, TEST_VALUE2);
        jsonDataService.setParamToJsonContent(requestContent, nationNode, "Taiwan");
        jsonDataService.setParamToJsonContent(requestContent, nationNode, "China");

        JsonObject requestContent = jsonDataService.removeUnnecessaryNode(this.requestContent);

        JsonObject element = (JsonObject) requestContent.get(PARENT_NODE_NAME);
        Assert.assertNull(element);
//        JsonObject requestContent = JsonDataServiceImplTest.requestContent.getRequestContent();
        JsonArray elements = requestContent.getAsJsonArray(phoneNode);
        for (int i = 0; i < elements.size(); i++) {
            JsonObject jsonElement = (JsonObject) elements.get(i);
            JsonArray checkNode = jsonElement.getAsJsonArray("dfield");
            switch (i) {
                case 0:
                    Assert.assertNotNull(checkNode);
                    break;
                case 1:
                    Assert.assertNull(checkNode);
                    break;
            }
        }
    }

    @Test
    public void test_primary_removeUnnecessaryNode() throws Exception {
        JsonObject requestContent = jsonDataService.removeUnnecessaryNode(this.requestContent);

        JsonArray elements = requestContent.getAsJsonArray(phoneNode);
        Assert.assertNull(elements);
    }
}