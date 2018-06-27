package com.iisigroup.colabase.webatm.service;

/*
 * 20060623 Frank：新增此 Interface, 用來控管所有呼叫記憶體的程式。
 *
 *  用法：所有實作此介面的物件，必需覆寫 function init()，取得／重拿 資料
 *
 */
public interface ConstMember {
    public void init();
}
