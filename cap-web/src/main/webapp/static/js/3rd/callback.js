pageInit(function() {
  $(function() {
    var _url = new URL(window.location.href);
    var code = _url.searchParams.get("code");
    var scope = _url.searchParams.get("scope");
    $.ajax({
      url : url('oauthhandler/token'),
      data : {
        'code' : code,
        'scope' : scope
      }
    }).done(function(d) {
      if ((d.scope && d.scope.indexOf('inquiry') > -1) && (d.username && d.username == "sunkist@cap")) {
        $("form").injectData(d);
        inquiryAccount(d);
      } else {
        $("#result").text(print(d));
      }
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
        data : d
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
