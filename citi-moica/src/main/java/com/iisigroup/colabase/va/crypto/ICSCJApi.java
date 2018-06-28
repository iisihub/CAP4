package com.iisigroup.colabase.va.crypto;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.iisigroup.colabase.base.va.crypto.IPHostNameVerifier;

public class ICSCJApi {

    static int CERR_ICSResponse_NotFound = 2001;
    static int CERR_ClientID_NotFound = 2002;
    static int CERR_ClientID_Incorrect = 2003;
    static int CERR_Nonce_NotFound = 2004;
    static int CERR_Nonce_Incorrect = 2005;
    static int CERR_ReqTime_NotFound = 2006;
    static int CERR_TotalCount_NotFound = 2007;
    static int CERR_TotalCount_BadFormat = 2008;
    static int CERR_TotalCount_BadValue = 2009;
    static int CERR_Rec_Fail = 2011;
    static int CERR_ID_Fail = 2013;
    static int CERR_SN_Fail = 2014;
    static int CERR_SSID4_Fail = 2015;
    static int CERR_Code_Fail = 2016;
    static int CERR_Code_Incorrect = 2017;
    int iCount = 0;
    String[][] IDSN = (String[][]) null;
    String ClientID = null;
    String Nonce = null;
    int iServerReturn = 0;
    int[] iQueryResult;
    int iDBFailureCount = 0;
    String ToBeSignedData = null;
    String ICSRequestPKT = null;
    String ICSResponsePKT = null;

    private String byteArray2hexString(byte[] paramArrayOfByte) {
        String[] arrayOfString = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
        if ((paramArrayOfByte == null) || (paramArrayOfByte.length <= 0)) {
            return null;
        }
        StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);

