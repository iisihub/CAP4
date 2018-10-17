/* 專案JS設定檔    */
$.extend(Properties || {}, {
  window : {
    closeConfirm : true,
    closeWindowMsg : '重新載入後資料將會消失!!\nReload the page data will be lost!!',
    onunload : function() {
      $.ajax({
        url : url('checktimeouthandler/checkClosePage'),
        type : 'post',
        async : false,
        data : {
          CCPAGENO : window.CCPAGENO
        }
      });
    }
  },
  contextName : "/cap-web/",
  ajaxTimeOut : 60 * 1000 * 3, // timeOut: 1000
  // 下拉選單handler
  ComboBoxHandler : 'codetypehandler/queryByKeys',
  Grid : {
    rowNum : 30,
    rowList : []
  },
  custLoadPageInit : function(isSubPage) {
    //for captcha start
    console.debug('cust load page init');
    this.find(".captcha").each(function() {
      var dom = $(this);
      var img = $("<img />", {
        src : url("captcha.png?cc=" + new Date().getTime()),
        css : {
          height : 24,
          weight : 60
        }
      });
      dom.on("refresh", function() {
        dom.val("");
        img.attr("src", url("captcha.png?cc=" + new Date().getTime()));
      });
      var refresh = $("<img />", {
        src : url("static/images/refresh.png"),
        css : {
          height : 24,
          cursor : 'pointer'
        },
        click : function() {
          dom.trigger("refresh");
        }
      });
      dom.after(refresh).after(img);
    });
    this.find(".audioCaptcha").each(function() {
      var dom = $(this);
      var audio = $("<audio controls autoplay />", {
        src : url("audio.wav?cc=" + new Date().getTime()),
        css : {
          height : 24,
          weight : 60
        }
      });
      dom.on("refresh", function() {
        dom.val("");
        audio.attr("src", url("audio.wav?cc=" + new Date().getTime()));
      });
      var refresh = $("<img />", {
        src : url("static/images/refresh.png"),
        css : {
          height : 24,
          cursor : 'pointer'
        },
        click : function() {
          dom.trigger("refresh");
        }
      });
      dom.after(refresh).after(audio);
    });
    //for captcha end
  },
  timeOut : 'TIME_OUT',
  // Control Client Timeout
  remindTimeout : false
});
