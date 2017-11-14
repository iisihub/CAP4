var compDefer = $.Deferred();
$(function () {
    window.comp = (function () {

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
        }

        function checkATM() {
            try {
                var version = ATLActiveXControl.GetAPIVersion();

                return version;
            } catch (e) {
                alert('晶片金融卡交易程式元件安裝失敗！請確認您的讀卡機驅動程式安裝完成。');
                return;
            }
        }

        function getATMService() {
            if (confirm("偵測到您尚未安裝元件，請先下載元件或者啟用已下載元件，是否進行下載?")) {
                window.setCloseConfirm(false);
                window.location.href = url("static/CitiWebATMServiceInstall.exe");
            }
        }

        var version = checkATM();
        var _agen = navigator.userAgent;
        var _app = navigator.appName;
        var isWin = navigator.userAgent.match(/(windows)/i) ? true : false,
            isMacLike = navigator.userAgent
            .match(/(Mac|iPhone|iPod|iPad)/i) ? true : false;

        var IERV = getIEVersion();
        if (isNaN(version)) {
            if (isMacLike) {
                alert('當前環境不支援此服務!');
            } else if (_app == 'Microsoft Internet Explorer' && IERV != 10 && IERV != 11) {
                alert('當前環境不支援此服務!');

            } else {

                getATMService();
            }
            return;
        } else {

            bRdrReady = ATLActiveXControl.connectReader();
            if (bRdrReady != 0) {
                alert('讀卡機連線失敗，請確認讀卡機是否已安裝妥當!');
            }
        }

        // for logDebug
        if (!window.console) {
            console = {
                log: function () {}
            };
        }

        var variable = {
            SESSION_ID: "sessionId",
            READER: "readerName",
            LOG_DONEFUN: false,
            DEF_CALL_BACK_NAME: "callback"
        };

        var attrs = {
            c: ATLActiveXControl,
            // baseUrl : url("ccmoicahandler/verify"),
            readerName: "",
            readerSelect: "readers",
            cardInfo: undefined,
            ieRev: IERV
        };

        var api = {

            set: function (key, value) {
                this[key] = value;
            },

            callback: function (result) {
                alert("Get in callback !");
                this.LOG_DONEFUN && logDebug(result);
                // TBD How to get here ?
            },

            getSessionId: function () {
                return this[this.SESSION_ID];
            },

            checkCode: function (code) {
                if (code != 0) {
                    throw code;
                }
            },

            getComp: function () {
                return this.c;
            },

            command: function (command) {
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

            _errorHandle: function (e, throwB) {
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
                        "99999": "Some description"
                    }[e];
                    return reasonCode;
                }
            }
        };
        return $.extend(ATLActiveXControl, variable, attrs, api);
    })();
    window.comp && compDefer.resolve(window.comp);

    $.extend(window, {
        _onunload: window.onunload,
        onunload: function () {
            this._onunload();
            window.onbeforeunload();
        }
    });
});