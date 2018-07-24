pageInit(function() {
  $(document).ready(function() {
    var form = $('#httpForm');

    // 現有匯檔流程
    $("#sendBtn").on('click', function() {
      var datas = form.serializeData();
      console.log(datas);
      $.ajax({
        url : url('demohttphandler/httpSend'),
        type : 'post',
        data : datas,
        success : function(d) {
          debugger
          if (d.responseString) {
            $('#resultBoard').html(d.responseString);
          }
//          else if (d.error) {
//            $('#resultBoard').html(d.error);
//          }
        },
        error : function(xhr, desc, err) { // error : function(xhr, status,
          // errorThrown) {
          debugger
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });
    
    $("#receiveBtn").on('click', function() {
      $.ajax({
        url : url('demohttphandler/httpReceive'),
        type : 'post',
        data : form.serializeData(),
        success : function(d) {
          if (d.responseString) {
            $('#resultBoard').html(d.responseString);
          }
        },
        error : function(xhr, desc, err) {
          $('#resultBoard').html("");
          console.log(xhr);
          console.log("Details: " + desc + "\nError:" + err);
        }
      });
    });
    
    $('#jsonStr').val("{\"name\": \"測試\",\"birthday\": \"19800101\",\"mobile\": \"0912345678\"}");
    
//    $('#basic').on('click', function() {
//      $('#paramNames1').show();
//      $('#paramValue1').show();
//    });
//    
//    $('#json').on('click', function() {
//      $('#jsonString1').show();
//    });
    
  });
});
