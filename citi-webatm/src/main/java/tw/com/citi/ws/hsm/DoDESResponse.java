/**
 * DoDESResponse.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public class DoDESResponse implements java.io.Serializable {
    private java.lang.String data1;
    private java.lang.String data2;
    private java.lang.String traceno;
    private java.lang.String returnData;
    private java.lang.String rv;
    private java.lang.String rvString;

    public DoDESResponse() {
    }

    public DoDESResponse(java.lang.String data1, java.lang.String data2, java.lang.String traceno, java.lang.String returnData, java.lang.String rv, java.lang.String rvString) {
        this.data1 = data1;
        this.data2 = data2;
        this.traceno = traceno;
        this.returnData = returnData;
        this.rv = rv;
        this.rvString = rvString;
    }

    /**
     * Gets the data1 value for this DoDESResponse.
     *
     * @return data1
     */
    public java.lang.String getData1() {
        return data1;
    }

    /**
     * Sets the data1 value for this DoDESResponse.
     *
     * @param data1
     */
    public void setData1(java.lang.String data1) {
        this.data1 = data1;
    }

    /**
     * Gets the data2 value for this DoDESResponse.
     *
     * @return data2
     */
    public java.lang.String getData2() {
        return data2;
    }

    /**
     * Sets the data2 value for this DoDESResponse.
     *
     * @param data2
     */
    public void setData2(java.lang.String data2) {
        this.data2 = data2;
    }

    /**
     * Gets the traceno value for this DoDESResponse.
     *
     * @return traceno
     */
    public java.lang.String getTraceno() {
        return traceno;
    }

    /**
     * Sets the traceno value for this DoDESResponse.
     *
     * @param traceno
     */
    public void setTraceno(java.lang.String traceno) {
        this.traceno = traceno;
    }

    /**
     * Gets the returnData value for this DoDESResponse.
     *
     * @return returnData
     */
    public java.lang.String getReturnData() {
        return returnData;
    }

    /**
     * Sets the returnData value for this DoDESResponse.
     *
     * @param returnData
     */
    public void setReturnData(java.lang.String returnData) {
        this.returnData = returnData;
    }

    /**
     * Gets the rv value for this DoDESResponse.
     *
     * @return rv
     */
    public java.lang.String getRv() {
        return rv;
    }

    /**
     * Sets the rv value for this DoDESResponse.
     *
     * @param rv
     */
    public void setRv(java.lang.String rv) {
        this.rv = rv;
    }

    /**
     * Gets the rvString value for this DoDESResponse.
     *
     * @return rvString
     */
    public java.lang.String getRvString() {
        return rvString;
    }

    /**
     * Sets the rvString value for this DoDESResponse.
     *
     * @param rvString
     */
    public void setRvString(java.lang.String rvString) {
        this.rvString = rvString;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DoDESResponse))
            return false;
        DoDESResponse other = (DoDESResponse) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.data1 == null && other.getData1() == null) || (this.data1 != null && this.data1.equals(other.getData1())))
                && ((this.data2 == null && other.getData2() == null) || (this.data2 != null && this.data2.equals(other.getData2())))
                && ((this.traceno == null && other.getTraceno() == null) || (this.traceno != null && this.traceno.equals(other.getTraceno())))
                && ((this.returnData == null && other.getReturnData() == null) || (this.returnData != null && this.returnData.equals(other.getReturnData())))
                && ((this.rv == null && other.getRv() == null) || (this.rv != null && this.rv.equals(other.getRv())))
                && ((this.rvString == null && other.getRvString() == null) || (this.rvString != null && this.rvString.equals(other.getRvString())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getData1() != null) {
            _hashCode += getData1().hashCode();
        }
        if (getData2() != null) {
            _hashCode += getData2().hashCode();
        }
        if (getTraceno() != null) {
            _hashCode += getTraceno().hashCode();
        }
        if (getReturnData() != null) {
            _hashCode += getReturnData().hashCode();
        }
        if (getRv() != null) {
            _hashCode += getRv().hashCode();
        }
        if (getRvString() != null) {
            _hashCode += getRvString().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
