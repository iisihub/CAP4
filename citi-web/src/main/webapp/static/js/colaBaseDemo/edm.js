pageInit(function() {
	$(document).ready(function() {

		// edm
		$("#sendEdmBtn").click(function() {
			var datas = {
			  mailAddress : $('#mailAddress').val(),
			  edmFtlPath : $('#edmFtlPath').val(),
			  edmCustomerName : $('#edmCustomerName').val(),
			  edmProject : $('#edmProject').val()
			};

			$.ajax({
				type : 'post',
				async : true,
				data : datas,
				url : url('demoedmhandler/sendEdmDemo'),
				success : function(e) {
					$('#edmResult').text(e.result);
				}
			});
		});
  });
});
