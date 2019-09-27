pageInit(function() {
  $(function() {

    var grid = $("#gridview").jqGrid({
      url : url('sysparmhandler/query'),
      sortname : 'parmId',
      height : 350,
      colModel : [ {
        header : i18n['sysparm']['sysparm.parmId'],// 代碼
        name : 'parmId',
        width : 20
      }, {
        header : i18n['sysparm']['sysparm.parmValue'],// 參數值
        name : 'parmValue',
        width : 30
      }, {
        header : i18n['sysparm']['sysparm.parmDesc'],// 說明
        name : 'parmDesc',
        width : 30
      }, {
        header : i18n.def.lastModBy,
        name : 'updater',
        width : 10,
        align : "center"
      }, {
        header : i18n.def.lastModTm,
        name : 'updateTime',
        width : 10,
        align : "center"
      } ]
    });

    var qDialog = $("#qryDailog"), qform = qDialog.find("#qform");
    qDialog.dialog({
      height : 150,
      width : 350,
      modal : true,
      close : function() {
        qform.reset();
        qform.validationEngine('hide');
      },
      buttons : API.createJSON([ {
        key : i18n.def.sure,
        value : function() {
          grid.jqGrid('setGridParam', {
            postData : {
              parmId : qform.find("#parmId").val()
            }
          });
          grid.trigger("reloadGrid");
          qDialog.dialog('close');
        }
      }, {
        key : i18n.def.close,
        value : function() {
          qDialog.dialog('close');
        }
      } ])
    });

    var eDialog = $("#editDialog"), eform = eDialog.find("#eform");
    eDialog.dialog({
      height : 250,
      width : 650,
      modal : true,
      open : function() {
        eform.find('#parmId').readOnly(eDialog.data('type') == 'A' ? false : true);
      },
      close : function() {
        eform.reset();
        eform.validationEngine('hide');
      },
      buttons : API.createJSON([ {
        key : i18n.def.sure,
        value : function() {
          eform.validationEngine('validate') && $.ajax({
            url : url("sysparmhandler/" + (eDialog.data('type') == 'A' ? 'add' : 'modify')),
            data : eform.serializeData()
          }).done(function() {
            grid.trigger("reloadGrid");
            eDialog.dialog('close');
          });
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
      eDialog.data('type', 'A').dialog('open');
    }).end().find("#modify").click(function() {// 修改
      var sel = grid.getSelRowDatas();
      if (sel) {
        eform.injectData(sel);
        eDialog.data('type', 'M').dialog('open');
      } else {
        API.showErrorMessage(i18n.def.grid_selector);
      }
    }).end().find("#delete").click(function() { // 刪除
      var sel = grid.getSelRowDatas();
      if (sel) {
        API.showConfirmMessage(i18n.def.actoin_001, function(data) {
          data && $.ajax({
            url : url("sysparmhandler/delete"),
            data : {
              parmId : sel.parmId
            }
          }).done(function() {
            grid.trigger("reloadGrid");
          });
        });
      } else {
        API.showErrorMessage(i18n.def.grid_selector);
      }
    });
  });
});
