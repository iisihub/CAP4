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
@Table(name = "CO_TRANSLOG", uniqueConstraints = @UniqueConstraint(columnNames = "oid") )
public class TransLog extends GenericBean implements DataObject {

    @Id
    @Column(length = 32, nullable = false)
    private String oid;

    @Column(length = 40, nullable = false)
    private String printSeq;

    @Column(length = 40, nullable = false)
    private String sourceIp;

    @Column(length = 4, nullable = false)
    private String status;

    @Column(length = 40, nullable = false)
    private String idHash;

    // TEXT
    @Column
    private String p7Data;

    @Column(length = 256)
    private String pdfPath;

    @Column(name = "trans_date")
    private Timestamp transDate;

    @Column(length = 256)
    private String pdfServer;
    @Column(length = 10)
    private String systemtype;

    public String getSystemtype() {
        return systemtype;
    }

    public void setSystemtype(String systemtype) {
        this.systemtype = systemtype;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPrintSeq() {
        return printSeq;
    }

    public void setPrintSeq(String printSeq) {
        this.printSeq = printSeq;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdHash() {
        return idHash;
    }

    public void setIdHash(String idHash) {
        this.idHash = idHash;
    }

    public String getP7Data() {
        return p7Data;
    }

    public void setP7Data(String p7Data) {
        this.p7Data = p7Data;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public Timestamp getTransDate() {
        return transDate;
    }

    public void setTransDate(Timestamp transDate) {
        this.transDate = transDate;
    }

    public String getP7FileName() {
        return getPrintSeq() + ".p7b";
    }

    public String getPdfServer() {
        return pdfServer;
    }

    public void setPdfServer(String pdfServer) {
        this.pdfServer = pdfServer;
    }
}
