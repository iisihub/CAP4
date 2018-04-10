package com.iisigroup.colabase.service;

import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;

import java.io.IOException;

public interface SslClient {

    ResponseContent sendRequest(RequestContent requestContent) throws IOException;

    ResponseContent sendRequestWithDefaultHeader(RequestContent requestContent) throws IOException;

}
