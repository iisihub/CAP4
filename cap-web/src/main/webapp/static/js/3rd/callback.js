pageInit(function() {
  $(function() {
    // var _url = new URL(window.location.href);
    // var code = _url.searchParams.get("code");
    // var scope = _url.searchParams.get("scope");
    $.ajax({
      url : url('oauthhandler/token'),
      data : {
        'code' : reqJSON.code
      // ,'scope' : scope
      }
    }).done(function(d) {
      $("#result").text(print(d));
      $("form").injectData(d);
      // FIXME username in session ?
      // FIXME who check this ?
      if ((d.scope && d.scope.indexOf(reqJSON.scope) > -1) && (d.username && d.username == reqJSON.username)) {
        inquiryAccount(d);
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
