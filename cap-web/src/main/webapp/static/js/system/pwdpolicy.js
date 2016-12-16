pageInit(function() {
  $(function() {
    $.ajax({
      url : url("pwdpolicyhandler/query")
    }).done(function(result) {
      $(document).injectData(result);
    });
    $('#confirm').click(function() {
      $.ajax({
        url : url("pwdpolicyhandler/modify"),
        data : $('#mform').serializeData()
      });
    });
  });
});
