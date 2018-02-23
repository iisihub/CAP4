pageInit(function() {
  $(function() {
    $("#submit").click(function() {
      if ($("form").validationEngine('validate')) {
        var _f = $("form").serializeData();
        var username = reqJSON.user;
        var scope = _f.inquiry + " " + _f.pay + " " + _f.other;
        var client_id = _f.client_id;

        $.ajax({
          url : url('mobilebankinghandler/user'),
          data : {
            username : username
          }
        });

        window.setCloseConfirm(false);
        window.location = 'http://59.124.83.56:9003/no-target/oauth/code?response_type=code&client_id=' + client_id + '&scope=' + scope + '&username=' + username;

        // $.ajax({
        // url : 'http://59.124.83.56:9003/no-target/oauth/code?response_type=code&client_id=' + client_id + '&scope=' + scope + '&username=' + username,
        // type : 'GET'
        // }).always(function(d) {
        // debugger;
        // $("html").html(d.responesText);
        // // window.location = '';
        // });

        // API.formSubmit({
        // url : 'http://59.124.83.56:9003/no-target/oauth/codeU+003Fresponse_type=code%26client_id=' + client_id + '%26scope=' + scope + '%26username=' + username,
        // type : 'GET'
        // })
      }
    });
  });
});
