// init
$(function() {
      /* timeout controls */
      // Do idle process
      var idleDuration = 10;
      try {
        idleDuration = prop && prop[Properties.timeOut];
        if (idleDuration == '' || parseInt(idleDuration) < 1) {
          idleDuration = 10;
        }
      } catch (e) {
        logDebug("Can't find prop");
      }

      // 計數器減差(這裡是分鐘)
      var gapTime = 0.9;
      if (Properties.remindTimeout) {
        // #Cola235 增加切換頁reset timer
        // 計數器(這裡是毫秒)
        window.timecount = (idleDuration - gapTime) * 60 * 1000;
        logDebug("set timer time::" + timecount);
        var t1merConfirm = [];
        var timer2 = null;
        var pathname = window.location.pathname;
        //記錄各分頁自己的pageNo(session TOCM使用)
        window.CCPAGENO = "";
        var cccheckMethod = function(dxx) {
          $.ajax({
            url : url('checktimeouthandler/checkTO'),
            async : true,
            data : {
              isCntnu : dxx.isCntnu,
              CCPAGENO : window.CCPAGENO
            },
            success : function(d) {
              //有errorPage,表示要導頁處理
              if (d.errorPage) {
                window.setCloseConfirm(false);
                window.location = d.errorPage;
              } else if (d.SHOW_REMIND === 'true') {
                if (!/(timeout)$|(error)$|(cancelPage)$/i.test(pathname)) {
                  timer2 = $.timer(gapTime * 60 * 1000, function() {
                    //超過時間沒給確認動作,就當做取消交易
                    cccheckMethod({
                      isCntnu : false
                    });
                  }, false);
                  API.showConfirmMessage("您已閒置一段時間，請問是否繼續作業?", function(data) {
                    if (data) {
                      timer2.stop();
                      cccheckMethod({
                        isCntnu : true
                      });
                      //按了之後,要重新倒數
                      takeTimerReset();
                    } else {
                      timer2.stop();
                      cccheckMethod({
                        isCntnu : false
                      });
                    }
                  });
                }
              }
            }
          });
        };

        if (!/(timeout)$|(login)$|(error)$|(cancelPage)$/i.test(pathname)) {
          window.timer = $.timer(timecount, function() {
            //每xx分鐘上server問是否要提示繼續交易
            cccheckMethod({
              CCPAGENO : pathname
            });
          }, false);
        }
        var takeTimerReset = function() {
          timer.reset(timecount);
        };

        // IDLE留著，當user沒看到confirm pop，時間到了idle還是要導倒timeout?
        ifvisible && ifvisible.setIdleDuration(idleDuration * 60);// minute*60
        // logDebug("idleDuration is ::: " + idleDuration);
        ifvisible.on('idle', function() {
          $.unblockUI();
          $.ajax({
            url : url('checktimeouthandler/check'),
            asyn : true,
            data : {}
          }).done(function(d) {
            if (d.errorPage) {
              window.setCloseConfirm(false);
              window.location = d.errorPage;
            }
          });
        });
        ifvisible.on('wakeup', function() {
          // $(".ui-dialog-content").dialog("close");
        });
      }

      window.i18n.load("messages", {async: true}).done(function() {
        $.extend(Properties, {
          myCustMessages : {
            custom_error_messages : {
              '#myName' : {
                'required' : {
                  'message' : i18n.messages('myName.required')
                },
                'fieldName' : {
                  'message' : i18n.messages('myName.fieldName')
                }
              },
              '.mine' : {
                'required' : {
                  'message' : i18n.messages('mine.required')
                }
              }
            }
          },
          myCustRegEx : {
            'minSize' : {
              'regex' : 'none',
              'alertText' : i18n.messages('minSize.alertText'),
              'alertText2' : i18n.messages('minSize.alertText2')
            },
            'myCustValid' : {
              'regex' : /^(0)(9)([0-9]{8})?$/,
              'alertText' : i18n.messages('myCustValid.alertText')
            }
          }
        });
      });

      // cust valiation regex
      $.extend($.validationEngineLanguage.allRules, Properties.myCustRegEx);

      // cust valid method
      $.extend(window, {
        _minSize : function(field, rules, i, options) {
          var min = rules[i + 2], len = field.val().length, mId = '#' + field.attr('id'), custMsg = '';
          if (len < min) {
            if (typeof options.custom_error_messages[mId] != "undefined" && typeof options.custom_error_messages[mId]['fieldName'] != "undefined") {
              custMsg = options.custom_error_messages[mId]['fieldName']['message'];
            }
            var rule = options.allrules.minSize;
            return custMsg + rule.alertText + min + rule.alertText2;
          }
        },
        regex : function(field, rules, i, options) {
          var val = field.val();
          rules.push('required');
          var r = new RegExp(options.allrules[rules[i + 2]].regex);
          if (val) {
            if (!r.test(val)) {
              return options.allrules[rules[i + 2]].alertText;
            }
          }
        }
      });

    });
