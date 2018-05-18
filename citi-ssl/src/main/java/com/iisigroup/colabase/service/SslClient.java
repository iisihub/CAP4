package com.iisigroup.colabase.service;

import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;

import java.io.IOException;

public interface SslClient<T extends ResponseContent> {

    T sendRequest(RequestContent requestContent) throws IOException;

    T sendRequestWithDefaultHeader(RequestContent requestContent) throws IOException;

}
