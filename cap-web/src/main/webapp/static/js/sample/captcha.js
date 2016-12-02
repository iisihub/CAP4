pageInit(function() {
  $(function() {
    "use strict";
    $("#submit").click(function() {
      $.ajax({
        url : url('samplehandler/checkCaptcha'),
        data : {
          captcha : $("#captcha").val()
        }
      }).always(function() {
        $("#captcha").trigger("refresh");
      });
    });

  });
});
