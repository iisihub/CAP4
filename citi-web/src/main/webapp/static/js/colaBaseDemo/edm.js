pageInit(function() {
	$(document).ready(function() {

		// edm
		$("#sendEdmBtn").click(function() {
			var datas = {
			  eamilAccount : $('#eamilAccount').val(),
			  edmFtlFile : $('#edmFtlFile').val(),
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
			  ftlFile : $('#ftlFile').val(),
			  ftlOutPath : $('#ftlOutPath').val(),
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
