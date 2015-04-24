package org.egov.ptis.domain.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.egov.demand.dao.DemandGenericDao;
import org.egov.demand.dao.EgBillDao;
import org.egov.demand.dao.EgDemandDao;
import org.egov.demand.interfaces.Billable;
import org.egov.demand.interfaces.LatePayPenaltyCalculator;
import org.egov.demand.model.AbstractBillable;
import org.egov.demand.model.EgBillType;
import org.egov.demand.model.EgDemand;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infstr.commons.Module;
import org.egov.infstr.commons.dao.ModuleDao;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.dao.property.PropertyDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.utils.PTISCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author satyam
 * 
 */
public abstract class PropertyTaxBillable extends AbstractBillable implements
		Billable, LatePayPenaltyCalculator {

	private BasicProperty basicProperty;
	private Long userId;
	EgBillType egBillType;
	PTISCacheManager ptcm = new PTISCacheManager();
	@Autowired
	@Qualifier(value = "demandDAO")
	private EgDemandDao demandDao;
	@Autowired
	@Qualifier(value = "moduleDAO")
	private ModuleDao moduleDao;
	@Autowired
	@Qualifier(value = "propertyDAO")
	private PropertyDAO propertyDao;
	@Autowired
	@Qualifier(value = "ptDemandDAO")
	private PtDemandDao ptDemandDao;
	@Autowired
	@Qualifier(value = "egBillDAO")
	private EgBillDao egBillDao;
	@Autowired
	@Qualifier(value = "demandGenericDAO")
	private DemandGenericDao demandGenericDao;
	@Autowired
	private UserService userService;

	@Override
	public Boolean getOverrideAccountHeadsAllowed() {
		Boolean retVal = Boolean.FALSE;
		return retVal;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BasicProperty getBasicProperty() {
		return basicProperty;
	}

	public void setBasicProperty(BasicProperty basicProperty) {
		this.basicProperty = basicProperty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.egov.demand.interfaces.Billable#getBillAddres()
	 */
	@Override
	public String getBillAddress() {

		return ptcm
				.buildAddressByImplemetation(getBasicProperty().getAddress());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.egov.demand.interfaces.Billable#getBillDemand()
	 */
	@Override
	public EgDemand getCurrentDemand() {
		BasicProperty bp = null;
		try {
			bp = getBasicProperty();
		} catch (Exception e) {
			throw new EGOVRuntimeException("Property does not exist" + e);
		}
		return ptDemandDao.getNonHistoryCurrDmdForProperty(bp.getProperty());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.egov.demand.interfaces.Billable#getBillLastDueDate()
	 */
	@Override
	public Date getBillLastDueDate() {
		Date Billlastdate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(Billlastdate);
		cal.get(Calendar.MONTH + 1);
		Billlastdate = cal.getTime();
		return (Billlastdate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.egov.demand.interfaces.Billable#getBillPayee()
	 */
	@Override
	public String getBillPayee() {
		return (ptcm.buildOwnerFullName(getBasicProperty().getProperty()
				.getPropertyOwnerSet())).trim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.egov.demand.interfaces.Billable#getBillType()
	 */
	@Override
	public EgBillType getBillType() {
		egBillType = egBillDao.getBillTypeByCode("AUTO");
		return egBillType;
	}

	@Override
	public String getBoundaryType() {
		return "Ward";
	}

	@Override
	public Long getBoundaryNum() {
		return getBasicProperty().getBoundary().getId();
	}

	@Override
	public Module getModule() {
		return moduleDao.getModuleByName(PropertyTaxConstants.PTMODULENAME);

	}

	@Override
	public String getCollModesNotAllowed() {
		String modesNotAllowed = "";
		BigDecimal chqBouncepenalty = BigDecimal.ZERO;
		EgDemand currDemand = getCurrentDemand();
		if (currDemand != null && currDemand.getMinAmtPayable() != null
				&& currDemand.getMinAmtPayable().compareTo(BigDecimal.ZERO) > 0) {
			chqBouncepenalty = getCurrentDemand().getMinAmtPayable();
		}
		if (getUserId() != null && !getUserId().equals("")) {
			String loginUser = userService.getUserById(getUserId()).getName();
			if (loginUser.equals(PropertyTaxConstants.CITIZENUSER)) {
				// New Modes for the Client are to be added i.e BlackBerry
				// payment etc.
				modesNotAllowed = "cash,cheque";
			} else if (!loginUser.equals(PropertyTaxConstants.CITIZENUSER)
					&& chqBouncepenalty.compareTo(BigDecimal.ZERO) > 0) {
				modesNotAllowed = "cheque";
			}
		}
		return modesNotAllowed;
	}

	@Override
	public String getDepartmentCode() {
		return "R";

	}

	@Override
	public Date getIssueDate() {
		return new Date();
	}

	@Override
	public Date getLastDate() {
		return getBillLastDueDate();
	}

	@Override
	public String getServiceCode() {
		return "PT";
	}

	@Override
	public BigDecimal getTotalAmount() {
		EgDemand currentDemand = getCurrentDemand();
		List instVsAmt = propertyDao.getDmdCollAmtInstWise(currentDemand);
		BigDecimal balance = BigDecimal.ZERO;
		for (Object object : instVsAmt) {
			Object[] ddObject = (Object[]) object;
			BigDecimal dmdAmt = (BigDecimal) ddObject[1];
			BigDecimal collAmt = BigDecimal.ZERO;
			if (ddObject[2] != null) {
				collAmt = (BigDecimal) ddObject[2];
			}
			balance = balance.add(dmdAmt.subtract(collAmt));
			BigDecimal penaltyAmount = demandGenericDao.getBalanceByDmdMasterCode(
					currentDemand, PropertyTaxConstants.PENALTY_DMD_RSN_CODE,
					getModule());
			if (penaltyAmount != null
					&& penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				balance = balance.add(penaltyAmount);
			}
		}
		return balance;
	}

	@Override
	public String getDescription() {
		return "Property Tax Bill Number: " + getBasicProperty().getUpicNo();
	}

	@Override
	public String getPropertyId() {
		return getBasicProperty().getUpicNo();
	}

	/**
	 * Method Overridden to get all the Demands (including all the history and
	 * non history) for a basicproperty .
	 * 
	 * @return java.util.List<EgDemand>
	 * 
	 */

	@Override
	public List<EgDemand> getAllDemands() {
		List<EgDemand> demands = null;
		List demandIds = propertyDao.getAllDemands(getBasicProperty());
		if (demandIds != null && !demandIds.isEmpty()) {
			demands = new ArrayList<EgDemand>();
			Iterator iter = demandIds.iterator();
			while (iter.hasNext()) {
				demands.add((EgDemand) demandDao.findById(
						Long.valueOf(iter.next().toString()), false));
			}
		}
		return demands;
	}

}