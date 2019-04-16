package com.iisigroup.colabase.json.model;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/18 AndyChen,new
 *          </ul>
 * @since 2018/5/18
 */
public interface ApiResponse {
    /**
     * 紀錄response中的json字串
     * 如果有針對特殊的呼叫(如送base64字串)導致過長，要自行override
     * @param jsonStr
     */
    void showResponseJsonStrLog(String jsonStr);
}
