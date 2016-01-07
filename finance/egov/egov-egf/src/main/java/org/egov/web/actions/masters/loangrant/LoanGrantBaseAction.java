/*******************************************************************************
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
 * 	1) All versions of this program, verbatim or modified must carry this
 * 	   Legal Notice.
 *
 * 	2) Any misrepresentation of the origin of the material is prohibited. It
 * 	   is required that all modified versions of this material be marked in
 * 	   reasonable ways as different from the original version.
 *
 * 	3) This license does not grant any rights to any user of the program
 * 	   with regards to rights under trademark law for use of the trade names
 * 	   or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.web.actions.masters.loangrant;

import java.util.Date;
import java.util.List;

import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class LoanGrantBaseAction extends BaseFormAction {
    private static final long serialVersionUID = -7332696247479705085L;

    protected Date fromDate;
    protected Date toDate;
    protected Integer subSchemeId;
    protected Integer schemeId;
    protected Integer fundId;
    protected @Autowired AppConfigValueService appConfigValuesService;
    private Integer defaultFundId;
    EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();

    @Override
    public void prepare()
    {
        super.prepare();
        // if Fundid is present then it is defaulted else it is made mandatory to select
        final List<AppConfigValues> appList = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                "loangrant.default.fundid");
        final String fundId = appList.get(0).getValue();
        if (fundId != null && !fundId.isEmpty())
            defaultFundId = Integer.parseInt(fundId);
        addDropdownData("fundList", masterCache.get("egi-fund"));

    }

    @Override
    public Object getModel() {

        return null;
    }

    public Integer getDefaultFundId() {
        return defaultFundId;
    }

    public void setDefaultFundId(final Integer defaultFundId) {
        this.defaultFundId = defaultFundId;
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

    public Integer getSubSchemeId() {
        return subSchemeId;
    }

    public void setSubSchemeId(final Integer subSchemeId) {
        this.subSchemeId = subSchemeId;
    }

    public Integer getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(final Integer schemeId) {
        this.schemeId = schemeId;
    }

    public Integer getFundId() {
        return fundId;
    }

    public void setFundId(final Integer fundId) {
        this.fundId = fundId;
    }

}
