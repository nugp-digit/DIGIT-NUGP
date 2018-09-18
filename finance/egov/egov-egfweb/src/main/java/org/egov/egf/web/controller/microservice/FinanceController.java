package org.egov.egf.web.controller.microservice;

import java.util.ArrayList;
import java.util.List;

import org.egov.commons.Bank;
import org.egov.commons.Bankbranch;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.service.BankBranchService;
import org.egov.commons.service.FinancialYearService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.service.FundService;
import org.egov.egf.contract.model.AuditDetails;
import org.egov.egf.contract.model.BankBranch;
import org.egov.egf.contract.model.BankBranchRequest;
import org.egov.egf.contract.model.BankBranchResponse;
import org.egov.egf.contract.model.BankRequest;
import org.egov.egf.contract.model.BankResponse;
import org.egov.egf.contract.model.FinancialYear;
import org.egov.egf.contract.model.FinancialYearRequest;
import org.egov.egf.contract.model.FinancialYearResponse;
import org.egov.egf.contract.model.Function;
import org.egov.egf.contract.model.FunctionRequest;
import org.egov.egf.contract.model.FunctionResponse;
import org.egov.egf.contract.model.FundRequest;
import org.egov.egf.contract.model.FundResponse;
import org.egov.infra.microservice.contract.Pagination;
import org.egov.infra.microservice.contract.ResponseInfo;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.services.masters.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FinanceController {

	@Autowired
	private FunctionService functionService;
	@Autowired
	private FundService fundService;
	@Autowired
	private FinancialYearService fyService;
	@Autowired
	private BankService bankService;
	@Autowired
	private BankBranchService branchService;

	@PostMapping(value = "/funds/_search")
	@ResponseBody
	public FundResponse fundSearch(@RequestBody FundRequest fundRequest) {
		Fund fund = new Fund();

		fund.setCode(fundRequest.getCode());
		fund.setName(fundRequest.getName());
		fund.setIsactive(fundRequest.getActive());

		List<Fund> funds = fundService.search(fund, fundRequest.getIds(), fundRequest.getSortBy(),
				fundRequest.getOffset(), fundRequest.getPageSize());

		return getSuccessFundResponse(funds, fundRequest);
	}

	@PostMapping(value = "/functions/_search")
	@ResponseBody
	public FunctionResponse functionSearch(@RequestBody FunctionRequest funcRequest) {
		CFunction function = new CFunction();
		function.setCode(funcRequest.getCode());
		function.setName(funcRequest.getName());
		function.setIsActive(funcRequest.getActive());

		List<CFunction> functions = functionService.search(function, funcRequest.getIds(), funcRequest.getSortBy(),
				funcRequest.getOffset(), funcRequest.getPageSize());

		FunctionResponse funRep = new FunctionResponse();

		// fundResp.

		return getSuccessFunctionResponse(functions, funcRequest);
	}

	@PostMapping(value = "/COA")
	public void COASearch() {
	}

	@PostMapping(value = "/financialyears/_search")
	@ResponseBody
	public FinancialYearResponse FinancialYearSearch(@RequestBody FinancialYearRequest fyRequest) {

		CFinancialYear finYear = new CFinancialYear();
		finYear.setFinYearRange(fyRequest.getFinYearRange());
		finYear.setStartingDate(fyRequest.getStartingDate());
		finYear.setEndingDate(fyRequest.getEndingDate());
		finYear.setIsActive(fyRequest.isActive());
		finYear.setIsActiveForPosting(fyRequest.isActiveForPosting());
		List<CFinancialYear> fyList = fyService.Search(finYear, fyRequest.getIds(), fyRequest.getSortBy(),
				fyRequest.getPageSize(), fyRequest.getOffset());

		return getSuccessFYResponse(fyList, fyRequest);
	}

	@PostMapping(value = "/bank")
	@ResponseBody
	public BankResponse BankSearch(@RequestBody BankRequest bankRequest) {

		Bank bank = new Bank();
		bank.setCode(bankRequest.getCode());
		bank.setName(bankRequest.getName());
		bank.setIsactive(bankRequest.isActive());

		List<Bank> banks = bankService.search(bank, bankRequest.getIds(), bankRequest.getSortBy(),
				bankRequest.getOffset(), bankRequest.getPageSize());

		return getSuccessBankResponse(banks, bankRequest);
	}

	@PostMapping(value = "/bankbranch")
	@ResponseBody
	public BankBranchResponse BankBranchSearch(@RequestBody BankBranchRequest bbRequest) {

		Bankbranch branch = new Bankbranch();
		branch.setBranchcode(bbRequest.getCode());
		branch.setBranchname(bbRequest.getName());
		branch.setIsactive(bbRequest.getActive());
		List<Bankbranch> branchList = branchService.search(branch, bbRequest.getIds(), bbRequest.getBank(),
				bbRequest.getSortBy(), bbRequest.getOffset(), bbRequest.getPageSize());

		return getSuccessBankBranchResponse(branchList, bbRequest);
	}

	@PostMapping(value = "/bankaccount")
	public void BankAccountSearch() {
	}

	@PostMapping(value = "/recovery")
	public void RecoverySearch() {
	}

	private ResponseInfo createResponseObj(RequestInfo requestinfo, boolean success) {
		ResponseInfo response = new ResponseInfo();
		response.setApiId(requestinfo.getApiId());
		response.setMsgId(requestinfo.getMsgId());
		response.setResMsgId(requestinfo.getMsgId());

		String responseStatus = success ? "successful" : "failed";
		response.setStatus(responseStatus);
		response.setVer(requestinfo.getVer());
		if (requestinfo.getTs() != null)
			response.setTs(requestinfo.getTs().toString());

		return response;
	}

	private FundResponse getSuccessFundResponse(List<Fund> funds, FundRequest fundRequest) {

		ResponseInfo responseInfo = createResponseObj(fundRequest.getRequestInfo(), true);

		Fund fu = new Fund();

		List<org.egov.egf.contract.model.Fund> fundlist = new ArrayList<>();

		funds.forEach(fund -> {

			AuditDetails auditDetails = new AuditDetails(fundRequest.getTenantId(),
					fund.getCreatedby() != null ? fund.getCreatedby().getId() : null,
					fund.getLastModifiedBy() != null ? fund.getLastModifiedBy().getId() : null, fund.getCreatedDate(),
					fund.getLastModifiedDate());
			org.egov.egf.contract.model.Fund _fund = new org.egov.egf.contract.model.Fund();
			_fund.setAuditDetils(auditDetails);
			_fund.setActive(fund.getIsactive());
			_fund.setCode(fund.getCode());
			_fund.setId(fund.getId().longValue());
			_fund.setIdentifier(fund.getIdentifier());
			_fund.setIsParent(fund.getIsnotleaf());
			_fund.setLevel(String.valueOf(fund.getLlevel()));
			_fund.setName(fund.getName());
			if (fund.getParentId() != null)
				_fund.setParent(fund.getParentId().getId().longValue());

			fundlist.add(_fund);
		});

		Pagination page = new Pagination();
		page.setOffSet(fundRequest.getOffset());
		page.setPageSize(fundRequest.getPageSize());
		page.setTotalResults(fundlist.size());

		return new FundResponse(responseInfo, fundlist, page);
	}

	private FunctionResponse getSuccessFunctionResponse(List<CFunction> functions, FunctionRequest funRequest) {

		ResponseInfo responseInfo = createResponseObj(funRequest.getRequestInfo(), true);
		List<Function> funList = new ArrayList<>();
		functions.forEach(function -> {
			AuditDetails auditDetails = new AuditDetails(funRequest.getTenantId(), function.getCreatedBy(),
					function.getLastModifiedBy(), function.getCreatedDate(), function.getLastModifiedDate());
			// Long id, String name, String code, Integer level, Boolean active,
			// Long parentId, AuditDetails auditDetails)
			Function _function = new Function(function.getId(), function.getName(), function.getCode(),
					function.getLlevel(), function.getIsActive(),
					function.getParentId() != null ? function.getParentId().getId() : 0, auditDetails);

			funList.add(_function);
		});
		Pagination page = new Pagination();
		page.setOffSet(funRequest.getOffset());
		page.setPageSize(funRequest.getPageSize());
		page.setTotalResults(funList.size());

		return new FunctionResponse(responseInfo, funList, page);
	}

	private FinancialYearResponse getSuccessFYResponse(List<CFinancialYear> financeyears,
			FinancialYearRequest fyRequest) {

		ResponseInfo responseInfo = createResponseObj(fyRequest.getRequestInfo(), true);

		List<FinancialYear> fyList = new ArrayList<>();
		financeyears.forEach(FYear -> {

			AuditDetails auditDetails = new AuditDetails(fyRequest.getTeanantId(), FYear.getCreatedBy(),
					FYear.getLastModifiedBy(), FYear.getCreatedDate(), FYear.getLastModifiedDate());
			FinancialYear fy = new FinancialYear(FYear.getId(), FYear.getFinYearRange(), FYear.getStartingDate(),
					FYear.getEndingDate(), FYear.getIsActive(), FYear.getIsActiveForPosting(), FYear.getIsClosed(),
					FYear.getTransferClosingBalance(), auditDetails);
			fyList.add(fy);
		});

		Pagination page = new Pagination();
		page.setOffSet(fyRequest.getOffset());
		page.setPageSize(fyRequest.getPageSize());
		page.setTotalResults(fyList.size());

		return new FinancialYearResponse(responseInfo, fyList, page);

	}

	private BankResponse getSuccessBankResponse(List<Bank> banks, BankRequest bankRequest) {

		ResponseInfo responseInfo = createResponseObj(bankRequest.getRequestInfo(), true);

		List<org.egov.egf.contract.model.Bank> bankList = new ArrayList<>();
		banks.forEach(bank -> {
			AuditDetails auditDetails = new AuditDetails(bankRequest.getTenantId(), bank.getCreatedBy(),
					bank.getLastModifiedBy(), bank.getCreatedDate(), bank.getLastModifiedDate());

			org.egov.egf.contract.model.Bank _bank = new org.egov.egf.contract.model.Bank(bank.getId(), bank.getCode(),
					bank.getName(), bank.getNarration(), bank.getIsactive(), bank.getType(), auditDetails);
			bankList.add(_bank);
		});

		Pagination page = new Pagination();
		page.setOffSet(bankRequest.getOffset());
		page.setPageSize(bankRequest.getPageSize());
		page.setTotalResults(bankList.size());

		return new BankResponse(responseInfo, bankList, page);
	}

	private BankBranchResponse getSuccessBankBranchResponse(List<Bankbranch> branches,
			BankBranchRequest branchRequest) {

		ResponseInfo responseInfo = createResponseObj(branchRequest.getRequestInfo(), true);

		List<BankBranch> branchList = new ArrayList<>();

		branches.forEach(branch -> {
			AuditDetails branchAudit = new AuditDetails(branchRequest.getTenantId(),
					branch.getCreatedBy() != null ? branch.getCreatedBy() : null,
					branch.getLastModifiedBy() != null ? branch.getLastModifiedBy() : null, branch.getCreatedDate(),
					branch.getLastModifiedDate());
			BankBranch _branch = new BankBranch();

			_branch.setActive(branch.getIsactive());
			_branch.setAddress(branch.getBranchaddress1());
			_branch.setAddress2(branch.getBranchaddress2());
			_branch.setAuditDetails(branchAudit);

			if (branch.getBank() != null) {
				AuditDetails bankAudit = new AuditDetails(branchRequest.getTenantId(),
						branch.getBank().getCreatedBy() != null ? branch.getBank().getCreatedBy() : null,
						branch.getBank().getLastModifiedBy() != null ? branch.getBank().getLastModifiedBy() : null,
						branch.getBank().getCreatedDate(), branch.getBank().getLastModifiedDate());
				org.egov.egf.contract.model.Bank _bank = new org.egov.egf.contract.model.Bank();
				_bank.setCode(branch.getBank().getCode());
				_bank.setId(branch.getBank().getId());
				_bank.setName(branch.getBank().getName());
				_bank.setActive(branch.getBank().getIsactive());
				_bank.setType(branch.getBank().getType());
				_bank.setDescription(branch.getBank().getNarration());
				_bank.setAuditDetails(bankAudit);
				_branch.setBank(_bank);
			}

			_branch.setCity(branch.getBranchcity());
			_branch.setCode(branch.getBranchcode());
			_branch.setContactPerson(branch.getContactperson());
			_branch.setDescription(branch.getNarration());
			_branch.setFax(branch.getBranchfax());
			_branch.setId(branch.getId().longValue());
			_branch.setMicr(branch.getBranchMICR());
			_branch.setName(branch.getBranchname());
			_branch.setPhone(branch.getBranchphone());
			_branch.setPincode(branch.getBranchpin());
			_branch.setState(branch.getBranchstate());

			branchList.add(_branch);
		});

		Pagination page = new Pagination();
		page.setOffSet(branchRequest.getOffset());
		page.setPageSize(branchRequest.getPageSize());
		page.setTotalResults(branchList.size());

		return new BankBranchResponse(responseInfo, branchList, page);
	}
}
