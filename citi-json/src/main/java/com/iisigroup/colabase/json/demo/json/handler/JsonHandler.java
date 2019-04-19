package com.iisigroup.colabase.json.demo.json.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.iisigroup.colabase.json.demo.json.model.DemoModel;
import com.iisigroup.colabase.json.model.JsonAbstract;
import com.iisigroup.colabase.json.util.JsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.mvc.handler.MFormHandler;


/**
 * <pre>
 * demo use handler
 * </pre>
 *
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/05/25,AndyChen,new
 *          </ul>
 * @since 2018/05/25
 */
@Controller("demojsonhandler")
public class JsonHandler extends MFormHandler {

    private static final Logger jLogger = LoggerFactory.getLogger(JsonHandler.class);


    public Result testSslClient(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String parentName = request.get("parentName", "");
        String name = request.get("name", "");
        String sex = request.get("sex", "");
        String phoneNumber1 = request.get("phoneNumber1", "");
        String phoneNumber2 = request.get("phoneNumber2", "");
        String phoneArea1 = request.get("phoneArea1", "");
        String phoneArea2 = request.get("phoneArea2", "");

        DemoModel model = JsonFactory.getInstance(DemoModel.class);
        List<String> noSDatas = this.getNoVNoSData(request, "nVnS_");
        List<String> priDatas = this.getNoVNoSData(request, "pri_");

        this.setListToObj(model, "noSendList", noSDatas);
        this.setListToObj(model, "primaryCleanList", priDatas);

        model.setParentName(parentName);
        model.setName(name);
        model.setSex(sex);
        model.setPhoneNumber(phoneNumber1);
        model.setPhoneArea(phoneArea1);
        model.setPhoneNumber(phoneNumber2);
        model.setPhoneArea(phoneArea2);

        result.set("result", model.getJsonString());
        return result;
    }

    private void setListToObj(JsonAbstract model, String fieldName, List<String> list) {
        try {
            Field declaredField = JsonAbstract.class.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(model, list);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            jLogger.debug("something wrong");
        }

    }

    private List<String> getNoVNoSData(Request request, String perfix) {
        String parentName = request.get(perfix + "parentName", "");
        String name = request.get(perfix + "name", "");
        String sex = request.get(perfix + "sex", "");
        String phoneNumber = request.get(perfix + "phoneNumber", "");
        String phoneArea = request.get(perfix + "phoneArea", "");

        List<String> rsult = new ArrayList<>();
        if("1".equals(parentName))
            rsult.add("personDetails.parent.name.fullName");
        if("1".equals(name))
            rsult.add("personDetails.name");
        if("1".equals(sex))
            rsult.add("personDetails.sex");
        if("1".equals(phoneNumber))
            rsult.add("phone[].number");
        if("1".equals(phoneArea))
            rsult.add("phone[].area");

        return rsult;
    }
}
