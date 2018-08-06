pageInit(function() {
  $(function() {
    var grid = $("#gridview").jqGrid({
      url : url('errorCodehandler/query'),
      height : "380",
      width : "100%",
      multiselect : false,
      hideMultiselect : false,
      autowidth : true,
      localFirst : true,
      colModel : [ {
        header : i18n['errorCode']['errorCode.code'],// "訊息代碼",
        name : 'code',
        align : 'left',
        width : 120,
        sortable : true
      }, {
        header : i18n['errorCode']['errorCode.locale'],// "語言別",
        name : 'locale',
        align : 'center',
        width : 60,
        sortable : false
      // }, {
      // header: i18n['errorCode']['errorCode.sysId'],//"系統別",
      // name: 'sysId', align: 'center', width: 60, sortable: true
      }, {
        header : i18n['errorCode']['errorCode.severity'],// "等級",
        name : 'severity',
        align : 'left',
        width : 60,
        sortable : false
      }, {
        header : i18n['errorCode']['errorCode.message'],// "狀態說明",
        name : 'message',
        align : 'left',
        width : 250,
        sortable : false
      }, {
        header : i18n['errorCode']['errorCode.suggestion'],// "建議處理方式",
        name : 'suggestion',
        align : 'left',
        width : 300,
        sortable : false
      }, {
        name : 'sendMon',
        hidden : true
      }, {
        name : 'helpURL',
        hidden : true
      }, {
        name : 'oid',
        hidden : true
      } ]
    });

    var qDialog = $("#qryDialog"), qform = qDialog.find("#qform");
    qDialog.dialog({
      title : i18n.def['query'],
      width : 400,
      height : 200,
      modal : true,
      close : function() {
        qform.reset();
      },
      buttons : API.createJSON([ {
        key : i18n.def.sure,
        value : function() {
          grid.jqGrid('setGridParam', {
            postData : {
              code : qform.find("#code").val(),
              locale : qform.find("#locale").val(),
              sysId : qform.find("#sysId").val()
            }
          });
          grid.trigger("reloadGrid");
          qDialog.dialog('close');
        }
      }, {
        key : i18n.def.clear,
        value : function() {
          qform.reset();
        }
      }, {
        key : i18n.def.close,
        value : function() {
          qDialog.dialog('close');
        }
      } ])
    });

    var eDialog = $("#editDialog"), eform = eDialog.find('#eform');
    eDialog.dialog({
      title : i18n.def['edit'], // 編輯
      modal : true,
      width : 600,
      height : 400,
      valign : "bottom",
      align : 'center',
      close : function() {
        eform.reset().validationEngine('hide');
      },
      buttons : API.createJSON([ {
        key : i18n.def.sure,
        value : function() {
          if (eform.validationEngine('validate')) {
            var sel = grid.getSelRowDatas();
            $.ajax({
              url : url('errorCodehandler/' + (eDialog.data('type') == 'M' ? 'modify' : 'add')),
              data : $.extend(eform.serializeData(), {
                oid : sel ? sel.oid : ''
              })
            }).done(function(responseData) {
              eDialog.dialog('close');
              grid.trigger("reloadGrid");
            });
          }
        }
      }, {
        key : i18n.def.close,
        value : function() {
          eDialog.dialog('close');
        }
      } ])
    });

    $(".btns").find("#qry").click(function() {// 查詢
      qDialog.dialog('open');
    }).end().find("#add").click(function() {// 新增
      eform.find('#code').val('').removeAttr("disabled");
      eform.find('#locale').val('').removeAttr("disabled");
      eform.find('#message').val('');
      eform.find('#suggestion').text('');
      eDialog.data('type', 'A').dialog('open');
    }).end().find("#modify").click(function() {// 修改
      var sel = grid.getSelRowDatas();
      if (sel) {
        oid = sel.oid;
        eform.find('#code').val(sel.code).attr("disabled", true);
        eform.find('#locale').val(sel.locale).attr("disabled", true);
        eform.find('#severity').val(sel.severity);
        eform.find('#message').val(sel.message);
        eform.find('#suggestion').text(sel.suggestion);
        eform.find('#sendMon').val(sel.sendMon);
        eform.find('#helpURL').val(sel.helpURL);
        eform.find('#sysId').val(sel.sysId);
        eDialog.data('type', 'M').dialog('open');
      } else {
        API.showMessage(i18n.def['selectd.msg']);
      }
    }).end().find("#delete").click(function() {// 刪除
      var sel = grid.getSelRowDatas();
      if (sel) {
        API.showConfirmMessage(i18n.def['del.confrim'], function(b) {
          if (b) {
            $.ajax({
              url : url('errorCodehandler/delete'),
              data : {
                oid : sel.oid
              }
            }).done(function(responseData) {
              grid.trigger("reloadGrid");
            });
          }
        });
      } else {
        API.showMessage(i18n.def['selectd.msg']);
      }
    });
    
    $(".btns").find("#qry").trigger("click");
  });
});
