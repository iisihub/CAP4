/**
 * HsmService_ServiceLocator.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public class HsmService_ServiceLocator extends org.apache.axis.client.Service implements tw.com.citi.ws.hsm.HsmService_Service {

    public HsmService_ServiceLocator() {
    }

    public HsmService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public HsmService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for HsmServiceSOAP
    private java.lang.String HsmServiceSOAP_address = "http://127.0.0.1:8880/axis/services/HsmServiceSOAP";

    public java.lang.String getHsmServiceSOAPAddress() {
        return HsmServiceSOAP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String HsmServiceSOAPWSDDServiceName = "HsmServiceSOAP";

    public java.lang.String getHsmServiceSOAPWSDDServiceName() {
        return HsmServiceSOAPWSDDServiceName;
    }

    public void setHsmServiceSOAPWSDDServiceName(java.lang.String name) {
        HsmServiceSOAPWSDDServiceName = name;
    }

    public tw.com.citi.ws.hsm.HsmService_PortType getHsmServiceSOAP() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(HsmServiceSOAP_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getHsmServiceSOAP(endpoint);
    }

    public tw.com.citi.ws.hsm.HsmService_PortType getHsmServiceSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            tw.com.citi.ws.hsm.HsmServiceSOAPStub _stub = new tw.com.citi.ws.hsm.HsmServiceSOAPStub(portAddress, this);
            _stub.setPortName(getHsmServiceSOAPWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setHsmServiceSOAPEndpointAddress(java.lang.String address) {
        HsmServiceSOAP_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (tw.com.citi.ws.hsm.HsmService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                tw.com.citi.ws.hsm.HsmServiceSOAPStub _stub = new tw.com.citi.ws.hsm.HsmServiceSOAPStub(new java.net.URL(HsmServiceSOAP_address), this);
                _stub.setPortName(getHsmServiceSOAPWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("HsmServiceSOAP".equals(inputPortName)) {
            return getHsmServiceSOAP();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "HsmService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "HsmServiceSOAP"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

        if ("HsmServiceSOAP".equals(portName)) {
            setHsmServiceSOAPEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
