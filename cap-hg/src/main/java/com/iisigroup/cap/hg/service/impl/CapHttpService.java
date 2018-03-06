package com.iisigroup.cap.hg.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.springframework.util.CollectionUtils;

import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.hg.constants.ConnStatus;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * HTTP UTIL
 * </pre>
 * 
 * @since 2011/11/10
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2011/11/10,rodeschen,new
 *          <li>2013/1/15,rodeschen,add setHeader,remove CapException
 *          </ul>
 */
public class CapHttpService extends AbstractHGservice {

    private HttpClientBuilder httpClientBuilder;
    private CloseableHttpClient httpClient;
    private HttpRequestBase httpMethod;
    // private HttpResponse httpResponse;
    private int httpStatus;
    private byte[] responseData;
    /** default Connection Timeout **/
    // private int defaultConnectTimeout = 3000;
    /** default socket Timeout **/
    // private int defaultSocketTimeout = 55000;
    /** default encode **/
    private Charset defaultEncode = Consts.UTF_8;
    /** connection status **/
    private ConnStatus status;
    /** async **/
    private boolean isAsync;

    private Object sendData;

    private Map<String, String> header;

    private List<NameValuePair> nvps = new ArrayList<NameValuePair>();

    /**
     * @param Map
     *            <String,Object> CapConstants.HOST_URL host url CapConstants.CONNECTION_TIMEOUT default 3000 CapConstants.SOCKET_TIMEOUT default 55000 CapConstants.TRANSFER_ENCODING default UTF-8
     *            CapConstants.ASYNC default false
     * 
     */

