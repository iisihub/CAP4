var ports="00000";
var PostRet="0";

function XOR_hex(a1, b1) {
    var res = "";
  
    for (i=0;i< a1.length;i++) {
    var c1=a1.charCodeAt(i);
    var d1=b1.charCodeAt(i);    
    var xval=(c1 ^ d1).toString(16);
    if (xval.length<2) 
      xval ="0"+xval;
        res =  res+xval;
  }
    return res;
}
function post(str,callback)
{
  var resp="";
  var isOK=false;
  var URL="";
  var tport="";
  var sess=false;
  //var portList = new Array("58533","58534","58535","58536","58537");  
  var portList = new Array("58533","58534","58535");    
  var echoStr="a1=get&a2=AtmEcho";  
  try
  {
    try
    {
      var xhr = new XMLHttpRequest();
      //var isTrue =xhr.withCredentials ;
      //var totalURL = document.URL.substring(0, document.URL.indexOf("?", document.URL.indexOf("?") + 1));
      var totalURL = document.URL.split("?");   
      //var urlLocation =   totalURL[0];//document.URL.substring(0, document.URL.indexOf("/", document.URL.indexOf("/") + 2));
      var urlLocation =   document.URL.substring(0, document.URL.indexOf("/", document.URL.indexOf("/") + 2));      
      if (ports=="00000") {
        //try port
        for (var i = 0; i < portList.length; i++) {         
          try{            
            URL="https://localhost:"+portList[i]+"/CitiWebATM?a0="+urlLocation+"&"+echoStr; 
            xhr.open("POST", URL, false);  
            //xhr.open("GET", URL, false);  
            //xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            xhr.send(null);
            resp=xhr.responseText;    
            if (resp != "") {
              var sp=resp.split(";;");
              if (sp[0]!="CITI670294586183") {            
                continue;
              }
              else {
                ports=portList[i];            
                break;              
              }
            }
            else {
              continue;       
            }           
          }catch(err){
            continue;
          }
        }
        if (ports == "00000" ) {
          return "-1";
        }       
      }
      URL="https://localhost:"+ports+"/CitiWebATM?a0="+urlLocation+"&"+str; 
      if (callback && typeof(callback)=== "function") { 
        xhr.open("POST", URL, true);        
        xhr.onload = function (e) {
          callback(JSON.parse(this.responseText));
        };
        xhr.send(null);
      }
      else {
        xhr.open("POST", URL, false);       
        xhr.send(null);     
          resp=xhr.responseText;      
      }
      
      xhr=null;
      isOK=true;
      PostRet="0";  
    }
    catch(err) {
      //resp=err.message;
      if (ports!="00000") {
        resp="-4%%"+err.message;
        PostRet="-3";       
      }
      else {
        resp="-3%%"+err.message;
        PostRet="-3";
      }
      //ports="00000";
    }

    if(isOK==true)
    {
       resp="0%%"+resp
    }
    else
    {
      resp="-1%%"+resp
      //ports="00000";
    }
    return resp;  
  }
  
  catch(err) {
  }
}
function send(str,callback)
{
  var ret="";
/*  try
  {
    ret=post(str, callback);  
  }
  
  catch(err) {
    alert( err.message);
  }*/
  try
  {
    ret=post(str,callback);
    
    var sp=ret.split("%%");
    if(sp[0]!="0")
    {
      if ((verOffset=navigator.userAgent.indexOf("Chrome"))!=-1) {
        //if (str=="a1=get&a2=AtmEcho" ) {
        if (ports!="00000" ) {          
          ret1=post(str);
          var sp=ret1.split("%%");
        }
      }
      if (sp[0]!="0")   { 
        if (ports=="00000")
           // alert("請確認網路ATM服務是否已啟動,或重啟網路ATM服務rc="+sp[0]+ " port="+ports);
          rc=sp[0];
        else {
          var rcinfo= sp[1].split("%%");
          if (rcinfo[0]== "-4") {
            alert("請確認網路ATM服務是否被其他瀏覽器使用");            
          }
        }
      }
      PostRet=sp[0];      
    }
    return sp[1]; 
  }
  
  catch(err) {
    alert( err.message);
  }
}
function VerifyCode()
{
  if(PostRet=="-2")
  {
    alert("請確認網路ATM服務是否已安裝rc="+PostRet);
    //PostRet=sp[0];
  } 
  else  if(PostRet=="-1")
  {
    alert("請確認網路ATM服務是否已安裝並且已啟動,或重啟網路ATM服務rc="+PostRet);
    //PostRet=sp[0];
  }
  else  if(PostRet!="0")
  {
    alert("請確認網路ATM服務是否已安裝並且已啟動,或重啟網路ATM服務rc="+PostRet);
    //PostRet=sp[0];
  }   
}
var ATLActiveXControl = 
{
  /********************************/
  /* mth Method : return RC       */ 
  /********************************/  
  GetAPIVersion :function(){
    var str="a1=mth&a2=GetAPIVersion";
    var result = send(str);
    return parseInt(result, 10);
  },//1
  ListReaders :function(){
    var str="a1=mth&a2=ListReaders";
    var result = send(str);
    return parseInt(result, 10);
  },//2 
  connectReader :function(){
    var str="a1=mth&a2=connectReader";
    var result = send(str);
    return parseInt(result, 10);
  },//3 
  ConnectCard :function(){
    var str="a1=mth&a2=ConnectCard";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//4
  CheckCardInsert :function(){
    var str="a1=mth&a2=CheckCardInsert";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//5 
  ReConnectCard :function(){
    var str="a1=mth&a2=ReConnectCard";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//6 
  DisConnectCard :function(){
    var str="a1=mth&a2=DisConnectCard";
    var result = send(str);
    return parseInt(result, 10);
  },//7
  DisConnectReader :function(){
    var str="a1=mth&a2=DisConnectReader";
    var result = send(str);
    return parseInt(result, 10);
  },//8   
  SelectAID :function(){
    var str="a1=mth&a2=SelectAID";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//9 
  VerifyPIN :function(){
    var str="a1=mth&a2=VerifyPIN1";
    var result = send(str);
    return parseInt(result, 10);
  },//10
  VerifyPIN2 :function(){
    var str="a1=mth&a2=VerifyPIN2";
    var result = send(str);
    return parseInt(result, 10);
  },//11  
  VerifyPIN3 :function(){
    var str="a1=mth&a2=VerifyPIN3";
    var result = send(str);
    return parseInt(result, 10);
  },//12    
  VerifyMAC :function(s1,s2,s3,s4){
    str="a1=mth&a2=VerifyMAC&a3="+encodeURIComponent(s1)+"&a4="+encodeURIComponent(s2)+"&a5="+encodeURIComponent(s3)+"&a6="+encodeURIComponent(s4);
    var result = send(str);
    return parseInt(result, 10);
  },//12      
  ChangePIN :function(){
    var str="a1=mth&a2=ChangePIN1";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//13  
  ChangePIN2 :function(){
    var str="a1=mth&a2=ChangePIN2";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//14    
  SelectEF :function(){
    var str="a1=mth&a2=SelectEF";
    var result = send(str);
    return parseInt(result, 10);
  },//15
  ReadRecord :function(){
    var str="a1=mth&a2=ReadRecord";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//16  
  WriteRecordWithSNUMTAC :function(){
    var str="a1=mth&a2=WriteRecordWithSNUMTAC1";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//17  
  WriteRecordWithSNUMTAC2 :function(){
    var str="a1=mth&a2=WriteRecordWithSNUMTAC2";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//18    
  WriteRecordWithSNUMTAC3 :function(){
    var str="a1=mth&a2=WriteRecordWithSNUMTAC3";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//19    
  WriteRecordWithSNUMTAC2S1 :function(){
    var str="a1=mth&a2=WriteRecordWithSNUMTAC2S1";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//20  
  WriteRecordWithSNUMTAC2S2 :function(){
    var str="a1=mth&a2=WriteRecordWithSNUMTAC2S2";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//21    
  FiscListAccounts :function(){
    var str="a1=mth&a2=FiscListAccounts1";
    var result = send(str);
    return parseInt(result, 10);
  },//22
  FiscListAccounts2 :function(){
    var str="a1=mth&a2=FiscListAccounts2";
    var result = send(str);
    return parseInt(result, 10);
  },//23  
  EncData :function(){
    var str="a1=mth&a2=EncData";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//24  
  DecData :function(){
    var str="a1=mth&a2=DecData";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//25    
  VerifyPINEx :function(s1, s2,s3){
    var str=""
    if (s3 !=""){
      var xStr1= XOR_hex(s1, s3);
       //alert("xStr1="+xStr1+" PIN="+s1+" Session="+s3); 
      str="a1=mth&a2=VerifyPINEx&a3="+encodeURIComponent(xStr1)+"&a4="+encodeURIComponent(s2)+"&a5="+encodeURIComponent(s3);  
    }     
    else    
       str="a1=mth&a2=VerifyPINEx&a3="+encodeURIComponent(s1)+"&a4="+encodeURIComponent(s2)+"&a5="+encodeURIComponent(s3);
    var result = send(str);
    return parseInt(result, 10);
  },//26
  WriteRecordWithSNUMTACProc :function(s1, s2, s3,s4){//20170103
    var str=""
    if (s3 !=""){
      var xStr1= XOR_hex(s1, s3);   
      str="a1=mth&a2=WriteRecordWithSNUMTACProc&a3="+encodeURIComponent(xStr1)+"&a4="+encodeURIComponent(s2)+"&a5="+encodeURIComponent(s3)+"&a6="+encodeURIComponent(s4); 
    }
    else    
       str="a1=mth&a2=WriteRecordWithSNUMTACProc&a3="+encodeURIComponent(s1)+"&a4="+encodeURIComponent(s2)+"&a5="+encodeURIComponent(s3)+"&a6="+encodeURIComponent(s4);
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//27  
  WriteRecordWithSNUMTACSndProc :function(s1, s2){//20170103
    var str="a1=mth&a2=WriteRecordWithSNUMTACSndProc&a3="+encodeURIComponent(s1)+"&a4="+encodeURIComponent(s2);
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//27    
  DESEncipher :function(s1, s2, s3,s4){//20170103
    var str="a1=mth&a2=DESEncipher&a3="+encodeURIComponent(parseInt(s1,10))+"&a4="+encodeURIComponent(parseInt(s2,10))+"&a5="+encodeURIComponent(s3)+"&a6="+encodeURIComponent(s4);
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//28  
  DESDecipher :function(s1, s2, s3,s4){//20170103
    var str="a1=mth&a2=DESDecipher&a3="+encodeURIComponent(parseInt(s1,10))+"&a4="+encodeURIComponent(parseInt(s2,10))+"&a5="+encodeURIComponent(s3)+"&a6="+encodeURIComponent(s4);
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//29  
  GetHexData :function(){//20170103
    var str="a1=get&a2=GetHexData";
    return send(str);
  },//30  
  /********************************/
  /* Set Property, return string  */ 
  /********************************/  
  set baReaderName (s1){
    var str="a1=set&a2=baReaderName&a3="+encodeURIComponent(s1);
    return send(str);
  },//1 
  set iDisConnectType (s1){
    var str="a1=set&a2=iDisConnectType&a3="+encodeURIComponent(parseInt(s1,10));
    return send(str);
  },//2 
  set baINBuf (s1){
    var str="a1=set&a2=baINBuf&a3="+encodeURIComponent(s1);
    return send(str);
  },//3   
  get baBuf () {
    var str="a1=get&a2=baBuf1";
    return send(str);
  },//4
  //get
  set baBuf (s1) {
      var str="a1=set&a2=baBuf1&a3="+encodeURIComponent(s1);
      return send(str);
  },//5
  get baBuf2 () {
    var str="a1=get&a2=baBuf2";
    return send(str);
  },//6
  //get
  set baBuf2 (s1) {
      var str="a1=set&a2=baBuf2&a3="+encodeURIComponent(s1);
      return send(str);
  },//7
  get vc () {
    var str="a1=get&a2=getVC";
    return send(str);
  },//8
  //get
  set vc (s1) {
      var str="a1=set&a2=setVC&a3="+encodeURIComponent(s1);
      return send(str);
  },//9
  set baIData (s1){
    var str="a1=set&a2=baIData&a3="+encodeURIComponent(s1);
    return send(str);
  },//10  
  set baEFID (s1){
    var str="a1=set&a2=baEFID&a3="+encodeURIComponent(s1);
    return send(str);
  },//11    
  set sid (s1){
    var str="a1=set&a2=sid&a3="+encodeURIComponent(s1);
    return send(str);
  },//12  
  set baP1P2 (s1){
    var str="a1=set&a2=baP1P2&a3="+encodeURIComponent(s1);
    return send(str);
  },//13  
  /////////////////////////////////////////////////////////// 
  set bPq (s1){
    var str="a1=set&a2=bPq&a3="+encodeURIComponent(parseInt(s1,10));
    return send(str);
  },//14  

  set bLc (s1){
    var str="a1=set&a2=bLc&a3="+encodeURIComponent(parseInt(s1,10));
    return send(str);
  },//15    
  set bRecID (s1){
    var str="a1=set&a2=bRecID&a3="+encodeURIComponent(parseInt(s1,10));
    return send(str);
  },//16  
  set bLen (s1){
    var str="a1=set&a2=bLen&a3="+encodeURIComponent(parseInt(s1,10));
    return send(str);
  },//17  
  set usUnknownPCode (s1){
    var str="a1=set&a2=usUnknownPCode&a3="+encodeURIComponent(parseInt(s1,10));
    return send(str);
  },//18  
  set ulRandNum (s1){
    var str="a1=set&a2=ulRandNum&a3="+encodeURIComponent(parseInt(s1,10));
    return send(str);
  },//19  
  
  /********************************/
  /* Get Property, return string  */ 
  /********************************/  
  GetAtmEcho :function(){
    var str="a1=get&a2=AtmEcho";
    return send(str);
  },//1
  get cVer () {
    var str="a1=get&a2=cVer";
    var result= send(str);
    return result;
  },//2   
  /*get *baReaderName () {
    var str="a1=get&a2=baReaderName";
    var result= send(str);
    return result;
  //},//3*/ 
  get baATR () {
    var str="a1=get&a2=baATR";
    var result= send(str);
    return result;
  },//4 
  get caReader1 () {
    var str="a1=get&a2=caReader1";
    var result= send(str);
    return result;
  },//5 
  get caReader2 () {
    var str="a1=get&a2=caReader2";
    var result= send(str);
    return result;
  },//6
  get caReader3 () {
    var str="a1=get&a2=caReader3";
    var result= send(str);
    return result;
  },//7
  get caReader4 () {
    var str="a1=get&a2=caReader4";
    var result= send(str);
    return result;
  },//8
  get caReader5 () {
    var str="a1=get&a2=caReader5";
    var result= send(str);
    return result;
  },//9 
  get baOutBuf () {
    var str="a1=get&a2=baOutBuf";
    var result= send(str);
    return result;
  },//10  
  get cErrorType () {
    var str="a1=get&a2=cErrorType";
    var result= send(str);
    return result;
  },//11
  /*account :function() {
    var str="a1=get&a2=account";
    var result= send(str);
    return result;
  },//12*/    
  get account0 () {
    var str="a1=get&a2=account0";
    var result= send(str);
    return result;
  },//13  
  get account1 () {
    var str="a1=get&a2=account1";
    var result= send(str);
    return result;
  },//14  
  get account2 () {
    var str="a1=get&a2=account2";
    var result= send(str);
    return result;
  },//15  
  get account3 () {
    var str="a1=get&a2=account3";
    var result= send(str);
    return result;
  },//16  
  get account4() {
    var str="a1=get&a2=account4";
    var result= send(str);
    return result;
  },//17  
  get account5 () {
    var str="a1=get&a2=account5";
    var result= send(str);
    return result;
  },//18  
  get account6 () {
    var str="a1=get&a2=account6";
    var result= send(str);
    return result;
  },//19  
  get account7 () {
    var str="a1=get&a2=account7";
    var result= send(str);
    return result;
  },//20    
  get iNoR (){
    var str="a1=get&a2=iNoR";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//21  
  get cardType (){
    var str="a1=get&a2=cardType";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//22  
  get uiSW12() {
    var str="a1=get&a2=uiSW12";
    var rc=send(str); 
    return  parseInt(rc, 10);
  },//23  
  get bRLen() {
    var str="a1=get&a2=bRLen";
    var rc=send(str); 
    return  parseInt(rc, 10);
  }//24   
}     
window.onbeforeunload = function(e) {
    if (ports != "00000" ) {
    var i =   ATLActiveXControl.DisConnectCard();
      i =   ATLActiveXControl.DisConnectReader();   
    }
};