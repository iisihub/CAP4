pageInit(function() {
  $(document).ready(function() {
    var form;
    $("#oneTierSendBtn").on('click', function(e) {
      $("textarea[name=cauculateResult]").val('');
      form = $("#oneTier");
      var datas = form.serializeData();
      $.ajax({
        url : url('democaculateloanhandler/caculateLoan'),
        type : 'post',
        data : datas,
        success : function(d) {
          var amount = d.amount;
          var apr = d.apr;
          var firstEppRate =  d.firstEppRate;
          var firstExpense =  d.firstExpense;
          var lastExpense =  d.lastExpense;
          var tenor =  d.tenor;
          var fee = d.upfrontFee;
          $("textarea[name=cauculateResult]").val('amount: ' + amount + "\r\n" + 'apr: ' + apr + "\r\n" + 'firstEppRate: ' + firstEppRate + "\r\n" + 'firstExpense: ' + firstExpense + "\r\n" + 'lastExpense: ' + lastExpense + "\r\n" + 'tenor: ' + tenor + "\r\n" + 'fee: ' + fee + "\r\n");
        },
        error : function(jqXHR, exception) {
          $("textarea[name=cauculateResult]").val(exception);
        }
      });
    });

    $("#twoTierSendBtn").on('click', function(e) {
      $("textarea[name=cauculateResult]").val('');
      form = $("#twoTier");
      var datas = form.serializeData();
      $.ajax({
        url : url('democaculateloanhandler/caculateLoan'),
        type : 'post',
        data : datas,
        success : function(d) {
          var amount = d.amount;
          var apr = d.apr;
          var firstEppRate =  d.firstEppRate;
          var firstExpense =  d.firstExpense;
          var lastExpense =  d.lastExpense;
          var secondEppRate = d.secondEppRate;
          var tier2Expense = d.tier2Expense;
          var tenor =  d.tenor;
          var fee = d.upfrontFee;
          $("textarea[name=cauculateResult]").val('amount: ' + amount + "\r\n" + 'apr: ' + apr + "\r\n" + 'firstEppRate: ' + firstEppRate + "\r\n" + 'firstExpense: ' + firstExpense + "\r\n" + 'lastExpense: ' + lastExpense + "\r\n" + 'secondEppRate: ' + secondEppRate + "\r\n" + 'tier2Expense: ' + tier2Expense + "\r\n" + 'tenor: ' + tenor + "\r\n" + 'fee: ' + fee + "\r\n");
        },
        error : function(jqXHR, exception) {
          $("textarea[name=cauculateResult]").val(exception);
        }
      });
    });

  });
});