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
package org.egov.adtax.service;

import java.util.List;

import org.egov.adtax.entity.AdvertisementRate;
import org.egov.adtax.entity.AdvertisementRatesDetails;
import org.egov.adtax.entity.HoardingCategory;
import org.egov.adtax.entity.RatesClass;
import org.egov.adtax.entity.SubCategory;
import org.egov.adtax.entity.UnitOfMeasure;
import org.egov.adtax.repository.AdvertisementRateDetailRepository;
import org.egov.adtax.repository.AdvertisementRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdvertisementRateService {
    private final AdvertisementRateRepository ratesRepository;
    private final AdvertisementRateDetailRepository rateDetailRepository;

    @Autowired
    public AdvertisementRateService(final AdvertisementRateRepository ratesRepository,
            final AdvertisementRateDetailRepository rateDetailRepository) {
        this.ratesRepository = ratesRepository;
        this.rateDetailRepository = rateDetailRepository;
    }

    public AdvertisementRate getScheduleOfRateById(final Long id) {
        return ratesRepository.findOne(id);
    }

    public List<AdvertisementRatesDetails> findScheduleOfRateDetailsByCategorySubcategoryUomAndClass(
            final HoardingCategory category, final SubCategory subCategory, final UnitOfMeasure unitOfMeasure,
            final RatesClass ratesClass) {
        return rateDetailRepository.findScheduleOfRateDetailsByCategorySubcategoryUomAndClass(category, subCategory,
                unitOfMeasure, ratesClass);
    }

    @Transactional
    public AdvertisementRate createScheduleOfRate(final AdvertisementRate rate) {

        // if(rate!=null && rate.getId()!=null)
        return ratesRepository.save(rate);
    }

    public AdvertisementRate findScheduleOfRateByCategorySubcategoryUomAndClass(final HoardingCategory category,
            final SubCategory subCategory, final UnitOfMeasure unitofmeasure, final RatesClass classtype) {
        return ratesRepository.findScheduleOfRateByCategorySubcategoryUomAndClass(category, subCategory, unitofmeasure,
                classtype);
    }

    public void deleteAllInBatch(final List<AdvertisementRatesDetails> existingRateDetails) {
        rateDetailRepository.deleteInBatch(existingRateDetails);

    }
    
    public Double getAmountByCategorySubcategoryUomAndClass(final HoardingCategory category,
            final SubCategory subCategory, final UnitOfMeasure unitofmeasure, final RatesClass classtype, Double units) {
        Double rate = Double.valueOf(0);

        if (units != null && category != null && subCategory != null && unitofmeasure != null && classtype != null)
            rate = rateDetailRepository.getAmountByCategorySubcategoryUomAndClass(category, subCategory, unitofmeasure,
                    classtype, units);

        return rate;
    }

    public Double getAmountBySubcategoryUomClassAndMeasurement(Long subCategoryId, Long unitOfMeasureId,
            Long rateClassId, Double measurement) {
        Double rate = Double.valueOf(0);

        if (measurement != null && subCategoryId != null && unitOfMeasureId != null && rateClassId != null )
            rate = rateDetailRepository.getAmountBySubcategoryUomClassAndMeasurement(measurement, subCategoryId, unitOfMeasureId,
                    rateClassId);
        if(rate==null) return Double.valueOf(0);
        return rate;
    }
    

}
