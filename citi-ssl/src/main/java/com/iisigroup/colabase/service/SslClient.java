package com.iisigroup.colabase.service;

import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;

public interface SslClient<T extends ResponseContent> {

    T sendRequest(RequestContent requestContent);

    T sendRequestWithDefaultHeader(RequestContent requestContent);

}
