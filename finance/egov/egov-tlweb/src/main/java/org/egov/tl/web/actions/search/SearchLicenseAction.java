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

package org.egov.tl.web.actions.search;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.tl.entity.TradeLicense;
import org.egov.tl.service.BaseLicenseService;
import org.egov.tl.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.validator.annotations.Validations;

@ParentPackage("egov")
@Validations
@Results({
    @Result(name = SearchLicenseAction.COMMON_FORM, location = "searchLicense-commonForm.jsp"),
    @Result(name = SearchLicenseAction.CANCEL_LICENSE, type = "redirectAction", location = "cancelLicense-newForm", params = {
            "namespace", "/cancellation", "licenseId", "${licenseId}" })
})
public class SearchLicenseAction extends BaseFormAction {

    /**
     *
     */
    private static final long serialVersionUID = 2620387601260939372L;
    protected static final String COMMON_FORM = "commonForm";
    private String mode;
    private String licenseNumber;
    private Long licenseId;
    @Autowired
    private BaseLicenseService baseLicenseService;
    public static final String CANCEL_LICENSE = "Cancel License";

    @Override
    public Object getModel() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Generalised method to give search license screen to perform different transactions like cancel, Objection, Suspension,
     * Renewal Notice etc
     * @return
     */
    @SkipValidation
    @Action(value = "/search/searchLicense-commonForm")
    public String commonForm() {
        return COMMON_FORM;
    }

    /**
     * Generalised method to redirect the form page to different transactional form pages
     * @return
     */
    @ValidationErrorPage(value = COMMON_FORM)
    @Action(value = "/search/searchLicense-commonSearch")
    public String commonSearch() {
        final TradeLicense tradeLicense = baseLicenseService.getTradeLicenseByLicenseNum(licenseNumber);
        if (tradeLicense == null) {
            addActionError(getText("validation.license.doesnot.exists"));
            return COMMON_FORM;
        }
        if (CANCEL_LICENSE.equals(mode))
            if (tradeLicense.getStatus() != null &&
            (tradeLicense.getStatus().getStatusCode().equalsIgnoreCase(Constants.STATUS_CANCELLED) ||
                    tradeLicense.getStatus().getStatusCode().equalsIgnoreCase(Constants.STATUS_UNDERWORKFLOW))) {
                addActionError(getText("validation.cannotPerform.licenseCancel"));
                return COMMON_FORM;
            }
        licenseId = tradeLicense.getId();
        return mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(final String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(final Long licenseId) {
        this.licenseId = licenseId;
    }

}
