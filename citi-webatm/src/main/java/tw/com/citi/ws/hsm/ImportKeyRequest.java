/**
 * ImportKeyRequest.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package tw.com.citi.ws.hsm;

public class ImportKeyRequest implements java.io.Serializable {
    private java.lang.String sysID;
    private java.lang.String checkCode;
    private java.lang.String slot;
    private java.lang.String user;
    private java.lang.String passwd;
    private java.lang.String keyName;
    private java.lang.String keyType;
    private java.lang.String keyNameCD;
    private java.lang.String keyTypeCD;
    private java.lang.String keyValue;
    private java.lang.String kcv;

    public ImportKeyRequest() {
    }

    public ImportKeyRequest(java.lang.String sysID, java.lang.String checkCode, java.lang.String slot, java.lang.String user, java.lang.String passwd, java.lang.String keyName,
            java.lang.String keyType, java.lang.String keyNameCD, java.lang.String keyTypeCD, java.lang.String keyValue, java.lang.String kcv) {
        this.sysID = sysID;
        this.checkCode = checkCode;
        this.slot = slot;
        this.user = user;
        this.passwd = passwd;
        this.keyName = keyName;
        this.keyType = keyType;
        this.keyNameCD = keyNameCD;
        this.keyTypeCD = keyTypeCD;
        this.keyValue = keyValue;
        this.kcv = kcv;
    }

    /**
     * Gets the sysID value for this ImportKeyRequest.
     *
     * @return sysID
     */
    public java.lang.String getSysID() {
        return sysID;
    }

    /**
     * Sets the sysID value for this ImportKeyRequest.
     *
     * @param sysID
     */
    public void setSysID(java.lang.String sysID) {
        this.sysID = sysID;
    }

    /**
     * Gets the checkCode value for this ImportKeyRequest.
     *
     * @return checkCode
     */
    public java.lang.String getCheckCode() {
        return checkCode;
    }

    /**
     * Sets the checkCode value for this ImportKeyRequest.
     *
     * @param checkCode
     */
    public void setCheckCode(java.lang.String checkCode) {
        this.checkCode = checkCode;
    }

    /**
     * Gets the slot value for this ImportKeyRequest.
     *
     * @return slot
     */
    public java.lang.String getSlot() {
        return slot;
    }

    /**
     * Sets the slot value for this ImportKeyRequest.
     *
     * @param slot
     */
    public void setSlot(java.lang.String slot) {
        this.slot = slot;
    }

    /**
     * Gets the user value for this ImportKeyRequest.
     *
     * @return user
     */
    public java.lang.String getUser() {
        return user;
    }

    /**
     * Sets the user value for this ImportKeyRequest.
     *
     * @param user
     */
    public void setUser(java.lang.String user) {
        this.user = user;
    }

    /**
     * Gets the passwd value for this ImportKeyRequest.
     *
     * @return passwd
     */
    public java.lang.String getPasswd() {
        return passwd;
    }

    /**
     * Sets the passwd value for this ImportKeyRequest.
     *
     * @param passwd
     */
    public void setPasswd(java.lang.String passwd) {
        this.passwd = passwd;
    }

    /**
     * Gets the keyName value for this ImportKeyRequest.
     *
     * @return keyName
     */
    public java.lang.String getKeyName() {
        return keyName;
    }

    /**
     * Sets the keyName value for this ImportKeyRequest.
     *
     * @param keyName
     */
    public void setKeyName(java.lang.String keyName) {
        this.keyName = keyName;
    }

    /**
     * Gets the keyType value for this ImportKeyRequest.
     *
     * @return keyType
     */
    public java.lang.String getKeyType() {
        return keyType;
    }

    /**
     * Sets the keyType value for this ImportKeyRequest.
     *
     * @param keyType
     */
    public void setKeyType(java.lang.String keyType) {
        this.keyType = keyType;
    }

    /**
     * Gets the keyNameCD value for this ImportKeyRequest.
     *
     * @return keyNameCD
     */
    public java.lang.String getKeyNameCD() {
        return keyNameCD;
    }

    /**
     * Sets the keyNameCD value for this ImportKeyRequest.
     *
     * @param keyNameCD
     */
    public void setKeyNameCD(java.lang.String keyNameCD) {
        this.keyNameCD = keyNameCD;
    }

    /**
     * Gets the keyTypeCD value for this ImportKeyRequest.
     *
     * @return keyTypeCD
     */
    public java.lang.String getKeyTypeCD() {
        return keyTypeCD;
    }

    /**
     * Sets the keyTypeCD value for this ImportKeyRequest.
     *
     * @param keyTypeCD
     */
    public void setKeyTypeCD(java.lang.String keyTypeCD) {
        this.keyTypeCD = keyTypeCD;
    }

    /**
     * Gets the keyValue value for this ImportKeyRequest.
     *
     * @return keyValue
     */
    public java.lang.String getKeyValue() {
        return keyValue;
    }

    /**
     * Sets the keyValue value for this ImportKeyRequest.
     *
     * @param keyValue
     */
    public void setKeyValue(java.lang.String keyValue) {
        this.keyValue = keyValue;
    }

    /**
     * Gets the kcv value for this ImportKeyRequest.
     *
     * @return kcv
     */
    public java.lang.String getKcv() {
        return kcv;
    }

    /**
     * Sets the kcv value for this ImportKeyRequest.
     *
     * @param kcv
     */
    public void setKcv(java.lang.String kcv) {
        this.kcv = kcv;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ImportKeyRequest))
            return false;
        ImportKeyRequest other = (ImportKeyRequest) obj;
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
                && ((this.keyNameCD == null && other.getKeyNameCD() == null) || (this.keyNameCD != null && this.keyNameCD.equals(other.getKeyNameCD())))
                && ((this.keyTypeCD == null && other.getKeyTypeCD() == null) || (this.keyTypeCD != null && this.keyTypeCD.equals(other.getKeyTypeCD())))
                && ((this.keyValue == null && other.getKeyValue() == null) || (this.keyValue != null && this.keyValue.equals(other.getKeyValue())))
                && ((this.kcv == null && other.getKcv() == null) || (this.kcv != null && this.kcv.equals(other.getKcv())));
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
        if (getKeyNameCD() != null) {
            _hashCode += getKeyNameCD().hashCode();
        }
        if (getKeyTypeCD() != null) {
            _hashCode += getKeyTypeCD().hashCode();
        }
        if (getKeyValue() != null) {
            _hashCode += getKeyValue().hashCode();
        }
        if (getKcv() != null) {
            _hashCode += getKcv().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