    /*
     * (non-Javadoc)
     * 
     * @see com.iisi.cap.hg.service.IHGService#getStatus()
     */
    @Override
    public ConnStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ConnStatus status) {
        this.status = status;
    }

    /**
     * get http status
     * 
     * @return int
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisi.cap.hg.service.IHGService#getMessage()
     */
    @Override
    public Object getReceiveData() throws CapException {
        return responseData;
    }

    public String getReceiveStringData() {
        return new String(responseData, defaultEncode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.hg.service.IHGService#setHeader(java.lang.Object)
     */
    @Override
    public void setHeader(Object data) {
        this.header = (Map<String, String>) data;

    }

    public void addHeader(String key, String value) {
        if (this.header == null) {
            this.header = new HashMap<String, String>();
        }
        this.header.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisi.cap.hg.service.IHGService#setSendMessage(java.lang.Object)
     */
    @Override
    public void setSendData(Object data) throws CapException {
        this.sendData = data;
    }

    /**
     * get send Data
     * 
     * @return sendData
     */
    public Object getSendData() {
        return this.sendData;
    }

    /**
     * set http request body
     * 
     * @param body
     *            string
     * @throws UnsupportedEncodingException
     */
    public void setRequestBody(String body) throws UnsupportedEncodingException {
        if (httpMethod instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) httpMethod).setEntity(new StringEntity(body, defaultEncode));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * set http request key-value data
     * 
     * @param map
     *            parameter
     * @throws UnsupportedEncodingException
     */
    public void setRequestParams(Map<String, String> map) throws UnsupportedEncodingException {
        nvps = new ArrayList<NameValuePair>();
        for (String key : map.keySet()) {
            nvps.add(new BasicNameValuePair(key, map.get(key)));
        }
    }

    public void setRequestHeader(Map<String, String> map) {
        for (String key : map.keySet()) {
            httpMethod.addHeader(key, map.get(key));
        }
    }

    private void excuteHttp() throws Exception {

        String url = (String) getProperty(Constants.HOST_URL);
        URI uri;
        if (httpMethod instanceof HttpEntityEnclosingRequestBase) {
            if (!CollectionUtils.isEmpty(nvps)) {
                ((HttpEntityEnclosingRequestBase) httpMethod).setEntity(new UrlEncodedFormEntity(nvps, defaultEncode));
            }
            uri = new URI(url);
        } else {
            if (!CollectionUtils.isEmpty(nvps)) {
                uri = new URIBuilder(url).addParameters(nvps).build();
            } else {
                uri = new URI(url);
            }
        }
        httpMethod.setURI(uri);
        long st = System.currentTimeMillis();

        HttpResponse httpResponse = httpClient.execute(httpMethod);
        logger.debug("Send Host spand time1: " + (System.currentTimeMillis() - st) + "ms");
        httpStatus = httpResponse.getStatusLine().getStatusCode();

        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                responseData = IOUtils.toByteArray(instream);

                // responseData = StringUtils.join(IOUtils.readLines(instream,
                // defaultEncode).toArray());

            } catch (RuntimeException ex) {
                httpMethod.abort();
                throw ex;
            } finally {
                instream.close();
            }

            httpClient.getConnectionManager().shutdown();
        }
        if (logger.isTraceEnabled()) {
            logger.trace("host response:" + new String(responseData));
        }
        logger.debug("Send Host spand time2: " + (System.currentTimeMillis() - st) + "ms");
        setStatus(ConnStatus.COMPLETE);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisi.cap.hg.service.IHGService#execute()
     */
    @Override
    public void execute() throws Exception {
        if (!ConnStatus.INIT.equals(status)) {
            throw new CapException("init error", getClass());
        }
        setStatus(ConnStatus.RUNNING);
        if (isAsync) {
            new Async().start();
        } else {
            excuteHttp();
        }

    }

    /**
     * <pre>
     * Async HTTP Connect
     * </pre>
     * 
     * @since 2011/12/5
     * @author rodeschen
     * @version
     *          <ul>
     *          <li>2011/12/5,rodeschen,new
     *          </ul>
     */
    private class Async extends Thread {
        @Override
        public void run() {
            try {
                excuteHttp();
            } catch (Exception e) {
                responseData = errorHandle(e).getBytes();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisi.cap.hg.service.IHGService#initConnection()
     */
    @Override
    public void initConnection() throws CapException {
        String method = getProperty(Constants.HTTP_METHOD);
        switch (method) {
        case HttpDelete.METHOD_NAME:
            httpMethod = new HttpDelete();
            break;
        case HttpPatch.METHOD_NAME:
            httpMethod = new HttpPatch();
            break;
        case HttpPost.METHOD_NAME:
            httpMethod = new HttpPost();
            break;
        case HttpPut.METHOD_NAME:
            httpMethod = new HttpPut();
            break;
        case HttpGet.METHOD_NAME:
            httpMethod = new HttpGet();
            break;
        case HttpHead.METHOD_NAME:
            httpMethod = new HttpHead();
            break;
        case HttpOptions.METHOD_NAME:
            httpMethod = new HttpOptions();
            break;
        case HttpTrace.METHOD_NAME:
            httpMethod = new HttpTrace();
            break;
        default:
            throw new UnsupportedOperationException("Http Method Unsupported: " + method);
        }

        if (this.header instanceof Map) {
            setRequestHeader((Map<String, String>) this.header);
        }
        try {
            if (this.sendData instanceof Map) {
                setRequestParams((Map<String, String>) this.sendData);
            } else if (this.sendData instanceof String && !CapString.isEmpty((String) this.sendData)) {
                Map<String, String> params = new HashMap<String, String>();
                List<NameValuePair> list = URLEncodedUtils.parse((String) this.sendData, defaultEncode);
                for (NameValuePair elem : list) {
                    params.put(elem.getName(), elem.getValue());
                }
                setRequestParams(params);
            }
        } catch (UnsupportedEncodingException e) {
            throw new CapException(e, getClass());
        }
        httpClientBuilder = HttpClientBuilder.create();
        String retryCount = getProperty(Constants.HTTP_RETRY_COUNT);
        if (retryCount != null) {
            final int count = Integer.valueOf(retryCount);
            httpClientBuilder.setRetryHandler(new HttpRequestRetryHandler() {

                @Override
                public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                    if (executionCount > count) {
                        return false;
                    }
                    logger.info("retry count:" + executionCount + " -- error:" + exception.getMessage());
                    return true;
                }

            });
        }

        int ct = Integer.valueOf((String) getProperty(Constants.CONNECTION_TIMEOUT));
        int st = Integer.valueOf((String) getProperty(Constants.CONNECTION_TIMEOUT));

        Charset encode = defaultEncode;
        String async = getProperty(Constants.ASYNC);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(ct).setSocketTimeout(st).build();
        httpClient = httpClientBuilder.setDefaultRequestConfig(config).build();
        defaultEncode = encode != null ? encode : defaultEncode;
        isAsync = (async != null ? Boolean.valueOf(async) : false);
        setStatus(ConnStatus.INIT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisi.cap.hg.service.IHGService#errorHandle(java.lang.Exception)
     */
    @Override
    public String errorHandle(Exception e) {
        logger.error(e.getMessage(), e);
        if (e instanceof HttpHostConnectException) {
            setStatus(ConnStatus.CONNECT_ERROR);
            return "{rc:'" + ConnStatus.CONNECT_ERROR + "',msg:'connect error'}";
        } else if (e instanceof SocketTimeoutException) {
            setStatus(ConnStatus.TIMEOUT);
            return "{rc:'" + ConnStatus.TIMEOUT + "',msg:'connect timeout'}";
        } else {
            setStatus(ConnStatus.ERROR);
            return "{rc:'" + ConnStatus.ERROR + "',msg:'" + e.getMessage() + "'}";
        }
    }

}
