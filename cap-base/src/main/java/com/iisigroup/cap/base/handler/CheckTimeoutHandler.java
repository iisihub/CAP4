/* 
 * CCCheckRouteHandler.java
 * 
 * Copyright (c) 2009-2013 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.base.CapSystemProperties;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * Check timeout handler
 * </pre>
 * 
 * @since 2014/4/2
 * @author TimChiang
 * @version
 *          <ul>
 *          <li>2014/4/2,new
 *          <li>2014/4/18,Sunkist Wang,update get sysProp
 *          </ul>
 */
@Controller("checktimeouthandler")
public class CheckTimeoutHandler extends MFormHandler {

    @Resource
    private CapSystemProperties sysProp;

	public static final String TOCM = "TOCM";
	public static final String CCPAGE_NO = "CCPAGENO";
	public static final String TIME_OUT = "CLIENT_TIME_OUT";

    public Result check(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        HttpServletRequest sreq = (HttpServletRequest) request.getServletRequest();
        // String path = sreq.getPathInfo();
        // boolean isNewSes = sreq.getSession(false).isNew();
        long time1 = sreq.getSession(false).getLastAccessedTime();
        long time2 = CapDate.getCurrentTimestamp().getTime();
        // session設定時間
        long time3 = sreq.getSession(false).getMaxInactiveInterval();
        // 讀取後台db設定（單位為分鐘）
        sysProp.remove(TIME_OUT);
        String stout = sysProp.get(TIME_OUT);
        if (!CapString.isEmpty(stout)) {
            time3 = Long.parseLong(stout);
            time3 = time3 * 60;
        }
        String isContinues = request.get("isContinues");
        // Calculate difference in milliseconds
        long diff = time2 - time1;
        // Difference in seconds
        long diffSec = diff / 1000;
        // session timeout 導向 error page
        String refPath = sreq.getHeader("referer");
        refPath = StringEscapeUtils.unescapeHtml(refPath);
        if ((diffSec > time3 && refPath.lastIndexOf("error") < 0 && refPath.lastIndexOf("timeout") < 0) || "false".equals(isContinues)) {
            // if(!isNewSes){
            result.set("errorPage", "/cap-web/page/timeout");
            sreq.getSession(false).invalidate();
            // }
        }
        result.set("msg", "cccheked");
        return result;
    }

