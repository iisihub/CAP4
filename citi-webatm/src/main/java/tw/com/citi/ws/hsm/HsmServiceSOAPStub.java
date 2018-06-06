/**
 * HsmServiceSOAPStub.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public class HsmServiceSOAPStub extends org.apache.axis.client.Stub implements tw.com.citi.ws.hsm.HsmService_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc[] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[3];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1() {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("doDES");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "doDESRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doDESRequest"), tw.com.citi.ws.hsm.DoDESRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doDESResponse"));
        oper.setReturnClass(tw.com.citi.ws.hsm.DoDESResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "doDESResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("doUnDES");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "doUnDESRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doUnDESRequest"), tw.com.citi.ws.hsm.DoUnDESRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doUnDESResponse"));
        oper.setReturnClass(tw.com.citi.ws.hsm.DoUnDESResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "doUnDESResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("importKey");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "importKeyRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">importKeyRequest"), tw.com.citi.ws.hsm.ImportKeyRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">importKeyResponse"));
        oper.setReturnClass(tw.com.citi.ws.hsm.ImportKeyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", "importKeyResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

    }

    public HsmServiceSOAPStub() throws org.apache.axis.AxisFault {
        this(null);
    }

    public HsmServiceSOAPStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        this(service);
        super.cachedEndpoint = endpointURL;
    }

    public HsmServiceSOAPStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
        java.lang.Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        qName = new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doDESRequest");
        cachedSerQNames.add(qName);
        cls = tw.com.citi.ws.hsm.DoDESRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doDESResponse");
        cachedSerQNames.add(qName);
        cls = tw.com.citi.ws.hsm.DoDESResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doUnDESRequest");
        cachedSerQNames.add(qName);
        cls = tw.com.citi.ws.hsm.DoUnDESRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">doUnDESResponse");
        cachedSerQNames.add(qName);
        cls = tw.com.citi.ws.hsm.DoUnDESResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">importKeyRequest");
        cachedSerQNames.add(qName);
        cls = tw.com.citi.ws.hsm.ImportKeyRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://ws.citi.com.tw/hsm", ">importKeyResponse");
        cachedSerQNames.add(qName);
        cls = tw.com.citi.ws.hsm.ImportKeyResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class) cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        } else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        } catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public tw.com.citi.ws.hsm.DoDESResponse doDES(tw.com.citi.ws.hsm.DoDESRequest doDESRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.citi.com.tw/hsm/NewOperation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "doDES"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { doDESRequest });

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                try {
                    return (tw.com.citi.ws.hsm.DoDESResponse) _resp;
                } catch (java.lang.Exception _exception) {
                    return (tw.com.citi.ws.hsm.DoDESResponse) org.apache.axis.utils.JavaUtils.convert(_resp, tw.com.citi.ws.hsm.DoDESResponse.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public tw.com.citi.ws.hsm.DoUnDESResponse doUnDES(tw.com.citi.ws.hsm.DoUnDESRequest doUnDESRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.citi.com.tw/hsm/NewOperation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "doUnDES"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { doUnDESRequest });

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                try {
                    return (tw.com.citi.ws.hsm.DoUnDESResponse) _resp;
                } catch (java.lang.Exception _exception) {
                    return (tw.com.citi.ws.hsm.DoUnDESResponse) org.apache.axis.utils.JavaUtils.convert(_resp, tw.com.citi.ws.hsm.DoUnDESResponse.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public tw.com.citi.ws.hsm.ImportKeyResponse importKey(tw.com.citi.ws.hsm.ImportKeyRequest importKeyRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "importKey"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { importKeyRequest });

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                try {
                    return (tw.com.citi.ws.hsm.ImportKeyResponse) _resp;
                } catch (java.lang.Exception _exception) {
                    return (tw.com.citi.ws.hsm.ImportKeyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, tw.com.citi.ws.hsm.ImportKeyResponse.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

}
