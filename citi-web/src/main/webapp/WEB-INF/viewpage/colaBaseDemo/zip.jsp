<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>ZIP TEST PAGE</title>
<style type="text/css">
#overwrite_Y, #overwrite_N {
	height: 20px;
	width: 30px;
}

.mainBody input {
	width: 70%;
}

.leftBox {
	width: 60%;
	float: left;
	border-right: 2.3px dotted #CBC9CF;
}

.rightBox {
	background: #E9E7ED;
	width: 39%;
	height: 15.65em;
	float: right;
}

.unzipRightBox {
	height: 11em;
}

.isEmptyFolderRightBox {
	height: 8em;
}

.isExistsFileRightBox {
	height: 5em;
}

.outerBox {
	display: inline-block;
	border-bottom: 1px solid #CBC9CF;
	padding-bottom: 0.7em;
	width: 100%;
}
</style>
</head>
<body>
	<script>
    loadScript('js/colaBaseDemo/zip');
  </script>
	<h3>《壓縮檔案》</h3>
	<div class="outerBox">
		<div class="leftBox">
			<form id="zipForm" autocomplete="off" method="post">
				<div>
					選擇檔案 <input type="text" id="zipFile" name="zipFile" />
				</div>
				<div>
					輸出目錄 <input type="text" id="zipOutPath" name="zipOutPath" />
				</div>
				<div>
					壓縮密碼 <input type="text" id="zipPassword" name="zipPassword" />
				</div>
				<div>
					檔案名稱 <input type="text" id="zipName" name="zipName" />
				</div>
				<span>是否覆寫 <input type="radio" id="overwrite_Y"
					name="overwrite" value="Y"><label for="overwrite_Y"
					class="radioClass"><span></span>是</label> <input type="radio"
					id="overwrite_N" name="overwrite" value="N"><label
					for="overwrite_N" class="radioClass"><span></span>否</label>
				</span>
			</form>
			<button id="sendZipBtn" class="sendbtn" type="button">送出</button>
		</div>
		<div class="rightBox">
			<p id="zipResult"></p>
		</div>
	</div>
	<div>
		<h3>《解壓縮檔案》</h3>
		<div class="outerBox">
			<div class="leftBox">
				<form id="unzipForm" autocomplete="off" method="post">
					<div>
						選擇檔案 <input type="text" id="unzipFile" name="unzipFile" />
					</div>
					<div>
						輸出目錄 <input type="text" id="unzipOutPath" name="unzipOutPath" />
					</div>
					<div>
						解壓密碼 <input type="text" id="unzipPassword" name="unzipPassword" />
					</div>
				</form>
				<button id="sendUnzipBtn" class="sendbtn" type="button">送出</button>
			</div>
			<div class="rightBox unzipRightBox">
				<p id="unzipResult"></p>
			</div>
		</div>
	</div>
	<div>
		<h3>《確認資料夾是否為空 》</h3>
		<div class="outerBox">
			<div class="leftBox">
				<form id="isEmptyFolderForm" autocomplete="off" method="post">
					<div>
						選擇檔案 <input type="text" id="isEmptyFolder1" name="isEmptyFolder1" />
					</div>
					<div>
						選擇檔案 <input type="text" id="isEmptyFolder2" name="isEmptyFolder2" />
					</div>
				</form>
				<button id="sendIsEmptyFolderBtn" class="sendbtn" type="button">送出</button>
			</div>
			<div class="rightBox isEmptyFolderRightBox">
				<p id="isEmptyFolderResult"></p>
			</div>
		</div>
	</div>
	<div>
		<h3>《確認檔案是否存在 》</h3>
		<div class="outerBox">
			<div class="leftBox">
				<form id="isExistsFileForm" autocomplete="off" method="post">
					<div>
						選擇檔案 <input type="text" id="isExistsFile" name="isExistsFile" />
					</div>
				</form>
				<button id="sendIsExistsFileBtn" class="sendbtn" type="button">送出</button>
			</div>
			<div class="rightBox isExistsFileRightBox">
				<p id="isExistsFileResult"></p>
			</div>
		</div>
	</div>
</body>
</html>
