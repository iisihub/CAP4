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
        header : i18n['errorCode']['code'],// "訊息代碼",
        name : 'code',
        align : 'left',
        width : 120,
        sortable : true
      }, {
        header : i18n['errorCode']['locale'],// "語言別",
        name : 'locale',
        align : 'center',
        width : 60,
        sortable : false
      // }, {
      // header: i18n['errorCode']['sysId'],//"系統別",
      // name: 'sysId', align: 'center', width: 60, sortable: true
      }, {
        header : i18n['errorCode']['severity'],// "等級",
        name : 'severity',
        align : 'left',
        width : 60,
        sortable : false
      }, {
        header : i18n['errorCode']['message'],// "狀態說明",
        name : 'message',
        align : 'left',
        width : 250,
        sortable : false
      }, {
        header : i18n['errorCode']['suggestion'],// "建議處理方式",
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
      } ],
      ondblClickRow : function() {
        openEditWindow(true);
      }
    });
    function openEditWindow(isEdit) {
      var eform = $('#eform');
      eform.reset();

      var oid = '';
      if (isEdit) {
        var sel = grid.getSelRowDatas();
        if (!sel) {
          API.showMessage(i18n.def['selectd.msg']);
          return;
        }
        oid = sel.oid;
        eform.find('#code').val(sel.code).attr("disabled", true);
        eform.find('#locale').val(sel.locale).attr("disabled", true);
        eform.find('#severity').val(sel.severity);
        eform.find('#message').val(sel.message);
        eform.find('#suggestion').text(sel.suggestion);
        eform.find('#sendMon').val(sel.sendMon);
        eform.find('#helpURL').val(sel.helpURL);
        eform.find('#sysId').val(sel.sysId);
      } else {
        eform.find('#code').val('').removeAttr("disabled");
        eform.find('#locale').val('').removeAttr("disabled");
        eform.find('#message').val('');
        eform.find('#suggestion').text('');
      }
      var eDialog = $("#editDialog").dialog({
        title : i18n.def['edit'], // 編輯
        modal : true,
        width : 600,
        height : 400,
        valign : "bottom",
        align : 'center',
        buttons : API.createJSON([ {
          key : i18n.def.sure,
          value : function() {
            if (eform.validationEngine('validate')) {
              $.ajax({
                url : url('errorCodehandler/' + (isEdit ? 'modify' : 'add')),
                data : $.extend(eform.serializeData(), {
                  oid : oid
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
      eDialog.dialog('open');
    }
    function openSearchWindow() {
      var qDialog = $("#qryDialog").dialog({
        title : i18n.def['query'],
        width : 400,
        height : 200,
        modal : true,
        close : function(){
          $("#qform").reset();
        },
        buttons : API.createJSON([ {
          key : i18n.def.sure,
          value : function() {
            grid.jqGrid('setGridParam', {
              postData : {
                code : qDialog.find("#code").val(),
                locale : qDialog.find("#locale").val(),
                sysId : qDialog.find("#sysId").val()
              }
            });
            grid.trigger("reloadGrid");
            qDialog.dialog('close');
          }
        }, {
          key : i18n.def.clear,
          value : function() {
            $("#qform").reset();
          }
        }, {
          key : i18n.def.close,
          value : function() {
            qDialog.dialog('close');
          }
        } ])
      });
      qDialog.dialog('open');
    }

    $(".btns").find("#qry").click(function() {// 查詢
      openSearchWindow();
    }).end().find("#add").click(function() {// 新增
      openEditWindow(false);
    }).end().find("#modify").click(function() {// 修改
      openEditWindow(true);
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
