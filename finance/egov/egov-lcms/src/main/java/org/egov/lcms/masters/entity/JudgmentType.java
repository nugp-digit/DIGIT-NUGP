/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.lcms.masters.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "EGLC_JUDGMENTTYPE_MASTER")
@Unique(id = "id", tableName = "EGLC_JUDGMENTTYPE_MASTER", columnName = { "code",
		"judgmenttype" }, enableDfltMsg = true)
@SequenceGenerator(name = JudgmentType.SEQ_JUDGMENTTYPE, sequenceName = JudgmentType.SEQ_JUDGMENTTYPE, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
		@AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class JudgmentType extends AbstractAuditable {

	private static final long serialVersionUID = 1L;
	public static final String SEQ_JUDGMENTTYPE = "SEQ_EGLC_JUDGMENTTYPE_MASTER";

	@Id
	@GeneratedValue(generator = SEQ_JUDGMENTTYPE, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@SafeHtml
	@Length(min = 1, max = 8)
	@Audited
	private String code;

	@NotNull
	@SafeHtml
	@Length(min = 1, max = 50)
	@Audited
	@Column(name = "judgmenttype")
	private String name;

	@SafeHtml
	@Length(min = 3, max = 256)
	private String description;

	@Min(1)
	@Max(1000)
	private Long orderNumber;

	@NotNull
	@Audited
	private Boolean active;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(final Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(final Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

}
