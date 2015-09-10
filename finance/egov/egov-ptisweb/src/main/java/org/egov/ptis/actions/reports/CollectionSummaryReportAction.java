package org.egov.ptis.actions.reports;

import static java.math.BigDecimal.ZERO;
import static org.egov.infra.web.struts.actions.BaseFormAction.VIEW;
import static org.egov.ptis.constants.PropertyTaxConstants.ADMIN_HIERARCHY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.COLL_MODES_MAP;
import static org.egov.ptis.constants.PropertyTaxConstants.LOCATION_HIERARCHY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.NON_VAC_LAND_PROPERTY_TYPE_CATEGORY;
import static org.egov.ptis.constants.PropertyTaxConstants.VAC_LAND_PROPERTY_TYPE_CATEGORY;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.ptis.actions.common.CommonServices;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.domain.entity.property.CollectionSummary;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.validator.annotations.Validations;

@SuppressWarnings("serial")
@ParentPackage("egov")
@Validations
@Results({ @Result(name = VIEW, location = "collectionSummaryReport-view.jsp") })
public class CollectionSummaryReportAction extends BaseFormAction {
    /**
     *
     */
    private static final long serialVersionUID = -3560529685172919434L;
    private final Logger LOGGER = Logger.getLogger(getClass());
    private String mode;
    private Map<Long, String> zoneBndryMap;
    private Map<Long, String> wardBndryMap;
    private Map<Long, String> blockBndryMap;
    private Map<Long, String> localityBndryMap;
    private Map<Character, String> collectionModesMap;
    private String fromDate;
    private String toDate;
    private String boundaryId;
    private String collMode;
    private String transMode;
    @Autowired
    public PropertyTaxUtil propertyTaxUtil;
    @Autowired
    public FinancialYearDAO financialYearDAO;
    private List<Map<String, Object>> resultList;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    public static final String ZONEWISE = "zoneWise";
    public static final String WARDWISE = "wardWise";
    public static final String BLOCKWISE = "blockWise";
    public static final String LOCALITYWISE = "localityWise";
    public static final String USAGEWISE = "usageWise";
    private String dateSelected;
    private static final String CURR_DATE = "currentDate";
    @Autowired
    private BoundaryService boundaryService;
    private String finYearStartDate;
    private Map<String, String> propTypeCategoryMap = new TreeMap<String, String>();
    private Long zoneId;
    private Long wardId;
    private Long areaId;
    private String propTypeCategoryId;

    BigDecimal taxAmount = ZERO, totTaxAmt = ZERO, arrearTaxAmount = ZERO, totArrearTaxAmt = ZERO, penaltyAmount = ZERO,
            totPenaltyAmt = ZERO;
    BigDecimal arrearPenaltyAmount = ZERO, totArrearPenaltyAmt = ZERO, libCessAmount = ZERO, totLibCessAmt = ZERO,
            arrearLibCessAmount = ZERO;
    BigDecimal totArrearLibCessAmt = ZERO, grandTotal = ZERO;
    Long prevZone = null, prevWard = null, prevBlock = null, prevLocality = null;
    String prevPropertyType = null;

    @Override
    public Object getModel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void prepare() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Entered into prepare method");
        super.prepare();
        setZoneBndryMap(CommonServices.getFormattedBndryMap(boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName("Zone", ADMIN_HIERARCHY_TYPE)));
        setWardBndryMap(CommonServices.getFormattedBndryMap(boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName("Ward", ADMIN_HIERARCHY_TYPE)));
        setBlockBndryMap(CommonServices.getFormattedBndryMap(boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName("Block", ADMIN_HIERARCHY_TYPE)));
        setLocalityBndryMap(CommonServices.getFormattedBndryMap(boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName("Locality", LOCATION_HIERARCHY_TYPE)));
        addDropdownData("instrumentTypeList", propertyTaxUtil.prepareInstrumentTypeList());
        setCollectionModesMap(COLL_MODES_MAP);
        final CFinancialYear finyear = financialYearDAO.getFinancialYearByDate(new Date());
        if (finyear != null)
            finYearStartDate = sdf.format(finyear.getStartingDate());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Exit from prepare method");

