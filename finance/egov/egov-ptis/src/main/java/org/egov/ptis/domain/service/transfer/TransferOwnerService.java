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
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org
 ******************************************************************************/
package org.egov.ptis.domain.service.transfer;

import static org.egov.dcb.bean.Payment.AMOUNT;
import static org.egov.ptis.constants.PropertyTaxConstants.CURR_DMD_STR;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_ISACTIVE;
import static org.egov.ptis.constants.PropertyTaxConstants.TRANSFER;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.egov.collection.integration.models.BillReceiptInfo;
import org.egov.commons.Installment;
import org.egov.dcb.bean.Payment;
import org.egov.demand.model.EgBill;
import org.egov.demand.utils.DemandConstants;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infstr.services.PersistenceService;
import org.egov.ptis.client.integration.utils.CollectionHelper;
import org.egov.ptis.client.util.PropertyTaxNumberGenerator;
import org.egov.ptis.domain.bill.PropertyTaxBillable;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.dao.property.PropertyMutationMasterDAO;
import org.egov.ptis.domain.entity.demand.FloorwiseDemandCalculations;
import org.egov.ptis.domain.entity.demand.PTDemandCalculations;
import org.egov.ptis.domain.entity.demand.Ptdemand;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Document;
import org.egov.ptis.domain.entity.property.DocumentType;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyAddress;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.entity.property.PropertyMutation;
import org.egov.ptis.domain.entity.property.PropertyMutationMaster;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class TransferOwnerService extends PersistenceService<PropertyMutation, Long> {
    private static final Logger LOGGER = Logger.getLogger(TransferOwnerService.class);
   
    @Autowired
    @Qualifier("propertyImplService")
    private PersistenceService<PropertyImpl, Long> propertyImplService;
    
    @Autowired
    @Qualifier("basicPropertyService")
    private PersistenceService<BasicProperty, Long> basicPropertyService;
    
    @Autowired
    private PtDemandDao ptDemandDAO;
    
    @Autowired
    private PropertyMutationMasterDAO propertyMutationMasterDAO;
    
    @Autowired
    @Qualifier("fileStoreService")
    private FileStoreService fileStoreService;
    
    @Autowired
    @Qualifier("propertyTaxNumberGenerator")
    private PropertyTaxNumberGenerator propertyTaxNumberGenerator;
    
    @Autowired
    @Qualifier("documentTypePersistenceService")
    private PersistenceService<DocumentType, Long> documentTypePersistenceService;
    
    public void doPropertyTransfer(PropertyMutation propertyMutation, String upicNo, List<PropertyOwnerInfo> newOwnerInfos) {
        processAndStoreDocument(propertyMutation.getDocuments());
        PropertyImpl propertyImpl = getActiveProperty(upicNo);
        BasicProperty basicProperty = propertyImpl.getBasicProperty();
        propertyMutation.setBasicProperty(basicProperty);
        propertyMutation.setProperty(propertyImpl);
        propertyMutation.getTransferorInfos().addAll(basicProperty.getPropertyOwnerInfo());
        basicProperty.getPropertyOwnerInfo().clear();
        basicProperty.getPropertyOwnerInfo().addAll(newOwnerInfos);
        basicProperty.getPropertyMutations().add(propertyMutation);
        basicPropertyService.persist(basicProperty);
    }
    

    public PropertyImpl getActiveProperty(String upicNo) {
        return propertyImplService.findByNamedQuery("getPropertyByUpicNoAndStatus", upicNo, STATUS_ISACTIVE);
    }
    
    public String getCurrentPropertyTax(Property propertyImpl) {
        return ptDemandDAO.getDemandCollMap(propertyImpl).get(CURR_DMD_STR).toString();
    }
    
    public List<DocumentType> getPropertyTransferDocumentTypes() {
        return documentTypePersistenceService.findAllByNamedQuery(DocumentType.DOCUMENTTYPE_BY_MODULE_AND_SUBMODULE, "PTIS", TRANSFER);
    }
    
    public List<PropertyMutationMaster> getPropertyTransferReasons() {
        return propertyMutationMasterDAO.getAllPropertyMutationMastersByType(TRANSFER);
    }
    
    private void processAndStoreDocument(List<Document> documents) {
        documents.forEach(document -> {
            if (!document.getUploads().isEmpty()) {
                int fileCount = 0;
                for (File file : document.getUploads()) {
                        FileStoreMapper fileStore = fileStoreService
                                        .store(file, document.getUploadFileNames().get(fileCount),
                                                document.getUploadFileMimeTypes().get(fileCount++), "PTIS");
                        document.getFiles().add(fileStore);
                }
            }
        });
    }
    
    private Map<Installment, PTDemandCalculations> getDemandCalMap(Property oldProperty) {
        Map<Installment, PTDemandCalculations> dmdCalMap = new HashMap<Installment, PTDemandCalculations>();
        for (Ptdemand dmd : oldProperty.getPtDemandSet()) {
            dmdCalMap.put(dmd.getEgInstallmentMaster(), dmd.getDmdCalculations());
        }
        return dmdCalMap;

    }

    private Set<Ptdemand> cloneDemandSet(Property clonedProperty, Property oldProperty) {
        Map<Installment, PTDemandCalculations> dmdCalMap = getDemandCalMap(oldProperty);
        Set<Ptdemand> demandSet = new HashSet<Ptdemand>();
        PTDemandCalculations ptDmdCal;
        for (Ptdemand ptDmd : clonedProperty.getPtDemandSet()) {
            PTDemandCalculations OldPTDmdCal = dmdCalMap.get(ptDmd.getEgInstallmentMaster());
            ptDmdCal = new PTDemandCalculations(ptDmd, OldPTDmdCal.getPropertyTax(), OldPTDmdCal.getRateOfTax(), null, null,
                    cloneFlrWiseDmdCal(OldPTDmdCal.getFlrwiseDmdCalculations()), OldPTDmdCal.getTaxInfo(), OldPTDmdCal.getAlv());
            ptDmd.setDmdCalculations(ptDmdCal);
            demandSet.add(ptDmd);
        }
        return demandSet;
    }

    private Set<FloorwiseDemandCalculations> cloneFlrWiseDmdCal(Set<FloorwiseDemandCalculations> flrDmdCal) {
        FloorwiseDemandCalculations flrWiseDmdCal;
        Set<FloorwiseDemandCalculations> floorDmdCalSet = new HashSet<FloorwiseDemandCalculations>();
        for (FloorwiseDemandCalculations flrCal : flrDmdCal) {
            flrWiseDmdCal = new FloorwiseDemandCalculations(null, flrCal.getFloor(), flrCal.getPTDemandCalculations(), new Date(),
                    new Date(), flrCal.getCategoryAmt(), flrCal.getOccupancyRebate(), flrCal.getConstructionRebate(),
                    flrCal.getDepreciation(), flrCal.getUsageRebate());
            floorDmdCalSet.add(flrWiseDmdCal);
        }
        return floorDmdCalSet;
    }


    /*
     * This method returns changed owner corr address as a Set
     */
    public List<PropertyOwnerInfo> getNewPropOwnerAdd(Property clonedProperty, boolean chkIsCorrIsDiff, String corrAddress1,
            String corrAddress2, String corrPinCode, List<PropertyOwnerInfo> propertyOwnerProxy) {
        int orderNo = 1;
        for (PropertyOwnerInfo newOwner : propertyOwnerProxy) {
            if (newOwner.isNew()) {
                newOwner.setOrderNo(orderNo);
                newOwner.getOwner().setUsername(newOwner.getOwner().getMobileNumber());
                newOwner.getOwner().setPassword("NOT SET");
                orderNo++;
            }
        }
        return propertyOwnerProxy;
    }

    /*
     * This method returns modified Owner Details for email and contact number
     */
    private PropertyAddress getChangedOwnerContact(BasicProperty bp, String email, String mobileNo) {
        PropertyAddress propAddr = bp.getAddress();
        if (email != null && email != "") {
            propAddr.getUser().setEmailId(email);
        }
        if (mobileNo != null && mobileNo != "") {
            propAddr.getUser().setMobileNumber(mobileNo);
        }
        return propAddr;
    }

    /**
     * Generates Miscellaneous receipt
     * 
     * @param basicProperty
     * @param amount
     * @return BillReceiptInfo
     */
    public BillReceiptInfo generateMiscReceipt(BasicProperty basicProperty, BigDecimal amount) {
        LOGGER.debug("Inside generateMiscReceipt method, Mutation Amount: " + amount);
        org.egov.ptis.client.integration.impl.PropertyImpl property = new org.egov.ptis.client.integration.impl.PropertyImpl();
        PropertyTaxBillable billable = new PropertyTaxBillable();
        billable.setBasicProperty(basicProperty);
        billable.setIsMiscellaneous(Boolean.TRUE);
        billable.setMutationFee(amount);
        billable.setCollectionType(DemandConstants.COLLECTIONTYPE_COUNTER);
        billable.setCallbackForApportion(Boolean.FALSE);
        billable.setUserId(Long.valueOf(EgovThreadLocals.getUserId()));
        billable.setReferenceNumber(propertyTaxNumberGenerator
                .generateBillNumber(basicProperty.getPropertyID().getWard().getBoundaryNum().toString()));
        property.setBillable(billable);
        EgBill bill = property.createBill();
        CollectionHelper collHelper = new CollectionHelper(bill);
        Payment payment = preparePayment(amount);
        return collHelper.generateMiscReceipt(payment);
    }

    /**
     * Prepares payment information
     * 
     * @param amount
     * @return
     */
    private Payment preparePayment(BigDecimal amount) {
        LOGGER.debug("Inside preparePayment method, Mutation Amount: " + amount);
        Map<String, String> payDetailMap = new HashMap<String, String>();
        payDetailMap.put(AMOUNT, String.valueOf(amount));
        Payment payment = Payment.create(Payment.CASH, payDetailMap);
        LOGGER.debug("Exit from preparePayment method ");
        return payment;
    }

}
