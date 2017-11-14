var compDefer = $.Deferred();
$(document).ready(function() {
    window.iisiNetComp = window.comp = (function() {
        var form = $("#dulForm");
        var $PIN_CDOE = $('#pinCode');

        function getIEVersion() {
            var rv = -1;
            if (navigator.appName == 'Microsoft Internet Explorer') {
                var ua = navigator.userAgent;
                var re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
                if (re.exec(ua) != null) {
                    rv = parseFloat(RegExp.$1);
                }
            } else if (navigator.appName == 'Netscape') {
                var ua = navigator.userAgent;
                // for IE 11
                var re = new RegExp("Trident/.*rv:([0-9]{1,}[\.0-9]{0,})");
                if (re.exec(ua) != null) {
                    rv = parseFloat(RegExp.$1);
                }
            }
            return rv;
        };

        var _agen = navigator.userAgent;
        var _app = navigator.appName;
        var isWin = navigator.userAgent.match(/(windows)/i) ? true : false,
            isMacLike = navigator.userAgent
            .match(/(Mac|iPhone|iPod|iPad)/i) ? true : false;

        var IERV = getIEVersion();
        if (_app == 'Microsoft Internet Explorer' || IERV == 10 || IERV == 11) {
            if (navigator.userAgent.match(/(Win64)/i)) {
                // 64bit
                $("body")
                    .append(
                        '<OBJECT id="myobj" codeBase="' + prop.REGIONAL_HOST + '/CTBPKCS11V3x64.cab#version=1,0,0,2" classid="clsid:74E3DB8C-7E99-438A-9542-B3BBA37AB925" VIEWASTEXT></OBJECT>');
            } else {
                // 32bit
                $("body")
                    .append(
                        '<OBJECT id="myobj" codeBase="' + prop.REGIONAL_HOST + '/CTBPKCS11V3.cab#version=1,0,0,2" classid="clsid:74E3DB8C-7E99-438A-9542-B3BBA37AB925" VIEWASTEXT></OBJECT>');
            }
        } else if (isWin) {
            if (_agen.match(/(Safari)/i) && !_agen.match(/(Chrome)/i)) {
                //不支援的瀏覽器
                showFancyBox(_i18n['DUL.002']);
            } else {
                // other browser
                $("body").append('<applet id="myobj" code="CTBPKCS11Applet.class" archive="' + prop.REGIONAL_HOST + '/CTBPKCS11Applet.jar" width=0 height=0></applet>');
            }
        } else {
            //          if (navigator.userAgent.match(/(Safari)/i)) {
            //          $("body")
            //          .append(
            //          '<applet id="myobj" code="CTBPKCS11Applet.class" archive="../static/CTBPKCS11AppletAP.jar" width=0 height=0></applet>');
            //          } else {
            //          $("body")
            //          .append(
            //          '<applet id="myobj" code="CTBPKCS11Applet.class" archive="../static/CTBPKCS11AppletAF.jar" width=0 height=0></applet>');
            //          }
        }

        // for logDebug
        if (!window.console) {
            console = {
                log: function() {}
            };
        }

        var isIE = ((navigator.userAgent.match(/(MSIE)/i)) || IERV == 11),
            getCompObj = function(isIE) {
                return $("#myobj");
            };

        var comp = getCompObj(isIE);

        try {
            comp.GetVersion();
        } catch (e) {
            //          logDebug("comp error reset");
            var tp = comp.parent(),
                tcomp = comp.clone();
            comp.remove();
            tp.append(tcomp);
            comp = tcomp;
        }

        var variable = {
            SESSION_ID: "sessionId",
            READER: "readerName",
            LOG_DONEFUN: false,
            PREPARE_COMP_DIV: false,
            COMP_DIV: "componentDiv",
            DEF_CALL_BACK_NAME: "callback"
        };

        var attrs = {
            c: comp[0],
            //              baseUrl : url("ccmoicahandler/verify"),
            readerName: "",
            readerSelect: "readers",
            cardInfo: undefined,
            ieRev: IERV
        };

        if (variable.PREPARE_COMP_DIV) {
            $("." + variable.COMP_DIV).size() && $("." + variable.COMP_DIV).css("position", "absolute")
                .css("left", "-9990").css("top", "-9999");
        }

        var api = {

            set: function(key, value) {
                this[key] = value;
            },

            callback: function(result) {
                alert("Get in callback !");
                this.LOG_DONEFUN && logDebug(result);
                // TBD How to get here ?
            },

            getSessionId: function() {
                return this[this.SESSION_ID];
            },

            checkCode: function(code) {
                if (code != 0) {
                    throw code;
                }
            },

            getComp: function() {
                return this.c;
            },
            /**
             * 1.GPKI 2.TWCA 3:其他卡片
             */
            getCardType: function() {
                return myobj.GetCardType();
            },

            /**
             * 0:有 1:沒有 2:沒有讀卡機
             */
            getCardStatus: function() {
                return myobj.GetCardStatus();
            },
            /**
             * 工商憑證專用 取得卡片編號
             */
            getGPKICardNo: function() {
                return myobj.GetGPKICardNo;
            },

            getErrorCode: function() {
                return myobj.GetErrorCode;
            },

            getErrorMessage: function() {
                return myobj.GetErrorMsg;
            },

            signCert: function(signData, id, pwd) {
                //                    $("#pinCode").val('');
                // version control
                if (myobj.GetVersion && pwd) { // 取得元件版本 && 判斷密碼是否有輸入
                    var _tokenStatus = myobj.SetTokenType('2');
                    // set token
                    if (_tokenStatus == '0') { // 設定token 1:PFX, 2卡片載具
                        var cardStatus = myobj.GetCardStatus();
                        var cardType = myobj.GetCardType();
                        // 檢查讀卡機中是否有卡片
                        if (cardStatus == '0' && cardType == '1') {
                            if (myobj.Login(pwd, '') == '0') {
                                // 選擇簽章憑證 1:簽章 2:加密
                                if (myobj.SelectCert('1') == '0') {
                                    // 取得身份證後四碼做比對
                                    // logDebug('Tail Citizen ID is '+ myobj.GetHiCertTailCitizenID);
                                    if (id.substr(6, 4) != myobj.GetHiCertTailCitizenID) {
                                        showFancyBox(_i18n['DUL.006']);
                                        $PIN_CDOE.val('');
                                    } else {
                                        // signing...簽章中
                                        if (myobj.SignPKCS7(signData) == '0') {
                                            var PKCS7Data = myobj.GetPKCS7Data;
                                            myobj.Logout();
                                            return encodeURIComponent(encodeURIComponent(PKCS7Data))
                                        } else {
                                            myobj.Logout();
                                            showFancyBox(_i18n['DUL.007']);
                                            $PIN_CDOE.val('');
                                        }
                                    }
                                }
                                myobj.Logout(); // 登出
                            }
                        } else if (cardStatus == '1') {
                            showFancyBox(_i18n['DUL.008']);
                            $PIN_CDOE.val('');
                        } else if (cardStatus == '2') {
                            showFancyBox(_i18n['DUL.009']);
                            $PIN_CDOE.val('');
                        } else {
                            showFancyBox(_i18n['DUL.008']);
                            $PIN_CDOE.val('');
                        }
                    } else {
                        showFancyBox(_i18n['DUL.007']);
                        $PIN_CDOE.val('');
                    }
                } else {
                    showFancyBox(_i18n['DUL.003']);
                }
                var F_GetErrorMsg = function F_GetErrorMsg() {
                    if (myobj.GetErrorCode != '0') {
                        if (_i18n['PKCS.EC.' + myobj.GetErrorCode] != undefined) {
                            showFancyBox(_i18n['PKCS.EC.' + myobj.GetErrorCode]);
                            form.find("#BtnBauConfirmPage").show();
                            if (myobj.GetErrorCode == '36897') {
                                $PIN_CDOE.val('');
                            }
                        } else {
                            logDebug("_i18n['PKCS.EC.'+myobj.GetErrorCode]]" + _i18n['PKCS.EC.' + myobj.GetErrorCode]);
                        }
                    }
                }();
                myobj.Logout(); // 登出
                return '';
            },

            getVersion: function() {
                return myobj.GetVersion;
            },

            command: function(command) {
                try {
                    if (this[command]) {
                        return this[command].apply(this, $.makeArray(arguments)
                            .slice(1));
                    } else {
                        return this.execute.apply(this, arguments);
                    }

                } catch (e) {
                    this._errorHandle(e);
                }
            },

            _errorHandle: function(e, throwB) {
                var errorMsg;
                if (e == -2146435026) {
                    errorMsg = _i18n['DUL.001'];
                } else if (e == 9999999991) {
                    errorMsg = "Need readerName & sessionId";
                } else if (getReasonCode(e)) {
                    errorMsg = getReasonCode(e);
                } else {
                    errorMsg = e.message;
                }
                API.showErrorMessage(errorMsg || e);
                // $.unblockUI();
                if (throwB !== false) {
                    throw "warpper:" + e;
                }

                function getReasonCode(e) {
                    var reasonCode = {
                        "32868": "檔案路徑錯誤",
                        "32869": "PKCS12密碼為空",
                        "32870": "PKCS12格式錯誤",
                        "32871": "PKCS12密碼錯誤",
                        "32877": "找無PKCS12憑證",
                        "32879": "找無PKCS12私鑰",
                        "36865": "簽章執行失敗",
                        "36870": "晶片卡登入錯誤",
                        "36871": "晶片卡登出錯誤",
                        "36896": "晶片卡密碼已鎖",
                        "36897": "晶片卡密碼錯誤",
                        "36898": "尚未初始化",
                        "36899": "從卡片挑選憑證失敗",
                        "36880": "解析憑證錯誤",
                        "36900": "輸入參數錯誤",
                        "4610": "Read Record失敗",
                        "4611": "密碼驗證失敗超過三次，IC卡已被鎖住 "
                    }[e];
                    return reasonCode;
                }
            }
        };
        return $.extend(comp, variable, attrs, api);
    })();
    window.iisiNetComp && compDefer.resolve(window.iisiNetComp);

    $.extend(window, {
        _onunload : window.onunload,
        onunload : function() {
            this._onunload();
            window.comp[0].Logout();
        }
    });
});