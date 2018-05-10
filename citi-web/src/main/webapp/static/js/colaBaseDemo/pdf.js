var otpTimer;

pageInit(function() {
  $(document).ready(function() {
    var form = $("#pdfForm");

    // 產生PDF
    $("#genPDF").on('click', function(e) {
      e.preventDefault();
      generatePDF();
    });

    // 下載PDF
    $("#dwnPDF").on('click', function(e) {
      e.preventDefault()
      window.setCloseConfirm(false);
      downloadPDF();
    });

    // 合併PDF檔案
    $("#mergePDF").on('click', function(e) {
      e.preventDefault()
      window.setCloseConfirm(false);
      mergePDF();
    });

    /**
     * 產生PDF
     */
    function generatePDF() {
      var custName = $("#custName").val();
      var idNo = $("#idNo").val();
      var mPhone = $("#mPhone").val();
      var pdfPath = $("#pdfPath").val();
      var pdfName = $("#pdfName").val();
      var pdfPwd = $("#pdfPwd").val();
      if (custName && custName && mPhone) {
        $.ajax({
          url : url('demopdfhandler/generatePDF'),
          type : 'post',
          dataType : 'json',
          data : {
            custName : custName,
            idNo : idNo,
            mPhone : mPhone,
            PDF_PATH : pdfPath,
            PDF_NAME : pdfName,
            PDF_PASSWORD : pdfPwd
          },
          success : function(d) {
            var pdfResultMsg;
            if (d.pdfReslut == "ok") {
              pdfResultMsg = "PDF產生成功！產生路徑： " + pdfPath;
              if (pdfPwd != "") {
                pdfResultMsg += "，且已進行加密。"
              }
            } else {
              pdfResultMsg = "PDF產生失敗！ Error Message: " + d.pdfReslut;
            }
            $("#pdfResultMsg").val(pdfResultMsg);
          }
        });
      } else {
        $("#pdfResultMsg").val("請輸入PDF內容！");
      }
    }

    /**
     * 下載PDF
     */
    function downloadPDF() {
      var custName = $("#custName").val();
      var idNo = $("#idNo").val();
      var mPhone = $("#mPhone").val();
      var pdfName = $("#pdfName").val();
      var pdfPwd = $("#pdfPwd").val();
      $.capFileDownload({
        url : url("demopdfhandler/downloadPDF"),
        target : '_self',
        includeEmpty : true,
        data : {
          custName : custName,
          idNo : idNo,
          mPhone : mPhone,
          PDF_NAME : pdfName,
          PDF_PASSWORD : pdfPwd
        },
        success : function(d) {
          var pdfResultMsg;
          if (d.pdfReslut == "ok") {
            pdfResultMsg = "PDF檔案已下載"
            if (pdfPwd != "") {
              pdfResultMsg += "，且已進行加密。"
            }
          } else {
            pdfResultMsg = "PDF產生/下載失敗！ Error Message: " + d.pdfReslut;
          }
          $("#pdfResultMsg").val(pdfResultMsg);
        }
      });
    }

    /**
     * 合併PDF
     */
    function mergePDF() {
      var mgPDFPath1 = $("#mgPDFPath1").val();
      var mgPDFPath2 = $("#mgPDFPath2").val();
      var genMgPDFPath = $("#genMgPDFPath").val();
      var genMgPDFName = $("#genMgPDFName").val();
      if (mgPDFPath1 && mgPDFPath2) {
        $.ajax({
          url : url('demopdfhandler/mergePDF'),
          type : 'post',
          dataType : 'json',
          data : {
            mgPDFPath1 : mgPDFPath1,
            mgPDFPath2 : mgPDFPath2,
            genMgPDFPath : genMgPDFPath,
            genMgPDFName : genMgPDFName
          },
          success : function(d) {
            var pdfResultMsg;
            if (d.pdfReslut == "ok") {
              pdfResultMsg = "PDF合併成功！產生路徑： " + genMgPDFPath;
            } else {
              pdfResultMsg = "PDF產生失敗！ Error Message: " + d.pdfReslut;
            }
            $("#pdfResultMsg").val(pdfResultMsg);
          }
        });
      } else {
        $("#pdfResultMsg").val("請輸入PDF合併資訊！");
      }
    }

  });
});
