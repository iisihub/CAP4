pageInit(function() {
  $(function() {
    var sform = $("#sform");
    var grid = $("#gridview").jqGrid({
      url : url('sequencehandler/query'),
      height : 350,
      colModel : [ {
        header : i18n['sequence']['sequence.seqNode'],//流水號代碼
        name : 'seqNode',
        width : 20
      }, {
        header : i18n['sequence']['sequence.nextSeq'],//下一個序號
        name : 'nextSeq',
        width : 10
      }, {
        header : i18n['sequence']['sequence.rounds'],//rounds
        name : 'rounds',
        width : 10,
      }, {
        header : i18n.def.lastModTm,
        name : 'updateTime',
        width : 10,
        align : "center"
      } ]
    });

    $("#getNewSeq").click(function() {
      sform.validationEngine('validate') && API.showConfirmMessage(i18n.def.actoin_001, function(data) {
        data && $.ajax({
          url : url("sequencehandler/getNewSeq"),
          data : sform.serializeData(),
          success : function(rtn) {
            sform.find("#theSeq").text(rtn.theSeq);
            grid.trigger("reloadGrid");
          }
        });
      });
    });

    var qDialog = $("#qryDialog"), qform = qDialog.find("#qform");
    qDialog.dialog({
      height : 180,
      width : 400,
      modal : true,
      close : function() {
        qform.reset();
      },
      buttons : API.createJSON([ {
        key : i18n.def.sure,
        value : function() {
          grid.jqGrid('setGridParam', {
            postData : {
              seqNode : qform.find("#seqNode").val()
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

    var sDialog = $("#sequenceDialog"), sform = sDialog.find("#sform");
    sDialog.dialog({
      height : 180,
      width : 500,
      modal : true,
      close : function() {
        sform.reset();
      },
      buttons : API.createJSON([ {
        key : i18n.def.close,
        value : function() {
          sDialog.dialog('close');
        }
      } ])
    });

    $('.btns').find("#qry").click(function() {
      qDialog.dialog('open');
    }).end().find("#sequence").click(function() {
      sDialog.dialog('open');
    });
  });
});