var sMsgD0001 = "若要發送至多個信箱，請用;分隔";
var sEnterNumItem = "PIN";
var bAllowFlag = false;

function toHexString(str) {
  var hex = '';
  for(var i=0;i<str.length;i++) {
    hex += ''+str.charCodeAt(i).toString(16);
  }
  return hex;
}

function HexToStr(s) {
	var retS = "";
	for (var i = 0; i < s.length; i+=2) {
		retS += String.fromCharCode("0x" + s.substring(i, i+2));
	}
	return retS;
}

//return cookie data
//ex:gC("JSESSIONID");
function gC(n) {
  var c = "" + document.cookie;
  i1 = c.indexOf(n + "=");
  if (i1 == -1)return null;
  var e1 = c.indexOf(";", i1);
  if (e1 == -1) e1 = c.length;
  return unescape(c.substring(i1 + n.length + 1, e1));
}

function MM_preloadImages() { //v3.0
  var d = document;
  if (d.images) {
    if (!d.MM_p) d.MM_p = new Array();
    var i, j = d.MM_p.length, a = MM_preloadImages.arguments;
    for (i = 0; i < a.length; i++)
      if (a[i].indexOf("#") != 0) {
        d.MM_p[j] = new Image;
        d.MM_p[j++].src = a[i];
      }
  }
}

function MM_swapImgRestore() { //v3.0
  var i, x, a = document.MM_sr;
  for (i = 0; a && i < a.length && (x = a[i]) && x.oSrc; i++) x.src = x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p, i, x;
  if (!d) d = document;
  if ((p = n.indexOf("?")) > 0 && parent.frames.length) {
    d = parent.frames[n.substring(p + 1)].document;
    n = n.substring(0, p);
  }
  if (!(x = d[n]) && d.all) x = d.all[n];
  for (i = 0; !x && i < d.forms.length; i++) x = d.forms[i][n];
  for (i = 0; !x && d.layers && i < d.layers.length; i++) x = MM_findObj(n, d.layers[i].document);
  if (!x && d.getElementById) x = d.getElementById(n);
  return x;
}

function MM_swapImage() { //v3.0
  var i, j = 0, x, a = MM_swapImage.arguments;
  document.MM_sr = new Array;
  for (i = 0; i < (a.length - 2); i += 3)
    if ((x = MM_findObj(a[i])) != null) {
      document.MM_sr[j++] = x;
      if (!x.oSrc) x.oSrc = x.src;
      x.src = a[i + 2];
    }
}

function MM_openBrWindow(theURL, winName, features) { //v2.0
  window.open(theURL, winName, features);
}
function logout2() {
  location.href = "../../exit.htm"
}
function logout() {
  location.href = "../exit.htm"
  // window.close('win');
  //  MM_openBrWindow('login.htm','','scrollbars=yes,width=737,height=580')
}

var arrIllegalKeyCode = new Array;
arrIllegalKeyCode.push(8); //Backspace

var arrRestrictionKeyCode = new Array;  //not Allow forever
arrRestrictionKeyCode.push(9); //Tab

function onKeyHandler() {
  for (i = 0; i < arrIllegalKeyCode.length; i++) {
    if (!bAllowFlag) {
      if (event.keyCode == arrIllegalKeyCode[i]) return false;
    }
  }
  for (i = 0; i < arrRestrictionKeyCode.length; i++) {
    if (event.keyCode == arrRestrictionKeyCode[i]) return false;
  }
  return true;
}

function KeyBlockOn() {
  bAllowFlag = false;
}

function KeyBlockOff() {
  bAllowFlag = true;
}

function enter_Num(enterValue) {
  if (sEnterNumItem == "PIN") {
    var objPIN = document.form1.PIN;
    if (document.form1.PIN.readOnly)
      document.form1.PIN.disabled = true;
    if (document.form1.NewPIN.readOnly)
      document.form1.NewPIN.disabled = false;
    if (document.form1.CfmPIN.readOnly)
      document.form1.CfmPIN.disabled = false;
  } else if (sEnterNumItem == "NewPIN") {
    var objPIN = document.form1.NewPIN;
    if (document.form1.NewPIN.readOnly)
      document.form1.NewPIN.disabled = true;
    if (document.form1.PIN.readOnly)
      document.form1.PIN.disabled = false;
    if (document.form1.CfmPIN.readOnly)
      document.form1.CfmPIN.disabled = false;
  } else if (sEnterNumItem == "CfmPIN") {
    var objPIN = document.form1.CfmPIN;
    if (document.form1.CfmPIN.readOnly)
      document.form1.CfmPIN.disabled = true;
    if (document.form1.PIN.readOnly)
      document.form1.PIN.disabled = false;
    if (document.form1.NewPIN.readOnly)
      document.form1.NewPIN.disabled = false;
  } else if (sEnterNumItem == "PERIOD") {
    var objPIN = document.form1.PERIOD;
  } else if (sEnterNumItem == "ACODE") {
    var objPIN = document.form1.AUTHCODE;
  }

  var pswd = objPIN.value;
  if (pswd != null && pswd.length < 12 && sEnterNumItem != "ACODE") {
    objPIN.value = pswd + enterValue;
  }
  if (sEnterNumItem == "ACODE" && pswd.length < 4) {
    objPIN.value = pswd + enterValue;
  }
  if (enterValue == "clear") {
    objPIN.value = "";
  }
}

