package com.example.hpgarbageservice.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.contract.bill.Demand;
import com.example.hpgarbageservice.contract.bill.DemandDetail;
import com.example.hpgarbageservice.contract.bill.DemandRepository;
import com.example.hpgarbageservice.contract.bill.DemandResponse;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.util.ApplicationPropertiesAndConstant;
import com.example.hpgarbageservice.util.RequestInfoWrapper;

@Service
public class DemandService {

//    @Autowired
//    private CalculationService calculationService;

    @Autowired
    private DemandRepository demandRepository;

    public List<Demand> generateDemand(RequestInfo requestInfo,GarbageAccount garbageAccount, String businessService){

    	// get total Tax
//    	ApplicationDetail applicationDetail = tradeLicenseService.getApplicationBillUserDetail(garbageAccount, requestInfo);
    	BigDecimal taxAmount = new BigDecimal("100.00");
    	
    	DemandDetail demandDetail = DemandDetail.builder()
    								.taxHeadMasterCode(ApplicationPropertiesAndConstant.BILLING_TAX_HEAD_MASTER_CODE)
    								.taxAmount(taxAmount)
    								.collectionAmount(BigDecimal.ZERO)
    								.build();
    	// create demand of 1 month
    	Demand demandOne = Demand.builder()
                .consumerCode(garbageAccount.getGrbgApplicationNumber())
                .demandDetails(Arrays.asList(demandDetail))
                .minimumAmountPayable(taxAmount)
                .tenantId(garbageAccount.getTenantId())
                .taxPeriodFrom(new Date().getTime())
                .taxPeriodTo(new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000)).getTime())
//                .taxPeriodTo(new Date((Calendar.getInstance().getTimeInMillis() + (long) 365 * 24 * 60 * 60 * 1000)).getTime())
                .consumerType(garbageAccount.getGrbgApplicationNumber())
                .businessService(ApplicationPropertiesAndConstant.BUSINESS_SERVICE)
                .build();
    	
    	List<Demand> demands = Arrays.asList(demandOne);
    	
    	List<Demand> savedDemands = demandRepository.saveDemand(requestInfo,demands);
    	
    	return savedDemands;
    }


    List<Demand> searchDemand(String tenantId,Set<String> consumerCodes,RequestInfo requestInfo, String businessService){
    	
    	RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
    	DemandResponse response = demandRepository.search(tenantId, consumerCodes, requestInfoWrapper, businessService);

        if(CollectionUtils.isEmpty(response.getDemands()))
            return null;

        else 
        	return response.getDemands();

    }


}
