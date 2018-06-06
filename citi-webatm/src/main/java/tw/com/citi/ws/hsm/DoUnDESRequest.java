/**
 * DoUnDESRequest.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public class DoUnDESRequest implements java.io.Serializable {
    private java.lang.String sysID;
    private java.lang.String checkCode;
    private java.lang.String slot;
    private java.lang.String user;
    private java.lang.String passwd;
    private java.lang.String keyName;
    private java.lang.String keyType;
    private java.lang.String data1;
    private java.lang.String data2;

    public DoUnDESRequest() {
    }

    public DoUnDESRequest(java.lang.String sysID, java.lang.String checkCode, java.lang.String slot, java.lang.String user, java.lang.String passwd, java.lang.String keyName, java.lang.String keyType,
            java.lang.String data1, java.lang.String data2) {
        this.sysID = sysID;
        this.checkCode = checkCode;
        this.slot = slot;
        this.user = user;
        this.passwd = passwd;
        this.keyName = keyName;
        this.keyType = keyType;
        this.data1 = data1;
        this.data2 = data2;
    }

    /**
     * Gets the sysID value for this DoUnDESRequest.
     *
     * @return sysID
     */
    public java.lang.String getSysID() {
        return sysID;
    }

    /**
     * Sets the sysID value for this DoUnDESRequest.
     *
     * @param sysID
     */
    public void setSysID(java.lang.String sysID) {
        this.sysID = sysID;
    }

    /**
     * Gets the checkCode value for this DoUnDESRequest.
     *
     * @return checkCode
     */
    public java.lang.String getCheckCode() {
        return checkCode;
    }

    /**
     * Sets the checkCode value for this DoUnDESRequest.
     *
     * @param checkCode
     */
    public void setCheckCode(java.lang.String checkCode) {
        this.checkCode = checkCode;
    }

    /**
     * Gets the slot value for this DoUnDESRequest.
     *
     * @return slot
     */
    public java.lang.String getSlot() {
        return slot;
    }

    /**
     * Sets the slot value for this DoUnDESRequest.
     *
     * @param slot
     */
    public void setSlot(java.lang.String slot) {
        this.slot = slot;
    }

    /**
     * Gets the user value for this DoUnDESRequest.
     *
     * @return user
     */
    public java.lang.String getUser() {
        return user;
    }

    /**
     * Sets the user value for this DoUnDESRequest.
     *
     * @param user
     */
    public void setUser(java.lang.String user) {
        this.user = user;
    }

    /**
     * Gets the passwd value for this DoUnDESRequest.
     *
     * @return passwd
     */
    public java.lang.String getPasswd() {
        return passwd;
    }

    /**
     * Sets the passwd value for this DoUnDESRequest.
     *
     * @param passwd
     */
    public void setPasswd(java.lang.String passwd) {
        this.passwd = passwd;
    }

    /**
     * Gets the keyName value for this DoUnDESRequest.
     *
     * @return keyName
     */
    public java.lang.String getKeyName() {
        return keyName;
    }

    /**
     * Sets the keyName value for this DoUnDESRequest.
     *
     * @param keyName
     */
    public void setKeyName(java.lang.String keyName) {
        this.keyName = keyName;
    }

    /**
     * Gets the keyType value for this DoUnDESRequest.
     *
     * @return keyType
     */
    public java.lang.String getKeyType() {
        return keyType;
    }

    /**
     * Sets the keyType value for this DoUnDESRequest.
     *
     * @param keyType
     */
    public void setKeyType(java.lang.String keyType) {
        this.keyType = keyType;
    }

    /**
     * Gets the data1 value for this DoUnDESRequest.
     *
     * @return data1
     */
    public java.lang.String getData1() {
        return data1;
    }

    /**
     * Sets the data1 value for this DoUnDESRequest.
     *
     * @param data1
     */
    public void setData1(java.lang.String data1) {
        this.data1 = data1;
    }

    /**
     * Gets the data2 value for this DoUnDESRequest.
     *
     * @return data2
     */
    public java.lang.String getData2() {
        return data2;
    }

    /**
     * Sets the data2 value for this DoUnDESRequest.
     *
     * @param data2
     */
    public void setData2(java.lang.String data2) {
        this.data2 = data2;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DoUnDESRequest))
            return false;
        DoUnDESRequest other = (DoUnDESRequest) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.sysID == null && other.getSysID() == null) || (this.sysID != null && this.sysID.equals(other.getSysID())))
                && ((this.checkCode == null && other.getCheckCode() == null) || (this.checkCode != null && this.checkCode.equals(other.getCheckCode())))
                && ((this.slot == null && other.getSlot() == null) || (this.slot != null && this.slot.equals(other.getSlot())))
                && ((this.user == null && other.getUser() == null) || (this.user != null && this.user.equals(other.getUser())))
                && ((this.passwd == null && other.getPasswd() == null) || (this.passwd != null && this.passwd.equals(other.getPasswd())))
                && ((this.keyName == null && other.getKeyName() == null) || (this.keyName != null && this.keyName.equals(other.getKeyName())))
                && ((this.keyType == null && other.getKeyType() == null) || (this.keyType != null && this.keyType.equals(other.getKeyType())))
                && ((this.data1 == null && other.getData1() == null) || (this.data1 != null && this.data1.equals(other.getData1())))
                && ((this.data2 == null && other.getData2() == null) || (this.data2 != null && this.data2.equals(other.getData2())));
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
        if (getSysID() != null) {
            _hashCode += getSysID().hashCode();
        }
        if (getCheckCode() != null) {
            _hashCode += getCheckCode().hashCode();
        }
        if (getSlot() != null) {
            _hashCode += getSlot().hashCode();
        }
        if (getUser() != null) {
            _hashCode += getUser().hashCode();
        }
        if (getPasswd() != null) {
            _hashCode += getPasswd().hashCode();
        }
        if (getKeyName() != null) {
            _hashCode += getKeyName().hashCode();
        }
        if (getKeyType() != null) {
            _hashCode += getKeyType().hashCode();
        }
        if (getData1() != null) {
            _hashCode += getData1().hashCode();
        }
        if (getData2() != null) {
            _hashCode += getData2().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
