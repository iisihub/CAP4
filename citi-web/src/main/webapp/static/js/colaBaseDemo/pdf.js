var otpTimer;

pageInit(function() {
  $(document).ready(function() {
    var form = $("#form").val();

    // 產生PDF
    $("#genPDF").on('click', function(e) {
      e.preventDefault();
    });

    // 下載PDF
    $("#dwnPDF").on('click', function(e) {
      e.preventDefault();
    });

    
    /**
     * 驗證OTP密碼
     */
    function generatePDF() {
        $.ajax({
          url : url('demopdfhandler/generatePDF'),
          type : 'post',
          dataType : 'json',
          data : {
            PDF_PATH : $("#pdfPath").val()
          },
          success : function(d) {
            
          }
        });
    }
  });
  
});
