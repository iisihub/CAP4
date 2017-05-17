pageInit(function() {
  $(function() {
    "use strict";
    $("#submit").click(function() {
      $.ajax({
        url : url('captchahandler/checkCaptcha'),
        data : {
          // captcha : $("#captcha").val(),
          audioCaptcha : $("#audioCaptcha").val()
        }
      }).always(function() {
        // $("#captcha").trigger("refresh");
        $("#audioCaptcha").trigger("refresh");
      });
    });

  });
});
