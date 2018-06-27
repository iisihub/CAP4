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

//**************************************************************** 
//General purpose functions
//
//Company: 
//Author : 
//Emial  : 
//
//**************************************************************** 
//Log of changes: 
//**************************************************************** 

//檢核是否為整數型態之數字
function chknumberformat(chkstr, msgheader) {
var iCode = 0
// parse every char, no restriction
for (var i = 0; i < chkstr.length; i++) {
 iCode = chkstr.charCodeAt(i)
 if ((iCode < '0'.charCodeAt(0) || iCode > '9'.charCodeAt(0))) {
   alert(msgheader + "必須為整數型態!");
   return false;
 }
}

if (chkstr.substring(0, 1) == '0') {
 alert(msgheader + "第一碼不可為0!");
 return false;
}

return true;
}

//不足位填滿0 str:欲補0的字串 len:補滿0後的總長度 align:left(靠左補0)/right(靠右補0) floatLen:小數點長度
function addZero2(str, len, align, floatLen) {
var floatIndex = 0;
var floatStr = "";
var fLen = 0;
if (floatLen != 0) {
 floatIndex = str.indexOf(".");
 if (floatIndex > -1) {
   fLen = str.length - floatIndex - 1;
   floatStr = str.substring(floatIndex + 1, fLen);
 }
 for (var j = 0; j < floatLen; j++) {
   floatStr += "0";
 }
 str += floatStr;
}
zeroCount = len - str.length;
zeroStr = "";
for (var i = 0; i < zeroCount; i++) {
 zeroStr += "0";
}
if (align == "left") {
 //表示str前面加0
 zeroStr = zeroStr + str;
} else {
 //表示str後面加0
 zeroStr = str + zeroStr;
}
return zeroStr;
}

//不足位填滿空白 str:欲補空白的字串 len:補滿空白後的總長度 align:left(靠左補0)/right(靠右補0)
function addSpace(str, len, align, floatLen) {
var fLen = 0;
spaceCount = len - str.length;
spaceStr = "";
for (var i = 0; i < spaceCount; i++) {
 spaceStr += " ";
}
if (align == "left") {
 //表示str前面加0
 spaceStr = spaceStr + str;
} else {
 //表示str後面加0
 spaceStr = str + spaceStr;
}
return spaceStr;
}

function addZero(Num) {
var strAmount;
var lgAmount = 0;
var lgMupltify = 1;
var len = 0;
var radix = 2;

var dAmount = parseFloat(amntFormat_inverse(Num));
if (dAmount == 0) {
 strAmount = "0.00";
 return strAmount;
}
try {
 do {
   for (var i = 0; i < radix; i++) {
     lgMupltify = lgMupltify * 10;
   }
   strAmount = "" + Math.round(dAmount * lgMupltify);
   len = strAmount.length;
   strAmount = strAmount.substring(0, len - radix) + "." + strAmount.substring(len - radix, len);
   break;
 } while (true);
} catch (e) {
 alert("ERROR FORMAT" + e.toString());
 strAmount = "N/A";
}
return strAmount;
}


function isAlpha(elmstr) {
if (elmstr != '') {
 for (var i = 0; i < elmstr.length; i++) {
   if ((elmstr.charAt(i) < '0' || elmstr.charAt(i) > '9') &&
     (elmstr.charAt(i) < 'A' || elmstr.charAt(i) > 'Z') &&
     (elmstr.charAt(i) < 'a' || elmstr.charAt(i) > 'z')) {
     return false;
   }
 }
}
return true;
}


