package com.iisigroup.colabase.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.colabase.model.*;
import com.iisigroup.colabase.service.SslClient;
import com.iisigroup.colabase.util.ColaSSLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.net.*;
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
    private Class<T> contentType;

    public SslClientImpl() {
        contentType = getType(getClass());
    }

    public SslClientImpl(String keyStorePath, String keyStorePWD, String trustStorePath)
            throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        sslSocketFactory = ColaSSLUtil.getSSLSocketFactory(keyStorePath, keyStorePWD, trustStorePath);
        isInit = true;
        contentType = getType(getClass());
    }

    private Class<T> getType(Class<?> tClass) {
        Class<T> type;
        try {
            type = (Class<T>) ((ParameterizedType) tClass.getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            return getType(tClass.getSuperclass());
        }
        return type;
    }

    private SSLSocketFactory initSslSocketFactory(String protocol, String keyStorePath, String trustStorePath, String keyStorePWD) {
        try {
            SSLSocketFactory sslSocketFactory = ColaSSLUtil.getSSLSocketFactory(protocol, keyStorePath, keyStorePWD, trustStorePath);
            return sslSocketFactory;
        } catch (CertificateException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | IOException | KeyManagementException e) {
            logger.error("init SslSocketFactory fail >>> ", e);
        }
        return null;
    }
    private SSLSocketFactory initSslSocketFactory(String protocol) {
        String keyStorePath = systemConfig.getProperty("keyStorePath");
        String trustStorePath = systemConfig.getProperty("trustStorePath");
        String keyStorePWD = systemConfig.getProperty("keyStorePWD");
        return this.initSslSocketFactory(protocol, keyStorePath, trustStorePath, keyStorePWD);
    }

    private SSLSocketFactory initSslSocketFactory() {
        return initSslSocketFactory("TLS");
    }

    /**
     *
     * @param requestContent
     * @return
     * @throws IOException
     */
    @Override
    public T sendRequest(RequestContent requestContent) {
        if (!isInit && requestContent.isUseOwnKeyAndTrustStore()) {
            SSLSocketFactory sslSocketFactory = this.initSslSocketFactory();
            if(sslSocketFactory != null) {
                this.sslSocketFactory = sslSocketFactory;
                isInit = true;
            }
        }

        T responseContent = null;
        for (int i = 0; i <= requestContent.getRetryTimes(); i++) {
            try {
                responseContent = this.clientSendRequest(requestContent);
                if (this.isStatusNeedToRetry(requestContent.getRetryHttpStatus(), responseContent.getStatusCode())) {
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
        return responseContent;
    }

    private boolean isStatusNeedToRetry(int[] retryHttpStatus, int httpStatus) {
        if (retryHttpStatus == null) {
            return false;
        }
        for (int status : retryHttpStatus) {
            if (httpStatus == status)
                return true;
        }
        return false;
    }

    /**
     * 針對citi RCE專案所客制的sendRequest method 所送header為定制
     * 
     * @param requestContent
     *            content included data and connection settings
     * @return T extends ResponseContent
     */
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
        logger.debug("==== send api module request start ====");
        if (requestContent.getRequestContent() == null) {
            throw new IllegalArgumentException("there is no json requestContent, please init RequestContent by JsonFactory");
        }
        long startTime = new Date().getTime();
        ArrayList<String> recordInfo = new ArrayList<>();
        T responseContent = null;
        int statusCode = 0;
        int timeOut = requestContent.getTimeout();
        RequestContent.HTTPMethod method = requestContent.getHttpMethod();
        ApiRequest.SendType sendType = requestContent.getSendType();
        String targetURL = requestContent.getTargetUrl();
        String jsonStr = requestContent.getJsonString();
        String apiName = requestContent.getApiName();
        boolean isUseOwnSslFactory = requestContent.isUseOwnKeyAndTrustStore();
        boolean isIgnoreSSLcert = requestContent.isIgnoreSSLcert();
        Map<String, List<String>> requestHeaders = requestContent.getRequestHeaders();
        Map<String, List<String>> responseHeaders = null;
        String protocol = requestContent.getProtocol();
        final ProxyConfig proxyConfig = requestContent.getProxyConfig();
        try {
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = formatter.format(date);
            logger.debug("start time = {}", time);
            logger.debug("API NAME = {}", apiName);
            logger.debug("Request: target = {}", targetURL);
            // add listForRecordInfos
            recordInfo.add("Request: time = " + time);
            recordInfo.add("API NAME = " + apiName);
            recordInfo.add("Request: target = " + targetURL);

            logger.debug("Request: need retry status = {}", requestContent.getRetryHttpStatus());

            //取得連線
            HttpURLConnection connection = this.getConnection(targetURL, timeOut, method, isUseOwnSslFactory,
                isIgnoreSSLcert, protocol, requestHeaders, recordInfo, proxyConfig);

            if (!RequestContent.HTTPMethod.GET.equals(method)) {
                if(sendType == null)
                    sendType = ApiRequest.SendType.JSON; //預設用JSON
                switch (sendType) {
                case POST_FORM:
                    if(requestContent instanceof PostFormData) {
                        Map<String, String> dataMap = ((PostFormData) requestContent).getDataMap();
                        this.sendPostFormDataToRemote(connection, dataMap, recordInfo);
                    } else {
                        throw new IllegalArgumentException("use POST_FORM sendType must use PostFormData.class");
                    }
                case JSON:
                default:
                    requestContent.showRequestJsonStrLog(jsonStr);
                    this.sendJsonDataToRemote(connection, jsonStr, recordInfo);
                }
            }

            InputStream is;

            statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            responseHeaders = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
                logger.debug("Response Header Key: {}, Value: {}", entry.getKey(), entry.getValue());

                // add listForRecordInfos
                recordInfo.add("Response: Header Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }

            StringBuilder responseBodySB = new StringBuilder();
            JsonObject responseJson = this.readResponse(is, responseBodySB, recordInfo);
            responseContent = getResponseInstance(statusCode, responseHeaders, responseJson, recordInfo);
            responseContent.showResponseJsonStrLog(responseBodySB.toString());
        } catch (Exception e) {
            if (responseContent == null) {
                if (responseHeaders == null)
                    responseHeaders = new HashMap<>();
                responseContent = getResponseInstance(statusCode, responseHeaders, new JsonObject(), recordInfo);
                responseContent.setException(e);
            }
        } finally {
            final ResponseContent renewResponseContent = responseContent;
            long endTime = new Date().getTime();
            long diffTime = endTime - startTime;
            logger.debug("[clientSendRequest] done. All cause time: {} ms", diffTime);
            logger.debug("==== send api module request end ====");

            // 由於有可能上層method標記為@NonTransactional，會導致與DB有交易的方法會失敗，另開執行緒執行。
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    logger.debug("==== after send api module request process start ====");
                    requestContent.afterSendRequest(renewResponseContent);
                    logger.debug("==== after send api module request process end ====");
                }
            });
            thread.start();
        }

        return responseContent;
    }

    private HttpURLConnection getConnection(String targetURL, int timeOut, ApiRequest.HTTPMethod method,
                                            boolean isUseOwnSslFactory, boolean isIgnoreSSLcert,
                                            String protocol, Map<String, List<String>> requestHeaders, ArrayList<String> recordInfo, ProxyConfig proxyConfig) throws IOException {

        HttpURLConnection connection = null;
        logger.debug("Request: use proxy = {}", proxyConfig != null && proxyConfig.isUseProxy());

        //檢查是否使用Proxy
        if (proxyConfig != null && proxyConfig.isUseProxy()) {
            String host = proxyConfig.getHost();
            int port = proxyConfig.getPort();
            if (host == null || "".equals(host) || port == 0)
                throw new IllegalStateException("if use proxy, must fill all proxy config");
            logger.debug("Request: use proxy host: {}, port: {}", host, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            connection = (HttpURLConnection) new URL(targetURL).openConnection(proxy);
        } else {
            connection = (HttpURLConnection) new URL(targetURL).openConnection();
        }

        //如果對象是https才需要進行憑證相關設定
        if(connection instanceof HttpsURLConnection) {
            // 設定 HttpsURLConnection 的 SSLSocketFactory and 是否忽略憑證驗證
            this.processSSLSettings((HttpsURLConnection)connection, isUseOwnSslFactory, isIgnoreSSLcert, protocol);
        }

        logger.debug("Request: use own ssl factory = {}", isUseOwnSslFactory);
        logger.debug("Request: ignore ssl certify = {}", isIgnoreSSLcert);
        logger.debug("Request: customize protocol = {}", protocol);

        connection.setConnectTimeout(timeOut);
        connection.setReadTimeout(timeOut);

        String requestMethod = method.toString();
        connection.setRequestMethod(requestMethod);
        logger.debug("Request: Method = {}", requestMethod);

        // add listForRecordInfos
        recordInfo.add("Request: Method = " + requestMethod);

        connection.setDoInput(true);
        if (!RequestContent.HTTPMethod.GET.equals(method)) {
            connection.setDoOutput(true);
        }

        //設定header
        Iterator<String> requestHeadersKeyIterator = requestHeaders.keySet().iterator();
        while (requestHeadersKeyIterator.hasNext()) {
            String requestHeaderKey = requestHeadersKeyIterator.next();
            List<String> requestHeaderValues = requestHeaders.get(requestHeaderKey);

            for (String requestHeaderValue : requestHeaderValues) {
                connection.setRequestProperty(requestHeaderKey, requestHeaderValue);

                logger.debug("Request Header Key: {}, Value: {}", requestHeaderKey, requestHeaderValue);

                // add listForRecordInfos
                recordInfo.add("Request: " + requestHeaderKey + " = " + requestHeaderValue);
            }
        }
        return connection;
    }

    private void processSSLSettings(HttpsURLConnection connection, boolean isUseOwnSslFactory,
                                    boolean isIgnoreSSLcert, String protocol) {
        // 設定 HttpsURLConnection 的 SSLSocketFactory
        SSLSocketFactory sslSocketFactory = null;
        if (isUseOwnSslFactory) {
            if(CapString.isEmpty(protocol)) {
                //檢查有無初始化factory
                if (isInit) {
                    connection.setSSLSocketFactory(sslSocketFactory);
                } else {
                    logger.debug("OwnSslFactory is not init. cancel use own ssl factory.");
                    isUseOwnSslFactory = false;
                }
            } else {
                //使用自定義的protocol
                sslSocketFactory = this.initSslSocketFactory(protocol);
            }
        } else if(!CapString.isEmpty(protocol)) { //單純設定protocol
            sslSocketFactory = this.initSslSocketFactory(protocol, null, null, null);
        }
        if(sslSocketFactory != null)
            connection.setSSLSocketFactory(sslSocketFactory);

        //是否忽略憑證驗證
        if (isIgnoreSSLcert) {
            try {
                HostnameVerifier allowAllHostnameVerifier = ColaSSLUtil.getAllowAllHostnameVerifier();
                SSLSocketFactory allTrustSSLSocketFactory;
                if(CapString.isEmpty(protocol)) {
                    allTrustSSLSocketFactory = ColaSSLUtil.getAllTrustSSLSocketFactory();
                } else {
                    allTrustSSLSocketFactory = ColaSSLUtil.getAllTrustSSLSocketFactory(protocol);
                }
                connection.setSSLSocketFactory(allTrustSSLSocketFactory);
                connection.setHostnameVerifier(allowAllHostnameVerifier);
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                logger.error("fail to init own ssl socket factory, e: {}", e);
            }
        }
    }

    private void sendJsonDataToRemote(HttpURLConnection connection, String jsonStr, List<String> recordInfo) throws IOException {
        try (OutputStream output = connection.getOutputStream(); PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
            writer.write(jsonStr);
            output.flush();
            // add listForRecordInfos
            recordInfo.add("Request: data = " + jsonStr);
        } catch (IOException e) {
            recordInfo.add("Output data Exception = " + e.toString());
            logger.warn("Output data Exception = {}", e);
            throw e;
        }
    }

    private void sendPostFormDataToRemote(HttpURLConnection connection, Map<String, String> dataMap, List<String> recordInfo) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String key : dataMap.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(URLEncoder.encode(key, "UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(dataMap.get(key), "UTF-8"));
        }
        recordInfo.add("ApiRequest: SendData = " + stringBuilder);
        logger.debug("ApiRequest: SendData = {}", stringBuilder);

        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes(stringBuilder.toString());
            out.flush();
        } catch (IOException e) {
            recordInfo.add("Output data Exception = " + e.toString());
            logger.warn("Output data Exception = {}", e);
            throw e;
        }
    }

    private T getResponseInstance(int statusCode, Map<String, List<String>> headers, JsonObject responseJson, List<String> records) {
        T result;
        try {
            Constructor<T> constructor = contentType.getConstructor(int.class, Map.class, JsonObject.class, List.class);
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
            recordInfo.add("Response Body : " + responseJson);
        } catch (JsonSyntaxException e) {
            recordInfo.add("Response Body: " + responseBodySB);
            recordInfo.add("Response Exception: " + e);
            throw e;
        }
        return responseJson;
    }


}
