package com.iisigroup.colabase.import_.model;

import javax.persistence.Column;
import javax.persistence.Id;

import com.iisigroup.cap.db.model.DataObject;
import com.iisigroup.cap.model.GenericBean;

/**
 * <pre>
 * Demo Import Customer Model
 * 實際使用時，需將model加入Entity與Table的annotation以及persistence設定
 * </pre>
 * 
 * @since 2018年5月14日
 * @author LilyPeng
 * @version <ul>
 *          <li>2018年5月14日,Lily,new
 *          </ul>
 */
// @SuppressWarnings("serial")
// @Entity
// @EntityListeners({ CapOidGeneratorListener.class })
// @Table(name = "DEMO_CUSTOMER", uniqueConstraints = @UniqueConstraint(columnNames = { "ID" }))
// 實際使用時，將上述annotation解開，並將此model加入persistence設定
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

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.db.model.DataObject#getOid()
     */
    public String getOid() {
        return oid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.db.model.DataObject#setOid(java.lang.String)
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return birthday
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * @param birthday
     *            birthday
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
