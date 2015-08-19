package org.egov.ptis.client.model.calculator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.wtms.ConsumerConsumtion;
import org.egov.ptis.wtms.PropertyWiseConsumptions;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DemandNoticeInfo {
	private BasicProperty basicProperty;
	private String billNo;
	private String oldAssessmentNo;
	private String noOfTap;
	private String sewarageConnectionNo;
	private String rentPaid;
	private List<DemandNoticeDetailsInfo> demandNoticeDetailsInfo;
	private ReportUtil reportUtil;
	private PropertyWiseConsumptions propertyWiseConsumptions;
	
	@Autowired
	@Qualifier("cityService")
	private CityService cityService;
	
	// reading cityname and logo from citywebsiteservice to support bulkbillgeneration through schedular
	public City getcityWebsite(){
	    City cw= cityService.findAll().get(0);
            return cw;
	}
	
	 public String getCityName() {
	     City cw= getcityWebsite();
	     return cw!=null?cw.getName():null;
	  }

	 public  String getCityLogo() {
	     String path=null;
	     try{
	         path=reportUtil.logoBasePath();
	     }catch(final Exception e){
	         Log.error(e.getMessage());
	     }
             return path;
          }

	public String getWardNo() {
		return getBasicProperty().getPropertyID().getWard().getBoundaryNum().toString();
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getBillDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(new Date());
	}

	public String getName() {
		return (basicProperty.getFullOwnerName());
	}

	public String getAssessmentNo() {
		return getBasicProperty().getUpicNo();
	}

	public String getOldAssessmentNo() {
		return oldAssessmentNo;
	}

	public void setOldAssessmentNo(String oldAssessmentNo) {
		this.oldAssessmentNo = oldAssessmentNo;
	}

	public String getWaterConnectionNo() {
	     String waterConnectionNo="";
	        if(propertyWiseConsumptions!=null){
	            if(propertyWiseConsumptions.getConsumerConsumtions()!=null && !propertyWiseConsumptions.getConsumerConsumtions().isEmpty()){
	                String hscno="";
	                for(ConsumerConsumtion cc:propertyWiseConsumptions.getConsumerConsumtions()){
	                    if(cc!=null){
	                        if(hscno!=null && hscno!="")  
	                            hscno=hscno+cc.getHscno()+",";
	                    } 
	                }
	                if(hscno!="" && hscno!=null)    
	                    waterConnectionNo=hscno.substring(0,hscno.length()-1); 
	            }
	        }

	        return waterConnectionNo;
	}

	public String getNoOfTap() {
    	        if(propertyWiseConsumptions!=null){
                  if(propertyWiseConsumptions.getConsumerConsumtions()!=null && !propertyWiseConsumptions.getConsumerConsumtions().isEmpty())  
                      noOfTap=Integer.toString(propertyWiseConsumptions.getConsumerConsumtions().size());
    	        }
		return noOfTap;
	}


	public String getSewarageConnectionNo() {
		return sewarageConnectionNo;
	}

	public void setSewarageConnectionNo(String sewarageConnectionNo) {
		this.sewarageConnectionNo = sewarageConnectionNo;
	}

	public String getRentPaid() {
		return rentPaid;
	}

	public void setRentPaid(String rentPaid) {
		this.rentPaid = rentPaid;
	}

	public String getStreetName() {
		return (basicProperty.getPropertyID().getStreet() != null ? basicProperty.getPropertyID().getStreet().getName()
				: null);
	}

	public String getHouseNo() {
		return getBasicProperty().getAddress().getHouseNoBldgApt();
	}

	public BasicProperty getBasicProperty() {
		return basicProperty;
	}

	public void setBasicProperty(BasicProperty basicProperty) {
		this.basicProperty = basicProperty;
	}

	public List<DemandNoticeDetailsInfo> getDemandNoticeDetailsInfo() {
		return demandNoticeDetailsInfo;
	}

	public void setDemandNoticeDetailsInfo(List<DemandNoticeDetailsInfo> demandNoticeDetailsInfo) {
		this.demandNoticeDetailsInfo = demandNoticeDetailsInfo;
	}

    public ReportUtil getReportUtil() {
        return reportUtil;
    }

    public void setReportUtil(ReportUtil reportUtil) {
        this.reportUtil = reportUtil;
    }

    public PropertyWiseConsumptions getPropertyWiseConsumptions() {
        return propertyWiseConsumptions;
    }

    public void setPropertyWiseConsumptions(PropertyWiseConsumptions propertyWiseConsumptions) {
        this.propertyWiseConsumptions = propertyWiseConsumptions;
    }

}
