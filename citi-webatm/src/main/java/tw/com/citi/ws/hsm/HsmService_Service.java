/**
 * HsmService_Service.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public interface HsmService_Service extends javax.xml.rpc.Service {
    public java.lang.String getHsmServiceSOAPAddress();

    public tw.com.citi.ws.hsm.HsmService_PortType getHsmServiceSOAP() throws javax.xml.rpc.ServiceException;

    public tw.com.citi.ws.hsm.HsmService_PortType getHsmServiceSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
