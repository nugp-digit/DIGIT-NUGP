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
//Source file: D:\\SUSHMA\\PROJECTS\\E-GOV\\ENGINEDESIGN\\com\\exilant\\GLEngine\\ChartOfAccounts.java
package com.exilant.GLEngine;

//import com.exilant.eGov.src.domain.GeneralLedger;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.dao.budget.BudgetDetailsHibernateDAO;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetDetail;
import org.egov.services.budget.BudgetService;
import org.egov.utils.Constants;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.eGov.src.domain.ClosedPeriods;
import com.exilant.eGov.src.domain.EgRemittanceGldtl;
import com.exilant.eGov.src.domain.GeneralLedger;
import com.exilant.eGov.src.domain.GeneralLedgerDetail;
import com.exilant.eGov.src.transactions.ExilPrecision;
import com.exilant.exility.common.DataCollection;
import com.exilant.exility.common.TaskFailedException;
import com.exilant.exility.dataservice.DataExtractor;

/**
 * This Singleton class contains all the account codes for the organization
 */
/**
 * @@org.jboss.cache.aop.InstanceOfAopMarker
 */
public class ChartOfAccounts {
    static ChartOfAccounts singletonInstance;
    private static final Logger LOGGER = Logger.getLogger(ChartOfAccounts.class);

    private static final String ROOTNODE = "/COA";
    private static final String GLACCCODENODE = "GlAccountCodes";
    private static final String GLACCIDNODE = "GlAccountIds";
    private static final String ACCOUNTDETAILTYPENODE = "AccountDetailType";
    private static final String EXP = "Exp=";
    private static final String EXILRPERROR = "exilRPError";
    private static PersistenceService<CChartOfAccountDetail, Integer> chartOfAccountDetailService;
    private PersistenceService<CVoucherHeader, Long> voucherHeaderPersitService;
    private List<Object[]> resultset;

    private static Cache<Object, Object> cache;
    @Autowired
    private BudgetDetailsHibernateDAO budgetDetailsDAO;
    static
    {
        singletonInstance = new ChartOfAccounts();
        try
        {
            // TODO Commenting reading cache from infinispan temporarily and building cachemanager through code
            // cache=(TreeCacheMBean)MBeanProxyExt.create(TreeCacheMBean.class, "jboss.cache:service=TreeCache", server);
                LOGGER.debug("Inside static method ");
            EgovMasterDataCaching.getInstance();
            LOGGER.debug("Got EgovMasterDataCaching ");
            final EmbeddedCacheManager manager = EgovMasterDataCaching.getCACHE_MANAGER();
            manager.defineConfiguration("chartofaccounts-cache", new ConfigurationBuilder().build());

            cache = manager.getCache("chartofaccounts-cache");
                LOGGER.debug("Cache = " + cache);
            if (cache == null)
                loadAccountData();
                LOGGER.debug("loadAccountData is done");
        } catch (final Exception e)
        {
                LOGGER.debug("Exception in static method");
            LOGGER.error(EXP + e.getMessage(), e);
            throw new ApplicationRuntimeException(e.getMessage());
        }
    }

    public ChartOfAccounts() {

    }

