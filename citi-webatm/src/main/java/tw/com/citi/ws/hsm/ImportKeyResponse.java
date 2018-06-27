/**
 * ImportKeyResponse.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public class ImportKeyResponse implements java.io.Serializable {
    private java.lang.String traceno;
    private boolean result;
    private java.lang.String rv;
    private java.lang.String rvString;

    public ImportKeyResponse() {
    }

    public ImportKeyResponse(java.lang.String traceno, boolean result, java.lang.String rv, java.lang.String rvString) {
        this.traceno = traceno;
        this.result = result;
        this.rv = rv;
        this.rvString = rvString;
    }

    /**
     * Gets the traceno value for this ImportKeyResponse.
     *
     * @return traceno
     */
    public java.lang.String getTraceno() {
        return traceno;
    }

    /**
     * Sets the traceno value for this ImportKeyResponse.
     *
     * @param traceno
     */
    public void setTraceno(java.lang.String traceno) {
        this.traceno = traceno;
    }

    /**
     * Gets the result value for this ImportKeyResponse.
     *
     * @return result
     */
    public boolean isResult() {
        return result;
    }

    /**
     * Sets the result value for this ImportKeyResponse.
     *
     * @param result
     */
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * Gets the rv value for this ImportKeyResponse.
     *
     * @return rv
     */
    public java.lang.String getRv() {
        return rv;
    }

    /**
     * Sets the rv value for this ImportKeyResponse.
     *
     * @param rv
     */
    public void setRv(java.lang.String rv) {
        this.rv = rv;
    }

    /**
     * Gets the rvString value for this ImportKeyResponse.
     *
     * @return rvString
     */
    public java.lang.String getRvString() {
        return rvString;
    }

    /**
     * Sets the rvString value for this ImportKeyResponse.
     *
     * @param rvString
     */
    public void setRvString(java.lang.String rvString) {
        this.rvString = rvString;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ImportKeyResponse))
            return false;
        ImportKeyResponse other = (ImportKeyResponse) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.traceno == null && other.getTraceno() == null) || (this.traceno != null && this.traceno.equals(other.getTraceno()))) && this.result == other.isResult()
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
        if (getTraceno() != null) {
            _hashCode += getTraceno().hashCode();
        }
        _hashCode += (isResult() ? Boolean.TRUE : Boolean.FALSE).hashCode();
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
