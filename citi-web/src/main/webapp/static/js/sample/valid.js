pageInit(function() {
  $(function() {
    var form = $("form");

    form.validationEngine('attach', $.extend({
      addSuccessCssClassToField: 'input-success',
      addFailureCssClassToField : 'input-error'
    }, Properties.myCustMessages));

    $("#check").click(function() {
      if (form.validationEngine('validate')) {
        logDebug('Valid succes~!');
      } else {
        logDebug('Valid failed~!');
      }
    });
  });
});
