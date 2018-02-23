pageInit(function() {
  $(function() {
    // var _url = new URL(window.location.href);
    // var code = _url.searchParams.get("code");
    // var scope = _url.searchParams.get("scope");
    $.ajax({
      url : url('oauthhandler/token'),
      data : {
        'code' : reqJSON.code
      }
    }).done(function(d) {
      $("form").injectData(d);
      $("#token").text(print(d));
      inquiryAccount(d);
    }).fail(function(d) {
      // window.location = 'http://localhost:8080/cap-web/j_spring_security_logout';
    });

    var print = function(d) {
      var t = '';
      for (i in d) {
        t += (i + " : " + d[i] + " ");
      }
      return t;
    };

    var inquiryAccount = function(d) {
      $("#result").empty();
      $.ajax({
        url : url('mobilebankinghandler/getAvailableBalance'),
        data : {
          'access_token' : d.access_token,
          'username' : d.username
        }
      }).done(function(d) {
        $("#result").text(print(d));
      });
    };

    $("#redo").click(function() {
      var _f = $("form").serializeData();
      _f && inquiryAccount(_f);
    })

  });
});