//輸出現在時間 2001/10/12 10:00
function mynow() {
var MyNow = new Date();
var now_year = MyNow.getFullYear();
var now_month = MyNow.getMonth() + 1;
if (now_month < 10) {
 now_month = "0" + now_month;
}
var now_day = MyNow.getDate();
if (now_day < 10) {
 now_day = "0" + now_day;
}
var now_hours = MyNow.getHours();
var now_min = MyNow.getMinutes();
if (now_min < 10) {
 now_min = "0" + now_min;
}
//輸出
document.writeln(now_year + "/" + now_month + "/" + now_day + "&nbsp;" + now_hours + ":" + now_min);
}
//輸出現在時間 2001/10/12 10:00:00
function mynow2() {
var MyNow = new Date();
var now_year = MyNow.getFullYear();
var now_month = MyNow.getMonth() + 1;
if (now_month < 10) {
 now_month = "0" + now_month;
}
var now_day = MyNow.getDate();
if (now_day < 10) {
 now_day = "0" + now_day;
}
var now_hours = MyNow.getHours();
var now_min = MyNow.getMinutes();
var now_sec = MyNow.getSeconds();
if (now_min < 10) {
 now_min = "0" + now_min;
}
if (now_sec < 10) {
 now_sec = "0" + now_sec;
}
//輸出
document.writeln(now_year + "/" + now_month + "/" + now_day + "&nbsp;" + now_hours + ":" + now_min + ":" + now_sec);
}
//輸出今天日期
function getToday() {
var MyNow = new Date();
var now_year = MyNow.getFullYear();
var now_month = MyNow.getMonth() + 1;
if (now_month < 10) {
 now_month = "0" + now_month;
}
var now_day = MyNow.getDate();
if (now_day < 10) {
 now_day = "0" + now_day;
}
//輸出
//nice  2001.10.12修改  加toString()轉成String
var myresult = now_year.toString() + now_month.toString() + now_day.toString();
return myresult;
}

//in:940101 out:94/01/01 民國年
function DateFormatShow2(indate) {
if (indate.length == 6) {
 var outdate = indate;
 outdate = outdate.substring(0, 2) + "/" + outdate.substring(2, 4) + "/" + outdate.substring(4, 6);
 return outdate;
} else {
 return indate;
}
}

//in:20010613 out:2001/06/13
function DateFormatShow(indate) {
if (indate.length == 8) {
 var outdate = indate;
 outdate = outdate.substring(0, 4) + "/" + outdate.substring(4, 6) + "/" + outdate.substring(6, 8);
 return outdate;
} else {
 return indate;
}
}
function convertime(inst) {
if (inst == "000000") {
 return "&nbsp;";
} else {
 inst = TimeFormats(inst);
 return inst;
}
}
//轉換Time(main Function)  in:HHMMSS  out:HH:MM:SS
function TimeFormats(intime) {
if (intime.length == 6) {
 var outtime = intime;
 outtime = outtime.substring(0, 2) + ":" + outtime.substring(2, 4) + ":" + outtime.substring(4, 6);
 return outtime;
} else if (intime == 'null') {
 return "";
} else {
 return intime;
}
}
//in:2001/06/13 out:20010613
function revertDateTime(indate2) {
if (indate2.length == 10) {
 var outdate = indate2;
 outdate = outdate.substring(0, 4) + outdate.substring(5, 7) + outdate.substring(8, 10);
 return outdate;
} else {
 return indate2;
}
}
//in:2001/06/13 out:民國年 900613--西元年轉民國年
function CHTtoAD(indate2) {
if (indate2.length == 10) {
 var outdate = indate2;
 var transyear = eval(outdate.substring(0, 4)) - 1911;
 outdate = transyear.toString() + outdate.substring(5, 7) + outdate.substring(8, 10);
 return outdate;
} else {
 return indate2;
}
}
//in:00900613 out:2001/06/13 民國年轉西元年
function ADtoCHT(indate2) {
if (indate2.length == 8) {
 var outdate = indate2;
 var transyear = outdate.substring(0, 4);
 for (i = 0; i < transyear.length; i++) {
   if (transyear.substring(i, i + 1) != "0") {
     transyear = transyear.substring(i);
   }
 }
 outdate = (eval(transyear) + 1911) + "/" + outdate.substring(4, 6) + "/" + outdate.substring(6, 8);
 return outdate;
} else {
 return indate2;
}
}
function isFloat(elmstr) {
if (elmstr != '') {
 var j = 0;
 if (elmstr.charAt(0) == '.') {
   return false;
 }

 if (elmstr.charAt(elmstr.length - 1) == '.') {
   return false;
 }
 for (var i = 0; i < elmstr.length; i++) {
   if ((elmstr.charAt(i) < '0' || elmstr.charAt(i) > '9') && elmstr.charAt(i) != '.') {
     return false;
   } else {
     if (elmstr.charAt(i) == '.') {
       j = j + 1;
       if (j > 1) {
         return false;
       }
     }
   }
 }
}
return true;
}

