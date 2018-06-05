package com.iisigroup.colabase.model;

import javax.persistence.*;

import com.iisigroup.cap.db.model.DataObject;
import com.iisigroup.cap.db.model.listener.CapOidGeneratorListener;
import com.iisigroup.cap.model.GenericBean;

/**
 * Zip Code data
 * @author TimChiang
 * @since  <li>2016/4/11,Tim,New
 *
 */
@SuppressWarnings("serial")
@Entity
@EntityListeners({ CapOidGeneratorListener.class })
@Table(name = "CO_XSL_ZIPCODE", uniqueConstraints = {@UniqueConstraint(columnNames = { "OID"})})
public class ZipCode extends GenericBean implements DataObject {

    /** OID */
    @Id
    @Column(name = "OID", length = 32, nullable = false)
    private String oid;

    /**
     * zip
     */
    @Column(name = "ZIP_Code", length = 10, nullable = false)
    private String zipCode;

    /** County */
    @Column(name = "COUNTY", length = 30, nullable=false)
    private String county;

    /** District */
    @Column(name = "DISTRICT", length = 30, nullable=false)
    private String district;

    /** Road */
    @Column(name = "ROAD", length = 100)
    private String road;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}



}
