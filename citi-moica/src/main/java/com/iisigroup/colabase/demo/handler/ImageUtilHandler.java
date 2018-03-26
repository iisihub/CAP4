package com.iisigroup.colabase.demo.handler;

import com.iisigroup.cap.mvc.handler.MFormHandler;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.mvc.i18n.MessageBundleScriptCreator;

/**
 * <pre>
 * demo use handler
 * </pre>
 * 
 * @since 2018/03/12
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/03/12,AndyChen,new
 *          </ul>
 */
@Controller("demoimageutilhandler")
public class ImageUtilHandler extends MFormHandler {

    public Result demo(Request params) {
        return new AjaxFormResult();
    }


}
