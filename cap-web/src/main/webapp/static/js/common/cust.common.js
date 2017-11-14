// init
var menu = {
  "child" : [ {

    "name" : "系統設定",
    "url" : "system",
    "child" : [ {
      "name" : "代碼設定",
      "url" : "system/codetype"
    }, {
      "name" : "參數設定",
      "url" : "system/sysparm"
    }, {
      "name" : "流水號檢視",
      "url" : "system/sequence"
    } ]
  }, {

    "name" : "系统功能",
    "url" : "sample",
    "child" : [ {
      "name" : "檔案上下傳",
      "url" : "sample/fileUpdDwn"
    } ]
  }, {
    "name" : "排程管理",
    "url" : "batch",
    "child" : [ {
      "name" : "排程設定",
      "url" : "batch/schedule"
    }, {
      "name" : "排程Job清單",
      "url" : "batch/jobs"
    }, {
      "name" : "排程監控",
      "url" : "batch/jobexecution"
    } ]

  } ]
}

// init
$(function() {
//      console.debug("cust common ready init");
      var navTop = $("nav.top"), navSub = $("nav.sub ol");
      function render(res) {
        var _menu = res.child, ul = $("nav.top ul.navmenu");
        // $("#userName").val(res.userName);
        navTop.on("click", "li a", function(ev) {
          ev.preventDefault();
          router.to($(this).attr("url"));
          $("article").empty();
        });

        navSub.on("click", "li a", function(ev) {
          var $this = $(this);
          if ($this.attr("url")) {
            router.to($(this).attr("url"));
          } else {
            if ($this.siblings("ul").length) {
              var sel = $this.siblings("ul");
              sel.is(":visible") ? sel.hide().parent("li").children("a").removeClass('clicked').children("span").removeClass('icon-5').addClass('icon-1') : sel.show().parent("li").children("a")
                  .addClass('clicked').children("span").removeClass('icon-1').addClass('icon-5');
            }
          }
          ev.preventDefault();
          return false;
        });

        // render menu
        for ( var m in _menu) {
          ul.append($("<li/>").append($("<a/>", {
            href : "#",
            url : _menu[m].url,
            data : {
              smenu : _menu[m].child,
              url : _menu[m].url
            },
            text : _menu[m].name
          })));
        }

        router.set({
          routes : {
            "" : "loadfirst", // default route
            ":page" : "loadsub", // http://xxxxx/xxx/#page
            ":page/:page2" : "loadpage" // http://xxxxx/xxx/#page/page2
          },
          loadfirst : function() {
            ul.find("li a:first").click();
          },
          loadsub : function(folder) {
            var tlink = navTop.find("a").removeClass("select").filter("a[url=" + folder + "]").addClass("select");
            var smenu = tlink.data("smenu");
            if (navSub.find('a').length) {
              navSub.animate({
                opacity : 0.01
              }, 200, _f);
            } else {
              navSub.css("opacity", "0.01");
              _f();
            }

            function _s(root, s_menu) {
              for ( var sm in s_menu) {
                if (s_menu[sm].child && s_menu[sm].child.length != 0) {
                  root.append($("<li/>").append($("<a/>", {
                    url : "",
                    data : {
                      url : ""
                    },
                    text : s_menu[sm].name
                  }).prepend("<span class='menu-icon icon-1'></span>")).append("<ul class='menu_sub'></ul>"));

                  _s(root.find("li ul").last(), s_menu[sm].child);
                } else if (s_menu[sm].url) {
                  root.append($("<li/>").append($("<a/>", {
                    url : s_menu[sm].url || "",
                    data : {
                      url : s_menu[sm].url || ""

                    },
                    text : s_menu[sm].name
                  }).prepend("<span class='menu-icon icon-4'></span>")));
                } else {
                  root.append($("<li/>").append($("<a/>", {
                    url : '#',
                    data : {
                      url : '#'
                    },
                    text : s_menu[sm].name
                  })));
                }
              }
            }

            function _f() {
              navSub.empty().data("cmenu", folder);
              _s(navSub, smenu);
              navSub.animate({
                opacity : 1
              });
            }
          },
          // router method
          loadpage : function(folder, page) {
            var topMenu = navTop.find("a").filter(function() {
              return filter($(this).data("smenu"), folder + "/" + page);
            });
            var topFolder = topMenu.attr("url");
            var refresh = !(navSub.data("cmenu") == topFolder);
            if (refresh) {
              this.loadsub(topFolder);
            }
            navSub.find('.selected').removeClass('selected').end().find("a[url='" + folder + '/' + page + "']").addClass("selected");
            if (refresh) {
              navSub.find('.selected').parents(".menu_sub").siblings("a").click();
            }

            // FIXME by sk
//            console.debug('API : '+ API);
            API && API.loadPage(folder + '/' + page);

            function filter(topSmenu, target) {
              for ( var m in topSmenu) {
                if (topSmenu[m].url == target) {
                  return true;
                }
                if (topSmenu[m].child) {
                  if (filter(topSmenu[m].child, target)) {
                    return true;
                  }
                }
              }
              return false;
            }
          }
        });
        return true;
      }

      // whit menuhandler start
      menu = false;
      // whit menuhandler end
      navTop.length && (menu && render(menu)) || $.get(url("menuhandler/queryMenu")).done(function(res) {
        render(res);
      });

      $("a[href='#language']").click(function() {
        var o = $(this).parents("ol");
        if (o.height() <= 18) {
          $(this).parent("li.lang").css('background-image', 'url(' + baseUrl + '/images/icon-down.png)');
          $(o).animate({
            height : 100
          });
        } else {
          $(this).parent("li.lang").css('background-image', 'url(' + baseUrl + '/images/icon-right.png)');
          $(o).animate({
            height : 18
          });
        }
        return false;
      });

      require(['jquery-ui'], function(jqueryui) {
        $.datepicker._gotoTodayOriginal = $.datepicker._gotoToday;
        $.datepicker._gotoToday = function(id) {
          // now, call the original handler
          $.datepicker._gotoTodayOriginal.apply(this, [ id ]);
          // invoke selectDate to select the current date and close datepicker.
          var target = $(id), inst = this._getInst(target[0]);
          var dateStr = (dateStr != null ? dateStr : this._formatDate(inst));
          inst.input.val(dateStr);
        };
        $.datepicker.setDefaults && $.datepicker.setDefaults({
          onChangeMonthYear : function(year, month, inst) {
            var ym = API.getToday().substr(0, 7), changeYm = year + "-" + (month < 10 ? "0" : "") + month;
            if (ym !== changeYm) {
              $(this).datepicker('setDate', changeYm + '-1');
            }
          }
        });
      });

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

    });