function isInt(elmstr) {
if (elmstr != '') {
 for (var i = 0; i < elmstr.length; i++) {
   if (elmstr.charAt(i) < '0' || elmstr.charAt(i) > '9') {
     return false;
   }
 }
}
return true;
}
/*
金額格式表達
@param amnt String
@return String 字串(99,999,999)
字串加上三位一撇(有處理科學符號, 但是無法處理有小數點的字串, 也沒有處理空白字串)
*/
function amntFormat(amnt) {
//add by 2003/06/19 解決網頁金額大於一千萬會顯示科學符號問題
amnt = parseFloat(amnt).toString();
var sAmnt = amnt;
var sign = "";
if (amnt < 0) {
 sAmnt = sAmnt.substring(1, sAmnt.length);
 sign = "-";
}

if (sAmnt.length <= 3) {
 return sAmnt;
}
var mony = "";

while (sAmnt.length > 3) {
 mony = "," + sAmnt.substring(sAmnt.length - 3, sAmnt.length) + mony;
 sAmnt = sAmnt.substring(0, sAmnt.length - 3);
 if (sAmnt.length <= 3) {
   mony = sAmnt + mony;
 }
}
return sign + mony;
}

//in:1234673.1
//out:1,234,673.10
//字串加上三位一撇(有處理科學符號, 也有處理小數點的字串,會將空白字串轉成 0)
function amntFormat2(instr) {
//add by 2003/06/19 解決網頁金額大於一千萬會顯示科學符號問題
instr = parseFloat(instr).toString();
if (instr == "") {
 return "0";
}
instr = instr + "";
var estr = instr.indexOf(".");
var intstr = "";
var floatstr = "";

//當沒有小數點時
if (estr == "-1") {
 return amntFormat(instr);
} else {
 //處理整數部份
 intstr = instr.substring(0, estr);
 intstr = amntFormat(intstr);
 //處理小數部份
 floatstr = instr.substring(estr + 1);
 if (floatstr.length >= 2) {
   floatstr = floatstr.substring(0, 2);
 } else {
   for (var i = 0; i < (2 - floatstr.length); i++) {
     floatstr = floatstr + "0";
   }
 }
}
return (intstr + "." + floatstr);
}


//in:1234673.1
//out:1,234,673.10
//字串加上三位一撇(沒有處理科學符號, 有處理小數點的字串,會將空白字串轉成 0)
//只有套過 acc.js 的 mathcal() 後才能使用此方法, 否則會有科學符號的問題
function amntFormat4(instr) {
if (instr == "") {
 return "0";
}
instr = instr + "";
var estr = instr.indexOf(".");
var intstr = "";
var floatstr = "";

//當沒有小數點時
if (estr == "-1") {
 return amntFormat(instr);
} else {
 //處理整數部份
 intstr = instr.substring(0, estr);
 intstr = amntFormat(intstr);
 //處理小數部份
 floatstr = instr.substring(estr + 1);
 if (floatstr.length >= 2) {
   floatstr = floatstr.substring(0, 2);
 } else {
   for (var i = 0; i < (2 - floatstr.length); i++) {
     floatstr = floatstr + "0";
   }
 }
}
return (intstr + "." + floatstr);
}
//in:1,234,673.10 
//out:1234673.1
function amntFormat_inverse(instr) {
var myindex;
var Resstr = instr;
while ((myindex = Resstr.indexOf(",")) != -1) {
 Resstr = Resstr.substring(0, myindex) + Resstr.substring(myindex + 1);
}
return Resstr;
}

