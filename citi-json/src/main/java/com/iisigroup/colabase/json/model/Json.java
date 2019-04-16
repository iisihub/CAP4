package com.iisigroup.colabase.json.model;

import com.google.gson.JsonObject;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
interface Json {

    /**
     * 取得內含的JsonObject
     * @return json object
     */
    JsonObject getRequestContent();

    /**
     * 取得內含JsonObject的String字串
     * @return jsonString
     */
    String getJsonString();

}
