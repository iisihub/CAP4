pageInit(function() {
  $(document).ready(function() {
    $("#otpBtn").on('click', function(e) {
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/otp')
    });
    $("#imageUtilBtn").on('click', function(e) {
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/imageUtil')
    });
    $("#writingBoardBtn").on('click', function(e) {
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/signaturePanel')
    });

  });
});
