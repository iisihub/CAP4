package com.iisigroup.colabase.va.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.iisigroup.cap.db.model.DataObject;
import com.iisigroup.cap.db.model.listener.CapOidGeneratorListener;
import com.iisigroup.cap.model.GenericBean;

@SuppressWarnings("serial")
@Entity
@EntityListeners({ CapOidGeneratorListener.class })
@Table(name = "CAINFO", uniqueConstraints = @UniqueConstraint(columnNames = "oid") )
public class CAInfo extends GenericBean implements DataObject {

    @Id
    @Column(length = 32, nullable = false)
    private String oid;

    @Column(length = 60, nullable = false)
    private String caName;

    @Column(length = 2)
    private String status;

    @Column(name = "active_date")
    private Timestamp activeDate;

    @Column(name = "expired_date")
    private Timestamp expiredDate;

    @Column(name = "import_date")
    private Timestamp importDate;

    @Column(length = 120)
    private String caDesc;

    @Column(length = 20)
    private String schedule;

    @Column(length = 256)
    private String crlUrl;

    @Column(length = 20)
    private String modifier;

    @Column(name = "modify_date")
    private Timestamp modifyDate;

    // TEXT
    @Column
    private String certData;

    @Column(name = "DL_CRL_RESULT")
    private boolean dlCrlResult;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCaName() {
        return caName;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Timestamp activeDate) {
        this.activeDate = activeDate;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Timestamp getImportDate() {
        return importDate;
    }

    public void setImportDate(Timestamp importDate) {
        this.importDate = importDate;
    }

    public String getCaDesc() {
        return caDesc;
    }

    public void setCaDesc(String caDesc) {
        this.caDesc = caDesc;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Timestamp getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Timestamp modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCertData() {
        return certData;
    }

    public void setCertData(String certData) {
        this.certData = certData;
    }

    public String getCrlUrl() {
        return crlUrl;
    }

    public void setCrlUrl(String crlUrl) {
        this.crlUrl = crlUrl;
    }

    public boolean isDlCrlResult() {
        return dlCrlResult;
    }

    public void setDlCrlResult(boolean dlCrlResult) {
        this.dlCrlResult = dlCrlResult;
    }
}
