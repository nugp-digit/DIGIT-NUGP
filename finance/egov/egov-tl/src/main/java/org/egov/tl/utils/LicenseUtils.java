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

package org.egov.tl.utils;

import org.apache.log4j.Logger;
import org.egov.collection.integration.models.BillReceiptInfo;
import org.egov.collection.integration.models.ReceiptInstrumentInfo;
import org.egov.collection.integration.services.CollectionIntegrationService;
import org.egov.commons.ContractorGrade;
import org.egov.commons.EgwStatus;
import org.egov.commons.Installment;
import org.egov.commons.dao.InstallmentDao;
import org.egov.demand.model.EgBill;
import org.egov.demand.model.EgBillDetails;
import org.egov.demand.model.EgDemand;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgdmCollectedReceipt;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.DesignationService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.BoundaryType;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.HierarchyType;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.BoundaryTypeService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.admin.master.service.HierarchyTypeService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.commons.Designation;
import org.egov.pims.commons.Position;
import org.egov.tl.entity.License;
import org.egov.tl.entity.LicenseStatus;
import org.egov.tl.entity.LicenseStatusValues;
import org.egov.tl.entity.LicenseSubCategory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;

@Service
public class LicenseUtils {
    private static final Logger LOGGER = Logger.getLogger(LicenseUtils.class);
    public static final String ADMIN_HIERARCHY_TYPE = "ADMINISTRATION";
    private static final String CITY_BOUNDARY_TYPE = "City";
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private BoundaryService boundaryService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private BoundaryTypeService boundaryTypeService;
    @Autowired
    private HierarchyTypeService hierarchyTypeService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DesignationService designationService;
    @Autowired
    private InstallmentDao installmentDao;
    @Autowired
    private AppConfigValueService appConfigValuesService;

    public Module getModule(final String moduleName) {
        return moduleService.getModuleByName(moduleName);
    }

    public void setPersistenceService(final PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public List<Boundary> getAllCity() {
        HierarchyType hType = null;
        try {
            hType = hierarchyTypeService.getHierarchyTypeByName(LicenseUtils.ADMIN_HIERARCHY_TYPE);
        } catch (final Exception e) {
            LOGGER.error("getAllCity()--Exception");
            throw new ApplicationRuntimeException("Unable to load Heirarchy information", e);
        }
        List<Boundary> cityList = null;
        final BoundaryType bType = boundaryTypeService.getBoundaryTypeByNameAndHierarchyType(
                LicenseUtils.CITY_BOUNDARY_TYPE, hType);
        cityList = boundaryService.getAllBoundariesByBoundaryTypeId(bType.getId());
        return cityList;
    }

   public List<LicenseSubCategory> getAllTradeNames(final String simpleName) {
        return persistenceService.findAllBy("from org.egov.tl.entity.LicenseSubCategory where licenseType.name=?",
                simpleName);
    }

    public LicenseStatus getLicenseStatusbyCode(final String statusCode) {
        return (LicenseStatus) persistenceService.find("FROM org.egov.tl.entity.LicenseStatus where statusCode=?",
                statusCode);
    }


    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }


    public Installment getCurrInstallment(final Module module) {
        final Installment currentInstall = installmentDao.getInsatllmentByModuleForGivenDate(module, new Date());
        return currentInstall;

    }

   public Boolean isDigitalSignEnabled() {
        final AppConfigValues appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                Constants.TRADELICENSE_MODULENAME, Constants.DIGITALSIGNINCLUDEINWORKFLOW).get(0);
        return "YES".equalsIgnoreCase(appConfigValue.getValue());
    }
    
    public String getDepartmentCodeForBillGenerate(){
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                Constants.TRADELICENSE_MODULENAME, "DEPARTMENTFORGENERATEBILL");
        return (!appConfigValue.isEmpty() ?appConfigValue.get(0).getValue():"");
    }

    public Position getCityLevelCommissioner() {
        Position pos = null;
        final Department deptObj = departmentService.getDepartmentByName(Constants.ROLE_COMMISSIONERDEPARTEMNT);
        final Designation desgnObj = designationService.getDesignationByName("Commissioner");

        List<Assignment> assignlist = new ArrayList<Assignment>();
        if(deptObj !=null && !"".equals(deptObj))
        assignlist = assignmentService.getAssignmentsByDeptDesigAndDates(deptObj.getId(), desgnObj.getId(), new Date(),
                new Date());
       if(assignlist.isEmpty())
            assignlist=    assignmentService.getAllPositionsByDepartmentAndDesignationForGivenRange(null, desgnObj.getId(), new Date());
       if(assignlist.isEmpty())
           assignlist=assignmentService.getAllActiveAssignments(desgnObj.getId());
      
        pos = !assignlist.isEmpty() ? assignlist.get(0).getPosition() : null;
        return pos;
    }

    public License applicationStatusChange(final License licenseObj, final String code) {
        final EgwStatus statusChange = (EgwStatus) persistenceService.find(
                "from org.egov.commons.EgwStatus where moduletype=? and code=?", Constants.TRADELICENSEMODULE, code);
        licenseObj.setEgwStatus(statusChange);
        return licenseObj;
    }
}