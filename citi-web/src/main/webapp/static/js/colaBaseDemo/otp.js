var otpTimer;

pageInit(function() {
  $(document).ready(function() {
    var form = $("#form").val();
    var otpTimoutSecVal = $("#otpTimoutSec").val();
    var reloadSec = 5;
    var OTPExpiredErrorMsg = '您的「簡訊動態密碼OTP」已超過輸入時間，為保障您資料傳輸安全，' + reloadSec + '秒後跳轉畫面，請重新輸入，謝謝';

    // invalidate session
    $.ajax({
      url : url('demootphandler/invalidateSession'),
      async : true,
      success : function(d) {
        // 本頁面Time out時間固定為10分鐘
        localTimer(OTPExpiredErrorMsg, 10 * 60 * 1000);
      }
    });

    // 發送OTP
    $("#sendBtn").on('click', function(e) {
      e.preventDefault();
      genAndSendOTP(false);
    });

    // 重新發送OTP
    $("#reSendOtpBtn").on('click', function(e) {
      e.preventDefault();
      genAndSendOTP(true);
    });

    // 認證OTP簡訊動態密碼
    $("#subimtBtn").on('click', function(e) {
      e.preventDefault();
      verifyOTP();
    });

    /**
     * 驗證OTP密碼
     */
    function verifyOTP() {
      cleanResultValue();
      var otpVal = $("#otp").val();
      if (otpVal) {
        $.ajax({
          url : url('demootphandler/verifyOTP'),
          type : 'post',
          dataType : 'json',
          data : {
            USER_OTP : otpVal
          },
          success : function(d) {
            if (d.isVerify == true) {
              window.setCloseConfirm(false);
              $("#otpResultMsg").val("OTP密碼驗證成功！");
              clearTimeout(otpTimer);
            } else if (d.isVerify == false) {
              $("#otpResultMsg").val("OTP密碼驗證失敗！");
            } else {
              $("#otpResultMsg").val("請先發送OTP簡訊！");
            }
          }
        });
      } else {
        $("#otpResultMsg").val("請輸入6碼OTP簡訊動態密碼！");
      }
    }

    /**
     * 產生&發送OTP
     */
    function genAndSendOTP(isResend) {
      var mobilePhoneVal = $("#mobilePhone").val();
      var otpTimoutSecVal = $("#otpTimoutSec").val();
      var otpMaxRetryVal = $("#otpMaxRetry").val();
      if ((mobilePhoneVal && mobilePhoneVal.startsWith("09")) && (!isResend && otpTimoutSecVal || isResend && otpTimoutSecVal && otpMaxRetryVal)) {
        var sendStr = "簡訊發送成功！"
        if (isResend) {
          sendStr = "簡訊重新發送成功！"
        }
        $.ajax({
          url : url('demootphandler/sendOTP'),
          type : 'post',
          data : {
            MOBILE_PHONE : mobilePhoneVal,
            OTP_TIMOUT_SECONDS : otpTimoutSecVal,
            OTP_MAX_RETRY : otpMaxRetryVal,
            IS_RESEND_OTP : isResend
          },
          success : function(data) {
            if (otpTimoutSecVal > 0) {
              // 倒數計時otp輸入時間 xx seconds
              otpTimer = localTimer(OTPExpiredErrorMsg, otpTimoutSecVal * 1000);
            }
            $("#otpSmsMsg").val(data.otpSmsMsg);
            $("#retryMsg").val(data.retryMsg);
            $("#otpResultMsg").val(sendStr);
            $("#sendBtn").hide();
            $("#reSendOtpBtn,.otpMaxRetry").show();
            if (otpMaxRetryVal != "") {
              $("#otpMaxRetry").readOnly();
            }
          }
        });
      } else {
        cleanResultValue();
        if (!otpTimoutSecVal) {
          $("#otpResultMsg").val("請輸入OTP密碼TIME OUT秒數！");
        } else if (isResend && !otpMaxRetryVal) {
          $("#retryMsg").val("請輸入最多重送次數！");
        } else if (!mobilePhoneVal || !mobilePhoneVal.startsWith("09")) {
          $("#otpResultMsg").val("請輸入正確手機號碼！");
        }
      }
    }

    /**
     * 清除Result Message訊息
     */
    function cleanResultValue() {
      $("#otpSmsMsg,#retryMsg,#otpResultMsg").val("");
    }

    /**
     * 清除欄位填寫值
     */
    function cleanColumnValue() {
      $("#mobilePhone,#otpTimoutSec,#otpMaxRetry,#otp").val("");
    }

    /**
     * 初始畫面
     */
    function initView() {
      $("#sendBtn").show();
      $("#reSendOtpBtn,.otpMaxRetry").hide();
      $('button').prop('disabled', false);
      $("input").readOnly(false);
      cleanColumnValue();
      cleanResultValue();
    }

    /**
     * Timer
     */
    var localTimer = function(showMsg, secTime) {
      var timerid = setTimeout(function() {
        $.ajax({
          url : url('demootphandler/invalidateSession'),
          async : true
        }).done(function() {
          cleanResultValue();
          $("#otpResultMsg").val(showMsg);
          $('button').prop('disabled', true);
          setTimeout(function() {
            initView();
          }, reloadSec * 1000);
        });
      }, secTime);
      return timerid;
    }

  });
});
