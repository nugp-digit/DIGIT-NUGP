package org.egov.council.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.egov.commons.EgwStatus;
import org.egov.council.entity.enums.PreambleType;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.search.domain.Searchable;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "egcncl_preamble")
@Searchable
@SequenceGenerator(name = CouncilPreamble.SEQ_COUNCILPREAMBLE, sequenceName = CouncilPreamble.SEQ_COUNCILPREAMBLE, allocationSize = 1)
public class CouncilPreamble extends StateAware {

    private static final long serialVersionUID = -2561739732877438517L;

    public static final String SEQ_COUNCILPREAMBLE = "seq_egcncl_preamble";

    @Id
    @GeneratedValue(generator = SEQ_COUNCILPREAMBLE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "department", nullable = false)
    private Department department;

    @Column(name = "preambleNumber", unique = true)
    @Length(max = 25)
    private String preambleNumber;

    private BigDecimal sanctionAmount;

    @NotNull
    @Length(max = 5000)
    @JoinColumn(name = "gistOfPreamble")
    private String gistOfPreamble;

    @Transient
    private MultipartFile attachments;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filestoreid")
    private FileStoreMapper filestoreid;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private EgwStatus status;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private PreambleType type;

    @Transient
    private Date fromDate;
    
    @Transient
    private Date toDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getPreambleNumber() {
        return preambleNumber;
    }

    public void setPreambleNumber(String preambleNumber) {
        this.preambleNumber = preambleNumber;
    }

    public BigDecimal getSanctionAmount() {
        return sanctionAmount;
    }

    public void setSanctionAmount(BigDecimal sanctionAmount) {
        this.sanctionAmount = sanctionAmount;
    }

    public String getGistOfPreamble() {
        return gistOfPreamble;
    }

    public void setGistOfPreamble(String gistOfPreamble) {
        this.gistOfPreamble = gistOfPreamble;
    }

    public MultipartFile getAttachments() {
        return attachments;
    }

    public void setAttachments(MultipartFile attachments) {
        this.attachments = attachments;
    }

    public EgwStatus getStatus() {
        return status;
    }

    public void setStatus(EgwStatus status) {
        this.status = status;
    }

    public PreambleType getType() {
        return type;
    }

    public void setType(PreambleType type) {
        this.type = type;
    }

    public FileStoreMapper getFilestoreid() {
        return filestoreid;
    }

    public void setFilestoreid(FileStoreMapper filestoreid) {
        this.filestoreid = filestoreid;
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

    @Override
    public String getStateDetails() {
        return String.format("Preamble Number %s ", preambleNumber);
    }

}
