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
		// ftl
		$("#sendFtlBtn").click(function() {
			var datas = {
				sourceFileName : $('#sourceFileName').val(),
				ftlDestination : $('#ftlDestination').val(),
			};

			$.ajax({
				type : 'post',
				async : true,
				data : datas,
				url : url('demoedmhandler/ftlDemo'),
				success : function(e) {
					$('#ftlResult').text(e.result);
				}
			});
		});

  });
});
