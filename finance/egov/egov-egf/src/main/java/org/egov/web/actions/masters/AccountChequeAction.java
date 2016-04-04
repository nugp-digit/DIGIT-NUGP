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
/**
 *
 */
package org.egov.web.actions.masters;



import org.egov.infstr.services.PersistenceService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.egf.commons.EgovCommon;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.cheque.AccountCheques;
import org.egov.model.cheque.ChequeDeptMapping;
import org.egov.model.masters.ChequeDetail;
import org.egov.services.cheque.AccountChequesService;
import org.egov.utils.Constants;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


@Results({
        @Result(name = "new", location = "accountCheque-new.jsp"),
        @Result(name = "manipulateCheques", location = "accountCheque-manipulateCheques.jsp")
})
public class AccountChequeAction extends BaseFormAction {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AccountChequeAction.class);
    private AccountCheques accountCheques = new AccountCheques();
    private List<ChequeDeptMapping> chequeList;
    private Bankaccount bankaccount;
    private List<ChequeDetail> chequeDetailsList;
   
 @Autowired
 @Qualifier("persistenceService")
 private PersistenceService persistenceService;
 @Autowired
    @Qualifier("accountChequesService")
    private AccountChequesService accountChequesService;
    
    @Autowired
    private EgovMasterDataCaching masterDataCache;
    
    @Autowired
    private FinancialYearDAO financialYearDAO;
    
    private String deletedChqDeptId;

    public AccountChequeAction() {

        addRelatedEntity("bankAccountId", Bankaccount.class);
    }

    @Override
    public Object getModel() {
        return accountCheques;
    }

    @Override
    public void prepare() {
        super.prepare();
        addDropdownData("departmentList", masterDataCache.get("egi-department"));
        addDropdownData("financialYearList", financialYearDAO.getAllActiveFinancialYearList());
    }

    @Action(value = "/masters/accountCheque-newform")
    public String newform() {
        addDropdownData("bankList", Collections.EMPTY_LIST);
        addDropdownData("accNumList", Collections.EMPTY_LIST);
        addDropdownData("fundList", masterDataCache.get("egi-fund"));
        return "new";

    }

    @ValidationErrorPage(value = "manipulateCheques")
    @SuppressWarnings("unchecked")
    @Action(value = "/masters/accountCheque-manipulateCheques")
    public String manipulateCheques() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("AccountChequeAction | manipulateCheques | Start");
        final Long bankAccId = Long.valueOf(parameters.get("bankAccId")[0]);
        bankaccount = (Bankaccount) persistenceService.find("from Bankaccount where id = " + bankAccId);

        // Get cheque leafs presents for this particular account number
        final StringBuffer query = new StringBuffer(200);
        query.append("select cd from ChequeDeptMapping cd where accountCheque.bankAccountId.id =?");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("AccountChequeAction | manipulateCheques | query = " + query.toString());
        chequeList = persistenceService.findAllBy(query.toString(), bankAccId);
        if (chequeList.size() > 0)
            prepareChequeDetails(chequeList);
        return "manipulateCheques";
    }

    private void prepareChequeDetails(final List<ChequeDeptMapping> chequeList) {

        chequeDetailsList = new ArrayList<ChequeDetail>();
        ChequeDetail chequeDetail;
        for (final ChequeDeptMapping chequeDeptMapping : chequeList) {

            chequeDetail = new ChequeDetail();
            chequeDetail.setFromChqNo(chequeDeptMapping.getAccountCheque().getFromChequeNumber());
            chequeDetail.setToChqNo(chequeDeptMapping.getAccountCheque().getToChequeNumber());
            chequeDetail.setDeptName(chequeDeptMapping.getAllotedTo().getName());
            chequeDetail.setDeptId(chequeDeptMapping.getAllotedTo().getId().intValue());
            CFinancialYear fy = (CFinancialYear) financialYearDAO.findById(Long.valueOf(chequeDeptMapping.getAccountCheque().getSerialNo()), false);
            chequeDetail.setSerialNoH(fy.getFinYearRange());
            chequeDetail
                    .setReceivedDate(Constants.DDMMYYYYFORMAT2.format(chequeDeptMapping.getAccountCheque().getReceivedDate()));
            chequeDetail.setSerialNo(chequeDeptMapping.getAccountCheque().getSerialNo().toString());
            if (null != chequeDeptMapping.getAccountCheque().getIsExhausted()
                    && chequeDeptMapping.getAccountCheque().getIsExhausted())
                chequeDetail.setIsExhusted("Yes");
            else
                chequeDetail.setIsExhusted("No");

            chequeDetail.setNextChqPresent(chequeDeptMapping.getAccountCheque().getNextChqNo() != null ? "Yes" : "No");
            chequeDetail.setAccountChequeId(chequeDeptMapping.getAccountCheque().getId());
            chequeDetail.setChequeDeptId(chequeDeptMapping.getId());
            chequeDetailsList.add(chequeDetail);
        }

    }

    @ValidationErrorPage(value = "manipulateCheques")
    @SuppressWarnings("unchecked")
    @Action(value = "/masters/accountCheque-save")
    public String save() {

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("AccountChequeAction | save | Start");
        final Session session = persistenceService.getSession();
        final Map<String, AccountCheques> chequeMap = new HashMap<String, AccountCheques>();
        final Map<String, String> chequeIdMap = new HashMap<String, String>();
        AccountCheques accountCheques;
        ChequeDeptMapping chqDept;
        removeEmptyRows();
        bankaccount = (Bankaccount) persistenceService.find("from Bankaccount where id ="
                + Long.valueOf(parameters.get("bankAccId")[0]));
        if (null == chequeDetailsList) {
            accountChequesService.deleteRecords(deletedChqDeptId, bankaccount);
            addActionMessage("Cheque Master deleted Successfully : No cheque leafs available");
            return "manipulateCheques";
        }
        accountChequesService.createCheques(chequeDetailsList, chequeIdMap, chequeMap, bankaccount, deletedChqDeptId);
        accountChequesService.deleteRecords(deletedChqDeptId, bankaccount);
        // Get cheque leafs presents for this particular account number
        final StringBuffer query = new StringBuffer(200);
        query.append("select cd from ChequeDeptMapping cd where accountCheque.bankAccountId.id =? ");
        chequeList = persistenceService.findAllBy(query.toString(), bankaccount.getId());
        if (chequeList.size() > 0)
            prepareChequeDetails(chequeList);
        addActionMessage("Cheque Master updated Successfully");
        return "manipulateCheques";
    }

    private void removeEmptyRows() {
        final List<ChequeDetail> trash = new ArrayList<ChequeDetail>();
        if (chequeDetailsList != null)
            for (final ChequeDetail cd : chequeDetailsList)
                if (cd == null)
                    trash.add(cd);

        for (final ChequeDetail cd : trash)
            chequeDetailsList.remove(cd);
        trash.clear();
    }

    public AccountCheques getAccountCheques() {
        return accountCheques;
    }

    public void setAccountCheques(final AccountCheques accountCheques) {
        this.accountCheques = accountCheques;
    }

    public void setEgovCommon(final EgovCommon egovCommon) {
    }

    public List<ChequeDeptMapping> getChequeList() {
        return chequeList;
    }

    public void setChequeList(final List<ChequeDeptMapping> chequeList) {
        this.chequeList = chequeList;
    }

    public Bankaccount getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(final Bankaccount bankaccount) {
        this.bankaccount = bankaccount;
    }

    public List<ChequeDetail> getChequeDetailsList() {
        return chequeDetailsList;
    }

    public void setChequeDetailsList(final List<ChequeDetail> chequeDetailsList) {
        this.chequeDetailsList = chequeDetailsList;
    }

    public String getDeletedChqDeptId() {
        return deletedChqDeptId;
    }

    public void setDeletedChqDeptId(final String deletedChqDeptId) {
        this.deletedChqDeptId = deletedChqDeptId;
    }

}
