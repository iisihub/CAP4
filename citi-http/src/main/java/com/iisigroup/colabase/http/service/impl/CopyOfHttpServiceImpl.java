/* 
 * HTTPServiceImpl.java
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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
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
import com.iisigroup.colabase.http.service.CopyOfHttpService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

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
public class CopyOfHttpServiceImpl implements CopyOfHttpService {

	private static Logger logger = LoggerFactory.getLogger(CopyOfHttpServiceImpl.class);

	/**
	 * 接收 傳送來的資料欄位名稱
	 */
	public String[] receiveCols = new String[] { "Otp1", "Otp2", "SubmitTime" };
//	public String defaultSendUrl = "http://127.0.0.1:8098/citi-web/demohttphandler/httpReceive";
	public String defaultSendUrl = "https://127.0.0.1:8443/http-test-server/v1/tw/sendTest";
    // String defaultSendUrl = "https://uat.newwebatm.citibank.com.tw/extfunc16/mltpreceivemfshandler/receiveMFSUpdateMessage";
	// String defaultSendUrl = "http://127.0.0.1:8098/citi-web/demohttphandler/httpReceive";
    // defaultSendUrl = "https://127.0.0.1:8443/mutual-authentication-server/v1/tw/onboarding/customers/deduplicationFlag";
    // defaultSendUrl = "http://127.0.0.1:8888/mutual-authentication-server/v1/tw/onboarding/customers/deduplicationFlag";

    public Result sendUrlEncodedForm(Map<String, String> request, String sendUrl, String[] sendCols, boolean isTestMode) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
//        HttpServletRequest sreq = (HttpServletRequest) request.getServletRequest();

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
            // HttpClient httpClient = HttpClientBuilder.create().build();
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
            // Basic POST (Using new UrlEncodedFormEntity(new
            // ArrayList<NameValuePair>()))
            httppost.addHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8");
            // ??
            // httppost.addHeader("Cache-Control", "no-cache");
            // httppost.addHeader("X-Requested-With", "XMLHttpRequest");
            // httppost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            httppost.setEntity(new UrlEncodedFormEntity(postParameters));

            /**
             * handle response here...
             */
            HttpResponse response = null;
            HttpEntity entity = null;
            String responseString = null;
            logger.debug("SSL.TrustStore >> " + System.getProperty("javax.net.ssl.trustStore"));
            // logger.debug("SSL.TrustStore >> " + System.getProperty("javax.net.ssl.trustStorePassword"));
            logger.debug("SSL.KeyStore >> " + System.getProperty("javax.net.ssl.keyStore"));
            try {
                response = httpClient.execute(httppost);
                entity = response.getEntity();
                responseString = EntityUtils.toString(entity, "UTF-8");
            } catch (Exception e) {
                logger.debug("Send Data Exception >>> " + e.getMessage(), e);
            }
            result.set("responseString", responseString);
//            result.set("httpResponse", response.toString());
//            result.set("statusLine", response.getStatusLine().toString());
        } catch (Exception ex) {
            logger.error("Http Send fail >>> " + ex.getMessage(), ex);
        } finally {
            // Deprecated
            // httpClient.getConnectionManager().shutdown();
        }

        return result;
    }
	
    public Result sendJSON(Map<String, String> request, String sendUrl, JSONObject json, boolean isTestMode) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
//        HttpServletRequest sreq = (HttpServletRequest) request.getServletRequest();

        /**
         * prepare data
         */
//        JSONObject json = new JSONObject();
        // POST with JSON (Using new StringEntity((new JSONObject()).toString(), "UTF-8"))
