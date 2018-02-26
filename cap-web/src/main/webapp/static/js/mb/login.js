pageInit(function() {
  $(function() {
    $("#submit").click(function() {
      window.setCloseConfirm(false);
      // API.formSubmit({
      //   url : url('page/mb/authorize'),
      //   data : $("form").serializeData()
      // });

      $.ajax({
        url: url('mobilebankinghandler/user'),
        data: {
          username: $("form").find("#user").val()
        }
      });

      router.to('mb/authorize', $("form").serializeData());
    });
  });
});
