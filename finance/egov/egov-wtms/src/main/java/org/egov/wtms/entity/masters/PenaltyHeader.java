/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.wtms.entity.masters;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "egwtr_penalty_header")
@SequenceGenerator(name = PenaltyHeader.SEQ_PENALTYHEADER, sequenceName = PenaltyHeader.SEQ_PENALTYHEADER, allocationSize = 1)
public class PenaltyHeader extends AbstractAuditable {

    private static final long serialVersionUID = -3851299107850036408L;
    public static final String SEQ_PENALTYHEADER = "SEQ_EGWTR_PENALTY_HEADER";

    @Id
    @GeneratedValue(generator = SEQ_PENALTYHEADER, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @SafeHtml
    @Length(min = 3, max = 50)
    @Column(name = "penalty_type", unique = true)
    private String penaltyType;

    @SafeHtml
    private String description;

    private boolean isActive;

    @OneToMany(mappedBy = "penaltyHeader", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<PenaltyDetails> penaltyDetails = new HashSet<PenaltyDetails>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getPenaltyType() {
        return penaltyType;
    }

    public void setPenaltyType(final String penaltyType) {
        this.penaltyType = penaltyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(final boolean isActive) {
        this.isActive = isActive;
    }

    public Set<PenaltyDetails> getPenaltyDetails() {
        return penaltyDetails;
    }

    public void setPenaltyDetails(final Set<PenaltyDetails> penaltyDetails) {
        this.penaltyDetails = penaltyDetails;
    }

    public void addPenaltyDetails(final PenaltyDetails penaltyDetail) {
        penaltyDetails.add(penaltyDetail);
    }
}