//        for (String colName : sendCols) {
//            json.put(colName, request.get(colName));
//        }
        /**
         * send HTTP post data use JSON
         */
        CloseableHttpClient httpClient = null;
        if (isTestMode) {
            // HttpClient httpClient = HttpClientBuilder.create().build();
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
            logger.debug("sendData::" + json.toString());
            StringEntity params = new StringEntity(json.toString(), "UTF-8");// 傳json字串到後端
             params.setChunked(true);
            // params.setContentType("application/x-www-form-urlencoded;charest=utf-8");
            params.setContentType("application/json");
            params.setContentEncoding("utf-8");
            httppost.addHeader("content-type", "application/json;charset=utf-8");
            httppost.addHeader("X-Requested-With", "XMLHttpRequest");
            httppost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            httppost.setEntity(params);

            /**
             * handle response here...
             */
            HttpResponse response = null;
            HttpEntity entity = null;
            String responseString = null;
            JSONObject resultJSON = null;
            logger.debug("SSL.TrustStore >> " + System.getProperty("javax.net.ssl.trustStore"));
            // logger.debug("SSL.TrustStore >> " + System.getProperty("javax.net.ssl.trustStorePassword"));
            logger.debug("SSL.KeyStore >> " + System.getProperty("javax.net.ssl.keyStore"));
            try {
                response = httpClient.execute(httppost);
                entity = response.getEntity();
                responseString = EntityUtils.toString(entity, "UTF-8");
                resultJSON = JSONObject.fromObject(responseString);
            } catch (Exception e) {
                logger.debug("Send Data Exception >>> " + e.getMessage(), e);
            }
            result.set("responseString", responseString);
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

		String refPath = sreq.getHeader("referer");
		String contentType = StringEscapeUtils.unescapeHtml(sreq.getHeader("Content-Type"));
		refPath = StringEscapeUtils.unescapeHtml(refPath);
		String path = sreq.getPathInfo();
		Enumeration paramNames = sreq.getParameterNames();
		String requestData = null;
		JSONObject js = new JSONObject();
		Set<Entry<String, Object>> setData = request.entrySet();
		if (setData != null && setData.size() >= 1) {
			for (Entry<String, Object> entry : setData) {
				logger.debug("SITTEST>>Key:" + entry.getKey() + "=Value:" + entry.getValue());
			}
		}
		logger.debug("Request::Header::Content-type=" + contentType);
		Enumeration headers = sreq.getHeaderNames();
		while (headers.hasMoreElements()) {
			Object obj = headers.nextElement();
			if (obj != null) {
				logger.debug("ReceiveHeader=>>" + obj + "||value=>>"
						+ StringEscapeUtils.unescapeHtml(sreq.getHeader((String) obj)));
			}
		}
		/* 2017/8/8,Tim,for uat header 問題 read Raw Json data method1 */
//		try {
//			js = readHttpServletRequestJsonData(sreq);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			result.set("status_msg", "Read JsonString error: " + e.getLocalizedMessage());
//			result.set("status_code", 504);
//			return result;
//		}
		if (contentType == null) {
			logger.debug("ReceiveContentTypeError=>>" + contentType + "||onlySupport:application/json");
			result.set("status_msg", "ReceiveContentTypeError=>>" + contentType + "is null");
			result.set("status_code", 507);
			return result;
		}
		if (contentType.indexOf("application/json") != -1) {
			/* read Raw Json data method1 */
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
			if (!request.containsKey("CustomerID")) {
				while (paramNames.hasMoreElements()) {
					Object obj = paramNames.nextElement();
					if (obj != null) {
						logger.debug("ReceiveDataKey=>>" + obj);
						logger.debug("ReceiveDataValue=>>" +request.get(obj));
//						requestData = (String) obj;
						js.put(obj, request.get(obj));
					}
				}
				try {
                    if (!js.isEmpty()) {
                        logger.debug("RequestXWWWForm2Json>>CustomerID=" + js.opt("CustomerID"));
                        logger.debug("RequestXWWWForm2Json>>CustomerNo=" + js.opt("CustomerNo"));
                    }
//					if (requestData != null) {
//						js = (JSONObject) JSONSerializer.toJSON(requestData);
//						if (!js.isEmpty()) {
//							logger.debug("RequestXWWWForm2Json>>CustomerID=" + js.opt("CustomerID"));
//							logger.debug("RequestXWWWForm2Json>>CustomerNo=" + js.opt("CustomerNo"));
//						}
//					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					result.set("status_msg", "Get x-www-form-data error: " + e.getLocalizedMessage());
					result.set("status_code", 505);
					return result;
				}
			}
		} else {
			logger.debug("ReceiveContentTypeError=>>" + contentType + "||onlySupport:application/json");
			result.set("status_msg", "ReceiveContentTypeError=>>" + contentType
					+ "||onlySupport:application/json or application/x-www-form-urlencoded");
			result.set("status_code", 506);
			return result;
		}
		/* Start check data length */
		String val = "";
		int i = 0;
		boolean validationResult = true;
		List<String> checkErrors = new ArrayList<String>();

		try {

			/** 2017/7/27,解密密碼 */
//			String userID, password, decryptPassword;
//			if (!js.isEmpty()) {
//				userID = js.optString("UserID", "");
//				logger.debug("AESKEY == " + sysProp.get(CCConstants.AES_SECURITY_KEY));
//				logger.debug("UserJSON == " + userID);
//				if (CapString.isEmpty(userID)) {
//					result.set("status_msg", "Authentication UserID Fail");
//					result.set("status_code", 401);
//				}
//				userID = CCEncryptionUtils.stringToDeBase64(userID);
//				logger.debug("UserJSON decode == " + userID);
//				if (!userID.equals(sysProp.get(CCConstants.CONNECT_USERNAME))) {
//					result.set("status_msg", "Authentication UserID Fail");
//					result.set("status_code", 401);
//				}
//				password = js.optString("Password", "");
//				logger.debug("password JSON == " + password);
//				CCEncryptionUtils.encryptAESCBCNoPaddingToBase64(sysProp.get(CCConstants.AES_SECURITY_KEY), CCEncryptionUtils.iv, password);
//				// decryptPassword =
//				// CCEncryptionUtils.desEncryptAESCBCNoPaddingByBases64(sysProp.get(CCConstants.AES_SECURITY_KEY)
//				// , CCEncryptionUtils.iv, password);
//				// logger.debug("decryptPassword == " + decryptPassword);
//				if (CapString.isEmpty(password)) {
//					result.set("status_msg", "Authentication Password Fail");
//					result.set("status_code", 401);
//				}
//				String dbPassword = sysProp.get(CCConstants.CONNECT_PASSWORDBASE64);
//				logger.debug("dbPassword == " + dbPassword);
//				if (!password.equals(dbPassword)) {
//					result.set("status_msg", "Authentication Password Fail");
//					result.set("status_code", 401);
//				}
//			} else {
//				result.set("status_msg", "Authentication Data Fail");
//				result.set("status_code", 401);
//			}
//			if (result.get("status_code") != null && !CapString.isEmpty(result.get("status_code").toString())) {
//				return result;
//			}

//			// prepare Log File
//			StringBuffer logstr;
//			Date today = Calendar.getInstance().getTime();
//			File logFolder = new File("D:\\TEST\\TEST_HTTP");
//			if (!logFolder.exists()) {
//				FileUtils.forceMkdir(logFolder);
//			}
//			File actlog = new File(logFolder.getAbsolutePath().concat(File.separator).concat("MLTP_RECEIVEMFS_")
//					.concat(FORMAT_PAGE_LOG_FILE_NAME.format(today)).concat(POSTFIX_LOG_FILE2));
//
//			if (!actlog.exists()) {
//				if (!actlog.createNewFile())
//					logger.error("HTTP Receive log can not create!");
//			}
//			// log格式: 日期時間, IP address, OS, Browser, 身分證號碼, Page#, OTP發送電話號碼
//			logstr = new StringBuffer(FORMAT_LOG_DATETIME.format(today) + ">>");
//
//			for (String colID : receiveCols) {
//				val = request.get(colID, "");
//				if (CapString.isEmpty(val) && !js.isEmpty()) {
//					val = js.optString(colID, "");
//				}
//				logstr.append(colID + "[" + val + "],");
//				logger.debug("Receive MFS Data=>COLUME[" + colID + "] = VALUE[" + val + "]");
//				// Check data Length
//				String[] checkResult = checkDataLength(val, receiveCheck[i], colID);
//				if ("false".equals(checkResult[0])) {
//					validationResult = false;
//					checkErrors.add(checkResult[1]);
//				}
//				i++;
//				String checkStr = val.toLowerCase();
//				// 資料不應該出現sql資料
//				if (checkStr.indexOf("insert") != -1 || checkStr.indexOf("update") != -1
//						|| checkStr.indexOf("delete") != -1) {
//					checkStr = checkStr.replaceAll("insert", "");
//					checkStr = checkStr.replaceAll("update", "");
//					checkStr = checkStr.replaceAll("delete", "");
//					logger.debug("Receive MFS Data illegal =>COLUME[" + colID + "] = VALUE[" + val + "]");
//					val = checkStr;
//				}
//			} // for end
//			if (logstr.lastIndexOf(",") != -1) {
//				logstr.deleteCharAt(logstr.lastIndexOf(","));
//			}
//			logstr.append("\r\n");
//			write(actlog, logstr.toString());

			// 檢查資料長度無異常，才可以存入DB
			i = 0;
			if (validationResult) {
			    // TODO
			} else {
				StringBuffer sb = new StringBuffer();
				if (!checkErrors.isEmpty() && checkErrors.size() > 0) {
					for (String col : checkErrors) {
						sb.append(col + ",");
					}
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				result.set("status_msg", "check data length fail:" + "errorCols=[" + sb.toString() + "]");
				result.set("status_code", 503);
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
        /* read Raw Json data method2 */
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
