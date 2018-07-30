pageInit(function() {
	$(document).ready(function() {

		// Zip
		$("#sendZipBtn").click(function() {
			var datas = {
				zipFile : $('#zipFile').val(),
				zipOutPath : $('#zipOutPath').val(),
				zipPassword : $('#zipPassword').val(),
				zipName : $('#zipName').val(),
				overwrite : $('input:radio[name="overwrite"]:checked').val()
			};

			$.ajax({
				type : 'post',
				async : true,
				data : datas,
				url : url('demoziphandler/zipDemo'),
				success : function(e) {
					$('#zipResult').text(e.result);
				}
			});
		});
		// Unzip
		$("#sendUnzipBtn").click(function() {
			var datas = {
				unzipFile : $('#unzipFile').val(),
				unzipOutPath : $('#unzipOutPath').val(),
				unzipPassword : $('#unzipPassword').val()
			};

			$.ajax({
				type : 'post',
				async : true,
				data : datas,
				url : url('demoziphandler/unzipDemo'),
				success : function(e) {
					$('#unzipResult').text(e.result);
				}
			});
		});

		// isEmptyFolder
		$("#sendIsEmptyFolderBtn").click(function() {
			var datas = {
				isEmptyFolder1 : $('#isEmptyFolder1').val(),
				isEmptyFolder2 : $('#isEmptyFolder2').val()
			};

			$.ajax({
				type : 'post',
				async : true,
				data : datas,
				url : url('demoziphandler/isEmptyFolderDemo'),
				success : function(e) {
					$('#isEmptyFolderResult').text(e.result);
				}
			});

		});

	});
});
