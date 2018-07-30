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

    // 分割PDF檔案
    $("#partitionPDF").on('click', function(e) {
      e.preventDefault()
      window.setCloseConfirm(false);
      partitionPDF()();
    });
    // PDF加入浮水印
    $("#waterMarkPDF").on('click', function(e) {
      e.preventDefault()
      window.setCloseConfirm(false);
      waterMarkPDF()();
    });
    // 選擇浮水印類型
    $(".wmType").on('change', function(e) {
      var wmTypeValue = $(".wmType").val();
      if (wmTypeValue == "text") {
        $("#wmNamePDF").show();
        $("#wmPDFImgPath").val("").hide();
      } else if (wmTypeValue == "imgPath") {
        $("#wmPDFImgPath").show();
        $("#wmNamePDF").val("").hide();
      }
    })

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
              pdfResultMsg = "PDF合併失敗！ Error Message: " + d.pdfReslut;
            }
            $("#pdfResultMsg").val(pdfResultMsg);
          }
        });
      } else {
        $("#pdfResultMsg").val("請輸入PDF合併資訊！");
      }
    }

    /**
     * 分割PDF
     */
    function partitionPDF() {
      var partPDFPath = $("#partPDFPath").val();
      var partPDFOutputPath = $("#partPDFOutputPath").val();
      var partPDFStartPage = $("#partPDFStartPage").val();
      if (partPDFPath && partPDFStartPage) {
        $.ajax({
          url : url('demopdfhandler/partitionPDF'),
          type : 'post',
          dataType : 'json',
          data : {
            partPDFPath : partPDFPath,
            partPDFOutputPath : partPDFOutputPath,
            partPDFStartPage : partPDFStartPage
          },
          success : function(d) {
            var pdfResultMsg;
            if (d.pdfReslut == "ok") {
              pdfResultMsg = "PDF分割成功！產生路徑： " + partPDFPath.substring(0, partPDFPath.lastIndexOf("/"));
            } else {
              pdfResultMsg = "PDF分割失敗！ Error Message: " + d.pdfReslut;
            }
            $("#pdfResultMsg").val(pdfResultMsg);
          }
        });
      } else {
        $("#pdfResultMsg").val("請輸入分割PDF資訊！");
      }
    }

    /**
     * PDF加入浮水印
     */
    function waterMarkPDF() {
      var wmPDFInputPath = $("#wmPDFInputPath").val();
      var wmPDFOutputPath = $("#wmPDFOutputPath").val();
      var wmNamePDF = $("#wmNamePDF").val();
      var wmPDFImgPath = $("#wmPDFImgPath").val();
      if (wmPDFInputPath && wmPDFOutputPath && (wmNamePDF || wmPDFImgPath)) {
        $.ajax({
          url : url('demopdfhandler/pdfAddWatermark'),
          type : 'post',
          dataType : 'json',
          data : {
            wmPDFInputPath : wmPDFInputPath,
            wmPDFOutputPath : wmPDFOutputPath,
            wmNamePDF : wmNamePDF,
            wmPDFImgPath : wmPDFImgPath
          },
          success : function(d) {
            var pdfResultMsg;
            if (d.pdfReslut == "ok") {
              pdfResultMsg = "PDF加入浮水印成功！產生路徑： " + wmPDFOutputPath;
            } else {
              pdfResultMsg = "PDF加入浮水印失敗！ Error Message: " + d.pdfReslut;
            }
            $("#pdfResultMsg").val(pdfResultMsg);
          }
        });
      } else {
        $("#pdfResultMsg").val("請輸入PDF加入浮水印資訊！");
      }
    }

  });
});