	@SuppressWarnings({ "unchecked", "unused" })
	public Result checkTO(Request request) throws CapException {
		AjaxFormResult result = new AjaxFormResult();
		HttpServletRequest sreq = (HttpServletRequest)request.getServletRequest();

		String refPath = sreq.getHeader("referer");
		refPath = StringEscapeUtils.unescapeHtml(refPath);
		String path = sreq.getPathInfo();
		boolean isNewSes = sreq.getSession(false).isNew();
		String isFresh = request.get("REFSH_TO","");
		
		HttpSession session = sreq.getSession(false);
		Map<String, String> map = (Map<String, String>)session.getAttribute(TOCM);
		
		String curPage = request.get(CCPAGE_NO);
		
		if(curPage.lastIndexOf("ap")>=0){
			if(map!=null && map.containsKey(curPage)){
				//DO NOT THING
			}else{
				map = new HashMap<String, String>();
				session.setAttribute(TOCM, map);
			}
		}
		if(map == null){
			map = new HashMap<String, String>();
		}
		if(!CapString.isEmpty(curPage)){
			if(map.containsKey(curPage) && !"Y".equals(isFresh)){
				String openTime = map.get(curPage);
				
				//讀取後台db設定（單位為分鐘）
				sysProp.remove(TIME_OUT);
				String stout = sysProp.get(TIME_OUT);
				if(CapString.isEmpty(stout)){
					stout = "10"; //default 10mins
				}
				
				//判斷自己是否為最後開啟的一頁
				String lastPageNo = getLastRcordTOMC(map);
				if(lastPageNo.compareTo(curPage)!=0){
					/* 如果最後開啟的一頁，已經超過timeout設定時間，即清除該筆記錄
					 * 因為這一頁可能已經被前端關掉了
					 */
					if(map.get(lastPageNo)!=null){
						long lastOpenTime = Long.parseLong(map.get(lastPageNo));
			            // Calculate difference in milliseconds
						long curTime = CapDate.getCurrentTimestamp().getTime();
			            long diff = curTime - lastOpenTime;
			            // Difference in seconds
			            long diffSec = diff / 1000;
			            long propTimeout = (Long.parseLong(stout)+1)*60;
			            if(diffSec>propTimeout){
			            	map.remove(lastPageNo);
			            	session.setAttribute(TOCM, map);
			            }
					}
					//自己不是最後一頁就回前端，不用跳出提示視窗
					return result;
				}
				
				long time = Long.parseLong(openTime);
				Timestamp ts1 = new Timestamp(time);
				String d12str = CapDate.convertTimestampToString(ts1, "HH:mm:ss.sss");
				long curTime = CapDate.getCurrentTimestamp().getTime();
				long propTimeOut = sreq.getSession(false).getMaxInactiveInterval();
		        if(!CapString.isEmpty(stout)){
		        	propTimeOut = Long.parseLong(stout);
		        	long remindTime = (propTimeOut-1)*60;
		        	propTimeOut = propTimeOut*60;
		            // Calculate difference in milliseconds
		            long diff = curTime - time;
		            // Difference in seconds
		            long diffSec = diff / 1000;
		            String isContinues = request.get("isCntnu");
		            if("true".equals(isContinues)){
		            	map.put(curPage, String.valueOf(curTime));
		            	session.setAttribute(TOCM, map);
		            }else if(CapString.isEmpty(isContinues) && diffSec>=remindTime){
		            	result.set("SHOW_REMIND", "true");
		            }
		            //前端回傳false表示timeout(user手動選取消,或是等1分鐘自動取消)
		            else if("false".equals(isContinues) || diffSec>propTimeOut){
		            	String webApUrl = sysProp.get("WEB_AP_URL");
		            	String st1 = sreq.getScheme();
	    				String st2 = sreq.getServerName();
	    				int port = sreq.getServerPort();
	    				String st4 = sreq.getContextPath();
	    				webApUrl = st1 + "://" + st2 + ":" + port + st4;
		            	result.set("errorPage", st4+"/page/timeout");
		            	map.remove(curPage);
						session.setAttribute(TOCM, map);
		            }
		        }
			}else{
				long curTime = CapDate.getCurrentTimestamp().getTime();
				map.put(curPage, String.valueOf(curTime));
				session.setAttribute(TOCM, map);
			}
		}
		return result;
	}
	
	//@SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	public Result checkClosePage(Request request) throws CapException {
		AjaxFormResult result = new AjaxFormResult();
		HttpServletRequest sreq = (HttpServletRequest)request.getServletRequest();

        String refPath = sreq.getHeader("referer");
        refPath = StringEscapeUtils.unescapeHtml(refPath);
        
        // boolean isNewSes = sreq.getSession(false).isNew();
		
		HttpSession session = sreq.getSession(false);
		Map<String, String> map = (Map<String, String>)session.getAttribute(TOCM);
		Map<String, Object> pmor = (Map<String, Object>)session.getAttribute("pmorq");
		if(pmor!=null){
			String cleanPreMoice = request.get("CLNPREMOICA");
			if("Y".equals(cleanPreMoice)){
				session.removeAttribute("pmorq");
			}
		}
		if(map!=null){
			String curPage = request.get(CCPAGE_NO);
			if(map.containsKey(curPage)){
				map.remove(curPage);
				session.setAttribute(TOCM, map);
			}
		}
		
//		if(refPath.indexOf("moica")!=-1){
//			String SERVER_O_REQUEST = "sorq";
//			session.setAttribute(SERVER_O_REQUEST, null);
//		}
		return result;
	}
	
	private String getLastRcordTOMC(Map<String, String> map){
		List<Map.Entry<String, String>> list_Data =
	            new ArrayList<Map.Entry<String, String>>(map.entrySet());
		Collections.sort(list_Data, new Comparator<Map.Entry<String, String>>(){
            public int compare(Map.Entry<String, String> entry1,
                               Map.Entry<String, String> entry2){
                return (int) (Long.parseLong(entry2.getValue()) - Long.parseLong(entry1.getValue()));
            }
        });
		
		return list_Data.get(0).getKey();
	}
}
