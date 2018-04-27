pageInit(function() {
  $(document).ready(function() {
    var $resultBoard = $('#resultBoard'),
        $ownData = $("#ownData"),
        $ownHeader = $("#ownHeader");
        $targetUrl = $("#targetUrl");
    

    $("#sendData").click(function() {
      debugger;
      var datas;
      try {
        JSON.parse($ownData.val());
        if($ownHeader.val() !== "")
          JSON.parse($ownHeader.val());
      } catch (e) {
        $resultBoard.html("please check your json data format")
        return;
      }

      datas = {
        headerData : $ownHeader.val(),
        jsonData : $ownData.val(),
        targetUrl: $targetUrl.val()
      };

      $.ajax({
        type : 'post',
        async : true,
        url : url('demosslclienthandler/testSslClient'),
        data : datas,
        success : function(res) {
          $resultBoard.val(res.result);
        }
      });
    });

  });
});
