pageInit(function() {
  $(document).ready(function() {
    var form = $('#copyForm');

    // 現有搬檔流程
    $("#sendBtn").on('click', function() {
      var datas = form.serializeData();
      console.log(datas);
      $.ajax({
        url : url('democopyfilehandler/copyFileProcess'),
        type : 'post',
        data : datas,
        success : function(d) {
          if (d.error) {
            $('#resultBoard').html(d.error);
          } else {
            $('#resultBoard').html("success !");
          }
        },
        error : function(xhr, desc, err) { // error : function(xhr, status,
          // errorThrown) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });

    // 1.檢查本地是否已掛載目標網路磁碟機
    $("#mappingBtn").on('click', function() {
      $.ajax({
        url : url('democopyfilehandler/mappingLocalPath'),
        type : 'post',
        data : form.serializeData(),
        success : function(d) {
          if (d.result) {
            $('#resultBoard').html(d.result);
          } else {
            $('#resultBoard').html("本地需要掛載目標網路磁碟機");
          }
        },
        error : function(xhr, desc, err) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });

    // 2.連接網路磁碟機，指定本地磁碟機代號
    $("#connectBtn1").on('click', function() {
      $.ajax({
        url : url('democopyfilehandler/connectDiskWithDrive'),
        type : 'post',
        data : form.serializeData(),
        success : function(d) {
          if (d.result) {
            $('#resultBoard').html(d.result);
          }
        },
        error : function(xhr, desc, err) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });

    // 2.連接網路磁碟機，不指定本地磁碟機代號
    $("#connectBtn2").on('click', function() {
      $.ajax({
        url : url('democopyfilehandler/connectDisk'),
        type : 'post',
        data : form.serializeData(),
        success : function(d) {
          if (d.result) {
            $('#resultBoard').html(d.result);
          }
        },
        error : function(xhr, desc, err) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });

    // 3.搬檔
    $("#copyBtn").on('click', function() {
      $.ajax({
        url : url('democopyfilehandler/copyFile'),
        type : 'post',
        data : form.serializeData(),
        success : function(d) {
          if (d.result) {
            $('#resultBoard').html(d.result);
          } else if (d.error) {
            $('#resultBoard').html(d.error);
          }
        },
        error : function(xhr, desc, err) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });

    // 4.卸載特定網路磁碟機
    $("#disconnectBtn").on('click', function() {
      $.ajax({
        url : url('democopyfilehandler/disconnectNetworkPath'),
        type : 'post',
        data : form.serializeData(),
        success : function(d) {
          if (d.result) {
            $('#resultBoard').html(d.result);
          } else if (d.error) {
            $('#resultBoard').html(d.error);
          }
        },
        error : function(xhr, desc, err) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });

    // 4.卸載所有網路磁碟機
    $("#disconnectAllBtn").on('click', function() {
      $.ajax({
        url : url('democopyfilehandler/disconnectAllNetworkPath'),
        type : 'post',
        data : form.serializeData(),
        success : function(d) {
          if (d.result) {
            $('#resultBoard').html(d.result);
          } else if (d.error) {
            $('#resultBoard').html(d.error);
          }
        },
        error : function(xhr, desc, err) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });

  });
});
