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
package org.egov.works.web.actions.tender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.commons.EgwStatus;
import org.egov.commons.service.CommonsService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQueryHQL;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.DateUtils;
import org.egov.pims.model.PersonalInformation;
import org.egov.pims.service.EmployeeService;
import org.egov.web.actions.SearchFormAction;
import org.egov.works.models.tender.SetStatus;
import org.egov.works.models.tender.WorksPackage;
import org.egov.works.services.AbstractEstimateService;
import org.egov.works.services.WorksPackageService;
import org.egov.works.services.WorksService;
import org.egov.works.utils.WorksConstants;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchWorksPackageAction extends SearchFormAction {

    private static final long serialVersionUID = -6268869129605734393L;
    private WorksPackage worksPackage = new WorksPackage();
    private List<WorksPackage> results = new LinkedList<WorksPackage>();
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CommonsService commonsService;
    private Date fromDate;
    private Date toDate;
    private String status;
    private String offlinestatus;
    private String setStatus;
    private Boolean checkRetenderedWP;

    private AbstractEstimateService abstractEstimateService;
    private WorksService worksService;
    private String negoCreatedBy;
    private String statusReq;
    private Long execDept;
    private String source;
    private String option = "";
    private String estimateOrWpSearchReq;
    public static final String OBJECT_TYPE = "WorksPackage";
    @Autowired
    private DepartmentService departmentService;
    private Long wpCancelId;
    private String cancelRemarks;
    private String cancellationReason;
    private String worksPackageNumber;
    private String messageKey;
    private static final String CANCEL_SUCCESS = "cancelSuccessful";
    private String estimateNumber;
    private WorksPackageService workspackageService;
    private PersistenceService<SetStatus, Long> worksStatusService;
    private static final String STATUS_OBJECTID = "getStatusDateByObjectId_Type_Desc";

    @Override
    public Object getModel() {
        return worksPackage;
    }

    public SearchWorksPackageAction() {
        addRelatedEntity("department", Department.class);
    }

    @Override
    public void prepare() {
        super.prepare();
        setupDropdownDataExcluding();
        addDropdownData("offlineStatusList", getAllOfflineStatus());
        if ("createNegotiationForWP".equalsIgnoreCase(source))
            perform();
    }

    public List<EgwStatus> getAllOfflineStatus() {
        String status;
        status = worksService.getWorksConfigValue("WorksPackage.setstatus");

        final List<String> statList = new ArrayList<String>();
        if (StringUtils.isNotBlank(status)) {
            final List<String> statusList = Arrays.asList(status.split(","));
            for (final String stat : statusList)
                statList.add(stat);
        }
        final List<EgwStatus> returnList = new LinkedList<EgwStatus>();
        final EgwStatus cancelledStatus = commonsService.getStatusByModuleAndCode(OBJECT_TYPE,
                WorksPackage.WorkPacakgeStatus.CANCELLED.toString());
        returnList.add(cancelledStatus);
        returnList.addAll(commonsService.getStatusListByModuleAndCodeList(OBJECT_TYPE, statList));
        return returnList;
    }

    @SuppressWarnings("unchecked")
    public void perform() {

        if (abstractEstimateService.getLatestAssignmentForCurrentLoginUser() != null)
            execDept = abstractEstimateService.getLatestAssignmentForCurrentLoginUser().getDeptId().getId();
        negoCreatedBy = worksService.getWorksConfigValue("TENDER_NEGOTIATION_CREATED_BY_SELECTION");
        statusReq = worksService.getWorksConfigValue("WorksPackage.laststatus");
        estimateOrWpSearchReq = worksService.getWorksConfigValue("ESTIMATE_OR_WP_SEARCH_REQ");

        addDropdownData("departmentList", worksService.getAllDeptmentsForLoggedInUser());
        if (StringUtils.isNotBlank(statusReq))
            setStatus(statusReq);
    }

    @Override
    public String execute() {
        return INDEX;
    }

    @SuppressWarnings("unchecked")
    protected void getPositionAndUser() {
        final List<WorksPackage> wpList = new ArrayList<WorksPackage>();

        final Iterator i = searchResult.getList().iterator();
        final String lastStatusDescription = getLastStatus();
        List<String> worksPackageActions = null;
        while (i.hasNext()) {
            final WorksPackage wp = (WorksPackage) i.next();

            if (wp.getCurrentState() != null) {
                if (!wp.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.APPROVED)
                        && !wp.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.CANCELLED_STATUS)) {
                    final PersonalInformation emp = employeeService.getEmployeeforPosition(wp.getCurrentState()
                            .getOwnerPosition());
                    if (emp != null && StringUtils.isNotBlank(emp.getEmployeeName()))
                        wp.setEmployeeName(emp.getEmployeeName());
                }
                final String approved = getApprovedValue();
                final SetStatus lastStatus = worksStatusService.findByNamedQuery(STATUS_OBJECTID, wp.getId(),
                        OBJECT_TYPE, lastStatusDescription);
                final String actions = worksService.getWorksConfigValue("WORKSPACKAGE_SEARCH_ACTIONS");
                worksPackageActions = new LinkedList<String>();
                if (StringUtils.isNotBlank(actions)) {
                    String setStat = "";
                    worksPackageActions.addAll(Arrays.asList(actions.split(",")));

                    if (lastStatus != null || "view".equalsIgnoreCase(setStatus) && wp.getSetStatuses() != null
                            && !wp.getSetStatuses().isEmpty())
                        setStat = worksService.getWorksConfigValue("WORKS_VIEW_OFFLINE_STATUS_VALUE");
                    else if (lastStatus == null && StringUtils.isNotBlank(approved) && wp.getEgwStatus() != null
                            && approved.equals(wp.getEgwStatus().getCode()) && "create".equalsIgnoreCase(setStatus))
                        setStat = worksService.getWorksConfigValue("WORKS_SETSTATUS_VALUE");
                    if (StringUtils.isNotBlank(setStat))
                        worksPackageActions.add(setStat);
                    wp.setWorksPackageActions(worksPackageActions);
                }
                setOnlineOrOfflineStatusForWp(wp);
                wpList.add(wp);
            }
        }
        searchResult.getList().clear();
        searchResult.getList().addAll(wpList);
        if (worksPackageActions == null)
            worksPackageActions = new ArrayList<String>();
    }

    void setOnlineOrOfflineStatusForWp(final WorksPackage wp) {
        if (wp.getEgwStatus() != null && wp.getEgwStatus().getCode().equals(WorksConstants.APPROVED)) {
            if (wp.getLatestOfflineStatus() != null && wp.getLatestOfflineStatus().getEgwStatus() != null
                    && StringUtils.isNotBlank(wp.getLatestOfflineStatus().getEgwStatus().getDescription()))
                wp.setWorksPackageStatus(wp.getLatestOfflineStatus().getEgwStatus().getDescription());
            else
                wp.setWorksPackageStatus(wp.getEgwStatus().getDescription());
        } else if (wp.getEgwStatus() != null)
            wp.setWorksPackageStatus(wp.getEgwStatus().getDescription());
    }

    public String getApprovedValue() {
        return worksService.getWorksConfigValue("WORKS_PACKAGE_STATUS");
    }

    public String getLastStatus() {
        return worksService.getWorksConfigValue(WorksConstants.WORKSPACKAGE_LASTSTATUS);
    }

    public List<EgwStatus> getPackageStatuses() {
        final String wpStatus = worksService.getWorksConfigValue("WP_STATUS_SEARCH");
        final String status = worksService.getWorksConfigValue("WorksPackage.setstatus");
        final String lastStatus = worksService.getWorksConfigValue("WorksPackage.laststatus");
        final List<String> statList = new ArrayList<String>();
        if (StringUtils.isNotBlank(wpStatus)) {
            final List<String> statusList = Arrays.asList(wpStatus.split(","));
            for (final String stat : statusList)
                statList.add(stat);
        }
        if (StringUtils.isNotBlank(status) && StringUtils.isNotBlank(lastStatus)) {
            final List<String> statusList = Arrays.asList(status.split(","));
            for (final String stat : statusList)
                if (stat.equals(lastStatus)) {
                    statList.add(stat);
                    break;
                } else
                    statList.add(stat);
        }
        return commonsService.getStatusListByModuleAndCodeList(WorksPackage.class.getSimpleName(), statList);
    }

    public List<EgwStatus> getCancelPackageStatuses() {
        final List<EgwStatus> statuses = getPackageStatuses();
        final List<EgwStatus> resultStatuses = new ArrayList<EgwStatus>();
        if (statuses != null && !statuses.isEmpty())
            for (final EgwStatus status : statuses)
                if (status.getCode().equalsIgnoreCase("CREATED") || status.getCode().equalsIgnoreCase("CHECKED")
                        || status.getCode().equalsIgnoreCase("REJECTED")
                        || status.getCode().equalsIgnoreCase("CANCELLED")
                        || status.getCode().equalsIgnoreCase("RESUBMITTED") || status.getCode().equalsIgnoreCase("NEW"))
                    continue;
                else
                    resultStatuses.add(status);
        return resultStatuses;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public List<WorksPackage> getResults() {
        return results;
    }

    public void setResults(final List<WorksPackage> results) {
        this.results = results;
    }

    public void setEmployeeService(final EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void setModel(final WorksPackage worksPackage) {
        this.worksPackage = worksPackage;
    }

    public void setCommonsService(final CommonsService commonsService) {
        this.commonsService = commonsService;
    }

    public void setAbstractEstimateService(final AbstractEstimateService abstractEstimateService) {
        this.abstractEstimateService = abstractEstimateService;
    }

    public String getNegoCreatedBy() {
        return negoCreatedBy;
    }

    public String getStatusReq() {
        return statusReq;
    }

    public void setWorksService(final WorksService worksService) {
        this.worksService = worksService;
    }

    public void setNegoCreatedBy(final String negoCreatedBy) {
        this.negoCreatedBy = negoCreatedBy;
    }

    public void setStatusReq(final String statusReq) {
        this.statusReq = statusReq;
    }

    public Long getExecDept() {
        return execDept;
    }

    public void setExecDept(final Long execDept) {
        this.execDept = execDept;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getSetStatus() {
        return setStatus;
    }

    public void setSetStatus(final String setStatus) {
        this.setStatus = setStatus;
    }

    public String getEstimateOrWpSearchReq() {
        return estimateOrWpSearchReq;
    }

    public void setEstimateOrWpSearchReq(final String estimateOrWpSearchReq) {
        this.estimateOrWpSearchReq = estimateOrWpSearchReq;
    }

    public String getOption() {
        return option;
    }

    public void setOption(final String option) {
        this.option = option;
    }

    @Override
    public SearchQuery prepareQuery(final String sortField, final String sortOrder) {
        final StringBuilder sb = new StringBuilder(300);
        final List<Object> paramList = new ArrayList<Object>();
        if ("createNegotiationForWP".equals(source)) {
            sb.append("from WorksPackage as wp where wp.egwStatus.code=? and wp.id in(select stat.objectId from "
                    + "SetStatus stat where stat.egwStatus.code= ? and stat.id = (select"
                    + " max(stat1.id) from SetStatus stat1 where stat1.objectType='" + OBJECT_TYPE
                    + "' and wp.id=stat1.objectId)) ");
            paramList.add(WorksPackage.WorkPacakgeStatus.APPROVED.toString());
            paramList.add(getStatus());

        } else {
            final String wpStatus = worksService.getWorksConfigValue("WP_STATUS_SEARCH");
            final List<String> statusList = Arrays.asList(wpStatus.split(","));
            if (checkRetenderedWP != null && checkRetenderedWP) {
                if (StringUtils.isNotBlank(getOfflinestatus())
                        && getOfflinestatus().equals(WorksPackage.WorkPacakgeStatus.CANCELLED.toString())) {
                    sb.append("from WorksPackage as wp where wp.egwStatus.code= ?  ");
                    paramList.add(getOfflinestatus());
                } else if (StringUtils.isNotBlank(getOfflinestatus()) && !getOfflinestatus().equals("-1")) {
                    sb.append("from WorksPackage as wp where wp.egwStatus.code= ? or (wp.egwStatus.code= ? and wp.id in(select stat.objectId from "
                            + "SetStatus stat where stat.egwStatus.code= ? and stat.id = (select"
                            + " max(stat1.id) from SetStatus stat1 where wp.id=stat1.objectId and stat1.objectType='"
                            + OBJECT_TYPE + "' ) and stat.objectType='" + OBJECT_TYPE + "')) ");
                    paramList.add(getOfflinestatus());
                    paramList.add(WorksPackage.WorkPacakgeStatus.APPROVED.toString());
                    paramList.add(getOfflinestatus());
                } else if (StringUtils.isNotBlank(getOfflinestatus()) && getOfflinestatus().equals("-1"))
                    sb.append("from WorksPackage as wp where wp.egwStatus.code<>'NEW'");
                status = offlinestatus;
            } else if (StringUtils.isNotBlank(getStatus())
                    && getStatus().equals(WorksPackage.WorkPacakgeStatus.APPROVED.toString())) {
                sb.append("from WorksPackage as wp where wp.egwStatus.code= ? and "
                        + " wp.id not in (select objectId from SetStatus where objectType='" + OBJECT_TYPE + "')");
                paramList.add(getStatus());
            } else if (StringUtils.isNotBlank(getStatus())
                    && getStatus().equals(WorksPackage.WorkPacakgeStatus.CANCELLED.toString())) {
                sb.append("from WorksPackage as wp where wp.egwStatus.code= ?  ");
                paramList.add(getStatus());
            } else if (StringUtils.isNotBlank(getStatus()) && !getStatus().equals("-1")
                    && !getStatus().equals(WorksPackage.WorkPacakgeStatus.APPROVED.toString())
                    && "view".equalsIgnoreCase(setStatus) && statusList.contains(getStatus())) {
                sb.append("from WorksPackage as wp where wp.egwStatus.code= ? and "
                        + " wp.id not in (select objectId from SetStatus where objectType='" + OBJECT_TYPE + "')");
                paramList.add(getStatus());
            } else if (StringUtils.isNotBlank(getStatus()) && !getStatus().equals("-1")) {
                sb.append("from WorksPackage as wp where wp.egwStatus.code= ? or (wp.egwStatus.code= ? and wp.id in(select stat.objectId from "
                        + "SetStatus stat where stat.egwStatus.code= ? and stat.id = (select"
                        + " max(stat1.id) from SetStatus stat1 where wp.id=stat1.objectId and stat1.objectType='"
                        + OBJECT_TYPE + "' ) and stat.objectType='" + OBJECT_TYPE + "')) ");
                paramList.add(getStatus());
                paramList.add(WorksPackage.WorkPacakgeStatus.APPROVED.toString());
                paramList.add(getStatus());
            } else if (StringUtils.isNotBlank(getStatus()) && getStatus().equals("-1"))
                sb.append("from WorksPackage as wp where wp.egwStatus.code<>'NEW'");

        }

        if (worksPackage.getDepartment() != null && worksPackage.getDepartment().getId() != null) {
            sb.append(" and wp.department.id= ?");
            paramList.add(worksPackage.getDepartment().getId());
        }
        if (StringUtils.isNotBlank(worksPackage.getWpNumber())) {
            sb.append(" and wp.wpNumber like ?");
            paramList.add("%" + worksPackage.getWpNumber() + "%");
        }
        if (StringUtils.isNotBlank(estimateNumber)) {
            sb.append(" and wp.id in (select wpd.worksPackage.id from WorksPackageDetails wpd where wpd.estimate.estimateNumber like ?) ");
            paramList.add("%" + estimateNumber + "%");
        }
        if (StringUtils.isNotBlank(source) && source.equalsIgnoreCase("cancelWP")) {
            sb.append(" and wp.egwStatus.code<> ? ");
            paramList.add("CANCELLED");
        }
        if (fromDate != null && toDate == null)
            addFieldError("enddate", getText("search.endDate.null"));
        if (toDate != null && fromDate == null)
            addFieldError("startdate", getText("search.startDate.null"));
        if (!DateUtils.compareDates(getToDate(), getFromDate()))
            addFieldError("enddate", getText("greaterthan.endDate.fromDate"));
        if (checkRetenderedWP != null && checkRetenderedWP) {
            sb.append(" and wp.id in (select r.worksPackage.id from Retender r where r.worksPackage.id=wp.id ) ");

            if (fromDate != null && toDate != null && getFieldErrors().isEmpty()) {
                sb.append(" and wp.id in (select r.worksPackage.id from Retender r where r.worksPackage.id=wp.id and r.retenderDate between ? and ? "
                        + "  and r.id=(select max(r1.id) from Retender r1 where r1.worksPackage.id=wp.id )  ) ");
                paramList.add(fromDate);
                paramList.add(toDate);
            }
        } else if (fromDate != null && toDate != null && getFieldErrors().isEmpty()) {
            sb.append(" and wp.packageDate between ? and ? ");
            paramList.add(fromDate);
            paramList.add(toDate);
        }

        if (StringUtils.isNotBlank(worksPackage.getTenderFileNumber())) {
            sb.append(" and wp.tenderFileNumber like ?");
            paramList.add("%" + worksPackage.getTenderFileNumber() + "%");
        }

        if ("createNegotiationForWP".equals(source))
            sb.append(" and wp.id not in(select " + "tr.tenderEstimate.worksPackage.id from TenderResponse tr where "
                    + "tr.egwStatus.code !='CANCELLED' and wp.id=tr.tenderEstimate.worksPackage.id)"
                    + " and wp.id not in(select " + "tr1.tenderEstimate.worksPackage.id from TenderResponse tr1 where "
                    + "tr1.egwStatus.code ='NEW' and wp.id=tr1.tenderEstimate.worksPackage.id)");

        final String query = sb.toString();
        final String countQuery = "select count(*) " + query;
        return new SearchQueryHQL(query, countQuery, paramList);
    }

    public String cancelWP() {
        final WorksPackage worksPackage = workspackageService.findById(wpCancelId, false);
        final PersonalInformation prsnlInfo = employeeService.getEmpForUserId(worksService.getCurrentLoggedInUserId());
        String empName = "";
        worksPackage.setEgwStatus(commonsService.getStatusByModuleAndCode("WorksPackage", "CANCELLED"));
        if (prsnlInfo.getEmployeeFirstName() != null)
            empName = prsnlInfo.getEmployeeFirstName();
        if (prsnlInfo.getEmployeeLastName() != null)
            empName = empName.concat(" ").concat(prsnlInfo.getEmployeeLastName());
        if (cancelRemarks != null && StringUtils.isNotBlank(cancelRemarks))
            getText("wp.canceled.by", new String[] { cancellationReason + " : " + cancelRemarks, empName });
        else
            getText("wp.canceled.by", new String[] { cancellationReason, empName });

        // TODO - The setter methods of variables in State.java are protected.
        // Need to alternative way to solve this issue.
        // Set the status and workflow state to cancelled
        /***
         * State oldEndState = worksPackage.getCurrentState(); Position owner =
         * prsnlInfo.getAssignment(new Date()).getPosition();
         * oldEndState.setCreatedBy(prsnlInfo.getUserMaster());
         * oldEndState.setModifiedBy(prsnlInfo.getUserMaster());
         * oldEndState.setCreatedDate(new Date());
         * oldEndState.setModifiedDate(new Date()); oldEndState.setOwner(owner);
         * oldEndState
         * .setValue(WorksPackage.WorkPacakgeStatus.CANCELLED.toString());
         * oldEndState.setText1(cancellationText);
         * worksPackage.changeState("END", owner, null);
         **/

        worksPackageNumber = worksPackage.getWpNumber();
        messageKey = worksPackageNumber + ": " + getText("workspackage.cancel.successful");
        return CANCEL_SUCCESS;
    }

    @Override
    public String search() {
        setPageSize(WorksConstants.PAGE_SIZE);
        final String retVal = super.search();

        getPositionAndUser();
        if (searchResult.getFullListSize() == 0)
            addFieldError("result not found", "No results found for search parameters");
        return retVal;
    }

    public void setDepartmentService(final DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void setWpCancelId(final Long wpCancelId) {
        this.wpCancelId = wpCancelId;
    }

    public String getCancelRemarks() {
        return cancelRemarks;
    }

    public void setCancelRemarks(final String cancelRemarks) {
        this.cancelRemarks = cancelRemarks;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(final String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(final String messageKey) {
        this.messageKey = messageKey;
    }

    public String getEstimateNumber() {
        return estimateNumber;
    }

    public void setEstimateNumber(final String estimateNumber) {
        this.estimateNumber = estimateNumber;
    }

    public void setWorkspackageService(final WorksPackageService workspackageService) {
        this.workspackageService = workspackageService;
    }

    public PersistenceService<SetStatus, Long> getWorksStatusService() {
        return worksStatusService;
    }

    public void setWorksStatusService(final PersistenceService<SetStatus, Long> worksStatusService) {
        this.worksStatusService = worksStatusService;
    }

    public Boolean getCheckRetenderedWP() {
        return checkRetenderedWP;
    }

    public void setCheckRetenderedWP(final Boolean checkRetenderedWP) {
        this.checkRetenderedWP = checkRetenderedWP;
    }

    public String getOfflinestatus() {
        return offlinestatus;
    }

    public void setOfflinestatus(final String offlinestatus) {
        this.offlinestatus = offlinestatus;
    }

}
