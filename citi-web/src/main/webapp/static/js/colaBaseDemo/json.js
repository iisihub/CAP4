pageInit(function() {
  $(document).ready(function() {
    var $resultBoard = $('#resultBoard'), $ownData = $("#ownData"), $ownHeader = $("#ownHeader");
    $targetUrl = $("#targetUrl");

    $("input[type='checkbox']").on('click', function() {
      var value = false;
      if (this.checked === true)
        value = true;
      $('input[class=' + this.className + ']').prop('checked', value);
    });

    $("#sendData").click(function() {
      debugger;
      var datas = $("#jsonForm").serializeFormJSON();

      $.ajax({
        type : 'post',
        async : true,
        url : url('demojsonhandler/testSslClient'),
        data : datas,
        success : function(res) {
          $resultBoard.val(res.result);
        }
      });
    });

    (function($) {
      $.fn.serializeFormJSON = function() {

        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
          if (o[this.name]) {
            if (!o[this.name].push) {
              o[this.name] = [ o[this.name] ];
            }
            o[this.name].push(this.value || '');
          } else {
            o[this.name] = this.value || '';
          }
        });
        return o;
      };
    })(jQuery);
  });
});