        int j = 0;
        while (j < paramArrayOfByte.length) {
            int i = (byte) (paramArrayOfByte[j] & 0xF0);
            i = (byte) (i >>> 4);
            i = (byte) (i & 0xF);
            localStringBuffer.append(arrayOfString[i]);

            i = (byte) (paramArrayOfByte[j] & 0xF);
            localStringBuffer.append(arrayOfString[i]);
            j++;
        }
        String str = new String(localStringBuffer);
        return str;
    }

    private int iSSL_SendThenRecv(String paramString1, int paramInt, String paramString2, Proxy proxy) {
        int i = 0;
        try {
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } };

            String str = "https://" + paramString1 + ":" + Integer.toString(paramInt) + paramString2;
            URL localURL = new URL(str);
            HttpsURLConnection localHttpsURLConnection;
            if (proxy == null) {
                localHttpsURLConnection = (HttpsURLConnection) localURL.openConnection();
            } else {
                localHttpsURLConnection = (HttpsURLConnection) localURL.openConnection(proxy);
            }
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            localHttpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
            localHttpsURLConnection.setHostnameVerifier(new IPHostNameVerifier());
            localHttpsURLConnection.setDoOutput(true);

            OutputStream localOutputStream = localHttpsURLConnection.getOutputStream();
            localOutputStream.write(this.ICSRequestPKT.getBytes());
            localOutputStream.close();

            InputStream localInputStream = localHttpsURLConnection.getInputStream();
            int j = 0;
            byte[] arrayOfByte = new byte[2048];
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            while ((j = localInputStream.read(arrayOfByte)) != -1) {
                localByteArrayOutputStream.write(arrayOfByte, 0, j);
            }
            localInputStream.close();

            this.ICSResponsePKT = new String(localByteArrayOutputStream.toByteArray());
            if (this.ICSResponsePKT == null) {
                i = -3;
            } else if (this.ICSResponsePKT.indexOf("<ICSResponse>") != -1) {
                this.ICSResponsePKT = this.ICSResponsePKT.substring(this.ICSResponsePKT.indexOf("<ICSResponse>"), this.ICSResponsePKT.length());
                i = 0;
            } else if (this.ICSResponsePKT.indexOf("<ErrID>") != -1) {
                this.ICSResponsePKT = this.ICSResponsePKT.substring(this.ICSResponsePKT.indexOf("<ErrID>"), this.ICSResponsePKT.length());
                i = -1;
            } else if (this.ICSResponsePKT.length() <= 0) {
                i = -3;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
            return -4;
        }
        return i;
    }

    private int iGetTagContent(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString) {
        int i = 0;
        int j = 0;
        String str = null;

        i = paramInt;
        if (paramInt >= paramString1.length()) {
            return -1;
        }
        i = paramString1.indexOf(paramString2, i);
        if (i == -1) {
            return -1;
        }
        j = paramString1.indexOf(paramString3, i + 1);
        if (j == -1) {
            return -2;
        }
        str = paramString1.substring(i + paramString2.length(), j);
        if ((str == null) || (str == "")) {
            return -3;
        }
        paramArrayOfString[0] = str;

        return 0;
    }

    public int iMake_ToBeSignedData(String paramString) {
        String str1 = new String();
        String str2 = new String();
        String str3 = new String();
        String str4 = new String();
        byte[] arrayOfByte1 = null;
        byte[] arrayOfByte2 = null;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        FileInputStream localFileInputStream = null;
        try {
            localFileInputStream = new FileInputStream(paramString);

            m = localFileInputStream.available();
            arrayOfByte2 = new byte[m];
            if (localFileInputStream.read(arrayOfByte2) != m) {
                return -1;
            }
        } catch (FileNotFoundException localFileNotFoundException) {
            return -1;
        } catch (IOException localIOException1) {
            return -1;
        } finally {
            if (localFileInputStream != null) {
                try {
                    localFileInputStream.close();
                } catch (IOException localIOException2) {
                    return -1;
                }
            }
        }
        try {
            MessageDigest localMessageDigest1 = MessageDigest.getInstance("SHA-1");
            localMessageDigest1.update(arrayOfByte2);
            MessageDigest localMessageDigest2 = (MessageDigest) localMessageDigest1.clone();
            arrayOfByte1 = localMessageDigest2.digest();
        } catch (CloneNotSupportedException localCloneNotSupportedException) {
            return -2;
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            return -2;
        }
        str2 = byteArray2hexString(arrayOfByte1);
        this.ClientID = str2;

        Calendar localCalendar = Calendar.getInstance();
        i1 = localCalendar.get(1);
        str4 = Integer.toString(i1);
        i2 = localCalendar.get(2) + 1;
        if (i2 < 10) {
            str4 = str4 + "0";
        }
        str4 = str4 + Integer.toString(i2);
        i3 = localCalendar.get(5);
        if (i3 < 10) {
            str4 = str4 + "0";
        }
        str4 = str4 + Integer.toString(i3);
        i4 = localCalendar.get(10);
        if (localCalendar.get(9) == 1) {
            i4 += 12;
        }
        if (i4 < 10) {
            str4 = str4 + "0";
        }
        str4 = str4 + Integer.toString(i4);
        i5 = localCalendar.get(12);
        if (i5 < 10) {
            str4 = str4 + "0";
        }
        str4 = str4 + Integer.toString(i5);
        i6 = localCalendar.get(13);
        if (i6 < 10) {
            str4 = str4 + "0";
        }
        str4 = str4 + Integer.toString(i6);

        localCalendar = Calendar.getInstance();
        n = localCalendar.get(13);
        Random localRandom = new Random(n * 1000L);
        for (j = 0; j <= 14; j++) {
            k = Math.abs(localRandom.nextInt()) % 62;
            if (k <= 9) {
                k += 48;
            } else if (k <= 35) {
                k = 55 + k;
            } else {
                k = 61 + k;
            }
            str3 = str3 + (char) k;
        }
        this.Nonce = str3;

        str1 = "<ICSReq><ClientID>" + str2 + "</ClientID>" + "<Nonce>" + str3 + "</Nonce>" + "<ReqTime>" + str4 + "</ReqTime>" + "<TotalCount>" + Integer.toString(this.iCount) + "</TotalCount>";
        for (j = 1; j <= this.iCount; j++) {
            str1 = str1 + "<Rec><ID>" + Integer.toString(j) + "</ID><SN>" + this.IDSN[(j - 1)][0] + "</SN><SSID>" + this.IDSN[(j - 1)][1] + "</SSID></Rec>";
        }
        str1 = str1 + "</ICSReq>";
        this.ToBeSignedData = str1;

        return 0;
    }

    public int iQuery(String paramString1, int paramInt, String paramString2, byte[] paramArrayOfByte, Proxy proxy) {
        String str = new String();
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;

        String[] arrayOfString = new String[1];

        this.iQueryResult = new int[this.iCount];

        str = byteArray2hexString(paramArrayOfByte);

        this.ICSRequestPKT = "ICSRequestXMLData=";
        this.ICSRequestPKT = (this.ICSRequestPKT + "<ICSRequest>" + this.ToBeSignedData + "<ICSReqLength>" + Integer.toString(this.ToBeSignedData.length()) + "</ICSReqLength>" + "<Signature>" + str
                + "</Signature>" + "</ICSRequest>");
        switch (iSSL_SendThenRecv(paramString1, paramInt, paramString2, proxy)) {
        case -4:
            return -4;
        case -3:
            return -3;
        case -1:
            j = this.ICSResponsePKT.indexOf("<ErrID>");
            k = this.ICSResponsePKT.indexOf("</ErrID>");
            try {
                this.iServerReturn = Integer.parseInt(this.ICSResponsePKT.substring(j + 7, k));
            } catch (NumberFormatException localNumberFormatException1) {
                return -2;
            }
            return -1;
        case 0:
            j = 0;
            j = this.ICSResponsePKT.indexOf("<ICSResponse>", j);
            if (j == -1) {
                return CERR_ICSResponse_NotFound;
            }
            j += 13;

            j = this.ICSResponsePKT.indexOf("<ClientID>", j);
            if (j == -1) {
                return CERR_ClientID_NotFound;
            }
            k = this.ICSResponsePKT.indexOf("</ClientID>", j + 1);
            if (k == -1) {
                return CERR_ClientID_NotFound;
            }
            if (this.ICSResponsePKT.substring(j + 10, k).compareTo(this.ClientID) != 0) {
                return CERR_ClientID_Incorrect;
            }
            j = k + 11;

            j = this.ICSResponsePKT.indexOf("<Nonce>", j);
            if (j == -1) {
                return CERR_Nonce_NotFound;
            }
            k = this.ICSResponsePKT.indexOf("</Nonce>", j + 1);
            if (k == -1) {
                return CERR_Nonce_NotFound;
            }
            if (this.ICSResponsePKT.substring(j + 7, k).compareTo(this.Nonce) != 0) {
                return CERR_Nonce_Incorrect;
            }
            j = k + 8;

            j = this.ICSResponsePKT.indexOf("<ReqTime>", j);
            if (j == -1) {
                return CERR_ReqTime_NotFound;
            }
            k = this.ICSResponsePKT.indexOf("</ReqTime>", j + 1);
            if (k == -1) {
                return CERR_ReqTime_NotFound;
            }
            j = k + 10;

            j = this.ICSResponsePKT.indexOf("<TotalCount>", j);
            if (j == -1) {
                return CERR_TotalCount_NotFound;
            }
            k = this.ICSResponsePKT.indexOf("</TotalCount>", j + 1);
            if (k == -1) {
                return CERR_TotalCount_NotFound;
            }
            try {
                m = Integer.parseInt(this.ICSResponsePKT.substring(j + 12, k));
            } catch (NumberFormatException localNumberFormatException2) {
                return CERR_TotalCount_BadFormat;
            }
            if ((m <= 0) || (m > 1000) || (m != this.iCount)) {
                return CERR_TotalCount_BadValue;
            }
            j = k + 13;
            for (n = 0; n < this.iCount; n++) {
                if (iGetTagContent(this.ICSResponsePKT, "<Rec>", "</Rec>", j, arrayOfString) < 0) {
                    return CERR_Rec_Fail;
                }
                j += "<Rec>".length();
                if (iGetTagContent(this.ICSResponsePKT, "<ID>", "</ID>", j, arrayOfString) < 0) {
                    return CERR_ID_Fail;
                }
                j += "<ID>".length() + arrayOfString[0].length() + "</ID>".length();
                if (iGetTagContent(this.ICSResponsePKT, "<SN>", "</SN>", j, arrayOfString) < 0) {
                    return CERR_SN_Fail;
                }
                j += "<SN>".length() + arrayOfString[0].length() + "</SN>".length();
                if (iGetTagContent(this.ICSResponsePKT, "<SSID4>", "</SSID4>", j, arrayOfString) < 0) {
                    return CERR_SSID4_Fail;
                }
                j += "<SSID4>".length() + arrayOfString[0].length() + "</SSID4>".length();
                if (iGetTagContent(this.ICSResponsePKT, "<Code>", "</Code>", j, arrayOfString) < 0) {
                    return CERR_Code_Fail;
                }
                try {
                    this.iQueryResult[n] = Integer.parseInt(arrayOfString[0]);
                } catch (NumberFormatException localNumberFormatException3) {
                    return CERR_Code_Incorrect;
                }
                j += "<Code>".length() + arrayOfString[0].length() + "</Code></Rec>".length();
                if (this.iQueryResult[n] == 999) {
                    this.iDBFailureCount += 1;
                }
            }
        }
        return 0;
    }

}
