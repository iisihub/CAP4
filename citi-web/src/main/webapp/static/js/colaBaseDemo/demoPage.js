pageInit(function() {
  $(document).ready(function() {
    $("#otpBtn").on('click',function(e){
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/otp')
    });
    $("#imageUtilBtn").on('click',function(e){
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/imageUtil')
    });
  });
});
