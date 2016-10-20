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
package org.egov.mrs.domain.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.commons.EgwStatus;
import org.egov.demand.model.EgDemand;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.mrs.masters.entity.MarriageAct;
import org.egov.mrs.masters.entity.MarriageFee;
import org.egov.mrs.masters.entity.MarriageRegistrationUnit;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "egmrs_registration")
@SequenceGenerator(name = MarriageRegistration.SEQ_REGISTRATION, sequenceName = MarriageRegistration.SEQ_REGISTRATION, allocationSize = 1)
public class MarriageRegistration extends StateAware {

    private static final long serialVersionUID = 6743094118312883758L;
    public static final String SEQ_REGISTRATION = "SEQ_EGMRS_REGISTRATION";
    
    public enum RegistrationStatus {
        CREATED, APPROVED, REJECTED, REGISTERED, CANCELLED
    }

    @Id
    @GeneratedValue(generator = SEQ_REGISTRATION, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private String applicationNo;

    @NotNull
    private Date applicationDate;

    private String registrationNo;

    @NotNull
    private Date dateOfMarriage;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marriageact")
    private MarriageAct marriageAct;

    @NotNull
    @SafeHtml
    @Length(max = 30)
    private String placeOfMarriage;

    /*
     * @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "registration") //Refers to registration field of
     * the Applicant class private Applicant husband = new Applicant();
     * @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "registration") //Refers to registration field of
     * the Applicant class private Applicant wife = new Applicant();
     */

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "husband")
  //  @QueryInit("name.firstName")
    private MrApplicant husband = new MrApplicant();

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "wife")
 //   @QueryInit("name.firstName")
    private MrApplicant wife = new MrApplicant();

    @NotNull
    @Valid
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registration")
    @Size(max = 3)
    private List<MarriageWitness> witnesses = new LinkedList<MarriageWitness>();

    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "priest")
    private MarriagePriest priest;

    private boolean coupleFromSamePlace;

    private boolean memorandumOfMarriage;
    private boolean courtFeeStamp;
    private boolean affidavit;
    private boolean marriageCard;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "feeCriteria")
    private MarriageFee feeCriteria;

    @NotNull
    private Double feePaid;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone")
    private Boundary zone;
    
    
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrationUnit")
    private MarriageRegistrationUnit marriageRegistrationUnit;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "demand")
    private EgDemand demand;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private EgwStatus status;

    @Length(max = 256)
    private String rejectionReason;

    @Length(max = 256)
    private String remarks;

    @Transient
    private Long approvalDepartment;

    @Transient
    private String approvalComent;
    @Transient
    private Date fromDate;
    @Transient
    private Date toDate;
    
    private boolean isLegacy;
    private boolean isActive;

    @NotNull
    @Valid
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registration")
    private List<RegistrationDocument> registrationDocuments = new ArrayList<RegistrationDocument>();
    
    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MarriageCertificate> marriageCertificate = new ArrayList<MarriageCertificate>();

    @Transient
    private List<MarriageDocument> documents;

    @Override
    public String getStateDetails() {
        return "Marriage registration application no : " + applicationNo;
    }

    public boolean isFeeCollected() {
        return demand.getBaseDemand().compareTo(demand.getAmtCollected()) == 0 ? true : false;
    }

    public void addRegistrationDocument(final RegistrationDocument registrationDocument) {
        registrationDocument.setRegistration(this);
        getRegistrationDocuments().add(registrationDocument);
    }

    public void addWitness(final MarriageWitness witness) {
        witness.setRegistration(this);
        getWitnesses().add(witness);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(final String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(final String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public Date getDateOfMarriage() {
        return dateOfMarriage;
    }

    public void setDateOfMarriage(final Date dateOfMarriage) {
        this.dateOfMarriage = dateOfMarriage;
    }

    public MarriageAct getMarriageAct() {
        return marriageAct;
    }

    public void setMarriageAct(final MarriageAct marriageAct) {
        this.marriageAct = marriageAct;
    }

    public String getPlaceOfMarriage() {
        return placeOfMarriage;
    }

    public void setPlaceOfMarriage(final String placeOfMarriage) {
        this.placeOfMarriage = placeOfMarriage;
    }

    public MrApplicant getHusband() {
        return husband;
    }

    public void setHusband(final MrApplicant husband) {
        this.husband = husband;
    }

    public MrApplicant getWife() {
        return wife;
    }

    public void setWife(final MrApplicant wife) {
        this.wife = wife;
    }

    public MarriagePriest getPriest() {
        return priest;
    }

    public void setPriest(final MarriagePriest priest) {
        this.priest = priest;
    }

    public boolean hasMemorandumOfMarriage() {
        return memorandumOfMarriage;
    }

    public boolean getMemorandumOfMarriage() {
        return memorandumOfMarriage;
    }

    public void setMemorandumOfMarriage(final boolean memorandumOfMarriage) {
        this.memorandumOfMarriage = memorandumOfMarriage;
    }

    public boolean getCourtFeeStamp() {
        return courtFeeStamp;
    }

    public void setCourtFeeStamp(final boolean courtFeeStamp) {
        this.courtFeeStamp = courtFeeStamp;
    }

    public boolean hasAffidavit() {
        return affidavit;
    }

    public boolean getAffidavit() {
        return affidavit;
    }

    public void setAffidavit(final boolean affidavit) {
        this.affidavit = affidavit;
    }

    public boolean hasMarriageCard() {
        return marriageCard;
    }

    public boolean getMarriageCard() {
        return marriageCard;
    }

    public void setMarriageCard(final boolean marriageCard) {
        this.marriageCard = marriageCard;
    }

    public boolean isCoupleFromSamePlace() {
        return coupleFromSamePlace;
    }

    public void setCoupleFromSamePlace(final boolean coupleFromSamePlace) {
        this.coupleFromSamePlace = coupleFromSamePlace;
    }

    public MarriageFee getFeeCriteria() {
        return feeCriteria;
    }

    public void setFeeCriteria(final MarriageFee feeCriteria) {
        this.feeCriteria = feeCriteria;
    }

    public Double getFeePaid() {
        return feePaid;
    }

    public void setFeePaid(final Double feePaid) {
        this.feePaid = feePaid;
    }

    public List<MarriageWitness> getWitnesses() {
        return witnesses;
    }

    public void setWitnesses(final List<MarriageWitness> witnesses) {
        this.witnesses = witnesses;
    }

    public Boundary getZone() {
        return zone;
    }

    public void setZone(final Boundary zone) {
        this.zone = zone;
    }

    public EgDemand getDemand() {
        return demand;
    }

    public void setDemand(final EgDemand demand) {
        this.demand = demand;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(final String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

    public Long getApprovalDepartment() {
        return approvalDepartment;
    }

    public void setApprovalDepartment(final Long approvalDepartment) {
        this.approvalDepartment = approvalDepartment;
    }

    public String getApprovalComent() {
        return approvalComent;
    }

    public void setApprovalComent(final String approvalComent) {
        this.approvalComent = approvalComent;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(final Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public List<RegistrationDocument> getRegistrationDocuments() {
        return registrationDocuments;
    }

    public void setRegistrationDocuments(final List<RegistrationDocument> registrationDocuments) {
        this.registrationDocuments = registrationDocuments;
    }

    public List<MarriageDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(final List<MarriageDocument> documents) {
        this.documents = documents;
    }

    public EgwStatus getStatus() {
        return status;
    }

    public void setStatus(EgwStatus status) {
        this.status = status;
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(final boolean isActive) {
        this.isActive = isActive;
    }

    public List<MarriageCertificate> getMarriageCertificate() {
        return marriageCertificate;
    }

    public void setMarriageCertificate(List<MarriageCertificate> marriageCertificate) {
        this.marriageCertificate = marriageCertificate;
    }
    
    public void addCertificate(final MarriageCertificate certificate) {
        getMarriageCertificate().add(certificate);
    }

    public void removeCertificate(final MarriageCertificate certificate) {
        getMarriageCertificate().remove(certificate);
    }

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public MarriageRegistrationUnit getMarriageRegistrationUnit() {
		return marriageRegistrationUnit;
	}

	public void setMarriageRegistrationUnit(
			MarriageRegistrationUnit marriageRegistrationUnit) {
		this.marriageRegistrationUnit = marriageRegistrationUnit;
	}
    
	 public boolean isLegacy() {
	        return isLegacy;
	    }

	    public void setLegacy(boolean isLegacy) {
	        this.isLegacy = isLegacy;
	    }

}
