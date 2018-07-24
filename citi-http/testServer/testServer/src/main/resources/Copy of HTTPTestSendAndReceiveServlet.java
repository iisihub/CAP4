import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Enumeration;
import java.io.BufferedInputStream;
import javax.json.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CopyOfHTTPTestSendAndReceiveServlet extends HttpServlet {
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
      JsonObjectBuilder respObj = Json.createObjectBuilder();

      //response all data from client
//      StringBuilder responseBodySB = new StringBuilder();
//      try (
//        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))
//      ) {
//        String tempStr;
//        while ((tempStr = reader.readLine()) != null) {
//          responseBodySB.append(tempStr);
//        }
//
//        System.out.println("result: " + responseBodySB);
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
      
    //response all data from client
      String responseBodySB = "";
      try (
              BufferedInputStream in = new BufferedInputStream(request.getInputStream())
      ) {
          byte[] contents = new byte[1024];

          int bytesRead = 0;
          while((bytesRead = in.read(contents)) != -1) { 
              responseBodySB += new String(contents, 0, bytesRead);              
          }

        System.out.println("result: " + responseBodySB);
      } catch (IOException e) {
         e.printStackTrace();
      }

      responseJOB.add("YouSendMe", responseBodySB);

    } else {
        //response all data from client
//        StringBuilder responseBodySB = new StringBuilder();
//        try (
//          BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))
//        ) {
//          String tempStr;
//          while ((tempStr = reader.readLine()) != null) {
//            responseBodySB.append(tempStr);
//          }
//
//          System.out.println("result: " + responseBodySB);
//        } catch (IOException e) {
//           e.printStackTrace();
//        }
//        
//        responseJOB.add("YouSendMe", responseBodySB.toString());
        
        
        //response all data from client
        String responseBodySB = "";
        try (
                BufferedInputStream in = new BufferedInputStream(request.getInputStream())
        ) {
            byte[] contents = new byte[1024];

            int bytesRead = 0;
            while((bytesRead = in.read(contents)) != -1) { 
                responseBodySB += new String(contents, 0, bytesRead);              
            }

          System.out.println("result: " + responseBodySB);
        } catch (IOException e) {
           e.printStackTrace();
        }

        responseJOB.add("YouSendMe", responseBodySB);
        
        
//        String contentType = request.getHeader("Content-Type");
//        
//        if (contentType.indexOf("json") != -1) {
//            //TODO
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~JSON:");
//        } else if (contentType.indexOf("urlencoded") != -1) {
//            String name = request.getParameter("name");
//            String birthday = request.getParameter("birthday");
//            String mobile = request.getParameter("mobile");
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~URLENCODED:" + name + "," + birthday + "," + mobile);
//        }

//      if ("https".equalsIgnoreCase(request.getScheme())) {
//        responseJOB.add("errorMessage", "HTTPS Request: client certificate not found");
//      } else {
//        responseJOB.add("errorMessage", "HTTP: can not get client certificate");
//      }
    }

    response.addHeader("Content-Type", "application/json");
    response.addHeader("uuid", "123456789012");
    response.addHeader("responseTimestamp", Calendar.getInstance().getTime().toString());

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
