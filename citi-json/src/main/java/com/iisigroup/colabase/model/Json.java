package com.iisigroup.colabase.model;

import com.google.gson.JsonObject;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
interface Json {

    JsonObject getRequestContent();

    String getJsonString();

}
