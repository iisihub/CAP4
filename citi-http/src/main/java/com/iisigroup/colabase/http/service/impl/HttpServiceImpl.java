/* 
 * HttpServiceImpl.java
 * 
 * Copyright (c) 2009-2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.http.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.colabase.http.response.CrossDomainAjaxFormResult;
import com.iisigroup.colabase.http.service.HttpService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapString;

import net.sf.json.JSONObject;

/**
 * <pre>
 * HTTP Send and Receive Service
 * </pre>
 * 
 * @since 2017/5/31
 * @author TimChiang
 * @version
 *          <ul>
 *          <li>2017/5/31,new
 *          <li>2017/12/1,Tim,ignore SSL check(trust all CAcert)
 *          </ul>
 */
@Service
public class HttpServiceImpl implements HttpService {

	private static Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);

	/**
	 * 接收 傳送來的資料欄位名稱
	 */
	public String defaultSendUrl = "https://127.0.0.1:8443/http-test-server/v1/tw/sendTest";
//    public String defaultSendUrl = "http://127.0.0.1:8098/citi-web/demohttphandler/httpReceiveTest";

    public Result sendUrlEncodedForm(Map<String, String> request, String sendUrl, String[] sendCols, boolean isTestMode) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        /**
         * prepare data
         */
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        // Basic POST (Using new UrlEncodedFormEntity(new ArrayList<NameValuePair>()))
        for (String colName : sendCols) {
            postParameters.add(new BasicNameValuePair(colName, request.get(colName)));
        }
        /**
         * send HTTP post data
         */
        CloseableHttpClient httpClient = null;
        if (isTestMode) {
            httpClient = HttpClientBuilder.create().build();
        } else {
            final SSLConnectionSocketFactory sslsf;
            try {
                // Ignore the SSL certificate and host name verifier
                SSLContextBuilder sslBuilder = new SSLContextBuilder();
                sslBuilder.loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        return true;
                    }
                }).build();
                sslsf = new SSLConnectionSocketFactory(sslBuilder.build(), new String[] { "TLSv1", "SSLv3" }, null, new TrustAllHostNameVerifier());
            } catch (NoSuchAlgorithmException e) {
                logger.error("sendHTTPData >> NoSuchAlgorithmException::" + e.getLocalizedMessage(), e);
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                logger.error("sendHTTPData >> KeyManagementException::" + e.getLocalizedMessage(), e);
                throw new RuntimeException(e);
            } catch (KeyStoreException e) {
                logger.error("sendHTTPData >> KeyStoreException::" + e.getLocalizedMessage(), e);
                throw new RuntimeException(e);
            }
            final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();
            final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(100);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).build();
        }
        try {
            if (CapString.isEmpty(sendUrl)) {
                sendUrl = defaultSendUrl;
            }
            logger.debug("Send Data URL => ", sendUrl);
            HttpPost httppost = new HttpPost(sendUrl);
            httppost.addHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8");
            httppost.addHeader("X-Requested-With", "XMLHttpRequest");
            // httppost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            httppost.setEntity(new UrlEncodedFormEntity(postParameters));
            /**
             * handle response here...
             */
            HttpResponse response = null;
            HttpEntity entity = null;
            try {
                response = httpClient.execute(httppost);
                entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                result.set("responseString", responseString);
                int statusCode = response.getStatusLine().getStatusCode();
                result.set("statusCode", statusCode);
            } catch (Exception e) {
                logger.debug("Send Data Exception >>> " + e.getMessage(), e);
            }
        } catch (Exception ex) {
            logger.error("Http Send fail >>> " + ex.getMessage(), ex);
        } finally {
            // Deprecated
            // httpClient.getConnectionManager().shutdown();
        }
        return result;
    }
	
    public Result sendJson(Map<String, String> request, String sendUrl, String jsonStr, boolean isTestMode) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        /**
         * send HTTP post data use JSON
         */
        CloseableHttpClient httpClient = null;
        if (isTestMode) {
            httpClient = HttpClientBuilder.create().build();
        } else {
            final SSLConnectionSocketFactory sslsf;
            try {
                // Ignore the SSL certificate and host name verifier
                SSLContextBuilder sslBuilder = new SSLContextBuilder();
                sslBuilder.loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        return true;
                    }
                }).build();
                sslsf = new SSLConnectionSocketFactory(sslBuilder.build(), new String[] { "TLSv1", "SSLv3" }, null, new TrustAllHostNameVerifier());
            } catch (NoSuchAlgorithmException e) {
                logger.error("sendHTTPData >> NoSuchAlgorithmException::" + e.getLocalizedMessage(), e);
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                logger.error("sendHTTPData >> KeyManagementException::" + e.getLocalizedMessage(), e);
                throw new RuntimeException(e);
            } catch (KeyStoreException e) {
                logger.error("sendHTTPData >> KeyStoreException::" + e.getLocalizedMessage(), e);
                throw new RuntimeException(e);
            }

            final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();

            final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(100);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).build();
        }
        try {
            if (CapString.isEmpty(sendUrl)) {
                sendUrl = defaultSendUrl;
            }
            logger.debug("Send Data URL => ", sendUrl);
            HttpPost httppost = new HttpPost(sendUrl);

            // POST with JSON (Using new StringEntity((new JSONObject()).toString(), "UTF-8"))
            logger.debug("sendData::" + jsonStr);
            StringEntity params = new StringEntity(jsonStr, "UTF-8");// 傳json字串到後端
            params.setChunked(true);
            params.setContentType("application/json");
            params.setContentEncoding("utf-8");
            httppost.addHeader("content-type", "application/json;charset=utf-8");
            httppost.addHeader("X-Requested-With", "XMLHttpRequest");
//            httppost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            httppost.setEntity(params);
            /**
             * handle response here...
             */
            HttpResponse response = null;
            HttpEntity entity = null;
            try {
                response = httpClient.execute(httppost);
                entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                result.set("responseString", responseString);
                int statusCode = response.getStatusLine().getStatusCode();
                result.set("statusCode", statusCode);
            } catch (Exception e) {
                logger.debug("Send Data Exception >>> " + e.getMessage(), e);
            }
        } catch (Exception ex) {
            logger.error("Http Send fail >>> " + ex.getMessage(), ex);
        } finally {
            // Deprecated
            // httpClient.getConnectionManager().shutdown();
        }

        return result;
    }
    
	@SuppressWarnings("unchecked")
	public Result receiveData(Request request) throws CapException {
		CrossDomainAjaxFormResult result = new CrossDomainAjaxFormResult();
		result.setCallback(request.get("callback"));
		result.setCorsDomain("*");
		HttpServletRequest sreq = (HttpServletRequest) request.getServletRequest();
        JSONObject js = new JSONObject();

        try {
            String contentType = StringEscapeUtils.unescapeHtml(sreq.getHeader("Content-Type"));
            String refPath = sreq.getHeader("referer");
            refPath = StringEscapeUtils.unescapeHtml(refPath);
            logger.debug("Request::Header::Content-type=" + contentType);
            logger.debug("ReceiveReferer=>>" + refPath);
            Enumeration headers = sreq.getHeaderNames();
            while (headers.hasMoreElements()) {
                Object obj = headers.nextElement();
                if (obj != null) {
                    logger.debug("ReceiveHeader=>>" + obj + "||value=>>" + StringEscapeUtils.unescapeHtml(sreq.getHeader((String) obj)));
                }
            }
            if (contentType == null) {
                logger.debug("ReceiveContentTypeError=>>" + contentType);
                result.set("status_msg", "ReceiveContentTypeError=>>" + contentType + "is null");
                result.set("status_code", 507);
                return result;
            }
            if (contentType.indexOf("application/json") != -1) {
                try {
                    if (js == null || js.isNullObject() || js.isEmpty()) {
                        js = readHttpServletRequestJsonData(sreq);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    result.set("status_msg", "Read JsonString error: " + e.getLocalizedMessage());
                    result.set("status_code", 504);
                    return result;
                }
            } else if (contentType.indexOf("application/x-www-form-urlencoded") != -1) {
                Enumeration paramNames = sreq.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    Object obj = paramNames.nextElement();
                    if (obj != null) {
                        logger.debug("ReceiveDataKey=>>" + obj);
                        logger.debug("ReceiveDataValue=>>" + request.get(obj));
                        js.put(obj, request.get(obj));
                    }
                }
            } else {
                logger.debug("ReceiveContentTypeError=>>" + contentType + "||onlySupport:application/json or application/x-www-form-urlencoded");
                result.set("status_msg", "ReceiveContentTypeError=>>" + contentType + "||onlySupport:application/json or application/x-www-form-urlencoded");
                result.set("status_code", 506);
                return result;
            }
            try {
                if (!js.isEmpty()) {
                    logger.debug("status_msg:{}", "Receive Data>>Success");
                    logger.debug("status_code:{}", 200);
                    logger.debug("receive content:{}", js);
                    result.set("status_msg", "Receive Data>>Success");
                    result.set("status_code", 200);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result.set("status_msg", "Read data error: " + e.getLocalizedMessage());
                result.set("status_code", 505);
                return result;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.set("status_msg", "get exception error>>" + e.getLocalizedMessage());
            result.set("status_code", 500);
        }
        return result;
	}

    private static class TrustAllHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

	private JSONObject readHttpServletRequestJsonData(HttpServletRequest sreq) throws IOException {
        logger.debug("Start Receive JSON data>>>");
        JSONObject js = new JSONObject();
        StringBuffer jb = new StringBuffer();
        String line = null;
        BufferedReader reader = sreq.getReader();
        while ((line = reader.readLine()) != null) {
            jb.append(line);
        }
        JSONObject jsonObject = JSONObject.fromObject(jb.toString());
        if (jsonObject != null) {
            js = jsonObject;
        }
        logger.debug("END Receive JSON become>>>" + jb.toString());
        return js;
    }

	private static class ContentLengthHeaderRemover implements HttpRequestInterceptor {
		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			request.removeHeaders(HTTP.CONTENT_LEN);// fighting org.apache.http.protocol.RequestContent's
													// ProtocolException("Content-Length header already present");
		}
	}

}
