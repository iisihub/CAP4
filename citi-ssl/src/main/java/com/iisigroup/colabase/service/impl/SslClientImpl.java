package com.iisigroup.colabase.service.impl;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.*;

import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;
import com.iisigroup.colabase.service.SslClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class SslClientImpl implements SslClient {


  private final Logger logger = LoggerFactory.getLogger(getClass());

  private SSLSocketFactory sslSocketFactory;

  public SslClientImpl() {
  }

  public SslClientImpl(String keyStorePath, String keyStorePWD, String trustStorePath) throws
          CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
      sslSocketFactory = getSSLSocketFactory(keyStorePath, keyStorePWD, trustStorePath);
    }

  /**
   *
   * @param requestContent
   * @return
   * @throws IOException
     */
  @Override
  public ResponseContent sendRequest(RequestContent requestContent) throws IOException {
    for (int i = 0; i <= requestContent.getRetryTimes(); i++) {
      try {
        ResponseContent responseContent = this.clientSendRequest(requestContent);
        if(this.isStatusNeedToRetry(requestContent.getRetryHttpStatus(), responseContent.getStatusCode())) {
          continue;
        }
        return responseContent;
      } catch (IOException e) {
        if (i == requestContent.getRetryTimes())
          throw e;
      }
    }
    throw new IOException();
  }

  private boolean isStatusNeedToRetry(int[] retryHttpStatus, int httpStatus){
    if (retryHttpStatus == null) {
      return false;
    }
    for (int status : retryHttpStatus) {
      if(httpStatus == status)
        return true;
    }
    return false;
  }

  @Override
  public ResponseContent sendRequestWithDefaultHeader(RequestContent requestContent) throws IOException {
    Map<String, List<String>> requestHeaders = new HashMap<>();
    requestHeaders.put("Accept", Arrays.asList(new String[]{"application/json"}));
    requestHeaders.put("Content-Type", Arrays.asList(new String[]{"application/json; charset=UTF-8"}));
    requestHeaders.put("uuid", Arrays.asList(new String[]{UUID.randomUUID().toString()}));
    requestHeaders.put("channelId", Arrays.asList(new String[]{"COA"}));
    requestHeaders.put("businessCode", Arrays.asList(new String[]{"GCB"}));
    requestHeaders.put("countryCode", Arrays.asList(new String[]{"TW"}));
    requestHeaders.put("consumerOrgCode", Arrays.asList(new String[]{"COLA"}));
    requestContent.setRequestHeaders(requestHeaders);
    return this.sendRequest(requestContent);
  }

  private ResponseContent clientSendRequest(RequestContent requestContent) throws IOException {
    logger.debug("==== send dual ssl request start ====");
    long startTime = new Date().getTime();
    ArrayList<String> listForRecordInfos = new ArrayList<>();

    ResponseContent responseContent = null;
    int statusCode = 0;
    int timeOut = requestContent.getTimeout();
    RequestContent.HTTPMethod method = requestContent.getHttpMethod();
    String targetURL = requestContent.getTargetUrl();
    String jsonStr = requestContent.getRequestContent().toString();
    boolean isUseOwnSslFactory = requestContent.isUseOwnKeyAndTrustStore();
    Map<String, List<String>> requestHeaders = requestContent.getRequestHeaders();
    try {
      Date date = Calendar.getInstance().getTime();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String time = formatter.format(date);
      logger.debug("start time = " + time);
      logger.debug("Request: target = " + targetURL);
      // add listForRecordInfos
      listForRecordInfos.add("Request: time = " + time);

      HttpsURLConnection connection = (HttpsURLConnection) new URL(targetURL).openConnection();
      // 設定 HttpsURLConnection 的 SSLSocketFactory
      if(isUseOwnSslFactory)
        connection.setSSLSocketFactory(sslSocketFactory);
      logger.debug("Request: use own ssl factory = " + isUseOwnSslFactory);
      logger.debug("Request: need retry status = " + Arrays.toString(requestContent.getRetryHttpStatus()));

      connection.setConnectTimeout(timeOut);
      connection.setReadTimeout(timeOut);

      String requestMethod = method.toString();
      connection.setRequestMethod(requestMethod);
      logger.debug("Request: Method = " + requestMethod);

      // add listForRecordInfos
      listForRecordInfos.add("Request: Method = " + requestMethod);

      connection.setDoInput(true);
      if (!RequestContent.HTTPMethod.GET.equals(method)) {
        connection.setDoOutput(true);
      }

      Iterator<String> requestHeadersKeyIterator = requestHeaders.keySet().iterator();
      while (requestHeadersKeyIterator.hasNext()) {
        String requestHeaderKey = requestHeadersKeyIterator.next();
        List<String> requestHeaderValues = requestHeaders.get(requestHeaderKey);

        for (String requestHeaderValue : requestHeaderValues) {
          connection.setRequestProperty(requestHeaderKey, requestHeaderValue);

          logger.debug("Request Header Key: " + requestHeaderKey + ", Value: " + requestHeaderValue);

          // add listForRecordInfos
          listForRecordInfos.add("Request: " + requestHeaderKey + " = " + requestHeaderValue);
        }
      }
      requestContent.showRequestJsonStrLog(jsonStr);

      if (!RequestContent.HTTPMethod.GET.equals(method)) {
        try (
          OutputStream output = connection.getOutputStream();
          PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)
        ) {
          writer.write(jsonStr);
          output.flush();

          // add listForRecordInfos
          listForRecordInfos.add("Request: data = " + jsonStr);
        } catch (IOException e) {
          listForRecordInfos.add("Output data Exception = " + e.toString());
          logger.warn("Output data Exception = " + e.toString());
          throw e;
        }
      }

      InputStream is;

      statusCode = connection.getResponseCode();
      if (statusCode == 200) {
        is = connection.getInputStream();
      } else {
        is = connection.getErrorStream();
      }

      Map<String, List<String>> headers = connection.getHeaderFields();
      for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
        logger.debug("Response Header Key: " + entry.getKey() + ", Value: " + entry.getValue());

        // add listForRecordInfos
        listForRecordInfos.add("Response: Header Key = " + entry.getKey() + ", Value = " + entry.getValue());
      }

      JSONObject responseJson = new JSONObject();
      StringBuilder responseBodySB = new StringBuilder();
      try (
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))
      ) {
        String tempStr;
        while ((tempStr = reader.readLine()) != null) {
          responseBodySB.append(tempStr);
        }

        requestContent.showResponseJsonStrLog(responseBodySB.toString());
        if(responseBodySB.length()!=0) {
            responseJson = JSONObject.fromObject(responseBodySB.toString());
        }
        listForRecordInfos.add("Response Body : " + responseJson.toString());
      } catch(JSONException e) {
        responseJson = new JSONObject();
        listForRecordInfos.add("Response Body: " + responseBodySB);
        listForRecordInfos.add("Response Exception: " + e);
      } catch (IOException e) {
        throw e;
      }

      responseContent = new ResponseContent(statusCode, headers, responseJson);
    } catch (Exception e) {
      if(responseContent == null) {
        responseContent = new ResponseContent(statusCode, new HashMap<String, List<String>>(), new JSONObject());
        responseContent.setException(e);
      }
      throw e;
    } finally {
      final ResponseContent renewResponseContent = responseContent;
        // 由於有可能上層method標記為@NonTransactional，會導致與DB有交易的方法會失敗，另開執行緒執行。
      Thread thread = new Thread(new Runnable() {
        @Override
            public void run() {
              logger.debug("==== after send dual ssl request process start ====");
              requestContent.afterSendRequest(renewResponseContent);
              logger.debug("==== after send dual ssl request process end ====");
            }
        });
        thread.start();
      long endTime = new Date().getTime();
      long diffTime = endTime - startTime;
      logger.debug("[clientSendRequest] done. All cause time: " + diffTime + " ms");
      logger.debug("==== send dual ssl request end ====");
    }

    return responseContent;
  }

  private SSLSocketFactory getSSLSocketFactory(String keyStorePath, String keyStorePWD, String trustStorePath) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    SSLSocketFactory sslSocketFactory;
    try (
      InputStream keyStoreInputStream = new FileInputStream(keyStorePath);
      InputStream trustStoreInputStream = new FileInputStream(trustStorePath)
    ) {
      // 讀取 Client KeyStore、TrustStore
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(keyStoreInputStream, keyStorePWD.toCharArray());
      keyStoreInputStream.close();

      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore, keyStorePWD.toCharArray());

      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(trustStoreInputStream, null);
      trustStoreInputStream.close();

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(trustStore);

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

      sslSocketFactory = sslContext.getSocketFactory();
    } catch (Exception e) {
      throw e;
    }

    return sslSocketFactory;
  }
}
