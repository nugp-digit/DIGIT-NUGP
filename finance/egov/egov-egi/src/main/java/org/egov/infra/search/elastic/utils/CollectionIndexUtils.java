package org.egov.infra.search.elastic.utils;

import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.search.elastic.entity.CollectionIndex;
import org.egov.infra.search.elastic.repository.CollectionIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CollectionIndexUtils {

    private final CollectionIndexRepository collectionIndexRepository;

    @Autowired
    private CityService cityService;

    @Autowired
    public CollectionIndexUtils(final CollectionIndexRepository collectionIndexRepository) {
        this.collectionIndexRepository = collectionIndexRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public CollectionIndex findByReceiptNumber(final String receiptNumber) {
        final City cityWebsite = cityService.getCityByURL(ApplicationThreadLocals.getDomainName());
        return collectionIndexRepository.findByReceiptNumberAndCityName(receiptNumber, cityWebsite.getName());
    }

}
