package com.iisigroup.cap.auth.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.iisigroup.cap.auth.exception.CapAuthenticationException;
import com.iisigroup.cap.utils.GsonUtil;

@Component("ajaxAuthenticationFailureHandler")
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        boolean capchaEnabled = ((CapAuthenticationException) exception).isCaptchaEnabled();
        boolean forceChangePwd = ((CapAuthenticationException) exception).isForceChangePwd();
        boolean askChangePwd = ((CapAuthenticationException) exception).isAskChangePwd();
        Map<String, Object> o = new HashMap<String, Object>();
        o.put("capchaEnabled", capchaEnabled);
        o.put("forceChangePwd", forceChangePwd);
        o.put("askChangePwd", askChangePwd);
        o.put("msg", exception.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, URLEncoder.encode(GsonUtil.mapToJson(o), "utf-8").replaceAll("\\+", "%20"));
    }

}
