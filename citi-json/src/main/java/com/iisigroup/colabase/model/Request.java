package com.iisigroup.colabase.model;

import com.google.gson.JsonObject;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
interface Request {

    void afterSendRequest(ResponseContent responseContent);

    JsonObject getRequestContent();

    String getJsonString();

    void showRequestJsonStrLog(String jsonStr);

}
