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
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
/*
 * MastersServiceBean.java Created on Sep 8, 2008
 *
 * Copyright 2005 eGovernments Foundation. All rights reserved.
 * EGOVERNMENTS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.egov.masters.services;

import org.apache.log4j.Logger;
import org.egov.commons.Accountdetailkey;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.dao.AccountdetailkeyHibernateDAO;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.masters.dao.AccountEntityHibernateDAO;
import org.egov.masters.dao.AccountdetailtypeHibernateDAO;
import org.egov.masters.dao.MastersDAOFactory;
import org.egov.masters.model.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sathish P
 * @version 1.00
 */
@Transactional(readOnly = true)
public class MastersService
{
    private static final Logger LOGGER = Logger.getLogger(MastersService.class);
    @Autowired
    private static AccountdetailkeyHibernateDAO accntDtlKeyDAO;

    @Transactional
    public AccountEntity createAccountEntity(final AccountEntity accountEntity)
    {
        AccountEntity accEntity = null;
        Accountdetailkey adk = null;
        // String[] attrName=null;
        try
        {

            final AccountEntityHibernateDAO accEntDao = MastersDAOFactory.getDAOFactory().getAccountEntityDAO();
            if (accountEntity.getAccountDetailKeyId() != null
                    && (accountEntity.getAccountdetailtype().getName().equalsIgnoreCase("Creditor") || accountEntity
                            .getAccountdetailtype().getName().equalsIgnoreCase("Employee")))
            {
                final Accountdetailtype accountdetailtype = getAccountdetailtypeByName(accountEntity.getAccountdetailtype()
                        .getName());
                adk = new Accountdetailkey();
                adk.setGroupid(1);
                adk.setDetailkey(accountEntity.getAccountDetailKeyId());
                adk.setDetailname(accountdetailtype.getAttributename());
                adk.setAccountdetailtype(accountdetailtype);
                accntDtlKeyDAO.create(adk);
                return accountEntity;
            }
            else
            {
                accEntity = (AccountEntity) accEntDao.create(accountEntity);
                if (accEntity.getCode() == null)
                    accEntity.setCode(accEntity.getId().toString());
                final Accountdetailtype accountdetailtype = getAccountdetailtypeByName(accountEntity.getAccountdetailtype()
                        .getName());
                adk = new Accountdetailkey();
                adk.setGroupid(1);
                adk.setDetailkey(accEntity.getId());
                adk.setDetailname(accountdetailtype.getAttributename());
                adk.setAccountdetailtype(accountdetailtype);
                accntDtlKeyDAO.create(adk);
                return accEntity;
            }

        } catch (final Exception ex)
        {
            LOGGER.error("Exp=" + ex.getMessage());
            //
            throw new ApplicationRuntimeException("Exception: " + ex.getMessage());
        }
    }

    @Transactional
    public void updateAccountEntity(final AccountEntity accountEntity)
    {
        final AccountEntityHibernateDAO accEntDao = MastersDAOFactory.getDAOFactory().getAccountEntityDAO();
        accEntDao.update(accountEntity);
    }

    public Accountdetailtype getAccountdetailtypeById(final Integer id)
    {
        final AccountdetailtypeHibernateDAO accDtlTypeDao = MastersDAOFactory.getDAOFactory().getAccountdetailtypeDAO();
        return (Accountdetailtype) accDtlTypeDao.findById(id, false);
    }

    public Accountdetailtype getAccountdetailtypeByName(final String name)
    {
        final AccountdetailtypeHibernateDAO accDtlTypeDao = MastersDAOFactory.getDAOFactory().getAccountdetailtypeDAO();
        return accDtlTypeDao.getAccountdetailtypeByName(name);
    }

    public AccountEntity getAccountEntitybyId(final Integer id)
    {
        final AccountEntityHibernateDAO accEntDao = MastersDAOFactory.getDAOFactory().getAccountEntityDAO();
        return (AccountEntity) accEntDao.findById(id, false);
    }
}