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
    $("#zipBtn").on('click', function(e) {
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/zip')
    });
    $("#netUseUtilBtn").on('click', function(e) {
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/copyFile')
    });
    $("#sslClientBtn").on('click', function(e) {
      window.setCloseConfirm(false);
      window.location.href = url('page/colaBaseDemo/sslClient')
    });
  });
});