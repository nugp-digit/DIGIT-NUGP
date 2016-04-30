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
package org.egov.collection.entity;

import org.egov.commons.EgwStatus;
import org.egov.infstr.models.BaseModel;
import org.egov.infstr.models.ServiceDetails;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ReceiptHeader generated by hbm2java
 */
public class OnlinePayment extends BaseModel {
    /**
     * Serial version uid
     */
    private static final long serialVersionUID = 1L;
    private ReceiptHeader receiptHeader;
    private EgwStatus status;
    private ServiceDetails service;
    private String transactionNumber;
    private BigDecimal transactionAmount;
    private Date transactionDate;
    private String authorisationStatusCode;
    private String remarks;

    public OnlinePayment() {
    }

    /**
     * @return the receiptHeader
     */
    public ReceiptHeader getReceiptHeader() {
        return receiptHeader;
    }

    /**
     * @param receiptHeader the receiptHeader to set
     */
    public void setReceiptHeader(final ReceiptHeader receiptHeader) {
        this.receiptHeader = receiptHeader;
    }

    /**
     * @return the egwStatus
     */
    public EgwStatus getStatus() {
        return status;
    }

    /**
     * @param egwStatus the egwStatus to set
     */
    public void setStatus(final EgwStatus status) {
        this.status = status;
    }

    /**
     * @return the service
     */
    public ServiceDetails getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(final ServiceDetails service) {
        this.service = service;
    }

    /**
     * @return the transactionNumber
     */
    public String getTransactionNumber() {
        return transactionNumber;
    }

    /**
     * @param transactionNumber the transactionNumber to set
     */
    public void setTransactionNumber(final String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    /**
     * @return the transactionAmount
     */
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * @param transactionAmount the transactionAmount to set
     */
    public void setTransactionAmount(final BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * @return the transactionDate
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate the transactionDate to set
     */
    public void setTransactionDate(final Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getAuthorisationStatusCode() {
        return authorisationStatusCode;
    }

    public void setAuthorisationStatusCode(final String authorisationStatusCode) {
        this.authorisationStatusCode = authorisationStatusCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

}
