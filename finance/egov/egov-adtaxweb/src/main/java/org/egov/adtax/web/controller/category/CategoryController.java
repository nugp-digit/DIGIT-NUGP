/** eGov suite of products aim to improve the internal efficiency,transparency,
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
package org.egov.adtax.web.controller.category;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.validation.Valid;

import org.egov.adtax.entity.HoardingCategory;
import org.egov.adtax.service.HoardingCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final HoardingCategoryService hoardingCategoryService;

    @Autowired
    public CategoryController(final HoardingCategoryService hoardingCategoryService) {
        this.hoardingCategoryService = hoardingCategoryService;
    }

    @ModelAttribute("hoardingCategory")
    public HoardingCategory hoardingCategory() { 
        return new HoardingCategory();
    }

    @ModelAttribute(value = "hoardingCategories")
    public List<HoardingCategory> getAllHoardingCategory() {
        return hoardingCategoryService.getAllHoardingCategory();
    }
    
    @RequestMapping(value = "create", method = GET)
    public String create() {
        return "category-form";
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search() {
        return "category-search";
    }
    @RequestMapping(value = "create", method = POST)
    public String create(@Valid @ModelAttribute final HoardingCategory hoardingCategory,
            final BindingResult errors, final RedirectAttributes redirectAttrs) {
        if (errors.hasErrors())
            return "category-form";
        hoardingCategoryService.createHoardingCategory(hoardingCategory);
        redirectAttrs.addFlashAttribute("hoardingCategory", hoardingCategory);
        redirectAttrs.addFlashAttribute("message", "message.category.create");
        return "redirect:/category/success/" + hoardingCategory.getId();
    }

    @RequestMapping(value = "/success/{name}", method = GET)
    public ModelAndView successView(@PathVariable("name") final Long name, @ModelAttribute final HoardingCategory hoardingCategory) {
        return new ModelAndView("category/category-success", "hoardingCategory", hoardingCategoryService.getCategoryById(name));

    }

}
