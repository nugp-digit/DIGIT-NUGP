/*
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
package org.egov.ptis.actions.transfer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.web.utils.WebUtils;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.DocumentType;
import org.egov.ptis.domain.entity.property.PropertyMutation;
import org.egov.ptis.domain.entity.property.PropertyMutationMaster;
import org.egov.ptis.domain.service.transfer.TransferOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Results({ @Result(name = BaseFormAction.NEW, location = "transfer/transferProperty-new.jsp"),
    @Result(name = BaseFormAction.EDIT, location = "transfer/transferProperty-edit.jsp"),
    @Result(name = "workFlowError", location = "workflow/workflow-error.jsp"),
    @Result(name = PropertyTransferAction.ACK, location = "transfer/transferProperty-ack.jsp",type="redirect"),
    @Result(name = "balance", location = "transfer/transferProperty-balance.jsp") })
@Namespace("/property/transfer")
public class PropertyTransferAction extends BaseFormAction {

    private static final long serialVersionUID = 1L;
    private static final String WTMS_TAXDUE_RESTURL = "%s/wtms/rest/watertax/due/byptno/%s";
    public static final String ACK = "ack";
    
    // Form Binding Model
    private PropertyMutation propertyMutation = new PropertyMutation();

    // Dependent Services
    @Autowired
    @Qualifier("transferOwnerService")
    private TransferOwnerService transferOwnerService;
    
    
    // Model and View data
    private Long mutationId;
    private String indexNumber;
    private String wfErrorMsg;
    private String currentPropertyTax;
    private List<DocumentType> documentTypes = new ArrayList<>();
    private BasicProperty basicproperty;
    
    public PropertyTransferAction() {
        addRelatedEntity("mutationReason", PropertyMutationMaster.class);
    }
    
    @SkipValidation
    @Action(value = "/new")
    public String showTransferForm() {
        if (basicproperty.isUnderWorkflow()) {
            wfErrorMsg = ("Could not do property transfer now, property is undergoing some workflow.");
            return "workFlowError";
        } else {
            final String wtmsRestURL = String.format(WTMS_TAXDUE_RESTURL, WebUtils.extractRequestDomainURL(ServletActionContext.getRequest(), false) , indexNumber);
            final boolean anyTaxDues = transferOwnerService.checkForTaxDues(wtmsRestURL,indexNumber);
            if (anyTaxDues)
                return "balance";
            else
                return NEW;
        }
    }

    @ValidationErrorPage(value = NEW)
    @Action(value = "/save")
    public String save() {
        transferOwnerService.initiatePropertyTransfer(basicproperty, propertyMutation);
        return ACK;
    }
    
    @SkipValidation
    @Action(value = "/view")
    public String view() {
        return EDIT;
    }
    
    @Override
    public void prepare() {
        if (StringUtils.isNotBlank(indexNumber))
            basicproperty = transferOwnerService.getBasicPropertyByUpicNo(indexNumber);
        
        if (mutationId != null) {
            propertyMutation = transferOwnerService.load(mutationId, PropertyMutation.class);
            basicproperty = propertyMutation.getBasicProperty();
        }
        propertyMutation.getDocuments();
        this.currentPropertyTax = transferOwnerService.getCurrentPropertyTax(basicproperty.getActiveProperty());
        this.documentTypes = transferOwnerService.getPropertyTransferDocumentTypes();
        addDropdownData("MutationReason", transferOwnerService.getPropertyTransferReasons());
        super.prepare();
    }

    public String getCurrentPropertyTax() {
        return this.currentPropertyTax;
    }
    
    @Override
    public Object getModel() {
        return propertyMutation;
    }

    public String getWfErrorMsg() {
        return wfErrorMsg;
    }
    
    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(final String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public BasicProperty getBasicproperty() {
        return basicproperty;
    }

    public List<DocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public Long getMutationId() {
        return mutationId;
    }

    public void setMutationId(Long mutationId) {
        this.mutationId = mutationId;
    }
}
