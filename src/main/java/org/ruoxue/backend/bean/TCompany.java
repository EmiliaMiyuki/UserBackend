package org.ruoxue.backend.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author fengjb
 * @since 2018-09-23
 */
@TableName("t_company")
public class TCompany extends Model<TCompany> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
	private Integer uid;
	private Integer parent;
	private String lpname;
	@TableField("tax_type")
	private String taxType;
	@TableField("vat_type")
	private String vatType;
	@TableField("ysa_range")
	private Integer ysaRange;
	@TableField("pre_tax_ratio")
	private Double preTaxRatio;
	@TableField("va_tax_ratio")
	private Double vaTaxRatio;
	@TableField("vatr_freq")
	private Double vatrFreq;
	@TableField("cb_tax")
	private Double cbTax;
	@TableField("ea_tax")
	private Double eaTax;
	@TableField("lea_tax")
	private Double leaTax;
	@TableField("river_tax")
	private Double riverTax;
	@TableField("ent_org_type")
	private String entOrgType;
	@TableField("inv_type")
	private String invType;
	@TableField("business_type")
	private String businessType;
	@TableField("tax_pack_start")
	private Date taxPackStart;
	@TableField("tax_pack_end")
	private Date taxPackEnd;
	@TableField("ori_tax_pack_start")
	private Date oriTaxPackStart;
	@TableField("ori_tax_pack_end")
	private Date oriTaxPackEnd;
	@TableField("tm_first_ec")
	private Date tmFirstEc;
	private String status;
	private String name;
	@TableField("ysa_status")
	private Integer ysaStatus;
	private Integer checked;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public String getLpname() {
		return lpname;
	}

	public void setLpname(String lpname) {
		this.lpname = lpname;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public String getVatType() {
		return vatType;
	}

	public void setVatType(String vatType) {
		this.vatType = vatType;
	}

	public Integer getYsaRange() {
		return ysaRange;
	}

	public void setYsaRange(Integer ysaRange) {
		this.ysaRange = ysaRange;
	}

	public Double getPreTaxRatio() {
		return preTaxRatio;
	}

	public void setPreTaxRatio(Double preTaxRatio) {
		this.preTaxRatio = preTaxRatio;
	}

	public Double getVaTaxRatio() {
		return vaTaxRatio;
	}

	public void setVaTaxRatio(Double vaTaxRatio) {
		this.vaTaxRatio = vaTaxRatio;
	}

	public Double getVatrFreq() {
		return vatrFreq;
	}

	public void setVatrFreq(Double vatrFreq) {
		this.vatrFreq = vatrFreq;
	}

	public Double getCbTax() {
		return cbTax;
	}

	public void setCbTax(Double cbTax) {
		this.cbTax = cbTax;
	}

	public Double getEaTax() {
		return eaTax;
	}

	public void setEaTax(Double eaTax) {
		this.eaTax = eaTax;
	}

	public Double getLeaTax() {
		return leaTax;
	}

	public void setLeaTax(Double leaTax) {
		this.leaTax = leaTax;
	}

	public Double getRiverTax() {
		return riverTax;
	}

	public void setRiverTax(Double riverTax) {
		this.riverTax = riverTax;
	}

	public String getEntOrgType() {
		return entOrgType;
	}

	public void setEntOrgType(String entOrgType) {
		this.entOrgType = entOrgType;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Date getTaxPackStart() {
		return taxPackStart;
	}

	public void setTaxPackStart(Date taxPackStart) {
		this.taxPackStart = taxPackStart;
	}

	public Date getTaxPackEnd() {
		return taxPackEnd;
	}

	public void setTaxPackEnd(Date taxPackEnd) {
		this.taxPackEnd = taxPackEnd;
	}

	public Date getOriTaxPackStart() {
		return oriTaxPackStart;
	}

	public void setOriTaxPackStart(Date oriTaxPackStart) {
		this.oriTaxPackStart = oriTaxPackStart;
	}

	public Date getOriTaxPackEnd() {
		return oriTaxPackEnd;
	}

	public void setOriTaxPackEnd(Date oriTaxPackEnd) {
		this.oriTaxPackEnd = oriTaxPackEnd;
	}

	public Date getTmFirstEc() {
		return tmFirstEc;
	}

	public void setTmFirstEc(Date tmFirstEc) {
		this.tmFirstEc = tmFirstEc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYsaStatus() {
		return ysaStatus;
	}

	public void setYsaStatus(Integer ysaStatus) {
		this.ysaStatus = ysaStatus;
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "TCompany{" +
			"id=" + id +
			", uid=" + uid +
			", parent=" + parent +
			", lpname=" + lpname +
			", taxType=" + taxType +
			", vatType=" + vatType +
			", ysaRange=" + ysaRange +
			", preTaxRatio=" + preTaxRatio +
			", vaTaxRatio=" + vaTaxRatio +
			", vatrFreq=" + vatrFreq +
			", cbTax=" + cbTax +
			", eaTax=" + eaTax +
			", leaTax=" + leaTax +
			", riverTax=" + riverTax +
			", entOrgType=" + entOrgType +
			", invType=" + invType +
			", businessType=" + businessType +
			", taxPackStart=" + taxPackStart +
			", taxPackEnd=" + taxPackEnd +
			", oriTaxPackStart=" + oriTaxPackStart +
			", oriTaxPackEnd=" + oriTaxPackEnd +
			", tmFirstEc=" + tmFirstEc +
			", status=" + status +
			", name=" + name +
			", ysaStatus=" + ysaStatus +
			", checked=" + checked +
			"}";
	}
}
