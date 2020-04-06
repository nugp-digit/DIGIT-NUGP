package org.egov.egf.web.service.report;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFinancialYear;
import org.egov.commons.Fund;
import org.egov.commons.dao.BankaccountHibernateDAO;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.dao.voucher.VoucherHibernateDAO;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.BusinessService;
import org.egov.infra.microservice.models.BusinessServiceCriteria;
import org.egov.infra.microservice.models.BusinessServiceMapping;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Instrument;
import org.egov.infra.microservice.models.InstrumentSearchContract;
import org.egov.infra.microservice.models.InstrumentVoucher;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.models.ReceiptSearchCriteria;
import org.egov.infra.microservice.models.Remittance;
import org.egov.infra.microservice.models.RemittanceInstrument;
import org.egov.infra.microservice.models.RemittanceResponse;
import org.egov.infra.microservice.models.RemittanceSearcCriteria;
import org.egov.infra.microservice.models.TransactionType;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.model.remittance.RemittanceReportModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RemittanceServiceImpl implements RemittanceService{
    
    @Autowired
    public MicroserviceUtils microserviceUtils;
    @Autowired
    private FundHibernateDAO fundHibernateDAO;
    @Autowired
    @Qualifier("voucherHibDAO")
    private VoucherHibernateDAO voucherHibDAO;
    @Autowired
    private BankaccountHibernateDAO bankaccountHibernateDAO;
    @Autowired
    private transient FinancialYearDAO financialYearDAO;
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public List<RemittanceReportModel> getRemittanceColectionsReports(RemittanceReportModel model) {
        RemittanceSearcCriteria criteria = new RemittanceSearcCriteria();
        this.prepareRemittanceSearchCriteria(model, criteria);
        RemittanceResponse response = microserviceUtils.getRemittance(criteria);
        List<Remittance> remittances = response.getRemittances();
        List<RemittanceReportModel> resultList = new ArrayList<>();
        if(remittances.isEmpty()){
            return resultList;
        }
        List<Instrument> instruments = this.getInstruments(model, remittances);
        Set<String> receiptIds = new HashSet();
        Map<String, Instrument> recInstrumentMap = new HashMap<>();
        Map<String, Remittance> instrumentRemittanceMap = new HashMap<>();
        
        for(Remittance rem : remittances){
            if (rem.getRemittanceInstruments() != null){
                for(RemittanceInstrument ri  : rem.getRemittanceInstruments()){
                    instrumentRemittanceMap.put(ri.getInstrument(), rem);
                }
            }
        }
        for (Instrument i : instruments) {
            if (i.getInstrumentVouchers() != null){
                for (InstrumentVoucher iv : i.getInstrumentVouchers()) {
                    receiptIds.add(iv.getReceiptHeaderId());
                    recInstrumentMap.put(iv.getReceiptHeaderId(), i);
                }
            }
        }
        if(!receiptIds.isEmpty()){
            ReceiptSearchCriteria rSearchcriteria = new ReceiptSearchCriteria().builder().receiptNumbers(receiptIds).businessCodes(Collections.singleton(model.getService())).build();
            List<Receipt> receipts = microserviceUtils.getReceipt(rSearchcriteria);
            if(!receipts.isEmpty())
                switch (model.getInstrumentType()) {
                case "Cash":
                    resultList = this.getConsolidatedCashReceiptForReport(receipts, recInstrumentMap, instrumentRemittanceMap);
                    break;
                case "Cheque":
                    resultList = this.getChequeReceiptsForReport(receipts, recInstrumentMap, instrumentRemittanceMap);
                    break;

                default:
                    break;
                }
        }
        return resultList;
    }
    
    private List<Instrument> getInstruments(RemittanceReportModel model, List<Remittance> remittances){
        Set<String> insturmentIds = remittances.stream().map(Remittance::getRemittanceInstruments).flatMap(x -> x.stream()).map(RemittanceInstrument::getInstrument).collect(Collectors.toSet());
        InstrumentSearchContract insSearchContra = new InstrumentSearchContract();
        insSearchContra.setIds(StringUtils.join(insturmentIds, ","));
        insSearchContra.setInstrumentTypes(model.getInstrumentType());
        insSearchContra.setTransactionType(TransactionType.Debit);
//        insSearchContra.setFinancialStatuses(instrumentStatus);
        insSearchContra.setBankAccountNumber(model.getBankAccount());
        return microserviceUtils.getInstrumentsBySearchCriteria(insSearchContra );
    }

    private void prepareRemittanceSearchCriteria(RemittanceReportModel model, RemittanceSearcCriteria criteria) {
        final CFinancialYear financialYear = financialYearDAO.getFinancialYearById(model.getFinancialYear());
        if(StringUtils.isNotBlank(model.getBankAccount())){
            criteria.setBankaccount(model.getBankAccount());
        }
        criteria.setFromDate(model.getFromDate() != null ? model.getFromDate().getTime() : financialYear.getStartingDate().getTime());
        criteria.setToDate(model.getToDate() != null ? model.getToDate().getTime() : financialYear.getEndingDate().getTime());
        criteria.setFund(model.getFund());
    }

    private List<RemittanceReportModel> getChequeReceiptsForReport(List<Receipt> receipts,Map<String, Instrument> recInstrumentMap, Map<String, Remittance> instrumentRemittanceMap) {
        List<RemittanceReportModel> resultList = new ArrayList<>();
        setRemittedDetailsToReceipt(receipts, recInstrumentMap, instrumentRemittanceMap);
        prepareResultListForChequeReport(receipts, resultList);
        populateNames(resultList);
        this.sortDefaultResultList(resultList);
        return resultList;
    }

    private void sortDefaultResultList(List<RemittanceReportModel> resultList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Collections.sort(resultList, (rrm1, rrm2) -> LocalDate.parse(rrm1.getRemittedOn(), formatter).compareTo(LocalDate.parse(rrm2.getRemittedOn(), formatter)));
        int srNo = 1;
        for(RemittanceReportModel remRepModel : resultList){
            remRepModel.setSrNo(srNo++);
        }
    }

    private void prepareResultListForChequeReport(List<Receipt> receipts, List<RemittanceReportModel> resultList) {
        receipts.stream().forEach(rec -> {
            RemittanceReportModel model = new RemittanceReportModel();
            String remittedOn = DateUtils.toDefaultDateFormat(rec.getRemittedOn());
            model.setRemittedOn(remittedOn);
            BillDetail billDetail = rec.getBill().get(0).getBillDetails().get(0);
            model.setService(billDetail.getBusinessService());
            model.setInstrumentType(rec.getInstrument().getInstrumentType().getName());
            model.setFund(billDetail.getFund());
            model.setDepartment(billDetail.getDepartment());
            model.setBankAccount(rec.getAccNumber());
            model.setPayee(StringUtils.defaultIfBlank(rec.getPayee(), "--"));
            model.setDrawer(rec.getDrawer());
            model.setTransactionNumber(rec.getTransactionNumber());
            model.setRemitterId(rec.getRemitterId() != null ? rec.getRemitterId().toString() : "--");
            model.setInstrumentAmount(rec.getInstrument().getAmount());
            resultList.add(model);
        });
    }

    private void populateResultList(String key, List<RemittanceReportModel> result, List<Receipt> tempList) {
        RemittanceReportModel model = new RemittanceReportModel();
        BigDecimal amount = BigDecimal.ZERO;

        for (Receipt r : tempList) {
            amount = amount.add(r.getInstrument().getAmount());
        }

        model.setRemittedOn(key.split("-")[0]);
        model.setService(key.split("-")[1]);
        model.setInstrumentType(key.split("-")[2]);
        if (key.split("-").length > 3)
            model.setFund(key.split("-")[3]);
        if (key.split("-").length > 4)
            model.setDepartment(key.split("-")[4]);
        if (key.split("-").length > 5){
            String bankAccount = key.split("-")[5];
            model.setBankAccount(bankAccount);
            
        }
        if (key.split("-").length > 6)
            model.setRemitterId(key.split("-")[6]);
        model.setInstrumentAmount(amount);
        result.add(model);
    }
    
    private void populateNames(List<RemittanceReportModel> resultList) {
        List<Fund> fundList = fundHibernateDAO.findAllActiveFunds();
        List<Department> departmentList = microserviceUtils.getDepartments();
        List<BusinessService> businessServiceList = microserviceUtils.getBusinessService(null);
        Set<Long> ids = resultList.stream().map(RemittanceReportModel::getRemitterId).map(ri -> Long.parseLong(ri)).collect(Collectors.toSet());
        List<EmployeeInfo> emps = microserviceUtils.getEmployeeByIds(ids );
        Map<String, String> fundCodeNameMap = new HashMap<>();
        Map<String, String> deptCodeNameMap = new HashMap<>();
        Map<String, String> businessDetailsCodeNameMap = new HashMap<>();
        Map<Long, EmployeeInfo> empIdMap = new HashMap();
        Set<String> bankAccountNumbers = resultList.stream().map(RemittanceReportModel::getBankAccount).collect(Collectors.toSet());
        List<Bankaccount> bankAcntList = this.getBankAccounts(bankAccountNumbers );

        if(emps != null){
            for(EmployeeInfo emp : emps){
                empIdMap.put((long) emp.getId(), emp);
            }
        }
        
        if (fundList != null)
            for (Fund f : fundList) {
                fundCodeNameMap.put(f.getCode(), f.getName());
            }

        if (departmentList != null)
            for (Department dept : departmentList) {
                deptCodeNameMap.put(dept.getCode(), dept.getName());
            }

        if (businessServiceList != null)
            for (BusinessService bd : businessServiceList) {
                businessDetailsCodeNameMap.put(bd.getCode(), bd.getBusinessService());
            }
        Set<String> bsCodes = new HashSet<>();
        for(RemittanceReportModel rb : resultList){
            bsCodes.add(rb.getService());
        }
        
        Map<String, Bankaccount> bankAccMap = bankAcntList.stream().collect(Collectors.toMap(Bankaccount::getAccountnumber, Function.identity()));
        
        BusinessServiceCriteria criteria = new BusinessServiceCriteria();
        criteria.setCode(StringUtils.join(bsCodes,','));
        criteria.setVoucherCreationEnabled(true);
        List<BusinessServiceMapping> businessServiceMappingList = microserviceUtils.getBusinessServiceMappingBySearchCriteria(criteria );
        Map<String,BusinessServiceMapping> bsServiceMapping = new HashMap<>();
        businessServiceMappingList.stream().forEach(bsm -> {
            bsServiceMapping.put(bsm.getCode(), bsm);
        });

        for (RemittanceReportModel rb : resultList) {
            String serviceCode = rb.getService();
            if (serviceCode != null && !serviceCode.isEmpty()){
                rb.setServiceName(businessDetailsCodeNameMap.get(serviceCode));
                BusinessServiceMapping serviceMapping = bsServiceMapping.get(serviceCode);
                if(StringUtils.isNumeric(serviceMapping.getFund())){
                    rb.setFundName(fundCodeNameMap.get(serviceMapping.getFund()));
                }
                if(StringUtils.isNoneBlank(serviceMapping.getDepartment())){
                    rb.setDepartmentName(deptCodeNameMap.get(serviceMapping.getDepartment()));
                }
            }
            if(StringUtils.isNoneBlank(rb.getBankAccount())){
                Bankaccount bankaccount = bankAccMap.get(rb.getBankAccount());
                rb.setBank(bankaccount.getBankbranch().getBank().getName());
                rb.setBankBranch(bankaccount.getBankbranch().getBranchname());
            }
            EmployeeInfo remittedBy = empIdMap.get(Long.parseLong(rb.getRemitterId()));
            rb.setRemittedBy(remittedBy != null ? remittedBy.getUser().getName() : (StringUtils.isNotBlank(rb.getRemitterId()) ? rb.getRemitterId() : "--" ));
        }
    }
    private List<RemittanceReportModel> getConsolidatedCashReceiptForReport(List<Receipt> receipts, Map<String, Instrument> recInstrumentMap, Map<String, Remittance> instRemittanceMap){
        Map<String, List<Receipt>> reconciledOnWiseMap = new HashMap<>();
        Map<String, List<Receipt>> serviceWiseMap = new HashMap<>();
        Map<String, List<Receipt>> instrumentWiseMap = new HashMap<>();
        Map<String, List<Receipt>> fundWiseMap = new HashMap<>();
        Map<String, List<Receipt>> departmentWiseMap = new HashMap<>();
        Map<String, List<Receipt>> accountWiseMap = new HashMap<>();
        Map<String, List<Receipt>> remitterWiseMap = new HashMap<>();
        List<RemittanceReportModel> resultList = new ArrayList<>();
        setRemittedDetailsToReceipt(receipts, recInstrumentMap, instRemittanceMap);
        groupByRemittedOn(reconciledOnWiseMap, receipts);
        for (String key : reconciledOnWiseMap.keySet()) {
            List<Receipt> tempList = reconciledOnWiseMap.get(key);
            groupByService(key, serviceWiseMap, tempList);
        }

        for (String key : serviceWiseMap.keySet()) {
            List<Receipt> tempList = serviceWiseMap.get(key);
            groupByInstrument(key, instrumentWiseMap, tempList);
        }

        for (String key : instrumentWiseMap.keySet()) {
            List<Receipt> tempList = instrumentWiseMap.get(key);
            groupByFund(key, fundWiseMap, tempList);
        }

        for (String key : fundWiseMap.keySet()) {
            List<Receipt> tempList = fundWiseMap.get(key);
            groupByDepartment(key, departmentWiseMap, tempList);
        }
        
        for(String key : departmentWiseMap.keySet()){
            List<Receipt> tempList = departmentWiseMap.get(key);
            groupByBankAccount(key, accountWiseMap, tempList);
        }
        
        for (String key : accountWiseMap.keySet()) {
            List<Receipt> tempList = accountWiseMap.get(key);
            groupByRemitter(key, remitterWiseMap, tempList);
        }

        for (String key : remitterWiseMap.keySet()) {
            List<Receipt> tempList = remitterWiseMap.get(key);
            populateResultList(key, resultList, tempList);
        }

        populateNames(resultList);
        sortDefaultResultList(resultList);
        return resultList;
    }

    private void groupByRemittedOn(Map<String, List<Receipt>> receiptDateWiseMap, List<Receipt> receipts) {
        String remittedOn;
        for (Receipt receipt : receipts) {
            remittedOn = DateUtils.toDefaultDateFormat(receipt.getRemittedOn());
            if (!receiptDateWiseMap.containsKey(remittedOn)) {
                List<Receipt> list = new ArrayList<Receipt>();
                list.add(receipt);
                receiptDateWiseMap.put(remittedOn, list);
            } else {
                receiptDateWiseMap.get(remittedOn).add(receipt);
            }
        }
    }

    private void groupByService(String receiptDate, Map<String, List<Receipt>> serviceWiseMap, List<Receipt> tempList) {
        String service;
        for (Receipt r : tempList) {
            service = r.getBill().get(0).getBillDetails().get(0).getBusinessService();
            if (!serviceWiseMap.containsKey(receiptDate + "-" + service)) {
                List<Receipt> list = new ArrayList<Receipt>();
                list.add(r);
                serviceWiseMap.put(receiptDate + "-" + service, list);
            } else {
                serviceWiseMap.get(receiptDate + "-" + service).add(r);
            }
        }
    }
    
    private void groupByFund(String key, Map<String, List<Receipt>> fundWiseMap,
            List<Receipt> tempList) {
        String fund;
        for (Receipt r : tempList) {
            fund = r.getBill().get(0).getBillDetails().get(0).getFund();
            if (fund != null && !fund.isEmpty()) {
                if (!fundWiseMap.containsKey(key + "-" + fund)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    fundWiseMap.put(key + "-" + fund, list);
                } else {
                    fundWiseMap.get(key + "-" + fund).add(r);
                }
            } else {
                if (!fundWiseMap.containsKey(key)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    fundWiseMap.put(key, list);
                } else {
                    fundWiseMap.get(key).add(r);
                }
            }
        }
    }
    
    private void groupByInstrument(String receiptDateAndService, Map<String, List<Receipt>> instrumentWiseMap,
            List<Receipt> tempList) {
        String instrument;
        for (Receipt r : tempList) {
            instrument = r.getInstrument().getInstrumentType().getName();
            if (!instrumentWiseMap.containsKey(receiptDateAndService + "-" + instrument)) {
                List<Receipt> list = new ArrayList<Receipt>();
                list.add(r);
                instrumentWiseMap.put(receiptDateAndService + "-" + instrument, list);
            } else {
                instrumentWiseMap.get(receiptDateAndService + "-" + instrument).add(r);
            }
        }
    }
    
    private void groupByBankAccount(String key, Map<String, List<Receipt>> accountWiseMap, List<Receipt> tempList) {
        for (Receipt r : tempList) {
            String accNumber = r.getAccNumber();
            if (accNumber != null) {
                if (!accountWiseMap.containsKey(key + "-" + accNumber)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    accountWiseMap.put(key + "-" + accNumber, list);
                } else {
                    accountWiseMap.get(key + "-" + accNumber).add(r);
                }
            }
        }
    }

    private void groupByRemitter(String key, Map<String, List<Receipt>> remitterWiseMap, List<Receipt> tempList) {
        for (Receipt r : tempList) {
            String remitterId = r.getRemitterId();
            if (remitterId != null) {
                if (!remitterWiseMap.containsKey(key + "-" + remitterId)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    remitterWiseMap.put(key + "-" + remitterId, list);
                } else {
                    remitterWiseMap.get(key + "-" + remitterId).add(r);
                }
            }
        }
    }

    private void setRemittedDetailsToReceipt(List<Receipt> receipts, Map<String, Instrument> recInstrumentMap, Map<String, Remittance> instrumentRemittanceMap) {
        switch (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) {
        case "V2":
        case "VERSION":
            receipts.stream().forEach(rec -> {
                Instrument instrument = recInstrumentMap.get(rec.getPaymentId());
                Remittance remittance = instrumentRemittanceMap.get(instrument.getId());
                rec.setRemittedOn(new Date(remittance.getReferenceDate()));
                rec.setRemitterId(remittance.getAuditDetails().getCreatedBy());
                String accountNumber = instrument.getBankAccount().getAccountNumber();
                rec.setAccNumber(accountNumber);
                rec.setPayee(rec.getBill().get(0).getPaidBy());
                rec.setDrawer(instrument.getDrawer());
                rec.setTransactionNumber(instrument.getTransactionNumber());
            });
            break;

        default:
            receipts.stream().forEach(rec -> {
                Instrument instrument = recInstrumentMap.get(rec.getBill().get(0).getBillDetails().get(0).getReceiptNumber());
                Remittance remittance = instrumentRemittanceMap.get(instrument.getId());
                rec.setRemittedOn(new Date(remittance.getReferenceDate()));
                rec.setRemitterId(remittance.getAuditDetails().getCreatedBy());
                String accountNumber = instrument.getBankAccount().getAccountNumber();
                rec.setAccNumber(accountNumber);
                rec.setPayee(rec.getBill().get(0).getPaidBy());
                rec.setDrawer(instrument.getDrawer());
                rec.setTransactionNumber(instrument.getTransactionNumber());
            });
            break;
        }
        
    }
    
    private void groupByDepartment(String key, Map<String, List<Receipt>> departmentWiseMap,
            List<Receipt> tempList) {
        String department;
        for (Receipt r : tempList) {
            department = r.getBill().get(0).getBillDetails().get(0).getDepartment();
            if (department != null && !department.isEmpty()) {
                if (!departmentWiseMap.containsKey(key + "-" + department)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    departmentWiseMap.put(key + "-" + department, list);
                } else {
                    departmentWiseMap.get(key + "-" + department).add(r);
                }
            } else {
                if (!departmentWiseMap.containsKey(key)) {
                    List<Receipt> list = new ArrayList<Receipt>();
                    list.add(r);
                    departmentWiseMap.put(key, list);
                } else {
                    departmentWiseMap.get(key).add(r);
                }
            }
        }
    }
    
    private List<Bankaccount> getBankAccounts(Set accNos){
        return bankaccountHibernateDAO.getBankAccountByAccountNumbers(accNos);
    }
}