/*
*帳號格式的轉換(XXX-XXX-XXXXXXX-X)
*/
function accountFormat(instr) {
if (instr == "null" || instr == "")
 return "";
if (instr.length < 14)
 return instr;
var ResStr = instr.substring(0, 3) + "-" + instr.substring(3, 6) + "-" + instr.substring(6, 13) + "-" + instr.substring(13, 14);
return ResStr;
}

/*
*金額格式轉換(6789.00-->$6,789.00，-57843.00-->-$57,843.00)
*/
function amountFormat(instr) {
if (instr == "" || instr == "null") {
 return "&nbsp;";
}
var amount = instr;
if (amount.charAt(0) == '-') {
 amount = amntFormat3(instr.substring(1));
 amount = "-$" + amount;
} else {
 amount = amntFormat3(instr);
 amount = "$" + amount;
}
return amount;
}

//字串加上三位一撇(有處理科學符號, 也有處理小數點的字串,但是空白字串還是空白字串)
function amntFormat3(instr) {

if (instr == "" || instr == "null") {
 return "";
}
//add by 2003/06/19 解決網頁金額大於一千萬會顯示科學符號問題
var instr2 = instr;
instr = parseFloat(instr).toString();
var estr = instr.indexOf(".");
var intstr = "";
var floatstr = "";
var floatlen = 0;

//當沒有小數點時
if (estr == "-1") {
 intstr = amntFormat(instr);
 floatlen = floatstr.length;
 for (var i = 0; i < (2 - floatlen); i++) {
   floatstr = floatstr + "0";
 }
 return (intstr + "." + floatstr);
} else {
 //處理整數部份
 intstr = instr.substring(0, estr);
 intstr = amntFormat(intstr);
 //處理小數部份
 floatstr = instr.substring(estr + 1);
 if (floatstr.length >= 2) {
   if (floatstr.length > 5) {
     floatstr = floatstr.substring(0, 5);
   } else {
     floatstr = floatstr.substring(0, floatstr.length);
   }
 } else {
   floatlen = floatstr.length;
   for (var i = 0; i < (2 - floatlen); i++) {
     floatstr = floatstr + "0";
   }
 }
}
return (intstr + "." + floatstr);
}

/*
*金額格式轉換(6789-->$6,789.00，-57843.00-->-$57,843.00)
*/
function amountFormat(instr) {
if (instr == "" || instr == "null") {
 return "&nbsp;";
}
var amount = instr;
if (amount.charAt(0) == '-') {
 amount = "-$"
 instr = instr.substring(1);

} else {
 amount = "$"
}
amount = amount + amntFormat3(instr);
return amount;
}


//將null字串轉換為空白
function strNull(str) {

if (str == null || str == "null") {
 return "&nbsp;";
} else {
 return str;
}
}
/*
*金額格式轉換(00000000012300-->$123.00，-00000000012300-->-$123.00)
*/
function amountFormat2(instr) {
if (instr == "" || instr == "null") {
 return "&nbsp;";
}
if (instr.length == 14) {
 instr = instr.substring(0, 12) + "." + instr.substring(12, 14)
 var amount = amntFormat3(instr);
 if (amount.charAt(0) == '-') {
   amount = "-$" + amount.substring(1);
 } else {
   amount = "$" + amount;
 }
 return amount;
} else {
 return instr;
}
}


//驗證是否有特殊字元
function CheckSymbol(values, name) {
var a, j, flag;
//a=Array("'","@","!","$","<",">","!"," ")
a = Array("'", "$", "\"", "<", ">", "+", "&");

flag = true;
for (j = 0; j < a.length; j++) {
 if (!(values.indexOf(a[j], 0) == -1 )) {
   //MsgBox "請勿輸入不合法字元"
   //return (false);
   flag = false;
 }
}
if (!(flag == true)) {
 alert("[ " + name + " ]請勿輸入特殊符號");
 return (false);
}
else {
 return (true);
}
}

//檢核E-Mail格式
function CheckEmail(values) {
//var endlength = values.length-(values.indexOf(".",0)+1);
if ((values.substr(values.length - 3, 1) != ".") && (values.substr(values.length - 4, 1) != ".")) {
 alert("[ E-mail ]格式不正確");
 return (false);
}
else if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(values)) {
 return (true);
}
else {
 alert("[ E-mail ]格式不正確");
 return (false);
}
}

