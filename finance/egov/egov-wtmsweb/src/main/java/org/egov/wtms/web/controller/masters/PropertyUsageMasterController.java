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

package org.egov.wtms.web.controller.masters;

import org.egov.wtms.masters.entity.WaterPropertyUsage;
import org.egov.wtms.masters.service.PropertyTypeService;
import org.egov.wtms.masters.service.UsageTypeService;
import org.egov.wtms.masters.service.WaterPropertyUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/masters")
public class PropertyUsageMasterController {

    private final PropertyTypeService propertyTypeService;

    private final WaterPropertyUsageService waterPropertyUsageService;

    private final UsageTypeService usageTypeService;

    @Autowired
    public PropertyUsageMasterController(final PropertyTypeService propertyTypeService,
            final WaterPropertyUsageService waterPropertyUsageService, final UsageTypeService usageTypeService) {
        this.propertyTypeService = propertyTypeService;
        this.waterPropertyUsageService = waterPropertyUsageService;
        this.usageTypeService = usageTypeService;
    }

    @RequestMapping(value = "/propertyUsageMaster", method = GET)
    public String viewForm(final Model model) {
        final WaterPropertyUsage waterPropertyUsage = new WaterPropertyUsage();
        model.addAttribute("waterPropertyUsage", waterPropertyUsage);
        model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
        model.addAttribute("usageType", usageTypeService.getActiveUsageTypes());
        model.addAttribute("reqAttr", "false");
        return "property-usage-master";
    }

    @RequestMapping(value = "/propertyUsageMaster", method = RequestMethod.POST)
    public String createPropertyUsageData(@Valid @ModelAttribute final WaterPropertyUsage waterPropertyUsage,
            final BindingResult errors, final Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
            model.addAttribute("usageType", usageTypeService.getActiveUsageTypes());
            return "property-usage-master";
        } else
            waterPropertyUsageService.createWaterPropertyUsage(waterPropertyUsage);
        return getPropertyUsageList(model);
    }

    @RequestMapping(value = "/propertyUsageMaster/list", method = GET)
    public String getPropertyUsageList(final Model model) {
        final List<WaterPropertyUsage> waterPropertyUsageList = waterPropertyUsageService.findAll();
        model.addAttribute("waterPropertyUsageList", waterPropertyUsageList);
        return "property-usage-master-list";

    }

    @RequestMapping(value = "/propertyUsageMaster/{waterPropertyUsageId}", method = GET)
    public String getPropertyUsageDetails(final Model model, @PathVariable final String waterPropertyUsageId) {
        final WaterPropertyUsage waterPropertyUsage = waterPropertyUsageService
                .findOne(Long.parseLong(waterPropertyUsageId));
        model.addAttribute("waterPropertyUsage", waterPropertyUsage);
        model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
        model.addAttribute("usageType", usageTypeService.getActiveUsageTypes());
        model.addAttribute("reqAttr", "true");
        return "property-usage-master";
    }

    @RequestMapping(value = "/propertyUsageMaster/{waterPropertyUsageId}", method = RequestMethod.POST)
    public String editPropertyUsageData(@Valid @ModelAttribute final WaterPropertyUsage waterPropertyUsage,
            final BindingResult errors, final Model model, @PathVariable final long waterPropertyUsageId) {
        if (errors.hasErrors()) {
            model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
            model.addAttribute("usageType", usageTypeService.getActiveUsageTypes());
            return "property-usage-master";
        } else
            waterPropertyUsageService.updateWaterPropertyUsage(waterPropertyUsage);
        return getPropertyUsageList(model);

    }
}