    public static ChartOfAccounts getInstance() throws TaskFailedException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("getInstancw called");
        if (getGlAccountCodes() == null || getGlAccountIds() == null || getAccountDetailType() == null) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("getInstancw called");
            loadAccountData();
        }
        return singletonInstance;
    }

    public void reLoadAccountData() throws TaskFailedException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("reLoadAccountData called");
        /*
         * 1.Loads all the account codes and details of that as GLAccount objects in theGLAccountCode,theGLAccountId HashMap's
         */
        if (getGlAccountCodes() != null)
            getGlAccountCodes().clear();
        if (getGlAccountIds() != null)
            getGlAccountIds().clear();
        if (getAccountDetailType() != null)
            getAccountDetailType().clear();

        // Temporary place holders
        final HashMap glAccountCodes = new HashMap();
        final HashMap glAccountIds = new HashMap();
        final HashMap accountDetailType = new HashMap();
        DataExtractor.getExtractor();
        String sql = "select id as \"id\",name as  \"name\",tableName as \"tableName\"," +
                "description as \"description\",columnName as \"columnName\",attributeName as \"attributeName\"" +
                ",nbrOfLevels as  \"nbrOfLevels\" from accountDetailType";
        final Session currentSession = HibernateUtil.getCurrentSession();
        SQLQuery createSQLQuery = currentSession.createSQLQuery(sql);
        createSQLQuery
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("name")
                .addScalar("tableName")
                .addScalar("description")
                .addScalar("columnName")
                .addScalar("attributeName")
                .setResultTransformer(Transformers.aliasToBean(AccountDetailType.class));
        List<AccountDetailType> accountDetailTypeList = new ArrayList<AccountDetailType>();
        List<GLAccount> glAccountCodesList = new ArrayList<GLAccount>();
        new ArrayList<GLAccount>();

        accountDetailTypeList = createSQLQuery.list();
        for (final AccountDetailType type : accountDetailTypeList)
            accountDetailType.put(type.getAttributeName(), type);
        sql = "select ID as \"ID\", glCode as \"glCode\" ,name as \"name\" ," +
                "isActiveForPosting as \"isActiveForPosting\"  from chartofaccounts ";
        createSQLQuery = currentSession.createSQLQuery(sql);
        createSQLQuery
                .addScalar("ID", IntegerType.INSTANCE)
                .addScalar("glCode")
                .addScalar("name")
                .addScalar("isActiveForPosting", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(GLAccount.class));

        glAccountCodesList = createSQLQuery.list();
        for (final GLAccount type : glAccountCodesList)
            glAccountCodes.put(type.getCode(), type);
        for (final GLAccount type : glAccountCodesList)
            glAccountIds.put(type.getId(), type);

        loadParameters(glAccountCodes, glAccountIds);
        try
        {
            final HashMap<String, HashMap> hm = new HashMap<String, HashMap>();
            hm.put(ACCOUNTDETAILTYPENODE, accountDetailType);
            hm.put(GLACCCODENODE, glAccountCodes);
            hm.put(GLACCIDNODE, glAccountIds);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("ReLoading size:" + glAccountCodes.size());
            // cache.put(ROOTNODE+"/"+FilterName.get(),ACCOUNTDETAILTYPENODE,accountDetailType);
            // cache.put(ROOTNODE+"/"+FilterName.get(),gLAccCodeNode,glAccountCodes);
            // cache.put(ROOTNODE+"/"+FilterName.get(),GLACCIDNODE,glAccountIds);
            cache.put(ROOTNODE + "/" + EgovThreadLocals.getDomainName(), hm);
        } catch (final Exception e)
        {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug(EXP + e.getMessage(), e);
            throw new TaskFailedException();
        }
    }

    static void loadAccountData() throws TaskFailedException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("loadAccountData called");
        /*
         * 1.Loads all the account codes and details of that as GLAccount objects in theGLAccountCode,theGLAccountId HashMap's
         */
        if (getGlAccountCodes() != null)
            getGlAccountCodes().clear();
        if (getGlAccountIds() != null)
            getGlAccountIds().clear();
        if (getAccountDetailType() != null)
            getAccountDetailType().clear();
        // Temporary place holders
        final HashMap glAccountCodes = new HashMap();
        final HashMap glAccountIds = new HashMap();
        final HashMap accountDetailType = new HashMap();

        DataExtractor.getExtractor();

        String sql = "select id as \"id\",name as  \"name\",tableName as \"tableName\"," +
                "description as \"description\",columnName as \"columnName\",attributeName as \"attributeName\"" +
                ",nbrOfLevels as  \"nbrOfLevels\" from AccountDetailType";

        final Session currentSession = HibernateUtil.getCurrentSession();
        SQLQuery createSQLQuery = currentSession.createSQLQuery(sql);
        createSQLQuery
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("name")
                .addScalar("tableName")
                .addScalar("description")
                .addScalar("columnName")
                .addScalar("attributeName")
                .setResultTransformer(Transformers.aliasToBean(AccountDetailType.class));
        List<AccountDetailType> accountDetailTypeList = new ArrayList<AccountDetailType>();
        List<GLAccount> glAccountCodesList = new ArrayList<GLAccount>();
        new ArrayList<GLAccount>();

        accountDetailTypeList = createSQLQuery.list();
        for (final AccountDetailType type : accountDetailTypeList)
            accountDetailType.put(type.getAttributeName(), type);
        sql = "select ID as \"ID\", glCode as \"glCode\" ,name as \"name\" ," +
                "isActiveForPosting as \"isActiveForPosting\"  from chartofaccounts ";
        createSQLQuery = currentSession.createSQLQuery(sql);
        createSQLQuery
                .addScalar("ID", IntegerType.INSTANCE)
                .addScalar("glCode")
                .addScalar("name")
                .addScalar("isActiveForPosting", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(GLAccount.class));

        glAccountCodesList = createSQLQuery.list();
        for (final GLAccount type : glAccountCodesList)
            glAccountCodes.put(type.getCode(), type);
        for (final GLAccount type : glAccountCodesList)
            glAccountIds.put(type.getId(), type);
        loadParameters(glAccountCodes, glAccountIds);
        try
        {
            final HashMap<String, HashMap> hm = new HashMap<String, HashMap>();
            hm.put(ACCOUNTDETAILTYPENODE, accountDetailType);
            hm.put(GLACCCODENODE, glAccountCodes);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Loading size:" + glAccountCodes.size());
            hm.put(GLACCIDNODE, glAccountIds);
            // cache.put(ROOTNODE+"/"+FilterName.get(),ACCOUNTDETAILTYPENODE,accountDetailType);
            // cache.put(ROOTNODE+"/"+FilterName.get(),GLACCCODENODE,glAccountCodes);
            // cache.put(ROOTNODE+"/"+FilterName.get(),GLACCIDNODE,glAccountIds);
            cache.put(ROOTNODE + "/" + EgovThreadLocals.getDomainName(), hm);
        } catch (final Exception e)
        {
            LOGGER.error(EXP + e.getMessage(), e);
            throw new TaskFailedException();

        }
    }

    // private static synchronized void loadParameters(HashMap glAccountCodes, HashMap glAccountIds)throws TaskFailedException{
    // if(LOGGER.isInfoEnabled()) LOGGER.info("loadParameters called");
    // Iterator it=glAccountCodes.keySet().iterator();
    // String sql="";
    // DataExtractor de=DataExtractor.getExtractor();
    // ArrayList reqParam;
    // while(it.hasNext()){
    // String obj=(String)it.next();
    //
    // GLAccount glAccCode=(GLAccount)glAccountCodes.get(obj);
    // GLAccount glAccId=(GLAccount)glAccountIds.get(String.valueOf(glAccCode.getId()));
    // sql="select  b.id as \"detailId\" , b.attributename as \"detailName\"" +
    // " from " +
    // "chartofaccountdetail a,accountDetailType b " +
    // "where  b.id=a.detailtypeid   and glcodeid='"+glAccCode.getId()+"'";
    // reqParam=new ArrayList();
    // reqParam=de.extractIntoList(sql,GLParameter.class);
    // glAccCode.setGLParameters(reqParam);
    // glAccId.setGLParameters(reqParam);
    // }
    // }

    private static synchronized void loadParameters(final HashMap glAccountCodes, final HashMap glAccountIds)
            throws TaskFailedException {
        final List<CChartOfAccountDetail> chList = chartOfAccountDetailService.findAllBy("from CChartOfAccountDetail");
        for (final CChartOfAccountDetail chartOfAccountDetail : chList) {
            final GLParameter parameter = new GLParameter();
            parameter.setDetailId(chartOfAccountDetail.getDetailTypeId().getId());
            parameter.setDetailName(chartOfAccountDetail.getDetailTypeId().getAttributename());
            final GLAccount glAccCode = getGlAccCode(chartOfAccountDetail.getGlCodeId(), glAccountCodes);
            final GLAccount glAccId = getGlAccId(chartOfAccountDetail.getGlCodeId(), glAccountIds);
            if (glAccCode != null && glAccCode.getGLParameters() != null)
                glAccCode.getGLParameters().add(parameter);
            if (glAccId != null && glAccId.getGLParameters() != null)
                glAccId.getGLParameters().add(parameter);
        }
    }

    private static Integer getIntegerValue(final Object[] element) {
        return element != null ? Integer.parseInt(element[0].toString()) : null;
    }

    private static Long getLongValue(final Object[] element) {
        return element != null ? Long.valueOf(element[0].toString()) : null;
    }

    private static GLAccount getGlAccCode(final CChartOfAccounts glCodeId, final Map glAccountCodes) {
        for (final Object key : glAccountCodes.keySet())
            if (((String) key).equalsIgnoreCase(glCodeId.getGlcode()))
                return (GLAccount) glAccountCodes.get(key);
        return null;
    }

    private static GLAccount getGlAccId(final CChartOfAccounts glCodeId, final Map glAccountIds) {
        for (final Object key : glAccountIds.keySet())
            if (key.toString().equalsIgnoreCase(glCodeId.getId().toString()))
                return (GLAccount) glAccountIds.get(key);
        return null;
    }

    private boolean validateGLCode(final Transaxtion txn, final DataCollection dc) throws TaskFailedException {
        // validate each gl code
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Inside the ValidateGLCode2");
        final HashMap hm = getGlAccountCodes();
        // if(LOGGER.isInfoEnabled()) LOGGER.info("HashMap value is :"+hm);
        if (hm == null)
        {
            LOGGER.error("Account Codes not initialized");
            dc.addMessage("Account Codes not initialized", txn.getGlCode() + " For " + txn.getGlName());
            return false;
        }

        if (hm.get(txn.getGlCode()) == null)
        {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("looking for:" + txn.getGlCode() + ":");
            final Iterator itr = hm.keySet().iterator();
            while (itr.hasNext())
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("GLCode:" + (String) itr.next() + ":");

        }
        final Object obj = hm.get(txn.getGlCode());
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Class Name:" + obj.getClass());
        final GLAccount glAcc = (GLAccount) hm.get(txn.getGlCode());
        if (glAcc == null) {
            dc.addMessage("exilInvalidCode", txn.getGlCode() + " For " + txn.getGlName());
            return false;
        }
        txn.setGlName(glAcc.getName());
        if (LOGGER.isInfoEnabled())
            LOGGER.info(txn.getGlCode() + " is activefor posting :" + glAcc.isActiveForPosting());
        if (!glAcc.isActiveForPosting()) {
            dc.addMessage("exilInActiveAccount", txn.getGlCode() + " For " + txn.glName);
            return false;
        }
        // this can be avoided
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Classification....in   :" + getClassificationForCode(txn.getGlCode()));
        if (getClassificationForCode(txn.getGlCode()) != 4) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("classification is not detailed code");
            dc.addMessage("exilNotDetailAccount", txn.getGlCode());
            return false;
        }

        if (Double.parseDouble(txn.getDrAmount()) > 0
                && Double.parseDouble(txn.getCrAmount()) > 0) {
            dc.addMessage("exilInvalidTrxn");
            return false;
        }
        if (!isRequiredPresent(txn, glAcc, dc))
            // dc.addMessage("exilDataInsufficient");
            return false;
        // return checkAllMasters(dc,con);
        return true;
    }

    public boolean validateGLCode(final Transaxtion txn) throws Exception {
        // validate each gl code
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Inside the ValidateGLCode1");
        final GLAccount glAcc = (GLAccount) getGlAccountCodes().get(txn.getGlCode());
        if (glAcc == null) {
            LOGGER.error("GLCode is null");
            return false;
        }
        txn.setGlName(glAcc.getName());
        if (LOGGER.isInfoEnabled())
            LOGGER.info(txn.getGlCode() + " is activefor posting :" + glAcc.isActiveForPosting());
        if (!glAcc.isActiveForPosting())
            return false;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Classification....:" + getClassificationForCode(txn.getGlCode()));
        if (getClassificationForCode(txn.getGlCode()) != 4) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("classification is not detailed code");
            throw new TaskFailedException("Cannot post to " + txn.getGlCode());
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Going to check the Amount.Debit: " + txn.getDrAmount() + " ** Credit :" + txn.getCrAmount());
        if (Double.parseDouble(txn.getDrAmount()) > 0
                && Double.parseDouble(txn.getCrAmount()) > 0)
            throw new TaskFailedException("Both Debit and Credit cannot be greater than Zero.");
        // return false;
        if (!isRequiredPresent(txn, glAcc))
            return false;
        return true;
    }

    /**
     * This function is to get the classification of any glcode provided
     * @param glcode
     * @param con
     * @return
     * @throws TaskFailedException
     */
    private int getClassificationForCode(final String glcode) throws TaskFailedException
    {
        Short retVal = 0;
        List<Short> rs = null;
        Query pstmt = null;
        try {
            final String query = "select classification from chartofaccounts where glcode= '" + glcode + "'";
            pstmt = HibernateUtil.getCurrentSession().createSQLQuery(query);
            rs = pstmt.list();
            if (rs != null && rs.size() > 0)
                retVal = rs != null ? rs.get(0) : 0;
        } catch (final Exception e)
        {
            LOGGER.error(EXP + e.getMessage(), e);
            throw new TaskFailedException();
        }
        return retVal.intValue();
    }

    private boolean isRequiredPresent(final Transaxtion txn, final GLAccount glAcc, final DataCollection dc)
            throws TaskFailedException {
        int requiredCount = 0;
        int foundCount = 0;
        final ArrayList glParamList = glAcc.getGLParameters();
        for (int i = 0; i < glParamList.size(); i++) {
            final GLParameter glPrm = (GLParameter) glParamList.get(i);
            requiredCount++;
            /*
             * if(!glPrm.getDetailKey().equalsIgnoreCase("0")&&glPrm.getDetailKey().length()>0){ foundCount++; continue; }
             */
            for (int j = 0; j < txn.transaxtionParameters.size(); j++) {
                final TransaxtionParameter txnPrm = (TransaxtionParameter) txn.transaxtionParameters.get(j);
                // if(LOGGER.isInfoEnabled()) LOGGER.info(glAcc.getCode()+" "+txnPrm.getDetailName()+" "+txnPrm.getDetailKey());
                if (txnPrm.getDetailName().equalsIgnoreCase(glPrm.getDetailName())) {
                    final int id = glPrm.getDetailId();
                    // validates the master keys here
                    final RequiredValidator rv = new RequiredValidator();
                    if (rv.validateKey(id, txnPrm.getDetailKey()))
                        foundCount++;
                    else {
                        dc.addMessage("exilWrongData", txnPrm.getDetailName());
                        return false;
                    }
                }
            }
        }
        if (foundCount < requiredCount) {
            dc.addMessage("exilDataInsufficient");
            return false;
        }
        return true;
    }

    private boolean isRequiredPresent(final Transaxtion txn, final GLAccount glAcc) throws Exception {
        int requiredCount = 0;
        int foundCount = 0;
        final ArrayList glParamList = glAcc.getGLParameters();
        for (int i = 0; i < glParamList.size(); i++) {
            final GLParameter glPrm = (GLParameter) glParamList.get(i);
            final TransaxtionParameter txnPrm1 = (TransaxtionParameter) txn.transaxtionParameters.get(0);
            if (glPrm.getDetailId() == Integer.parseInt(txnPrm1.getDetailTypeId()))
                requiredCount++;
            /*
             * if(!glPrm.getDetailKey().equalsIgnoreCase("0")&&glPrm.getDetailKey().length()>0){ foundCount++; continue; }
             */
            for (int j = 0; j < txn.transaxtionParameters.size(); j++) {
                final TransaxtionParameter txnPrm = (TransaxtionParameter) txn.transaxtionParameters.get(j);
                // if(LOGGER.isInfoEnabled()) LOGGER.info(glAcc.getCode()+" "+txnPrm.getDetailName()+" "+txnPrm.getDetailKey());
                if (txnPrm.getDetailName().equalsIgnoreCase(glPrm.getDetailName())) {
                    final int id = glPrm.getDetailId();
                    // validates the master keys here
                    final RequiredValidator rv = new RequiredValidator();
                    if (rv.validateKey(id, txnPrm.getDetailKey()))
                        foundCount++;
                    else
                        return false;
                }
            }
        }
        if (foundCount < requiredCount)
            return false;
        return true;
    }

    private boolean validateTxns(final Transaxtion txnList[], final DataCollection dc) throws TaskFailedException {
        // validate the array list for the total number of txns
        if (txnList.length < 2) {
            dc.addMessage("exilWrongTrxn");
            return false;
        }
        double dbAmt = 0;
        double crAmt = 0;
        try {
            for (final Transaxtion element : txnList) {
                final Transaxtion txn = element;
                if (!validateGLCode(txn, dc))
                    return false;
                dbAmt += Double.parseDouble(txn.getDrAmount());
                crAmt += Double.parseDouble(txn.getCrAmount());
            }
        } catch (final Exception e) {
            dc.addMessage(EXILRPERROR, e.toString());
            LOGGER.error(e.getMessage(), e);
            throw new TaskFailedException();
        }
        dbAmt = ExilPrecision.convertToDouble(dbAmt, 2);
        crAmt = ExilPrecision.convertToDouble(crAmt, 2);
        if (dbAmt != crAmt) {
            dc.addMessage("exilAmountMismatch");
            return false;
        }
        return true;
    }

    private boolean validateTxns(final Transaxtion txnList[]) throws Exception {
        // validate the array list for the total number of txns
        if (txnList.length < 2)
            return false;
        double dbAmt = 0;
        double crAmt = 0;
        try {
            for (final Transaxtion element : txnList) {
                final Transaxtion txn = element;
                if (!validateGLCode(txn))
                    return false;
                dbAmt += Double.parseDouble(txn.getDrAmount());
                crAmt += Double.parseDouble(txn.getCrAmount());
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage());

            return false;
        } finally {
            RequiredValidator.clearEmployeeMap();
        }
        dbAmt = ExilPrecision.convertToDouble(dbAmt, 2);
        crAmt = ExilPrecision.convertToDouble(crAmt, 2);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Total Checking.....Debit total is :" + dbAmt + "  Credit total is :" + crAmt);
        if (dbAmt != crAmt)
            throw new TaskFailedException("Total debit and credit not matching. Total debit amount is: " + dbAmt
                    + " Total credit amount is :" + crAmt);
        // return false;
        return true;
    }

    public boolean postTransaxtions(final Transaxtion txnList[], final DataCollection dc) throws Exception, TaskFailedException,
            ParseException, SQLException, ApplicationException, ValidationException
    {
        if (!checkBudget(txnList))
            throw new TaskFailedException("Budgetary check is failed");
        // if objects are lost load them
        if (getGlAccountCodes() == null || getGlAccountIds() == null || getAccountDetailType() == null ||
                getGlAccountCodes().size() == 0 || getGlAccountIds().size() == 0 || getAccountDetailType().size() == 0)
            reLoadAccountData();
        try {
            Date dt = new Date();
            final String vdt = dc.getValue("voucherHeader_voucherDate");
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Constants.LOCALE);
            final SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATEFORMAT, Constants.LOCALE);
            dt = sdf.parse(vdt);
            final String dateformat = formatter.format(dt);
            if (!validPeriod(dateformat)) {
                dc.addMessage("exilPostingPeriodError");
                return false;
            }
            if (!validateTxns(txnList, dc))
                return false;
        } catch (final Exception e) {
            LOGGER.error("Error in post transaction", e);
            throw new TaskFailedException();
        }
        if (dc.getValue("modeOfExec").toString().equalsIgnoreCase("edit")) {
            if (!updateInGL(txnList, dc))
                return false;
        } else if (!postInGL(txnList, dc))
            return false;
        return true;
    }

    public void setBudgetDetailsDAO() {
        // This fix is for Phoenix Migration.
        budgetDetailsDAO = new BudgetDetailsHibernateDAO(BudgetDetail.class, HibernateUtil.getCurrentSession());
        // budgetDetailsDAO.setGenericDao(new GenericHibernateDaoFactory());
        if (LOGGER.isInfoEnabled())
            LOGGER.info("setting services manually .............................. ");
        final PersistenceService service = new PersistenceService();
        budgetDetailsDAO.setPersistenceService(service);
        final BudgetService budgetService = new BudgetService();
        budgetService.setType(Budget.class);
        budgetDetailsDAO.setBudgetService(budgetService);
        // budgetDetailsDAO.setAppConfigValuesService(new AppConfigValueService());
        budgetDetailsDAO.setFinancialYearDAO(new FinancialYearHibernateDAO(CFinancialYear.class, HibernateUtil
                .getCurrentSession()));

    }

    public void setScriptService()
    {
        // This fix is for Phoenix Migration.
        /*
         * ScriptService scriptService = new ScriptService(100,100,100,100); scriptService.setSessionFactory(new
         * SessionFactory()); budgetDetailsDAO.setScriptExecutionService(scriptService); SequenceGenerator sequenceGenerator = new
         * SequenceGenerator(new SessionFactory()); budgetDetailsDAO.setSequenceGenerator(sequenceGenerator);
         */
    }

    private boolean checkBudget(final Transaxtion txnList[]) throws Exception, ValidationException
    {
        Map<String, Object> paramMap = null;
        Transaxtion txnObj = null;

        CVoucherHeader voucherHeader = null;
        for (final Transaxtion element : txnList) {
            txnObj = element;
            voucherHeader = voucherHeaderPersitService.find("from CVoucherHeader where id = ?",
                    Long.valueOf(txnObj.voucherHeaderId));
            paramMap = new HashMap<String, Object>();
            if (txnObj.getDrAmount() == null || txnObj.getDrAmount().equals(""))
                paramMap.put("debitAmt", null);
            else
                paramMap.put("debitAmt", new BigDecimal(txnObj.getDrAmount() + ""));
            if (txnObj.getCrAmount() == null || txnObj.getCrAmount().equals(""))
                paramMap.put("creditAmt", null);
            else
                paramMap.put("creditAmt", new BigDecimal(txnObj.getCrAmount() + ""));
            if (voucherHeader.getFundId() != null)
                paramMap.put("fundid", voucherHeader.getFundId().getId());
            if (voucherHeader.getVouchermis().getDepartmentid() != null)
                paramMap.put("deptid", voucherHeader.getVouchermis().getDepartmentid().getId());
            if (txnObj.functionId != null && !txnObj.functionId.equals(""))
                paramMap.put("functionid", Long.valueOf(txnObj.functionId));
            if (voucherHeader.getVouchermis().getFunctionary() != null)
                paramMap.put("functionaryid", voucherHeader.getVouchermis().getFunctionary().getId());
            if (voucherHeader.getVouchermis().getSchemeid() != null)
                paramMap.put("schemeid", voucherHeader.getVouchermis().getSchemeid().getId());
            if (voucherHeader.getVouchermis().getSubschemeid() != null)
                paramMap.put("subschemeid", voucherHeader.getVouchermis().getSubschemeid().getId());
            if (voucherHeader.getVouchermis().getDivisionid() != null)
                paramMap.put("boundaryid", voucherHeader.getVouchermis().getDivisionid().getId());
            paramMap.put("glcode", txnObj.getGlCode());
            paramMap.put("asondate", voucherHeader.getVoucherDate());
            paramMap.put("mis.budgetcheckreq", voucherHeader.getVouchermis().isBudgetCheckReq());
            paramMap.put("voucherHeader", voucherHeader);
            if (txnObj.getBillId() != null)
                paramMap.put("bill", txnObj.getBillId());
            if (!budgetDetailsDAO.budgetaryCheck(paramMap))
                throw new Exception("Budgetary check is failed for " + txnObj.getGlCode());
        }
        return true;
    }

    @Transactional
    public boolean postTransaxtions(final Transaxtion txnList[], final String vDate) throws Exception, ValidationException
    {
        if (!checkBudget(txnList))
            throw new Exception("Budgetary check is failed");
        // if objects are lost load them
        if (getGlAccountCodes() == null || getGlAccountIds() == null || getAccountDetailType() == null ||
                getGlAccountCodes().size() == 0 || getGlAccountIds().size() == 0 || getAccountDetailType().size() == 0)
            reLoadAccountData();
        try
        {
            if (!validPeriod(vDate))
                throw new TaskFailedException("Voucher Date is not within an open period. Please use an open period for posting");
            if (!validateTxns(txnList))
                return false;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new TaskFailedException(e.getMessage());
        }
        if (!postInGL(txnList))
            return false;
        return true;
    }

    private void checkfuctreqd(final String glcode, final String fuctid, final DataCollection dc) throws Exception {

        final String sql = "select FUNCTIONREQD from chartofaccounts where glcode = ?";
        final Query pst = HibernateUtil.getCurrentSession().createSQLQuery(sql);
        pst.setString(1, glcode);
        List<Object[]> rs = null;
        rs = pst.list();
        for (final Object[] element : rs)
            if (Integer.parseInt(element[0].toString()) == 1)
                if (fuctid.length() > 0 && fuctid != null) {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("in COA33--" + fuctid);
                }
                else {
                    dc.addMessage("exilError", "Select functionName for this glcode " + glcode);
                    throw new TaskFailedException();

                }

    }

    private boolean checkfuctreqd(final String glcode) {
        final Session session = HibernateUtil.getCurrentSession();
        final List<CChartOfAccounts> list = session.createQuery(
                "from CChartOfAccounts where functionReqd=true and glcode='" + glcode + "'").list();
        return list.size() == 1 ? true : false;
    }

    private boolean postInGL(final Transaxtion txnList[], final DataCollection dc) throws ParseException {
        final GeneralLedger gLedger = new GeneralLedger();
        final GeneralLedgerDetail gLedgerDet = new GeneralLedgerDetail();
        EgRemittanceGldtl egRemitGldtl = new EgRemittanceGldtl();
        // DataExtractor de=DataExtractor.getExtractor();

        for (final Transaxtion element : txnList) {
            final Transaxtion txn = element;
            if (LOGGER.isInfoEnabled())
                LOGGER.info("GL Code is :" + txn.getGlCode() + "  Debit Amount :" + Double.parseDouble(txn.getDrAmount())
                        + " Credit Amt :" + Double.parseDouble(txn.getCrAmount()));
            // double dbAmt=Double.parseDouble(txn.getDrAmount());

            if (Double.parseDouble(txn.getDrAmount()) == 0.0 && Double.parseDouble(txn.getCrAmount()) == 0.0) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Comming in the zero  block");

            }
            else
            {
                final GLAccount glAcc = (GLAccount) getGlAccountCodes().get(txn.getGlCode());
                gLedger.setVoucherLineId(txn.getVoucherLineId());
                gLedger.setGlCodeId(String.valueOf(glAcc.getId()));
                gLedger.setGlCode(txn.getGlCode());
                gLedger.setDebitAmount(txn.getDrAmount());
                gLedger.setCreditAmount(txn.getCrAmount());
                gLedger.setDescription(txn.getNarration());
                gLedger.setVoucherHeaderId(txn.getVoucherHeaderId());
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Value of function in COA before setting :" + txn.getFunctionId());
                if (!(txn.getFunctionId() == null || txn.getFunctionId().equals("")))
                    gLedger.setFunctionId(txn.getFunctionId());
                else
                    gLedger.setFunctionId(null);
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("txn.getGlCode()" + txn.getGlCode());
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("txn.getFunctionId()" + txn.getFunctionId());
                final String fucid = txn.getFunctionId();
                if (fucid != null && !fucid.equals(""))

                    try {
                        checkfuctreqd(txn.getGlCode(), txn.getFunctionId(), dc);
                    } catch (final Exception e)
                    {
                        LOGGER.error("Inside checkfuctreqd" + e.getMessage(), e);
                        return false;
                    }

                // gLedger.setfunctionId(txn.getFunctionId());
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Value of function id in COA --" + txn.getFunctionId());

                try {
                    // if(LOGGER.isInfoEnabled()) LOGGER.info("inside the postin gl function before insert ----");
                    gLedger.insert();
                } catch (final Exception e) {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("error in the gl++++++++++" + e, e);
                    return false;
                }
                // if that code doesnot require any details no nedd to insert in GL details
                if (glAcc.getGLParameters().size() <= 0)
                    continue;
                final ArrayList glParamList = glAcc.getGLParameters();
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("glParamList size.... :" + glParamList.size());
                final ArrayList txnPrm = txn.getTransaxtionParam();
                String detKeyId = "";
                for (int a = 0; a < glParamList.size(); a++)
                    try {
                        // post the defaults set for details
                        final GLParameter glPrm = (GLParameter) glParamList.get(a);
                        /*
                         * if(!glPrm.getDetailKey().equalsIgnoreCase("0")&&glPrm.getDetailKey().length()>0){
                         * gLedgerDet.setGLId(String.valueOf(gLedger.getId()));
                         * gLedgerDet.setDetailTypeId(String.valueOf(glPrm.getDetailId()));
                         * gLedgerDet.setDetailKeyId(glPrm.getDetailKey()); if(LOGGER.isInfoEnabled())
                         * LOGGER.info("glPrm.getDetailAmt() in glPrm:"+glPrm.getDetailAmt());
                         * gLedgerDet.setDetailAmt(glPrm.getDetailAmt()); gLedgerDet.insert(con); try {
                         * if(validRecoveryGlcode(gLedger.getglCodeId(),con) && Double.parseDouble(gLedger.getcreditAmount())>0) {
                         * egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                         * egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt()); if(glPrm.getTdsId()!=null)
                         * egRemitGldtl.setTdsId(glPrm.getTdsId()); egRemitGldtl.insert(con); } } catch(Exception e) {
                         * LOGGER.error("Error while inserting to eg_remittance_gldtl "+e); return false; } }else
                         */{ // Post the details sent apart from defaults
                            for (int z = 0; z < txnPrm.size(); z++) {
                                final TransaxtionParameter tParam = (TransaxtionParameter) txnPrm.get(z);
                                if (tParam.getDetailName().equalsIgnoreCase(glPrm.getDetailName())
                                        && tParam.getGlcodeId().equalsIgnoreCase(gLedger.getGlCodeId()))
                                {
                                    detKeyId = tParam.getDetailKey();
                                    gLedgerDet.setGLId(String.valueOf(gLedger.getId()));
                                    gLedgerDet.setDetailTypeId(String.valueOf(glPrm.getDetailId()));
                                    gLedgerDet.setDetailKeyId(detKeyId);
                                    if (LOGGER.isInfoEnabled())
                                        LOGGER.info("glPrm.getDetailAmt() in tParam:" + tParam.getDetailAmt());
                                    gLedgerDet.setDetailAmt(tParam.getDetailAmt());
                                    gLedgerDet.insert();
                                    try
                                    {
                                        if (validRecoveryGlcode(gLedger.getGlCodeId())
                                                && Double.parseDouble(gLedger.getCreditAmount()) > 0)
                                        {
                                            egRemitGldtl = new EgRemittanceGldtl();
                                            egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                                            egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt());
                                            if (tParam.getTdsId() != null)
                                                egRemitGldtl.setTdsId(tParam.getTdsId());
                                            egRemitGldtl.insert();
                                        }
                                    } catch (final Exception e)
                                    {
                                        LOGGER.error("Error while inserting to eg_remittance_gldtl " + e, e);
                                        return false;
                                    }
                                }
                            }
                        }
                        // post the gldetailid, gldtlamt to eg_remittance_gldtl table
                        /*
                         * try { if(validRecoveryGlcode(gLedger.getglCodeId(),con) &&
                         * Double.parseDouble(gLedger.getcreditAmount())>0) {
                         * egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                         * egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt()); egRemitGldtl.insert(con); } } catch(Exception e) {
                         * LOGGER.error("Error while inserting to eg_remittance_gldtl "+e); return false; }
                         */
                    } catch (final Exception e) {
                        LOGGER.error("Inside postInGL " + e.getMessage(), e);
                        dc.addMessage(EXILRPERROR, "General Ledger Details Error " + e.toString());
                        return false;
                    }
            }
        }
        return true;
    }

    private boolean postInGL(final Transaxtion txnList[]) throws Exception {
        final GeneralLedger gLedger = new GeneralLedger();
        final GeneralLedgerDetail gLedgerDet = new GeneralLedgerDetail();
        EgRemittanceGldtl egRemitGldtl = new EgRemittanceGldtl();
        // DataExtractor de=DataExtractor.getExtractor();

        for (final Transaxtion element : txnList) {
            final Transaxtion txn = element;
            if (LOGGER.isInfoEnabled())
                LOGGER.info("GL Code is :" + txn.getGlCode() + ":txn.getFunctionId()" + txn.getFunctionId() + "  Debit Amount :"
                        + String.valueOf(txn.getDrAmount()) + " Credit Amt :" + String.valueOf(txn.getCrAmount()));
            if (String.valueOf(txn.getDrAmount()) == "0" && String.valueOf(txn.getCrAmount()) == "0") {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Comming in the zero  block");
                return false;
            }
            else
            {

                final GLAccount glAcc = (GLAccount) getGlAccountCodes().get(txn.getGlCode());
                gLedger.setVoucherLineId(txn.getVoucherLineId());
                gLedger.setGlCodeId(String.valueOf(glAcc.getId()));
                gLedger.setGlCode(txn.getGlCode());
                gLedger.setDebitAmount(String.valueOf(txn.getDrAmount()));
                gLedger.setCreditAmount(String.valueOf(txn.getCrAmount()));
                gLedger.setDescription(txn.getNarration());
                gLedger.setVoucherHeaderId(txn.getVoucherHeaderId());
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Value of function in COA before setting :" + txn.getFunctionId());
                if (!(txn.getFunctionId() == null || txn.getFunctionId().trim().equals("") || txn.getFunctionId().equals("0")))
                {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("txn.getFunctionId()" + txn.getFunctionId());
                    gLedger.setFunctionId(txn.getFunctionId());
                }
                else if (checkfuctreqd(txn.getGlCode())) {
                    final List<ValidationError> errors = new ArrayList<ValidationError>();
                    errors.add(new ValidationError("exp", "function is required for account code : " + txn.getGlCode()));
                    throw new ValidationException(errors);

                } else
                    gLedger.setFunctionId(null);

                try {
                    // if(LOGGER.isInfoEnabled()) LOGGER.info("inside the postin gl function before insert ----");
                    gLedger.insert();
                } catch (final Exception e) {
                    LOGGER.error("error in the gl++++++++++" + e, e);
                    return false;
                }
                // if that code doesnot require any details no nedd to insert in GL details
                if (glAcc.getGLParameters().size() <= 0)
                    continue;
                final ArrayList glParamList = glAcc.getGLParameters();
                final Set temp = new HashSet<>();
                temp.addAll(glParamList);
                glParamList.clear();
                glParamList.addAll(temp);
                final ArrayList txnPrm = txn.getTransaxtionParam();
                String detKeyId = "";
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("glParamList size :" + glParamList.size());
                for (int a = 0; a < glParamList.size(); a++)
                    try {
                        // post the defaults set for details
                        final GLParameter glPrm = (GLParameter) glParamList.get(a);
                        /*
                         * if(!glPrm.getDetailKey().equalsIgnoreCase("0")&&glPrm.getDetailKey().length()>0){
                         * gLedgerDet.setGLId(String.valueOf(gLedger.getId()));
                         * gLedgerDet.setDetailTypeId(String.valueOf(glPrm.getDetailId()));
                         * gLedgerDet.setDetailKeyId(glPrm.getDetailKey()); if(LOGGER.isInfoEnabled())
                         * LOGGER.info("glPrm.getDetailAmt() in glParam:"+glPrm.getDetailAmt());
                         * gLedgerDet.setDetailAmt(glPrm.getDetailAmt()); gLedgerDet.insert(con); try {
                         * if(validRecoveryGlcode(gLedger.getglCodeId(),con) && Double.parseDouble(gLedger.getcreditAmount())>0) {
                         * egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                         * egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt()); if(glPrm.getTdsId()!=null)
                         * egRemitGldtl.setTdsId(glPrm.getTdsId()); egRemitGldtl.insert(con); } } catch(Exception e) {
                         * LOGGER.error("Error while inserting to eg_remittance_gldtl "+e); return false; } }else
                         */{ // Post the details sent apart from defaults
                            for (int z = 0; z < txnPrm.size(); z++)
                            {
                                final TransaxtionParameter tParam = (TransaxtionParameter) txnPrm.get(z);
                                if (LOGGER.isInfoEnabled())
                                    LOGGER.info("tParam.getGlcodeId():" + tParam.getGlcodeId());
                                if (LOGGER.isInfoEnabled())
                                    LOGGER.info("gLedger.getglCodeId():" + gLedger.getGlCodeId());
                                if (tParam.getDetailName().equalsIgnoreCase(glPrm.getDetailName())
                                        && tParam.getGlcodeId().equalsIgnoreCase(gLedger.getGlCodeId()))
                                {
                                    detKeyId = tParam.getDetailKey();
                                    gLedgerDet.setGLId(String.valueOf(gLedger.getId()));
                                    gLedgerDet.setDetailTypeId(String.valueOf(glPrm.getDetailId()));
                                    gLedgerDet.setDetailKeyId(detKeyId);
                                    gLedgerDet.setDetailAmt(tParam.getDetailAmt());
                                    gLedgerDet.insert();
                                    try
                                    {
                                        if (validRecoveryGlcode(gLedger.getGlCodeId())
                                                && Double.parseDouble(gLedger.getCreditAmount()) > 0)
                                        {
                                            egRemitGldtl = new EgRemittanceGldtl();
                                            // if(LOGGER.isInfoEnabled()) LOGGER.info("----------"+gLedger.getGlCodeId());
                                            egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                                            egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt());
                                            if (tParam.getTdsId() != null)
                                                egRemitGldtl.setTdsId(tParam.getTdsId());
                                            egRemitGldtl.insert();
                                        }
                                    } catch (final Exception e)
                                    {
                                        LOGGER.error("Error while inserting to eg_remittance_gldtl " + e, e);
                                        return false;
                                    }
                                }
                            }
                        }
                    } catch (final Exception e) {
                        LOGGER.error("inside postInGL" + e.getMessage(), e);
                        throw new TaskFailedException();
                    }
            }
        }
        return true;
    }

    private boolean updateInGL(final Transaxtion txnList[], final DataCollection dc) throws TaskFailedException, ParseException,
            SQLException {
        final GeneralLedger gLedger = new GeneralLedger();
        final GeneralLedgerDetail gLedgerDet = new GeneralLedgerDetail();
        EgRemittanceGldtl egRemitGldtl = new EgRemittanceGldtl();
        // DataExtractor de=DataExtractor.getExtractor();
        final ArrayList glHeaderId = new ArrayList();
        final Transaxtion txn1 = txnList[0];
        final int VoucherHeaderId = Integer.parseInt(txn1.getVoucherHeaderId());
        if (LOGGER.isInfoEnabled())
            LOGGER.info("VoucherHeaderId----" + VoucherHeaderId);
        final String query = "select id from generalledger where voucherheaderid= ? order by id";
        Query pst = HibernateUtil.getCurrentSession().createSQLQuery(query);
        pst.setInteger(1, VoucherHeaderId);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("select id from generalledger where voucherheaderid=" + VoucherHeaderId + " order by id");

        resultset = pst.list();
        int c = 0;
        for (final Object[] element : resultset) {

            glHeaderId.add(c, element[0].toString());
            c++;
        }

        final int count = glHeaderId.size();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("count**********" + count);
        for (int k = 0; k < count; k++)
            try {
                final String delremitsql = "delete from eg_remittance_gldtl where gldtlid in (select id from generalledgerdetail where generalledgerid='"
                        + glHeaderId.get(k).toString() + "')";
                pst = HibernateUtil.getCurrentSession().createSQLQuery(delremitsql);
                pst.setString(1, glHeaderId.get(k).toString());
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("deleting remittance Query " + delremitsql);
                pst.executeUpdate();
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("delete from generalledgerdetail where generalledgerid='" + glHeaderId.get(k).toString() + "'");
                final String delGenLedDet = "delete from generalledgerdetail where generalledgerid= ?";
                pst = HibernateUtil.getCurrentSession().createSQLQuery(delGenLedDet);
                pst.setString(1, glHeaderId.get(k).toString());
                final int del = pst.executeUpdate();
                if (del > 0)
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("Records deleted from general ledger detail for GLH " + glHeaderId.get(k).toString());
            } catch (final Exception e) {
                LOGGER.error("Exp in reading from generalledgerdetail: " + e, e);
                throw new TaskFailedException(e.getMessage());
            }

        if (count > 0)
            try {

                final String genLed = "DELETE FROM generalledger WHERE voucherheaderid= ?";
                pst = HibernateUtil.getCurrentSession().createSQLQuery(genLed);
                pst.setInteger(1, VoucherHeaderId);
                final int del = pst.executeUpdate();
                if (del > 0)
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("DELETE FROM generalledger WHERE voucherheaderid=" + VoucherHeaderId);

            } catch (final Exception e) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Exp in reading from generalledger: " + e, e);
            }

        for (final Transaxtion txn : txnList) {
            final GLAccount glAcc = (GLAccount) getGlAccountCodes().get(txn.getGlCode());
            gLedger.setVoucherLineId(txn.getVoucherLineId());
            gLedger.setGlCodeId(String.valueOf(glAcc.getId()));
            gLedger.setGlCode(txn.getGlCode());
            gLedger.setDebitAmount(String.valueOf(txn.getDrAmount()));
            gLedger.setCreditAmount(String.valueOf(txn.getCrAmount()));
            gLedger.setDescription(txn.getNarration());
            gLedger.setVoucherHeaderId(txn.getVoucherHeaderId());
            gLedger.setFunctionId(txn.getFunctionId());

            try {
                // if(LOGGER.isInfoEnabled()) LOGGER.info("inside the postin gl function before insert ----");
                gLedger.insert();
            } catch (final Exception e) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("error in the gl++++++++++" + e, e);
                dc.addMessage("exilSQLError", e.toString());
                return false;
            }
            // if that code doesnot require any details no nedd to insert in GL details
            if (glAcc.getGLParameters().size() <= 0)
                continue;
            final ArrayList glParamList = glAcc.getGLParameters();
            final ArrayList txnPrm = txn.getTransaxtionParam();
            String detKeyId = "";
            for (int a = 0; a < glParamList.size(); a++)
                try {
                    // post the defaults set for details
                    final GLParameter glPrm = (GLParameter) glParamList.get(a);
                    /*
                     * if(!glPrm.getDetailKey().equalsIgnoreCase("0")&&glPrm.getDetailKey().length()>0){
                     * gLedgerDet.setGLId(String.valueOf(gLedger.getId()));
                     * gLedgerDet.setDetailTypeId(String.valueOf(glPrm.getDetailId()));
                     * gLedgerDet.setDetailKeyId(glPrm.getDetailKey()); gLedgerDet.setDetailAmt(glPrm.getDetailAmt());
                     * gLedgerDet.insert(con); try { if(validRecoveryGlcode(gLedger.getglCodeId(),con) &&
                     * Double.parseDouble(gLedger.getcreditAmount())>0) {
                     * egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                     * egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt()); if(glPrm.getTdsId()!=null)
                     * egRemitGldtl.setTdsId(glPrm.getTdsId()); egRemitGldtl.insert(con); } } catch(Exception e) {
                     * LOGGER.error("Error while inserting to eg_remittance_gldtl "+e); return false; } }else
                     */{ // Post the details sent apart from defaults
                        for (int z = 0; z < txnPrm.size(); z++) {
                            final TransaxtionParameter tParam = (TransaxtionParameter) txnPrm.get(z);
                            if (tParam.getDetailName().equalsIgnoreCase(glPrm.getDetailName())
                                    && tParam.getGlcodeId().equalsIgnoreCase(gLedger.getGlCodeId()))
                            {
                                detKeyId = tParam.getDetailKey();
                                gLedgerDet.setGLId(String.valueOf(gLedger.getId()));
                                gLedgerDet.setDetailTypeId(String.valueOf(glPrm.getDetailId()));
                                gLedgerDet.setDetailKeyId(detKeyId);
                                gLedgerDet.setDetailAmt(tParam.getDetailAmt());
                                gLedgerDet.insert();
                                try
                                {
                                    if (validRecoveryGlcode(gLedger.getGlCodeId())
                                            && Double.parseDouble(gLedger.getCreditAmount()) > 0)
                                    {
                                        egRemitGldtl = new EgRemittanceGldtl();
                                        egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                                        egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt());
                                        if (tParam.getTdsId() != null)
                                            egRemitGldtl.setTdsId(tParam.getTdsId());
                                        egRemitGldtl.insert();
                                    }
                                } catch (final Exception e)
                                {
                                    LOGGER.error("Error while inserting to eg_remittance_gldtl " + e, e);
                                    return false;
                                }
                            }
                        }
                    }
                    // post the gldetailid, gldtlamt to eg_remittance_gldtl table
                    /*
                     * try { if(validRecoveryGlcode(gLedger.getglCodeId(),con) && Double.parseDouble(gLedger.getcreditAmount())>0)
                     * { egRemitGldtl.setGldtlId(String.valueOf(gLedgerDet.getId()));
                     * egRemitGldtl.setGldtlAmt(gLedgerDet.getDetailAmt()); egRemitGldtl.insert(con); } } catch(Exception e) {
                     * LOGGER.error("Error while inserting to eg_remittance_gldtl "+e); return false; }
                     */
                } catch (final Exception e) {
                    LOGGER.error("Inside updateInGl" + e.getMessage(), e);
                    throw new TaskFailedException();
                }
        }
        // if(LOGGER.isInfoEnabled()) LOGGER.info("HI- 396-6");// TBR
        return true;
    }

    public String getGLCode(final String detailName, final String detailKey, final Connection con) throws TaskFailedException {
        String code = "";
        try {
            final String str = "select glcode as \"code\" from chartofaccounts,bankaccount where bankaccount.glcodeid=chartofaccounts.id and bankaccount.id= ?";
            final PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, detailKey);
            final ResultSet resultset = pst.executeQuery();

            if (resultset.next())
                code = resultset.getString("code");
        } catch (final Exception e)
        {
            LOGGER.error("error" + e.toString(), e);
        }
        return code;
    }

    public String getFiscalYearID(final String voucherDate, final Connection con, final DataCollection dc) {
        String fiscalyearid = "";
        final String sql = "select ID as \"fiscalperiodID\" from fiscalperiod where " +
                "to_date(?,'dd-mon-yyyy') between startingdate and endingdate";
        try {
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, voucherDate);
            final ResultSet rs = pst.executeQuery();
            if (rs.next())
                fiscalyearid = rs.getString("fiscalperiodID");
        } catch (final Exception e) {
            LOGGER.error("Excepion in getFiscalYearID() " + e, e);
        }
        return fiscalyearid;
    }

    private boolean validPeriod(final String vDate) throws TaskFailedException {
        try {
            if (ClosedPeriods.isClosedForPosting(vDate))
                return false;
        } catch (final Exception e) {
            LOGGER.error("Inside validPeriod " + e.getMessage(), e);
            throw new TaskFailedException();
        }
        return true;
    }

    public void test() throws TaskFailedException {
        final Iterator it = getGlAccountCodes().keySet().iterator();
        while (it.hasNext()) {
            final GLAccount glAcc = (GLAccount) getGlAccountCodes().get(it.next());
            final ArrayList a = glAcc.getGLParameters();
            for (int i = 0; i < a.size(); i++) {
                GLParameter glp = (GLParameter) a.get(i);
            }
            ;
        }
    }

    /**
     * @return Returns the getAccountDetailType().
     */
    public static HashMap getAccountDetailType()
    {
        LOGGER.debug("in getAccountDetailType():jndi name is :" + EgovThreadLocals.getDomainName());
        HashMap retMap = null;
        try
        {
            HashMap cacheValuesHashMap = new HashMap<Object, Object>();
            cacheValuesHashMap = (HashMap) cache.get(ROOTNODE + "/" + EgovThreadLocals.getDomainName());
            if (cacheValuesHashMap != null && !cacheValuesHashMap.isEmpty())
                retMap = (HashMap) cacheValuesHashMap.get(ACCOUNTDETAILTYPENODE);

        } catch (final Exception e)
        {
            LOGGER.debug(EXP + e.getMessage());
            throw new ApplicationRuntimeException(e.getMessage());
        }
        return retMap;
    }

    /**
     * @return Returns the getGlAccountCodes().
     */
    public static HashMap getGlAccountCodes()
    {
        LOGGER.debug("in getGlAccountCodes():jndi name is :" + EgovThreadLocals.getDomainName());
        HashMap retMap = null;
        try
        {
            HashMap cacheValuesHashMap = new HashMap<Object, Object>();
            cacheValuesHashMap = (HashMap) cache.get(ROOTNODE + "/" + EgovThreadLocals.getDomainName());
            if (cacheValuesHashMap != null && !cacheValuesHashMap.isEmpty())
                retMap = (HashMap) cacheValuesHashMap.get(GLACCCODENODE);
            if (retMap != null)
                LOGGER.debug("in getGlAccountCodes() size is :" + retMap.size());

        } catch (final Exception e)
        {
            LOGGER.debug(EXP + e.getMessage());
            throw new ApplicationRuntimeException(e.getMessage());
        }
        return retMap;
    }

    /**
     * @return Returns the getGlAccountIds().
     */
    public static HashMap getGlAccountIds()
    {
        LOGGER.debug("in getGlAccountIds():jndi name is :" + EgovThreadLocals.getDomainName());
        HashMap retMap = null;
        try
        {
            HashMap cacheValuesHashMap = new HashMap<Object, Object>();
            cacheValuesHashMap = (HashMap) cache.get(ROOTNODE + "/" + EgovThreadLocals.getDomainName());
            if (cacheValuesHashMap != null && !cacheValuesHashMap.isEmpty())
                retMap = (HashMap) cacheValuesHashMap.get(GLACCIDNODE);

        } catch (final Exception e)
        {
            LOGGER.debug(EXP + e.getMessage());
            throw new ApplicationRuntimeException(e.getMessage());
        }
        return retMap;
    }

    private boolean validRecoveryGlcode(final String glcodeId) throws TaskFailedException {
        try {
            final String query = "select id from tds where glcodeid= ? and isactive=1";
            final Query pst = HibernateUtil.getCurrentSession().createSQLQuery(query);
            pst.setLong(0, Long.valueOf(glcodeId));
            if (LOGGER.isInfoEnabled())
                LOGGER.info("query-->" + query);
            final List<Object[]> rset = pst.list();
            if (rset != null && rset.size() > 0)
            {
                for (final Object[] element : rset)
                    if (element[0].toString().equals("0"))
                        return false;

            }
            else
                return false;

        } catch (final Exception e)
        {
            LOGGER.error("Inside validRecoveryGlcode" + e.getMessage(), e);
            throw new TaskFailedException();
        }
        return true;
    }

    public void setCacheInstance(final Cache<Object, Object> cacheInstance) {
        cache = cacheInstance;
    }

    public PersistenceService<CVoucherHeader, Long> getVoucherHeaderPersitService() {
        return voucherHeaderPersitService;
    }

    public void setVoucherHeaderPersitService(
            final PersistenceService<CVoucherHeader, Long> voucherHeaderPersitService) {
        this.voucherHeaderPersitService = voucherHeaderPersitService;
    }

    public static PersistenceService<CChartOfAccountDetail, Integer> getChartOfAccountDetailService() {
        return chartOfAccountDetailService;
    }

    public static void setChartOfAccountDetailService(
            final PersistenceService<CChartOfAccountDetail, Integer> chartOfAccountDetailService) {
        ChartOfAccounts.chartOfAccountDetailService = chartOfAccountDetailService;
    }

    public BudgetDetailsHibernateDAO getBudgetDetailsDAO() {
        return budgetDetailsDAO;
    }

    public void setBudgetDetailsDAO(final BudgetDetailsHibernateDAO budgetDetailsDAO) {
        this.budgetDetailsDAO = budgetDetailsDAO;
    }

}