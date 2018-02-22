pageInit(function() {
  $(function() {
    $("#submit").click(function() {
      window.setCloseConfirm(false);
      API.formSubmit({
        url : url('page/mb/authorize'),
        data : $("form").serializeData()
      });
    });
  });
});
