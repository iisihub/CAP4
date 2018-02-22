pageInit(function() {
  $(function() {
    var _f = $("form");
    $("#submit")
        .click(
            function() {
              if (_f.validationEngine('validate')) {
                window.setCloseConfirm(false);
                window.location = 'http://59.124.83.56:9003/no-target/oauth/code?response_type=code&client_id=PufzaI4gHJNccdAEcG8EyAj1AfYrWZqf&scope=inquiry%20pay&username=sunkist@cap';
                // $
                // .ajax(
                // {
                // url :
                // 'http://59.124.83.56:9003/no-target/oauth/code?response_type=code&client_id=PufzaI4gHJNccdAEcG8EyAj1AfYrWZqf&scope=inquiry%20pay&username=sunkist',
                // type : 'GET'
                // }).always(function(d) {
                // $("html").html(d.responseText);
                // });
              }
            });
  });
});
