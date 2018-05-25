package com.iisigroup.colabase.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;
import com.iisigroup.colabase.service.SslClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class SslClientImpl<T extends ResponseContent> implements SslClient<T> {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private SSLSocketFactory sslSocketFactory;

  private boolean isInit = false;

  @Autowired
  private CapSystemConfig systemConfig;
  private Class<T> type;

  public SslClientImpl() {
    type = getType(getClass());
  }

  public SslClientImpl(String keyStorePath, String keyStorePWD, String trustStorePath) throws
          CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    sslSocketFactory = getSSLSocketFactory(keyStorePath, keyStorePWD, trustStorePath);
    isInit = true;
    type = getType(getClass());
  }

  private Class<T> getType(Class<?> tClass) {
    Class<T> type;
    try {
      type = (Class<T>)((ParameterizedType) tClass.getGenericSuperclass()).getActualTypeArguments()[0];
    } catch (Exception e ) {
      return getType(tClass.getSuperclass());
    }
    return type;
  }

  private void initSslSocketFactory(){
    if(sslSocketFactory == null) {
      String keyStorePath = systemConfig.getProperty("keyStorePath");
      String trustStorePath = systemConfig.getProperty("trustStorePath");
      String keyStorePWD = systemConfig.getProperty("keyStorePWD");
      try {
        sslSocketFactory = getSSLSocketFactory(keyStorePath, keyStorePWD, trustStorePath);
        isInit = true;
      } catch (CertificateException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | IOException | KeyManagementException e) {
        logger.error("init SslSocketFactory fail >>> {}" , e.getCause());
      }
    }
  }

  /**
   *
   * @param requestContent
   * @return
   * @throws IOException
   */
  @Override
  public T sendRequest(RequestContent requestContent) {
    if(!isInit)
      this.initSslSocketFactory();

    for (int i = 0; i <= requestContent.getRetryTimes(); i++) {
      T responseContent = null;
      try {
        responseContent= this.clientSendRequest(requestContent);
        if(this.isStatusNeedToRetry(requestContent.getRetryHttpStatus(), responseContent.getStatusCode())) {
          continue;
        }
        return responseContent;
      } catch (IllegalArgumentException e) {
        if (i == requestContent.getRetryTimes())
          throw e;
      } catch (Exception e) {
        if (i == requestContent.getRetryTimes())
          return responseContent;
      }
    }
    throw new IllegalStateException("did not have any response");
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
  public T sendRequestWithDefaultHeader(RequestContent requestContent) {
    Map<String, List<String>> requestHeaders = new HashMap<>();
    requestHeaders.put("Accept", Collections.singletonList("application/json"));
    requestHeaders.put("Content-Type", Collections.singletonList("application/json; charset=UTF-8"));
    requestHeaders.put("uuid", Collections.singletonList(UUID.randomUUID().toString()));
    requestHeaders.put("channelId", Collections.singletonList("COA"));
    requestHeaders.put("businessCode", Collections.singletonList("GCB"));
    requestHeaders.put("countryCode", Collections.singletonList("TW"));
    requestHeaders.put("consumerOrgCode", Collections.singletonList("COLA"));
    requestContent.setRequestHeaders(requestHeaders);
    return this.sendRequest(requestContent);
  }

  private T clientSendRequest(final RequestContent requestContent) {
    logger.debug("==== send dual ssl request start ====");
    if (requestContent.getRequestContent() == null) {
      throw new IllegalArgumentException("there is no json requestContent, please init RequestContent by JsonFactory");
    }
    long startTime = new Date().getTime();
    ArrayList<String> recordInfo = new ArrayList<>();
    T responseContent = null;
    int statusCode = 0;
    int timeOut = requestContent.getTimeout();
    RequestContent.HTTPMethod method = requestContent.getHttpMethod();
    String targetURL = requestContent.getTargetUrl();
    String jsonStr = requestContent.getJsonString();
    boolean isUseOwnSslFactory = requestContent.isUseOwnKeyAndTrustStore();
    Map<String, List<String>> requestHeaders = requestContent.getRequestHeaders();
    Map<String, List<String>> headers = null;
    try {
      Date date = Calendar.getInstance().getTime();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String time = formatter.format(date);
      logger.debug("start time = {}",  time);
      logger.debug("Request: target = {}", targetURL);
      // add listForRecordInfos
      recordInfo.add("Request: time = " + time);

      HttpsURLConnection connection = (HttpsURLConnection) new URL(targetURL).openConnection();
      // 設定 HttpsURLConnection 的 SSLSocketFactory
      if(isUseOwnSslFactory) {
        if(isInit) {
          connection.setSSLSocketFactory(sslSocketFactory);
        } else {
          logger.debug("OwnSslFactory is not init. cancel use own ssl factory.");
          isUseOwnSslFactory = false;
        }
      }
      logger.debug("Request: use own ssl factory = " + isUseOwnSslFactory);
      logger.debug("Request: need retry status = " + Arrays.toString(requestContent.getRetryHttpStatus()));

      connection.setConnectTimeout(timeOut);
      connection.setReadTimeout(timeOut);

      String requestMethod = method.toString();
      connection.setRequestMethod(requestMethod);
      logger.debug("Request: Method = {}" , requestMethod);

      // add listForRecordInfos
      recordInfo.add("Request: Method = " + requestMethod);

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

          logger.debug("Request Header Key: {}, Value: {}",requestHeaderKey,  requestHeaderValue);

          // add listForRecordInfos
          recordInfo.add("Request: " + requestHeaderKey + " = " + requestHeaderValue);
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
          recordInfo.add("Request: data = " + jsonStr);
        } catch (IOException e) {
          recordInfo.add("Output data Exception = " + e.toString());
          logger.warn("Output data Exception = {}" , e.toString());
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

      headers = connection.getHeaderFields();
      for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
        logger.debug("Response Header Key: {}, Value: {}", entry.getKey(), entry.getValue());

        // add listForRecordInfos
        recordInfo.add("Response: Header Key = " + entry.getKey() + ", Value = " + entry.getValue());
      }

      StringBuilder responseBodySB = new StringBuilder();
      JsonObject responseJson = this.readResponse(is, responseBodySB, recordInfo);
      responseContent = getResponseInstance(statusCode, headers, responseJson, recordInfo);
      responseContent.showResponseJsonStrLog(responseBodySB.toString());
    } catch (Exception e) {
      if(responseContent == null) {
        if (headers == null)
          headers = new HashMap<>();
        responseContent = getResponseInstance(statusCode, headers, new JsonObject(), recordInfo);
        responseContent.setException(e);
      }
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
      logger.debug("[clientSendRequest] done. All cause time: {} ms", diffTime );
      logger.debug("==== send dual ssl request end ====");
    }

    return responseContent;
  }

  private T getResponseInstance(int statusCode, Map<String, List<String>> headers, JsonObject responseJson, List<String> records){
    T result;
    try {
      Constructor<T> constructor = type.getConstructor(int.class, Map.class, JsonObject.class, List.class);
      result = constructor.newInstance(statusCode, headers, responseJson, records);
    } catch (Exception e) {
      throw new IllegalStateException("can not init response instance. e: " + e.getMessage());
    }
    return result;
  }


  private JsonObject readResponse(InputStream is, StringBuilder responseBodySB, ArrayList<String> recordInfo) throws IOException {
    JsonObject responseJson = null;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      String tempStr;
      while ((tempStr = reader.readLine()) != null) {
        responseBodySB.append(tempStr);
      }

      Gson gson = new Gson();
      if (responseBodySB.length() != 0) {
        responseJson = gson.fromJson(responseBodySB.toString(), JsonObject.class);
      }
      recordInfo.add("Response Body : " + responseJson.toString());
    } catch (JsonSyntaxException e) {
      responseJson = new JsonObject();
      recordInfo.add("Response Body: " + responseBodySB);
      recordInfo.add("Response Exception: " + e);
    }
    return responseJson;
  }

  private SSLSocketFactory getSSLSocketFactory(String keyStorePath, String keyStorePWD, String trustStorePath) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    SSLSocketFactory factory;
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

      factory = sslContext.getSocketFactory();
    }

    return factory;
  }
}
