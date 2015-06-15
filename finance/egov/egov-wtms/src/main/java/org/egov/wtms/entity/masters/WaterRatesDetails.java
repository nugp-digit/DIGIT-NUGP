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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractPersistable;

@Entity
@Table(name = "egwtr_water_rates_details")
@SequenceGenerator(name = WaterRatesDetails.SEQ_WATERRATESDETAILS, sequenceName = WaterRatesDetails.SEQ_WATERRATESDETAILS, allocationSize = 1)
public class WaterRatesDetails extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -8237417567777811811L;
    public static final String SEQ_WATERRATESDETAILS = "SEQ_EGWTR_WATER_RATES_DETAILS";

    @Id
    @GeneratedValue(generator = SEQ_WATERRATESDETAILS, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "water_rates_header_id", nullable = false)
    private WaterRatesHeader waterRatesHeader;

    @Column(name = "starting_units")
    private Long startingUnits;

    @Column(name = "ending_units")
    private Long endingUnits;

    private Long quantity;

    @Column(name = "unit_rate")
    private double unitRate;

    @Column(name = "min_rate")
    private double minimumRate;

    @Column(name = "rate_per_month")
    private double monthlyRate;

    @NotNull
    @Temporal(value = TemporalType.DATE)
    @Column(name = "from_date")
    private Date fromDate;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "to_date")
    private Date toDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public WaterRatesHeader getWaterRatesHeader() {
        return waterRatesHeader;
    }

    public void setWaterRatesHeader(final WaterRatesHeader waterRatesHeader) {
        this.waterRatesHeader = waterRatesHeader;
    }

    public Long getStartingUnits() {
        return startingUnits;
    }

    public void setStartingUnits(final Long startingUnits) {
        this.startingUnits = startingUnits;
    }

    public Long getEndingUnits() {
        return endingUnits;
    }

    public void setEndingUnits(final Long endingUnits) {
        this.endingUnits = endingUnits;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(final Long quantity) {
        this.quantity = quantity;
    }

    public double getUnitRate() {
        return unitRate;
    }

    public void setUnitRate(final double unitRate) {
        this.unitRate = unitRate;
    }

    public double getMinimumRate() {
        return minimumRate;
    }

    public void setMinimumRate(final double minimumRate) {
        this.minimumRate = minimumRate;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(final double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

}