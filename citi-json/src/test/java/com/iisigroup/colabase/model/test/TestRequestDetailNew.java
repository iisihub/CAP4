package com.iisigroup.colabase.model.test;

import com.iisigroup.colabase.annotation.ApiRequest;
import com.iisigroup.colabase.annotation.JsonTemp;

public class TestRequestDetailNew extends TestRequestServiceModel{


  /**
   * for DocumentTranslog
   */
  private String urn;

  private String ppNumber;

  private String fileName;

  private String documentType;


  //test
  @ApiRequest(path = "fullName", noValueNoSend = true)
  private String parentName;
  @ApiRequest(noValueNoSend = true)
  private String documentIdType;

  @ApiRequest(path = "documentDetails.documentFormat", noValueNoSend = true)
  private String documentFormat;

  @ApiRequest(defaultValue = "haha")
  private String controlFlowId;

  @ApiRequest()
  private String number;

  @ApiRequest(path = "phone[].area[].code", defaultValue = "code0123")
  private String myCode;
  @ApiRequest(path = "text")
  private String myText;
  @ApiRequest(primaryEmptyClean = true)
  private String nation;
  @ApiRequest(path = "phone[].dfield[].code", noValueNoSend = true)
  private String otherCode;

  @JsonTemp(location = "test.json")
  private String jsonTempStr;

  public TestRequestDetailNew() {

  }

  public String getUrn() {
    return urn;
  }

  public void setUrn(String urn) {
    this.urn = urn;
  }

  public String getPpNumber() {
    return ppNumber;
  }

  public void setPpNumber(String ppNumber) {
    this.ppNumber = ppNumber;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getDocumentType() {
    return documentType;
  }

  public void setDocumentType(String documentType) {
    this.documentType = documentType;
  }

  public String getDocumentIdType() {
    return documentIdType;
  }

  public void setDocumentIdType(String documentIdType) {
    this.documentIdType = documentIdType;
  }

  public String getDocumentFormat() {
    return documentFormat;
  }

  public void setDocumentFormat(String documentFormat) {
    this.documentFormat = documentFormat;
  }

  public String getControlFlowId() {
    return controlFlowId;
  }

  public void setControlFlowId(String controlFlowId) {
    this.controlFlowId = controlFlowId;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }


  public String getMyCode() {
    return myCode;
  }

  public void setMyCode(String myCode) {
    this.myCode = myCode;
  }


  public String getMyText() {
    return myText;
  }

  public void setMyText(String myText) {
    this.myText = myText;
  }

  public String getNation() {
    return nation;
  }

  public void setNation(String nation) {
    this.nation = nation;
  }

  public String getOtherCode() {
    return otherCode;
  }

  public void setOtherCode(String otherCode) {
    this.otherCode = otherCode;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  //  private String applicationFormRemove(String jsonStr){
//    JSONObject jsonObject = JSONObject.fromObject(jsonStr);
//    JSONObject documentDetailsObj = (JSONObject) jsonObject.get(documentDetails);
//    List<String> checkKeys = getNoValueNoSendList();
//    for(String key : checkKeys){
//      String value = documentDetailsObj.getString(key);
//      if("".equals(value)){
//        documentDetailsObj.remove(key);
//      }
//    }
//    return jsonObject.toString();
//  }

  public enum UploadDocName {
      NATIONAL_ID_FRONT(Document.NATIONAL_ID_FRONT.getDocumentId(), "身分證正面"),
      NATIONAL_ID_BACK(Document.NATIONAL_ID_BACK.getDocumentId(), "身分證反面"),
      SECONDARY_ID(Document.SECONDARY_ID.getDocumentId(), "第二證件正面"),
      INCOME_DOCUMENT(Document.INCOME_DOCUMENT.getDocumentId(), "財力證明文件");

      private String documentId;
      private String documentName;

      UploadDocName(String documentId, String documentName) {
        this.documentId = documentId;
        this.documentName = documentName;
      }
      public String getDocumentId(){
        return documentId;
      }
      public String getDocumenName(){
        return documentName;
      }

    }


  public enum Document {
    PREFILLED_APPLICATION_FORM("APPLICATION_FORM", "", "AppForm"), //TODO PDF proofType要放什麼
    NATIONAL_ID_FRONT("NATIONAL_ID_FRONT", "IDENTITY", "ID-F"),
    NATIONAL_ID_BACK("NATIONAL_ID_BACK", "IDENTITY", "ID-B"),
    SECONDARY_ID("SECONDARY_ID", "IDENTITY", "Second ID"),
    INCOME_DOCUMENT("INCOME_DOCUMENT", "INCOME", "Income");
    private String documentId;
    private String proofType;
    private String documentumTransLogType;

    Document(String documentId, String proofType, String documentumTransLogType) {
      this.documentId = documentId;
      this.proofType = proofType;
      this.documentumTransLogType = documentumTransLogType;
    }
    public String getDocumentId(){
      return documentId;
    }
    public String getDproofType(){
      return proofType;
    }
    public String getDocumentumTransLogType(){
      return documentumTransLogType;
    }

  }

  private enum UploadFileFormat{
    //2017,12,04 documentFormat要放大寫PDF、JPG、PNG，但tiff可以小寫
    PDF("PDF"),
    JPG("JPEG"),
//    TIF("TIF"),
    TIFF("TIFF");
//    PNG("PNG"),
//    JPEG("JPEG");

    private String sendFormat;
    UploadFileFormat(String sendFormat){
      this.sendFormat = sendFormat;
    }
    public String getSendFormat(){
      return this.sendFormat;
    }

    public boolean isSameType(String checkFormat){
      if(checkFormat == null)
        return false;
      if(this.toString().equals(checkFormat.toUpperCase())){
        return true;
      } else {
        return false;
      }
    }
  }

}