function unlock_PIN() {
  if (document.form1.PIN)
    document.form1.PIN.disabled = false;
  if (document.form1.NewPIN)
    document.form1.NewPIN.disabled = false;
  if (document.form1.CfmPIN)
    document.form1.CfmPIN.disabled = false;
}

function goMainMenu() {
  location.href = "../main.htm";
}
function goMainMenu2(menuName) {
  if (menuName == "m0701") {
    location.href = "../../ap07/0101.htm";
  } else if (menuName == "m0702") {
    location.href = "../../ap07/0201.htm";
  } else if (menuName == "m04") {
    location.href = "01.htm";
  } else if (menuName == "m07") {
    location.href = "01.htm";
  } else {
    location.href = "../main.htm";
  }
}
function confirmBtn() {

  location.href = "../main.htm";

}

/*** 功能描述 : TimeOut 設定時間登出操作畫面 ***/
//    Number of seconds before the expiration of ATM operation
var iOperSeco = 300;
//    Period of checking ATM card (1 second = 1000)
var iChecPeri = 100;
// Input text object to display seconds remained
var oTimeRema;
// Input text object to display seconds remained
var iTimeRema;
// Seconds remained
var iChecDispInte;
var iOperInte;
// Seconds timeout for ATM Operation
var iOperTimeOut;
var iShouDispTimeRema = 1;
var sMsgE0001 = "由於您超過5分鐘未操作本系統\n系統已自動登出";
var iProcessType = 0;

function InitDispTimeRema(iType) {
  iProcessType = iType;
  iOperTimeOut = setTimeout("OperTimeOut();", iOperSeco * 1000);
  if (iShouDispTimeRema == 1)
    clearInterval(iChecDispInte);
  iChecDispInte = setInterval("ChecDispObje();", 10);
}

function StopTimeRema() {
  clearInterval(iChecDispInte);
  clearInterval(iOperInte);
}

function OperTimeOut() {
  //2007.09.11 直接跳出不秀出警示小框 
  //ShowMessAndQuit("sTimeOutOper",sMsgE0001);

  //if (iProcessType==0)
  //	process('logout');
  //else
  //	process('logout',0);

  form1._service.value = 'logoutTimeOut';
  form1.submit();
}

function ShowMessAndQuit(sOper, sErroMess) {
  //DiscCard();
  //eval(sOper);
  //if (iShouLinkAler!=0)
  alert(sErroMess);
}

function DispTimeRema() {
  iTimeRema = iTimeRema - 1;
  if (iTimeRema <= 0) iTimeRema = 0;
  oTimeRema.value = iTimeRema;
}

function ChecDispObje() {
  if (oTimeRema != null) {
    if (typeof(oTimeRema) == "object") {
//			oTimeRema.width=iTimeRemaWidt;
      iTimeRema = iOperSeco;
      oTimeRema.value = iTimeRema;

      clearTimeout(iOperTimeOut);
      iOperInte = setTimeout("OperTimeOut();", iOperSeco * 1000);
      clearInterval(iChecDispInte);
      iChecDispInte = setInterval("DispTimeRema();", 1000);
    }
  }
}

/******************************************/

var charArray = new Array(
		' ','!','"','#','$','%','&',"'",'(',')','*','+',',','-',
		'.','/','0','1','2','3','4','5','6','7','8','9',':',';',
		'<','=','>','?','@','A','B','C','D','E','F','G','H','I',
		'J','K','L','M','N','O','P','Q','R','S','T','U','V','W',
		'X','Y','Z','[','\\',']','^','_','`','a','b','c','d','e',
		'f','g','h','i','j','k','l','m','n','o','p','q','r','s',
		't','u','v','w','x','y','z','{','|','}','~','','C\,','u\"',
		'e\'','a^','a\"','a\`','a*','c,','e^','e"','e\`','i\"',
		'i^','i\`','A\"','A*','A*','E\'','?','?','o^','o\"','o\`',
		'u^','u\`','y\"','O\"','U\"','?','?','?','','f','a\'',
		'i\'','o\'','u\'','n~','N~','^(a)','^(o)','?','?','?','1/2',
		'1/4','?','?','?','_','_','_','?','?','A\'','A^','A`','(c)',
		'?','?','+','+','?','?','+','+','-','-','+','-','+','a~','A~',
		'+','+','-','-','?','-','+','?','?','?','E^','E"','E`','i',
		'I\'','I^','I"','+','+','_','_','?','I`','_','O\'','?','O^','O`',
		'o~','O~','?','?','?','U\'','U^','U`','y\'','Y\'','','?','?','',
		'_','3/4','?','','','?','X','','P','^(1)','^(3)','^(2)','_',' ');
