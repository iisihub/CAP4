import javax.json.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;

public class MutualAuthenticationServlet extends HttpServlet {
  static int count = 2;
  // Tomcat server.xml settings:
  // <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
  //            maxThreads="150" SSLEnabled="true"
  //            scheme="https" secure="true" clientAuth="true" sslProtocol="TLS"
  //            keystoreFile="path/to/keyStoreFile" keystorePass="password"
  //            truststoreFile="path/to/trustStoreFile" />
  // clientAuth="true" 表示開啟雙向 SSL

  public void doGet(HttpServletRequest request, HttpServletResponse response) {
//    if (count % 2 == 0) {
//      try {
//        System.out.println("====================sleep....====================");
//        Thread.sleep(70000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      System.out.println("====================walkup====================");
//    }
//    ++count;

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    JsonObjectBuilder responseJOB = Json.createObjectBuilder();

    X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
    // Tomcat Connector 設定的 clientAuth="false" 的話，certs 會是 null
    if (certs != null) {
      JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
      for (int i = 0; i < certs.length; i++) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("isVerify", verifyCertificate(certs[i]));
        jsonBuilder.add("detail", certs[i].toString());

      }

      JsonObjectBuilder custIndicatorObj = Json.createObjectBuilder();
//      custIndicatorObj.add("customerIndicator", "NEW_TO_CARD");
       custIndicatorObj.add("customerIndicator", "NEW_TO_BANK");
    //  custIndicatorObj.add("customerIndicator", "ADD_ON_BASIC");
      custIndicatorObj.add("controlFlowId", "57446a455379616c744961477972793757324941795a4c46686539433637374d55493134374a69617768343d");
      responseJOB.add("CustomerSearchResponse",custIndicatorObj);
      responseJOB.add("clientCertificates", jArrayBuilder);

      //nccc
      responseJOB.add("cardInterchangeMessageCode", "IDENTITY_CHECK_POSITIVE");//成功
//    nccc.add("cardInterchangeMessageCode", "IDENTITY_CHECK_NEGATIVE");//失敗
//    nccc.add("cardInterchangeMessageCode", "SYSTEM_ERROR");//系統錯誤（若收到這個值，COLA需要立即再重call一次）

//      // PRE
      responseJOB.add("applicationId", "yoho123456789");
      responseJOB.add("controlFlowId", "controlYo123456");
      responseJOB.add("applicationStage", "applicationStage1234");
//
//
//      // IPA
      JsonArrayBuilder ipa = Json.createArrayBuilder();
      JsonObjectBuilder requestedProductDecision = Json.createObjectBuilder();
      requestedProductDecision.add("creditDecision", "000");
      ipa.add(requestedProductDecision);
      responseJOB.add("requestedProductDecision", ipa);
      responseJOB.add("applicationStage", "APPROVAL");
//
//      // test IPA 400 wait
//      responseJOB.add("code", "awaitingBackgroundScreeningResult");



      //for DUL
      // MFA
//      responseJOB.add("controlFlowId", "DUL_controlFlow_1234566");
//
//      //status Inq !!! 會與IPA衝突
//      JsonArrayBuilder inqProductDecisionArray = Json.createArrayBuilder();
//      JsonObjectBuilder inqProductDecision = Json.createObjectBuilder();
//      JsonArrayBuilder inqDocumentsArray = Json.createArrayBuilder();
//
//      JsonObjectBuilder inqDocuments = Json.createObjectBuilder();
//      inqDocuments.add("documentIdType", "NATIONAL_ID_FRONT");
//      inqDocuments.add("documentStatus", "PENDING");
//      inqDocuments.add("proofType", "INCOME_PROOF");
//      inqDocumentsArray.add(inqDocuments);
//
//
//      JsonObjectBuilder inqDocuments2 = Json.createObjectBuilder();
//      inqDocuments2.add("documentIdType", "SECONDARY_ID");
//      inqDocuments2.add("documentStatus", "PENDING");
//      inqDocuments2.add("proofType", "INCOME_PROOF");
//      inqDocumentsArray.add(inqDocuments2);
//
//      JsonObjectBuilder inqDocuments3 = Json.createObjectBuilder();
//      inqDocuments3.add("documentIdType", "NATIONAL_ID_BACK");
//      inqDocuments3.add("documentStatus", "PENDING");
//      inqDocuments3.add("proofType", "IDENTITY");
//      inqDocumentsArray.add(inqDocuments3);
//
//      JsonObjectBuilder inqDocuments4 = Json.createObjectBuilder();
//      inqDocuments4.add("documentIdType", "INCOME_DOCUMENT");
//      inqDocuments4.add("documentStatus", "PENDING");
//      inqDocuments4.add("proofType", "INCOME");
//      inqDocumentsArray.add(inqDocuments4);
//
//      JsonObjectBuilder inqDocuments5 = Json.createObjectBuilder();
//      inqDocuments5.add("documentIdType", "APPLICATION_FORM");
//      inqDocuments5.add("documentStatus", "SUBMITTED");
//      inqDocuments5.add("proofType", "INCOME");
//      inqDocumentsArray.add(inqDocuments5);
//
//      inqProductDecision.add("requiredDocuments", inqDocumentsArray);
//      inqProductDecisionArray.add(inqProductDecision);
//
//
//      JsonObjectBuilder responseJson = Json.createObjectBuilder();
//      responseJson.add("requestedProductDecision", inqProductDecisionArray);
//      responseJOB.add("Response", responseJson);

    } else {
      if ("https".equalsIgnoreCase(request.getScheme())) {
        responseJOB.add("errorMessage", "HTTPS Request: client certificate not found");
      } else {
        responseJOB.add("errorMessage", "HTTP: can not get client certificate");
      }
    }

    response.addHeader("Content-Type", "application/json");
    response.addHeader("Citiuuid", "111111111111");
    response.addHeader("uuid", "22222222222222");
    response.addHeader("responseTimestamp", Calendar.getInstance().getTime().toString());
//    response.setStatus(500);

//    try {
//      Thread.sleep(10000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }

    JsonObject responseJson = responseJOB.build();
    try (
      PrintWriter out = response.getWriter()
    ) {
      out.println(responseJson);
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public  void doPut(HttpServletRequest request, HttpServletResponse response){
    doGet(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    doGet(request, response);
  }

  // Check Client Certificate
  private boolean verifyCertificate(X509Certificate certificate) {
    boolean valid = false;
    try {
      certificate.checkValidity();
      valid = true;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return valid;
  }
}
