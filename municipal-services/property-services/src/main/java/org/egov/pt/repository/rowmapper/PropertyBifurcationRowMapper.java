package org.egov.pt.repository.rowmapper;


import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.models.Address;
import org.egov.pt.models.AmalgamatedProperty;
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.ConstructionDetail;
import org.egov.pt.models.Document;
import org.egov.pt.models.GeoLocation;
import org.egov.pt.models.Institution;
import org.egov.pt.models.Locality;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyBifurcation;
import org.egov.pt.models.TypeOfRoad;
import org.egov.pt.models.Unit;
import org.egov.pt.models.enums.Channel;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Relationship;
import org.egov.pt.models.enums.Status;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class PropertyBifurcationRowMapper implements ResultSetExtractor<List<PropertyBifurcation>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<PropertyBifurcation> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, PropertyBifurcation> propertyMap = new LinkedHashMap<>();

		while (rs.next()) {

			String propertyUuId = rs.getString("id");
			PropertyBifurcation currentProperty = propertyMap.get(propertyUuId);
			

			if (null == currentProperty) {
				
				currentProperty = PropertyBifurcation.builder()
						.propertyDetails(getadditionalDetail(rs, "property_details"))
						.parentPropertyId(rs.getString("parent_property"))
						.id(rs.getInt("id"))
						.createdTime(rs.getInt("createdtime"))
						.maxBifurcation(rs.getInt("max_bifurcation"))
						.status(rs.getBoolean("status"))
						.childpropertyuuid(rs.getString("childpropertyuuid"))
						.build();

				
				@SuppressWarnings("unchecked")
				Property additionalDetails = mapper.convertValue(currentProperty.getPropertyDetails(), Property.class);
				if(null!=additionalDetails) {
					@SuppressWarnings("unchecked")
					PropertyRequest propertyRequest = new PropertyRequest();
					propertyRequest.setProperty(additionalDetails);
					currentProperty.setPropertyRequest(propertyRequest);
				}
				
				propertyMap.put(propertyUuId, currentProperty);
			}

			
		
		}
		return new ArrayList<>(propertyMap.values());
	
	}

	/**
	 * Adding children elements to Property
	 * 
	 * @param rs
	 * @param currentProperty
	 * @throws SQLException
	 */
	private void addChildrenToProperty(ResultSet rs, Property currentProperty)
			throws SQLException {

		addOwnerToProperty(rs, currentProperty);
		addDocToProperty(rs, currentProperty);
		addUnitsToProperty(rs, currentProperty);
	}


	/**
	 * Adds units to property
	 * @param rs
	 * @param currentProperty
	 * @throws SQLException 
	 */
	private void addUnitsToProperty(ResultSet rs, Property currentProperty) throws SQLException {
		
		List<Unit> units = currentProperty.getUnits();

		String unitId = rs.getString("unitid");
		if (null == unitId)
			return;

		if (!CollectionUtils.isEmpty(units))
			for (Unit unit : units) {
				if (unit.getId().equals(unitId))
					return;
			}
		
		Long constructionDate = 0 == rs.getLong("constructionDate") ? null : rs.getLong("constructionDate");

		ConstructionDetail consDetail = ConstructionDetail.builder()
				.constructionType(rs.getString("constructionType"))
				.dimensions(getadditionalDetail(rs, "dimensions"))
				.constructionDate(constructionDate)
				.superBuiltUpArea(rs.getBigDecimal("unitspba"))
				.builtUpArea(rs.getBigDecimal("builtUpArea"))
				.carpetArea(rs.getBigDecimal("carpetArea"))
				.plinthArea(rs.getBigDecimal("plinthArea"))
				.build();

		
		BigDecimal arv = rs.getBigDecimal("arv");
		if (null != arv)
			arv = arv.stripTrailingZeros();

		Unit unit = Unit.builder()
				.occupancyType(rs.getString("occupancyType"))
				.usageCategory(rs.getString("unitusageCategory"))
				.occupancyDate(rs.getLong("occupancyDate"))
				.active(rs.getBoolean("isunitactive"))
				.unitType(rs.getString("unitType"))
				.constructionDetail(consDetail)
				.floorNo(rs.getInt("floorno"))
				.ageOfProperty(rs.getString("ageofproperty"))
				.structureType(rs.getString("structuretype"))
				.arv(arv)
				.id(unitId)
				.build();
		
		currentProperty.addUnitsItem(unit);
	}

	
	/**
	 * Adds document to Property
	 * 
	 * Same document table is being used by both property and owner table, so id check is mandatory
	 * 
	 * @param rs
	 * @param property
	 * @throws SQLException
	 */
	private void addDocToProperty(ResultSet rs, Property property) throws SQLException {

		String docId = rs.getString("pdocid");
		String entityId = rs.getString("pdocentityid");
		List<Document> docs = property.getDocuments();
		
		if (!(docId != null && entityId.equals(property.getId())))
			return;

		if (!CollectionUtils.isEmpty(docs))
			for (Document doc : docs) {
				if (doc.getId().equals(docId))
					return;
			}

		Document doc =  Document.builder()
			.status(Status.fromValue(rs.getString("pdocstatus")))
			.documentType(rs.getString("pdoctype"))
			.fileStoreId(rs.getString("pdocfileStore"))
			.documentUid(rs.getString("pdocuid"))
			.id(docId)
			.build();
		
		property.addDocumentsItem(doc);
	}
	
	/**
	 * Adds Owner Object to Property
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private void addOwnerToProperty(ResultSet rs, Property property) throws SQLException {
		
		String uuid = rs.getString("userid");
		List<OwnerInfo> owners = property.getOwners();

		if (!CollectionUtils.isEmpty(owners))
			for (OwnerInfo owner : owners) {
				if (owner.getUuid().equals(uuid))
					return;
			}

		Double ownerShipPercentage = rs.getDouble("ownerShipPercentage");
		if(rs.wasNull()) {
			ownerShipPercentage = null;
			}
		
		Boolean isPrimaryOwner = rs.getBoolean("isPrimaryOwner");
		if(rs.wasNull()) {
			isPrimaryOwner = null;
			}
		
		OwnerInfo owner = OwnerInfo.builder()
				.relationship(Relationship.fromValue(rs.getString("relationship")))
				.status(Status.fromValue(rs.getString("ownstatus")))
				.institutionId(rs.getString("owninstitutionid"))
				.ownerInfoUuid(rs.getString("ownerInfoUuid"))
				.ownerShipPercentage(ownerShipPercentage)
				.tenantId(rs.getString("owntenantid"))
				.ownerType(rs.getString("ownerType"))
				.isPrimaryOwner(isPrimaryOwner)
				.uuid(uuid)
				.build();
		
		addDocToOwner(rs, owner);
		
		property.addOwnersItem(owner);
	}
	
	/**
	 * Method to add documents to Owner
	 * 
	 * Same document table is being used by both property and owner table, so id check is mandatory
	 * 
	 * @param rs
	 * @param owner
	 * @throws SQLException
	 */
	private void addDocToOwner(ResultSet rs, OwnerInfo owner) throws SQLException {
		
		String docId = rs.getString("owndocid");
		String 	entityId = rs.getString("owndocentityId");
		List<Document> docs = owner.getDocuments();

		if (!(null != docId && entityId.equals(owner.getOwnerInfoUuid())))
			return;

		if (!CollectionUtils.isEmpty(docs))
			for (Document doc : docs) {
				if (doc.getId().equals(docId))
					return;
			}
	
		Document doc = Document.builder()
			.status(Status.fromValue(rs.getString("owndocstatus")))
			.fileStoreId(rs.getString("owndocfileStore"))
			.documentType(rs.getString("owndoctype"))
			.documentUid(rs.getString("owndocuid"))
			.id(docId)
			.build();
		
		owner.addDocumentsItem(doc);
	}

	
	/**
	 * creates and adds the address object to property 
	 * 
	 * @param rs
	 * @param tenanId
	 * @return
	 * @throws SQLException
	 */
	private Address getAddress(ResultSet rs, String tenanId) throws SQLException {
		
		Locality locality = Locality.builder().code(rs.getString("locality")).build();

		GeoLocation geoLocation = GeoLocation.builder()
				.longitude(rs.getDouble("longitude"))
				.latitude(rs.getDouble("latitude"))
				.build();

		return Address.builder()
		.additionalDetails(getadditionalDetail(rs, "addressadditionaldetails"))
		.buildingName(rs.getString("buildingname"))
		.landmark(rs.getString("landmark"))
		.district(rs.getString("district"))
		.country(rs.getString("country"))
		.pincode(rs.getString("pincode"))
		.dagNo(rs.getString("dag_no"))
		.village(rs.getString("village"))
		.pattaNo(rs.getString("patta_no"))
		.doorNo(rs.getString("doorNo"))
		.plotNo(rs.getString("plotNo"))
		.principalRoadName(rs.getString("principal_road_name"))
		.subSideRoadName(rs.getString("sub_side_road_name"))
		.commonNameOfBuilding(rs.getString("common_name_of_building"))
		.region(rs.getString("region"))
		.street(rs.getString("street"))
		.id(rs.getString("addressid"))
		.state(rs.getString("state"))
		.city(rs.getString("city"))
		.typeOfRoad(getTypeOfRoad(rs,"type_of_road"))
		.commonNameOfBuilding(rs.getString("common_name_of_building"))
		.geoLocation(geoLocation)
		.locality(locality)
		.tenantId(tenanId)
		.build();
	}
	private TypeOfRoad getTypeOfRoad(ResultSet rs, String key) throws SQLException {

		TypeOfRoad propertyAdditionalDetails = null;

		try {

			String obj =  rs.getString(key);
			if (obj != null) {
				propertyAdditionalDetails =new TypeOfRoad();
			
				propertyAdditionalDetails.setCode( obj);
			}

		} catch (Exception e) {
			throw new CustomException("PARSING ERROR", "The propertyAdditionalDetail json cannot be parsed");
		}
		
		//System.out.println("propertyAdditionalDetails:::"+propertyAdditionalDetails.getCode());

		//if(propertyAdditionalDetails.getCode().isEmpty())
			//propertyAdditionalDetails = null;
		
		return propertyAdditionalDetails;
	}
	/**
	 * prepares and returns an audit detail object
	 * 
	 * depending on the source the column names of result set will vary
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private AuditDetails getAuditDetail(ResultSet rs, String source) throws SQLException {
		
		switch (source) {
		
		case "property":
			
			Long lastModifiedTime = rs.getLong("plastModifiedTime");
			if (rs.wasNull()) {
				lastModifiedTime = null;
			}
			
			return AuditDetails.builder().createdBy(rs.getString("pcreatedBy"))
					.createdTime(rs.getLong("pcreatedTime")).lastModifiedBy(rs.getString("plastModifiedBy"))
					.lastModifiedTime(lastModifiedTime).build();
			
		default: 
			return null;
			
		}

	}
	
	/**
	 * method parses the PGobject and returns the JSON node
	 * 
	 * @param rs
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	private JsonNode getadditionalDetail(ResultSet rs, String key) throws SQLException {

		JsonNode propertyAdditionalDetails = null;

		try {

			PGobject obj = (PGobject) rs.getObject(key);
			if (obj != null) {
				propertyAdditionalDetails = mapper.readTree(obj.getValue());
			}

		} catch (IOException e) {
			throw new CustomException("PARSING ERROR", "The propertyAdditionalDetail json cannot be parsed");
		}

		if(propertyAdditionalDetails.isEmpty())
			propertyAdditionalDetails = null;
		
		return propertyAdditionalDetails;

	}

	/*
	 *  method sets all the data for PropertyInfo
	 *
	 * @param currentProperty
	 * @param rs
	 * @param tenantId
	 * @param propertyUuId
	 *
	 * @throws SQLException
	* */
	private void setPropertyInfo(Property currentProperty, ResultSet rs, String tenantId, String propertyUuId,
								 List<String> linkedProperties, Address address)
			throws SQLException {
		currentProperty.setPropertyId(rs.getString("propertyid"));
		currentProperty.setAddress(address);
		currentProperty.setStatus(Status.fromValue(rs.getString("propertystatus")));
		currentProperty.setOldPropertyId(rs.getString("oldPropertyId"));
		currentProperty.setAccountId(rs.getString("accountid"));
		currentProperty.setSurveyId(rs.getString("surveyId"));
		currentProperty.setLinkedProperties(linkedProperties);
		currentProperty.setTenantId(tenantId);
		currentProperty.setId(propertyUuId);
	}

}