var hex_digits = new Array(
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F');
function byteToChar(n){
	if(n < 32 || n > 255) {
		return " ";//Periodly Fixed for kas
	}
	return charArray[n-32];
}

function chars_from_hex(hex_str){
	var char_str = "";
	var i;
	for(i=0; i < hex_str.length; i+=2) {
		char_str += byteToChar(parseInt(hex_str.substring(i,i+2),16));
	}
	return char_str;
}

function repeat(s, num){
    return new Array( num + 1 ).join( s );
}

function padEnd(s, targetLength, padString) {
  targetLength = targetLength >> 0; //floor if number or convert non-number to 0;
  padString = String((typeof padString !== 'undefined' ? padString : ' '));
  if (s.length > targetLength) {
      return String(s);
  }
  else {
      targetLength = targetLength - s.length;
      if (targetLength > padString.length) {
          padString += repeat(padString, targetLength / padString.length); //append to original to ensure we are longer than needed
      }
      return String(s) + padString.slice(0, targetLength);
  }
}

//轉24byte 不足補0x00
function paddingRight(str){
	var len = str.length;
	if(len >= len/48 && len % 48 == 0 )
	return str;
	else
	return padEnd(str, parseInt(len/48) * 48 + 48, 0x00);
//	return paddingRight(str+"0x00");
 }

function isInteger(val) {
	if (val == null) {
		return false;
	}
	var str = val.toString();
	for (var i=0; i<str.length; i++) {
		var ch = str.charAt(i);
		if (i==0   && ch=='-') {
			continue;
		}
		if (ch<'0' || ch>'9') {
			return false;
		}
	}
	return true;
}

function isPosInteger(val) {
	if (val == null) {
		return false;
	}
	var str = val.toString();
	for (var i=0; i<str.length; i++) {
		var ch = str.charAt(i);
		if (ch < '0' || ch > '9') {
			return false;
		}
	}
	return true;
}

function leftTrim(item, char1){
	if (!char1) {
		char1 = " ";
	}
	if (item){
		while (item.substring(0,1) == char1)
		item = item.substring(1);
	}
	return item;
}

function rightTrim(item, char1){
	if (!char1) {
		char1 = " ";
	}
	if (item) {
		while (item.substring(item.length-1, item.length) == char1)
		item = item.substring(0, item.length-1);
	}
	return item;
}

function trim(item, char1){
	if (!char1) {
		char1 = " ";
	}
	item = leftTrim(item, char1);
	item = rightTrim(item, char1);
	return item;
}


//AWATM

function checkATM() {
	try {
		var version = ATM.GetAPIVersion();
		
		return version;
	} catch (e) {
		alert('晶片金融卡交易程式元件安裝失敗！請確認您的讀卡機驅動程式安裝完成。');
		return;
	}
}

function ConnectReaderV3(Reader, needDisconnect) {
	if (Reader == "")
		return false;

	ATM.baReaderName = Reader;
	bRdrReady = ATM.connectReader();
	if (bRdrReady != 0) {
		alert("讀卡機連線失敗，請確認讀卡機是否已安裝妥當!");
		return false;
	} else {
		if (needDisconnect == true) {
			ATM.DisConnectReader();
			return true;
		}
		return true;
	}
}


function ConnectCardV3(Reader, needDisconnect) {

	if (ConnectReaderV3(Reader, false) == false) {
		return false;
	}

	// 使用ReConnectCard測試是否已ConnectCard
	// ATM.iResetType = 0;
	// bRdrReady = ATM.ReConnectCard();
	// if (bRdrReady != 0) {
	ATM.DisConnectCard();
	ATM.DisConnectReader();
	ATM.connectReader();
	bRdrReady = ATM.ConnectCard();
	if (bRdrReady != 0) {
		alert("請檢查晶片卡是否已依正確方向插入讀卡機!");
		return false;
	}

	var iRc = ATM.SelectAID(); // 驗證是否為晶片金融卡
	if (iRc != 0) {
		ATM.DisConnectCard();
		ATM.DisConnectReader();
		alert("請插入正確的晶片金融卡");
		return false;
	}
	if (needDisconnect == true) {
		ATM.DisConnectCard();
		ATM.DisConnectReader();
	}
	return true;
}

function getCardInfoV3(Reader) {

	var accts = new Array();
	var iRC;

	if (ConnectCardV3(Reader, false) == false) {
		return null;
	}

	if (ATM.SelectAID() != 0) { // 驗證是否為晶片金融卡
		alert("請插入正確的晶片金融卡");
		ATM.DisConnectCard();
		ATM.DisConnectReader();
		return null;
	}

	ATM.baEFID = '1001';
	var isEFopen = ATM.SelectEF();

	if (isEFopen == 0) {
		if (ATM.uiSW12 != 0x9000) {
			alert("卡片驗證失敗");
			return null;
		}
		ATM.bRecID = '01';
		ATM.bLen = 10;
		iRC = ATM.ReadRecord();
		if (iRC != 0) {
			alert("卡片驗證失敗");
			ATM.DisConnectCard();
			ATM.DisConnectReader();
			return null;
		} else {
			accts[0] = (chars_from_hex(ATM.baBuf)).substring(2, 10);
		}

		ATM.bRecID = '02';
		ATM.bLen = 253;
		iRC = ATM.ReadRecord();
		if (iRC != 0) {
			alert("卡片資料讀取錯誤！");
			ATM.DisConnectCard();
			ATM.DisConnectReader();
			return null;
		} else {
			accts[1] = (ATM.baBuf).substring(4, 64);
		}
	} else {
		alert("卡片操作失敗！");
		ATM.DisConnectCard();
		ATM.DisConnectReader();
		return null;
	}

	var accountNo = ATM.FiscListAccounts2() / 16;
	if (accountNo == 0) {
		alert('讀取轉出帳號有誤!');
		ATM.DisConnectCard();
		ATM.DisConnectReader();
		return null;
	}
	for (var i = 0; i < accountNo; i++) {
		var acct = eval("ATM.account" + i);
		// alert(i + ':' + acct);
		if (acct == "0000000000000000") {
			break;
		}
		if (trim(acct, " ") != "") {
			accts[accts.length] = acct;
		}
	}
	return accts;
}

function EncData(sSessionId, sEncInput) {
	debugger;
	ATM.sid=getAscStr(sSessionId);
	if ((sEncInput.length % 8) != 0){
		addCount = 8 - (sEncInput.length % 8);
		for(var i =0;i<addCount;i++){
			sEncInput +=" ";	
		} 
	}
	ATM.baINBuf=getAscStr(sEncInput);
	ATM.EncData();
	return ATM.baOutBuf;
}

function getAscStr(s) {
	ret = '';
	for (i = 0; i < s.length; i++) {
		ret = ret + (toHexString(s.charCodeAt(i))) ;
	}
	return ret ;
}

//取隨機產生的認證碼(4位數字)

function getAuthCode(){
	
	var sRand="";
	for (var a=0;a<4;a++){
		var rand=parseInt(10*Math.random());
		sRand+=rand;
		
	}
	return sRand;
}

function getIEVersion() {
    var rv = -1;
    if (navigator.appName == 'Microsoft Internet Explorer') {
        var ua = navigator.userAgent;
        var re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
        if (re.exec(ua) != null) {
            rv = parseFloat(RegExp.$1);
        }
    } else if (navigator.appName == 'Netscape') {
        var ua = navigator.userAgent;
        // for IE 11
        var re = new RegExp("Trident/.*rv:([0-9]{1,}[\.0-9]{0,})");
        if (re.exec(ua) != null) {
            rv = parseFloat(RegExp.$1);
        }
    }
    return rv;
};


//取得使用者瀏覽器名稱
function detectBrowser(){
	var isIE = navigator.userAgent.search("MSIE") > -1; //IE 系列
    var isIE7 = navigator.userAgent.search("MSIE 7") > -1; //IE 7
    var isFirefox = navigator.userAgent.search("Firefox") > -1; //Firefox火狐瀏覽器
    var isOpera = navigator.userAgent.search("Opera") > -1; //Opera 系列
    var isChrome = navigator.userAgent.search("Chrome") > -1;//Google 瀏覽器核心系列
    if (isIE7) {
        browser = 'IE7';
    }
    if (isIE) {
        browser = 'IE';
    }
    if (isFirefox) {
        browser = 'Firefox';
    }
    if (isOpera) {
        browser = 'Opera';
    }
    if (isChrome) {
        browser = 'Chrome';
    }
    return browser;
}


//參數1 str：需要補0的字串
//參數2 len：要補0的長度
// 左邊補0
function padLeft(str,lenght){
if(str.length >= lenght)
return str;
else
return padLeft("0" +str,lenght);
}
//右邊補0
function padRight(str,lenght){
if(str.length >= lenght)
return str;
else
return padRight(str+"0",lenght);
}