function checkDateFormat(Chk_DATE, msg) {
if (Chk_DATE.length == 8) {
 var date_arr = Chk_DATE.split("/");
 if (date_arr.length != 3) {
   alert(msg + "輸入有錯，格式為(YY/MM/DD)，請重新輸入");
   return false;
 }

 var nyear = 1911 + eval(date_arr[0]);
 if ((date_arr[1] > 12) || (date_arr[1] < 1) ||
   (date_arr[2] > 31) || (date_arr[2] < 1)) {
   alert(msg + "輸入有錯，格式為(YY/MM/DD)，請重新輸入");
   return false;
 }
 if ((date_arr[1] == 04) || (date_arr[1] == 06) ||
   (date_arr[1] == 09) || (date_arr[1] == 11)) {
   chd = 30;
 } else if ((date_arr[1] == 01) || (date_arr[1] == 03) ||
   (date_arr[1] == 05) || (date_arr[1] == 7) ||
   (date_arr[1] == 8) || (date_arr[1] == 10)
   || (date_arr[1] == 12)) {
   chd = 31;
 } else {
   if ((nyear % 4 == 0 && nyear % 100 != 0) || nyear % 400 == 0) {
     chd = 29;
   } else {
     chd = 28;
   }
 }
 if (date_arr[2] > chd) {
   alert(msg + "輸入有錯，格式為(YY/MM/DD)，請重新輸入");
   return false;
 }
} else if (Chk_DATE.length == 6) {
 var YY = Chk_DATE.substring(0, 2);
 var MM = Chk_DATE.substring(2, 4);
 var DD = Chk_DATE.substring(4, 6);
 var nyear = 1911 + eval(YY);

 if ((MM > 12) || (MM < 1) || (DD > 31) || (DD < 1)) {
   alert(msg + "輸入有錯，請重新輸入");
   return false;
 }
 if ((MM == 04) || (MM == 06) || (MM == 09) || (MM == 11)) {
   chd = 30;
 } else if ((MM == 01) || (MM == 03) || (MM == 05) || (MM == 7) ||
   (MM == 8) || (MM == 10) || (MM == 12)) {
   chd = 31;
 } else {
   if ((nyear % 4 == 0 && nyear % 100 != 0) || nyear % 400 == 0) {
     chd = 29;
   } else {
     chd = 28;
   }
 }
 if (DD > chd) {
   alert(msg + "輸入有錯，請重新輸入");
   return false;
 }
} else if (Chk_DATE.length == 10) {
 var date_arr = Chk_DATE.split("/");
 if (date_arr.length != 3) {
   alert(msg + "輸入有錯，格式為(YYYY/MM/DD)，請重新輸入");
   return false;
 }

 var nyear = eval(date_arr[0]);
 if ((date_arr[1] > 12) || (date_arr[1] < 1) ||
   (date_arr[2] > 31) || (date_arr[2] < 1)) {
   alert(msg + "輸入有錯，格式為(YYYY/MM/DD)，請重新輸入");
   return false;
 }
 if ((date_arr[1] == 04) || (date_arr[1] == 06) ||
   (date_arr[1] == 09) || (date_arr[1] == 11)) {
   chd = 30;
 } else if ((date_arr[1] == 01) || (date_arr[1] == 03) ||
   (date_arr[1] == 05) || (date_arr[1] == 7) ||
   (date_arr[1] == 8) || (date_arr[1] == 10)
   || (date_arr[1] == 12)) {
   chd = 31;
 } else {
   if ((nyear % 4 == 0 && nyear % 100 != 0) || nyear % 400 == 0) {
     chd = 29;
   } else {
     chd = 28;
   }
 }
 if (date_arr[2] > chd) {
   alert(msg + "輸入有錯，格式為(YYYY/MM/DD)，請重新輸入");
   return false;
 }
} else {
 alert(msg + "輸入有錯，請重新輸入");
 return false;
}
return true;
}

