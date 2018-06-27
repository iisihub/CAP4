/**
 * HsmService_PortType.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public interface HsmService_PortType extends java.rmi.Remote {
    public tw.com.citi.ws.hsm.DoDESResponse doDES(tw.com.citi.ws.hsm.DoDESRequest doDESRequest) throws java.rmi.RemoteException;

    public tw.com.citi.ws.hsm.DoUnDESResponse doUnDES(tw.com.citi.ws.hsm.DoUnDESRequest doUnDESRequest) throws java.rmi.RemoteException;

    public tw.com.citi.ws.hsm.ImportKeyResponse importKey(tw.com.citi.ws.hsm.ImportKeyRequest importKeyRequest) throws java.rmi.RemoteException;
}
