/* eGov suite of products aim to improve the internal efficiency,transparency,
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
package org.egov.adtax.web.controller.hoarding;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.adtax.entity.Advertisement;
import org.egov.adtax.entity.AdvertisementPermitDetail;
import org.egov.adtax.entity.SubCategory;
import org.egov.adtax.entity.enums.AdvertisementStatus;
import org.egov.adtax.utils.constants.AdvertisementTaxConstants;
import org.egov.adtax.web.controller.common.HoardingControllerSupport;
import org.egov.commons.Installment;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.utils.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hoarding")
public class CreateAdvertisementController extends HoardingControllerSupport {
   

    /*
     * @ModelAttribute public Hoarding hoarding() { return new Hoarding(); }
     */
    @RequestMapping(value = "child-boundaries", method = GET, produces = APPLICATION_JSON_VALUE)
    public @ResponseBody List<Boundary> childBoundaries(@RequestParam final Long parentBoundaryId) {
        return boundaryService.getActiveChildBoundariesByBoundaryId(parentBoundaryId);
    }

    @RequestMapping(value = "calculateTaxAmount", method = GET, produces = APPLICATION_JSON_VALUE)
    public @ResponseBody Double getTaxAmount(@RequestParam final Long unitOfMeasureId,
            @RequestParam final Double measurement, @RequestParam final Long subCategoryId,
            @RequestParam final Long rateClassId) {
        Double rate = Double.valueOf(0);
        rate = advertisementRateService.getAmountBySubcategoryUomClassAndMeasurement(subCategoryId, unitOfMeasureId,
                rateClassId, measurement);
        if (rate == null)
            return Double.valueOf(0);
        // TODO MULTIPLY WITH MEASUREMENT TO GET TOTAL AMOUNT.
        return BigDecimal.valueOf(rate).multiply(BigDecimal.valueOf(measurement)).setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    @RequestMapping(value = "subcategories", method = GET, produces = APPLICATION_JSON_VALUE)
    public @ResponseBody List<SubCategory> hoardingSubcategories(@RequestParam final Long categoryId) {
        return subCategoryService.getAllActiveSubCategoryByCategoryId(categoryId);
    }

    @RequestMapping(value = "create", method = GET)
    public String createHoardingForm(@ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail,final Model model) {
        prepareWorkflow(model, advertisementPermitDetail, new WorkflowContainer());
        model.addAttribute("additionalRule","CREATEHOARDING");
        model.addAttribute("stateType", advertisementPermitDetail.getClass().getSimpleName());
        return "hoarding-create";
    }

    @RequestMapping(value = "createLegacy", method = GET)
    public String createLegacyHoardingForm(@ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail) {
        if(advertisementPermitDetail!=null && advertisementPermitDetail.getAdvertisement()==null)
        {
            advertisementPermitDetail.setAdvertisement(new Advertisement());
        }
        advertisementPermitDetail.setStatus(advertisementPermitDetailService.getStatusByModuleAndCode(AdvertisementTaxConstants.APPLICATION_STATUS_ADTAXPERMITGENERATED));
        advertisementPermitDetail.getAdvertisement().setStatus(AdvertisementStatus.ACTIVE);
        advertisementPermitDetail.getAdvertisement().setLegacy(Boolean.TRUE);
        return "hoarding-createLegacy";
    }

    @RequestMapping(value = "create", method = POST)
    public String createAdvertisement(@Valid @ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail, final BindingResult resultBinder,
            final RedirectAttributes redirAttrib,final HttpServletRequest request, final Model model, @RequestParam String workFlowAction) {
        validateHoardingDocs(advertisementPermitDetail, resultBinder);
        validateApplicationDate(advertisementPermitDetail, resultBinder);
        if (advertisementPermitDetail.getState() == null)
            advertisementPermitDetail.setStatus(advertisementPermitDetailService.getStatusByModuleAndCode(AdvertisementTaxConstants.APPLICATION_STATUS_CREATED));
        advertisementPermitDetail.getAdvertisement().setStatus(AdvertisementStatus.INACTIVE);
        if (resultBinder.hasErrors())
            return "hoarding-create";
        
        storeHoardingDocuments(advertisementPermitDetail);
        
        Long approvalPosition = 0l;
        String approvalComment = "";
        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");
        if (request.getParameter("workFlowAction") != null)
            workFlowAction = request.getParameter("workFlowAction");
        if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));
        advertisementPermitDetail.getAdvertisement().setPenaltyCalculationDate(advertisementPermitDetail.getApplicationDate());
        advertisementPermitDetailService.createAdvertisementPermitDetail(advertisementPermitDetail, approvalPosition, approvalComment, "CREATEADVERTISEMENT", workFlowAction);
        redirAttrib.addFlashAttribute("advertisementPermitDetail", advertisementPermitDetail);
        redirAttrib.addFlashAttribute("message", "hoarding.create.success");
        return "redirect:/hoarding/success/" + advertisementPermitDetail.getId();
    }
    
    private void validateApplicationDate(AdvertisementPermitDetail advertisementPermitDetail, BindingResult resultBinder) {
       if(advertisementPermitDetail!=null && advertisementPermitDetail.getApplicationDate()!=null )
       {
           final Installment installmentObj = advertisementDemandService.getCurrentInstallment();
           if (installmentObj != null && installmentObj.getFromDate() != null)
           {
               if( advertisementPermitDetail.getApplicationDate().after(DateUtils.endOfDay(installmentObj.getToDate()))||
                       advertisementPermitDetail.getApplicationDate().before(DateUtils.startOfDay(installmentObj.getFromDate())))
               {
                   resultBinder.rejectValue("applicationDate", "invalid.applicationDate");
               }
           }
           
       }
        
    }
    private void validateLegacyApplicationDate(AdvertisementPermitDetail advertisementPermitDetail, BindingResult resultBinder) {
        /*if(hoarding!=null && hoarding.getApplicationDate()!=null )
        {
            final Installment installmentObj = advertisementDemandService.getCurrentInstallment();
            if (installmentObj != null && installmentObj.getToDate() != null)
            {
                if( hoarding.getApplicationDate().after(DateUtils.endOfDay(installmentObj.getToDate())))
                {
                    resultBinder.rejectValue("applicationDate", "invalid.applicationDateForLegacy");
                }
            }
            
        }
      */   
     }
    @RequestMapping(value = "createLegacy", method = POST)
    public String createLegacyHoarding(@Valid @ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail,
            final BindingResult resultBinder, final RedirectAttributes redirAttrib) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        validateHoardingDocs(advertisementPermitDetail, resultBinder);
        validateLegacyApplicationDate(advertisementPermitDetail, resultBinder);
        if (resultBinder.hasErrors())
            return "hoarding-createLegacy";
        storeHoardingDocuments(advertisementPermitDetail);

        final Installment installmentObj = advertisementDemandService.getCurrentInstallment();
        if (installmentObj != null && installmentObj.getFromDate() != null)
            try {
                advertisementPermitDetail.getAdvertisement().setPenaltyCalculationDate(formatter.parse(formatter.format(installmentObj.getFromDate())));
            } catch (final ParseException e) {
                e.printStackTrace();// TODO: CHECK THIS CASE AGAIN.
            }

        advertisementPermitDetailService.createAdvertisementPermitDetail(advertisementPermitDetail,null,null,null,null);
        redirAttrib.addFlashAttribute("message", "hoarding.create.success");
        return "redirect:/hoarding/createLegacy";
    }

    @RequestMapping(value = "/success/{id}", method = GET)
    public ModelAndView successView(@PathVariable("id") final String id, @ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail) {
        return new ModelAndView("hoarding/hoarding-success", "hoarding", advertisementPermitDetailService.findBy(Long.valueOf(id)));

    }
  
}
