pageInit(function() {
  $(function() {
    var _f = $("form");
    var client_id = _f.find("#client_id").val();
    var scope = _f.find("#scope").val();
    var username = reqJSON.user;
    $("#submit").click(function() {
      if (_f.validationEngine('validate')) {
        window.setCloseConfirm(false);
        window.location = 'http://59.124.83.56:9003/no-target/oauth/code?response_type=code&client_id=' + client_id + '&scope=' + scope + '&username=' + username;
        // $.ajax({
        // url : 'http://59.124.83.56:9003/no-target/oauth/code?response_type=code&client_id=' + client_id + '&scope=' + scope + '&username=' + username,
        // type : 'GET'
        // }).always(function(d) {
        // debugger;
        // // window.location = '';
        // });
      }
    });
  });
});