        super.prepare();
        final List<Boundary> zoneList = boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName("Zone",
                ADMIN_HIERARCHY_TYPE);
        addDropdownData("zoneList", zoneList);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Zone id : " + zoneId + ", " + "Ward id : " + wardId);
        prepareWardDropDownData(zoneId != null, wardId != null);
        if (wardId == null || wardId.equals(-1))
            addDropdownData("blockList", Collections.EMPTY_LIST);
        prepareBlockDropDownData(wardId != null, areaId != null);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Exit from prepare method");
        propTypeCategoryMap.putAll(VAC_LAND_PROPERTY_TYPE_CATEGORY);
        propTypeCategoryMap.putAll(NON_VAC_LAND_PROPERTY_TYPE_CATEGORY);
        setPropTypeCategoryMap(propTypeCategoryMap);
    }

    /**
     * Loads ward for a selected Zone
     * @param zoneExists
     * @param wardExists
     */
    @SuppressWarnings("unchecked")
    private void prepareWardDropDownData(final boolean zoneExists, final boolean wardExists) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Entered into prepareWardDropDownData method");
            LOGGER.debug("Zone Exists ? : " + zoneExists + ", " + "Ward Exists ? : " + wardExists);
        }
        if (zoneExists && wardExists) {
            List<Boundary> wardList = new ArrayList<Boundary>();
            wardList = boundaryService.getActiveChildBoundariesByBoundaryId(getZoneId());
            addDropdownData("wardList", wardList);
        } else
            addDropdownData("wardList", Collections.EMPTY_LIST);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Exit from prepareWardDropDownData method");
    }

    /**
     * Loads block based on selected ward
     * @param wardExists
     * @param blockExists
     */
    @SuppressWarnings("unchecked")
    private void prepareBlockDropDownData(final boolean wardExists, final boolean blockExists) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Entered into prepareBlockDropDownData method");
            LOGGER.debug("Ward Exists ? : " + wardExists + ", " + "Block Exists ? : " + blockExists);
        }
        if (wardExists && blockExists) {
            List<Boundary> blockList = new ArrayList<Boundary>();
            blockList = boundaryService.getActiveChildBoundariesByBoundaryId(getWardId());
            addDropdownData("blockList", blockList);
        } else
            addDropdownData("blockList", Collections.EMPTY_LIST);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Exit from prepareWardDropDownData method");
    }

    /**
     * @return to Zonewise Collection Summary Search Screen
     */
    @SkipValidation
    @Action(value = "/reports/collectionSummaryReport-zoneWise")
    public String zoneWise() {
        fromDate = finYearStartDate;
        toDate = sdf.format(new Date());
        setMode("zoneWise");
        return VIEW;
    }

    /**
     * @return to Wardwise Collection Summary Search Screen
     */
    @SkipValidation
    @Action(value = "/reports/collectionSummaryReport-wardWise")
    public String wardWise() {
        fromDate = finYearStartDate;
        toDate = sdf.format(new Date());
        setMode("wardWise");
        return VIEW;
    }

    /**
     * @return to Blockwise Collection Summary Search Screen
     */
    @SkipValidation
    @Action(value = "/reports/collectionSummaryReport-blockWise")
    public String blockWise() {
        fromDate = finYearStartDate;
        toDate = sdf.format(new Date());
        setMode("blockWise");
        return VIEW;
    }

    /**
     * @return to Localitywise Collection Summary Search Screen
     */
    @SkipValidation
    @Action(value = "/reports/collectionSummaryReport-localityWise")
    public String localityWise() {
        fromDate = finYearStartDate;
        toDate = sdf.format(new Date());
        setMode("localityWise");
        return VIEW;
    }

    /**
     * @return to Property Usagewise Collection Summary Search Screen
     */
    @SkipValidation
    @Action(value = "/reports/collectionSummaryReport-usageWise")
    public String usageWise() {
        fromDate = finYearStartDate;
        toDate = sdf.format(new Date());
        setMode("usageWise");
        return VIEW;
    }

    /**
     * Invoked from Collection Summary screens to retrieve aggregated collection summary for selected zone / ward / block /
     * locality or usagetype
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    @ValidationErrorPage(value = "view")
    @Action(value = "/reports/collectionSummaryReport-list")
    public void list() throws ParseException {
        List<CollectionSummaryReportResult> resultList = new ArrayList<CollectionSummaryReportResult>();
        String result = null;
        final Query query = prepareQuery();
        resultList = prepareOutput(query.list());
        // for converting resultList to JSON objects.
        // Write back the JSON Response.
        result = new StringBuilder("{ \"data\":").append(toJSON(resultList)).append("}").toString();
        final HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            IOUtils.write(result, response.getWriter());
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param object
     * @return
     */
    private Object toJSON(final Object object) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.registerTypeAdapter(CollectionSummaryReportResult.class,
                new CollectionSummaryReportHelperAdaptor()).create();
        final String json = gson.toJson(object);
        return json;
    }

    /**
     * @return
     */
    public Query prepareQuery() {
        try {
            final String currDate = sdf.format(new Date());
            if (currDate.equals(fromDate) || currDate.equals(toDate))
                dateSelected = CURR_DATE;
            return propertyTaxUtil.prepareQueryforCollectionSummaryReport(fromDate, toDate, collMode, transMode, mode,
                    boundaryId,
                    propTypeCategoryId, zoneId, wardId, areaId);
        } catch (final Exception e) {
            e.printStackTrace();
            LOGGER.error("Error occured in Class : CollectionSummaryReportAction  Method : list", e);
            throw new ApplicationRuntimeException("Error occured in Class : CollectionSummaryReportAction  Method : list "
                    + e.getMessage());
        }
    }

    /**
     * @param collectionSummaryList
     * @return
     * @throws ParseException
     */
    private List<CollectionSummaryReportResult> prepareOutput(final List<CollectionSummary> collectionSummaryList)
            throws ParseException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Entered into prepareResultList method");
        final List<CollectionSummaryReportResult> csrFinalList = new LinkedList<CollectionSummaryReportResult>();

        try {
            if (collectionSummaryList != null && !collectionSummaryList.isEmpty()) {
                for (final CollectionSummary collSummObj : collectionSummaryList) {
                    final CollectionSummary collSummary = collSummObj;
                    if (prevZone == null && prevWard == null && prevBlock == null && prevLocality == null
                            && prevPropertyType == null)
                        initializeReasonAmount(collSummary);
                    else if (prevZone != null && prevZone.equals(collSummary.getZoneId().getId())
                            || prevWard != null && prevWard.equals(collSummary.getWardId().getId())
                            || prevBlock != null && prevBlock.equals(collSummary.getAreaId().getId())
                            || prevLocality != null && prevLocality.equals(collSummary.getLocalityId().getId())
                            || prevPropertyType != null &&
                            prevPropertyType.equalsIgnoreCase(collSummary.getProperty().getPropertyDetail().getCategoryType())) {
                        if (taxAmount != null)
                            taxAmount = collSummary.getTaxColl() != null ? taxAmount.add(collSummary.getTaxColl())
                                    : taxAmount;
                        else
                            taxAmount = collSummary.getTaxColl();
                        if (arrearTaxAmount != null)
                            arrearTaxAmount = collSummary.getArrearTaxColl() != null ? arrearTaxAmount.add(collSummary
                                    .getArrearTaxColl())
                                    : arrearTaxAmount;
                        else
                            arrearTaxAmount = collSummary.getArrearTaxColl();
                        if (penaltyAmount != null)
                            penaltyAmount = collSummary.getPenaltyColl() != null ? penaltyAmount.add(collSummary
                                    .getPenaltyColl()) : penaltyAmount;
                        else
                            penaltyAmount = collSummary.getPenaltyColl();
                        if (arrearPenaltyAmount != null)
                            arrearPenaltyAmount = collSummary.getArrearPenaltyColl() != null ? arrearPenaltyAmount
                                    .add(collSummary
                                            .getArrearPenaltyColl()) : arrearPenaltyAmount;
                        else
                            arrearPenaltyAmount = collSummary.getArrearPenaltyColl();
                        if (libCessAmount != null)
                            libCessAmount = collSummary.getLibCessColl() != null ? libCessAmount.add(collSummary
                                    .getLibCessColl()) : libCessAmount;
                        else
                            libCessAmount = collSummary.getLibCessColl();
                        if (arrearLibCessAmount != null)
                            arrearLibCessAmount = collSummary.getArrearLibCessColl() != null ? arrearLibCessAmount
                                    .add(collSummary
                                            .getArrearLibCessColl()) : arrearLibCessAmount;
                        else
                            arrearLibCessAmount = collSummary.getArrearLibCessColl();
                    } else {
                        csrFinalList.add(getCalculatedResultMap());
                        initializeReasonAmount(collSummary);
                    }
                }
                // Last Row
                csrFinalList.add(getCalculatedResultMap());
            }
        } catch (final Exception e) {
            LOGGER.error("Exception in prepareBndryWiseResultList method : " + e.getMessage());
            e.printStackTrace();
            throw new ApplicationRuntimeException("Exception in prepareBndryWiseResultList method : ", e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Exit from prepareResultList method");
        return csrFinalList;
    }

    /**
     * @param collSummary
     */
    private void initializeReasonAmount(final CollectionSummary collSummary) {
        if (mode.equals(ZONEWISE))
            prevZone = collSummary.getZoneId().getId();
        else if (mode.equals(WARDWISE))
            prevWard = collSummary.getWardId().getId();
        else if (mode.equals(BLOCKWISE))
            prevBlock = collSummary.getAreaId().getId();
        else if (mode.equals(LOCALITYWISE))
            prevLocality = collSummary.getLocalityId().getId();
        else if (mode.equals(USAGEWISE))
            prevPropertyType = collSummary.getProperty().getPropertyDetail().getCategoryType();
        taxAmount = collSummary.getTaxColl();
        arrearTaxAmount = collSummary.getArrearTaxColl();
        penaltyAmount = collSummary.getPenaltyColl();
        arrearPenaltyAmount = collSummary.getArrearPenaltyColl();
        libCessAmount = collSummary.getLibCessColl();
        arrearLibCessAmount = collSummary.getArrearLibCessColl();
    }

    /**
     * @return
     */
    private CollectionSummaryReportResult getCalculatedResultMap() {
        final CollectionSummaryReportResult result = new CollectionSummaryReportResult();

        if (mode.equals(ZONEWISE))
            result.setBoundaryName(boundaryService.getBoundaryById(prevZone).getName());
        else if (mode.equals(WARDWISE))
            result.setBoundaryName(boundaryService.getBoundaryById(prevWard).getName());
        else if (mode.equals(BLOCKWISE))
            result.setBoundaryName(boundaryService.getBoundaryById(prevBlock).getName());
        else if (mode.equals(LOCALITYWISE))
            result.setBoundaryName(boundaryService.getBoundaryById(prevLocality).getName());
        else if (mode.equals(USAGEWISE))
            result.setPropertyType(prevPropertyType);
        result.setArrearTaxAmount(arrearTaxAmount != null ? arrearTaxAmount : ZERO);
        totArrearTaxAmt = arrearTaxAmount != null ? totArrearTaxAmt.add(arrearTaxAmount) : totArrearTaxAmt
                .add(ZERO);

        result.setArrearLibraryCess(arrearLibCessAmount != null ? arrearLibCessAmount : ZERO);
        totArrearLibCessAmt = arrearLibCessAmount != null ? totArrearLibCessAmt.add(arrearLibCessAmount) : totArrearLibCessAmt
                .add(ZERO);
        result.setArrearTotal(totArrearTaxAmt.add(totArrearLibCessAmt));

        result.setTaxAmount(taxAmount);
        totTaxAmt = taxAmount != null ? totTaxAmt.add(taxAmount) : totTaxAmt.add(ZERO);

        result.setLibraryCess(libCessAmount != null ? libCessAmount : ZERO);
        totLibCessAmt = libCessAmount != null ? totLibCessAmt.add(libCessAmount) : totLibCessAmt
                .add(ZERO);
        result.setCurrentTotal(totTaxAmt.add(totLibCessAmt));

        result.setPenalty(penaltyAmount != null ? penaltyAmount : ZERO);
        totPenaltyAmt = penaltyAmount != null ? totPenaltyAmt.add(penaltyAmount) : totPenaltyAmt
                .add(ZERO);

        result.setArrearPenalty(arrearPenaltyAmount != null ? arrearPenaltyAmount : ZERO);
        totArrearPenaltyAmt = arrearPenaltyAmount != null ? totArrearPenaltyAmt.add(arrearPenaltyAmount) : totArrearPenaltyAmt
                .add(ZERO);
        result.setPenaltyTotal(totPenaltyAmt.add(totArrearPenaltyAmt));

        if (arrearTaxAmount != null)
            taxAmount = taxAmount.add(arrearTaxAmount);
        if (penaltyAmount != null)
            taxAmount = taxAmount.add(penaltyAmount);
        if (arrearPenaltyAmount != null)
            taxAmount = taxAmount.add(arrearPenaltyAmount);
        if (libCessAmount != null)
            taxAmount = taxAmount.add(libCessAmount);
        if (arrearLibCessAmount != null)
            taxAmount = taxAmount.add(arrearLibCessAmount);
        result.setTotal(taxAmount);
        return result;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public Map<Character, String> getCollectionModesMap() {
        return collectionModesMap;
    }

    public void setCollectionModesMap(final Map<Character, String> collectionModesMap) {
        this.collectionModesMap = collectionModesMap;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(final String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(final String toDate) {
        this.toDate = toDate;
    }

    public String getCollMode() {
        return collMode;
    }

    public void setCollMode(final String collMode) {
        this.collMode = collMode;
    }

    public String getTransMode() {
        return transMode;
    }

    public void setTransMode(final String transMode) {
        this.transMode = transMode;
    }

    public List<Map<String, Object>> getResultList() {
        return resultList;
    }

    public void setResultList(final List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    public String getDateSelected() {
        return dateSelected;
    }

    public void setDateSelected(final String dateSelected) {
        this.dateSelected = dateSelected;
    }

    public String getBoundaryId() {
        return boundaryId;
    }

    public void setBoundaryId(final String boundaryId) {
        this.boundaryId = boundaryId;
    }

    public Map<Long, String> getWardBndryMap() {
        return wardBndryMap;
    }

    public void setWardBndryMap(final Map<Long, String> wardBndryMap) {
        this.wardBndryMap = wardBndryMap;
    }

    public Map<Long, String> getBlockBndryMap() {
        return blockBndryMap;
    }

    public void setBlockBndryMap(final Map<Long, String> blockBndryMap) {
        this.blockBndryMap = blockBndryMap;
    }

    public Map<Long, String> getLocalityBndryMap() {
        return localityBndryMap;
    }

    public void setLocalityBndryMap(final Map<Long, String> localityBndryMap) {
        this.localityBndryMap = localityBndryMap;
    }

    public Map<Long, String> getZoneBndryMap() {
        return zoneBndryMap;
    }

    public void setZoneBndryMap(final Map<Long, String> zoneBndryMap) {
        this.zoneBndryMap = zoneBndryMap;
    }

    public Map<String, String> getPropTypeCategoryMap() {
        return propTypeCategoryMap;
    }

    public void setPropTypeCategoryMap(final Map<String, String> propTypeCategoryMap) {
        this.propTypeCategoryMap = propTypeCategoryMap;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(final Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(final Long wardId) {
        this.wardId = wardId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(final Long areaId) {
        this.areaId = areaId;
    }

    public String getPropTypeCategoryId() {
        return propTypeCategoryId;
    }

    public void setPropTypeCategoryId(final String propTypeCategoryId) {
        this.propTypeCategoryId = propTypeCategoryId;
    }

    public String getFinYearStartDate() {
        return finYearStartDate;
    }

    public void setFinYearStartDate(final String finYearStartDate) {
        this.finYearStartDate = finYearStartDate;
    }

}
