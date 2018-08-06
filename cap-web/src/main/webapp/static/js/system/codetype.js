pageInit(function() {
  $(function() {
    var grid = $("#gridview").jqGrid({
      url : url('codetypehandler/query'),
      sortname : 'codeType',
      sortorder : "desc",
      height : 350,
      colModel : [ {
        name : 'oid',
        hidden : true
      }, {
        header : i18n['codetype']['codetype.codeType'],
        name : 'codeType',
        width : 110,
        align : "center"
      }, {
        header : i18n['codetype']['codetype.codeVal'],
        name : 'codeValue',
        width : 80,
        align : "center"
      }, {
        header : i18n['codetype']['codetype.codeDesc'],
        name : 'codeDesc',
        width : 80,
        align : "center"
      }, {
        header : i18n['codetype']['codetype.codeOrder'],
        name : 'codeOrder',
        width : 80,
        align : "center"
      }, {
        header : i18n.def.lastModBy,
        name : 'updater',
        width : 80,
        align : "center"
      }, {
        header : i18n.def.lastModTm,
        name : 'updateTime',
        width : 80,
        align : "center"
      } ]
    });

    var qDialog = $("#qryDialog"), qform = qDialog.find("#qform");
    qDialog.dialog({
      height : 200,
      width : 350,
      modal : true,
      close : function() {
        qform.reset();
      },
      buttons : API.createJSON([ {
        key : i18n.def.sure,
        value : function() {
          grid.jqGrid('setGridParam', {
            postData : {
              locale : qform.find("#locale").val(),
              codeType : qform.find("#codeType").val()
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
      height : 300,
      width : 650,
      modal : true,
      close : function() {
        eform.reset().validationEngine('hide');
      },
      buttons : API.createJSON([ {
        key : i18n.def.sure,
        value : function() {
          eform.validationEngine('validate') && API.showConfirmMessage(i18n.def.actoin_001, function(data) {
            data && $.ajax({
              url : url("codetypehandler/" + (eDialog.data('type') == 'A' ? 'add' : 'modify')),
              data : eform.serializeData()
            }).done(function() {
              grid.trigger("reloadGrid");
              eDialog.dialog('close');
            });
          });
        }
      }, {
        key : i18n.def.close,
        value : function() {
          eDialog.dialog('close');
        }
      } ])
    });

    $(".btns").find("#qry").click(function() { // 查詢
      qDialog.dialog('open');
    }).end().find("#add").click(function() { // 新增
      eDialog.data('type', 'A').dialog('open');
    }).end().find("#modify").click(function() { // 修改
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
            url : url("codetypehandler/delete"),
            data : {
              oid : sel.oid
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
