package com.iisigroup.colabase.address.handler;

import com.google.gson.Gson;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.model.Address;
import com.iisigroup.colabase.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 *          <li>2018/08/02,AndyChen,new
 *          </ul>
 * @since 2018/08/02
 */
@Controller("demoaddresshandler")
public class AddressHandler extends MFormHandler {

    private static final Logger jLogger = LoggerFactory.getLogger(AddressHandler.class);
    private final String RESULT_KEY = "result";

    @Autowired
    private AddressService addressService;


    public Result testAddressNormal(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String addressStr = request.get("address");
        if(CapString.isEmpty(addressStr)) {
            result.set(RESULT_KEY, "address is empty!");
            return result;
        }
        try {
            Address address = addressService.normalizeAddress(addressStr);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(address);
            result.set(RESULT_KEY, jsonStr);
        } catch (Exception e) {
            result.set(RESULT_KEY, e.toString());
        }
        return result;
    }

}
