package com.iisigroup.colabase.import_.model;

import javax.persistence.Column;
import javax.persistence.Id;

import com.iisigroup.cap.db.model.DataObject;
import com.iisigroup.cap.model.GenericBean;

/***
 * Demo Customer Model
 * 
 * @author LilyPeng
 *
 */
//@SuppressWarnings("serial")
//@Entity
//@EntityListeners({ CapOidGeneratorListener.class })
//@Table(name = "DEMO_CUSTOMER", uniqueConstraints = @UniqueConstraint(columnNames = { "ID" }))
public class DemoImportCustomer extends GenericBean implements DataObject {

    // OID
    @Id
    @Column(name = "OID", length = 32, nullable = false)
    private String oid;

    @Id
    @Column(name = "ID", length = 10, nullable = false)
    private String id;

    @Column(name = "BIRTHDAY", length = 10, nullable = false)
    private String birthday;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
