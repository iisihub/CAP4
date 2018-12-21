package com.iisigroup.colabase.demo.handler;

import com.iisigroup.cap.mvc.handler.MFormHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.colabase.va.service.VAService;

/**
 * <pre>
 * demo use handler
 * </pre>
 * @since  2018年4月2日
 * @author Roger
 * @version <ul>
 *           <li>2018年4月2日,skunk,new
 *          </ul>
 */
@Controller("demomoicahandler")
public class DemoMoicaHandler extends MFormHandler {
	
    @Autowired
    private VAService vaService;

    public Result demoMoica(Request params) {
      String personalId = params.get("id");
      String p7b = params.get("PKCS7Data");
//      p7b = URLDecoder.decode(p7b, "UTF-8");
      String msg = vaService.doVerifyPKCS7(personalId, p7b);
      AjaxFormResult result = new AjaxFormResult();
      result.set("msg", msg);
      return result;
    }
    
    public Result genP7bData(Request params) {
        AjaxFormResult result = new AjaxFormResult();
        String p7b = params.get("PKCS7Data");
        String p7bPath = params.get("p7bPath");
        byte[] p7byte = Base64.decodeBase64(p7b);
        try (FileOutputStream os = new FileOutputStream(p7bPath);){
            os.write(p7byte);
            result.set("msg", "gen p7bData success");
        } catch (IOException e) {
            result.set("msg", "gen p7bData fail");
        }
        return result;
    }


}
