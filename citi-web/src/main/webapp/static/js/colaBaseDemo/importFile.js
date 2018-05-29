pageInit(function() {
  $(document).ready(function() {
    var form = $('#importForm');

    // 現有匯檔流程
    $("#sendBtn").on('click', function() {
      var datas = form.serializeData();
      console.log(datas);
      $.ajax({
        url : url('demoimportfilehandler/importFileProcess'),
        type : 'post',
        data : datas,
        success : function(d) {
          if (d.result) {
            $('#resultBoard').html(d.result);
          } else if (d.error) {
            $('#resultBoard').html(d.error);
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
    
    $("#checkTimeBtn").on('click', function() {
      $.ajax({
        url : url('demoimportfilehandler/checkTime'),
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
    
    $("#checkDateBtn").on('click', function() {
      $.ajax({
        url : url('demoimportfilehandler/checkDate'),
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
    
    $("#countRowsBtn").on('click', function() {
      $.ajax({
        url : url('demoimportfilehandler/countRows'),
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
    
    $("#runSPBtn").on('click', function() {
      $.ajax({
        url : url('demoimportfilehandler/runSP'),
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
