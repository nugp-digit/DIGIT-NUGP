package org.egov.swservice.repository.rowmapper;

import org.egov.swservice.util.SWConstants;
import org.egov.swservice.web.models.*;
import org.egov.swservice.web.models.Connection.StatusEnum;
import org.egov.swservice.web.models.workflow.ProcessInstance;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class SewerageRowMapperForTable implements ResultSetExtractor<List<SewerageConnection>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<SewerageConnection> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, SewerageConnection> connectionListMap = new LinkedHashMap<>();
		SewerageConnection sewarageConnection = new SewerageConnection();
		while (rs.next()) {
			String Id = rs.getString("connection_Id");
			if (connectionListMap.getOrDefault(Id, null) == null) {
				sewarageConnection = new SewerageConnection();
				sewarageConnection.setTenantId(rs.getString("tenantid"));
				sewarageConnection.setId(rs.getString("connection_Id"));
				sewarageConnection.setApplicationNo(rs.getString("applicationNo"));
				sewarageConnection.setApplicationStatus(rs.getString("applicationstatus"));
				sewarageConnection.setStatus(StatusEnum.fromValue(rs.getString("status")));
				sewarageConnection.setConnectionNo(rs.getString("connectionNo"));
				sewarageConnection.setOldConnectionNo(rs.getString("oldConnectionNo"));
				sewarageConnection.setConnectionExecutionDate(rs.getLong("connectionExecutionDate"));
				sewarageConnection.setNoOfToilets(rs.getInt("noOfToilets"));
				sewarageConnection.setNoOfWaterClosets(rs.getInt("noOfWaterClosets"));
				sewarageConnection.setProposedToilets(rs.getInt("proposedToilets"));
				sewarageConnection.setProposedWaterClosets(rs.getInt("proposedWaterClosets"));
				sewarageConnection.setConnectionType(rs.getString("connectionType"));
				sewarageConnection.setRoadCuttingArea(rs.getFloat("roadcuttingarea"));
				sewarageConnection.setRoadType(rs.getString("roadtype"));
				sewarageConnection.setOldApplication(rs.getBoolean("isoldapplication"));
				sewarageConnection.setDisconnectionReason(rs.getString("disconnectionReason"));
				sewarageConnection.setIsDisconnectionTemporary(rs.getBoolean("isDisconnectionTemporary"));
				sewarageConnection.setDisconnectionExecutionDate(rs.getLong("disconnectionExecutionDate"));

				// get property id and get property object
//                PGobject pgObj = (PGobject) rs.getObject("additionaldetails");
				ObjectNode addtionalDetails = null;
//				if (pgObj != null) {
//
//					try {
//						addtionalDetails = mapper.readValue(pgObj.getValue(), ObjectNode.class);
//					} catch (IOException ex) {
//						// TODO Auto-generated catch block
//						throw new CustomException("PARSING ERROR", "The additionalDetail json cannot be parsed");
//					}
//				} else {
				addtionalDetails = mapper.createObjectNode();
//				}
				// HashMap<String, Object> addtionalDetails = new HashMap<>();
				addtionalDetails.put(SWConstants.ADHOC_PENALTY, rs.getBigDecimal("adhocpenalty"));
				addtionalDetails.put(SWConstants.ADHOC_REBATE, rs.getBigDecimal("adhocrebate"));
				addtionalDetails.put(SWConstants.ADHOC_PENALTY_REASON, rs.getString("adhocpenaltyreason"));
				addtionalDetails.put(SWConstants.ADHOC_PENALTY_COMMENT, rs.getString("adhocpenaltycomment"));
				addtionalDetails.put(SWConstants.ADHOC_REBATE_REASON, rs.getString("adhocrebatereason"));
				addtionalDetails.put(SWConstants.ADHOC_REBATE_COMMENT, rs.getString("adhocrebatecomment"));
				addtionalDetails.put(SWConstants.APP_CREATED_DATE, rs.getBigDecimal("appCreatedDate"));
				addtionalDetails.put(SWConstants.DETAILS_PROVIDED_BY, rs.getString("detailsprovidedby"));
				addtionalDetails.put(SWConstants.ESTIMATION_FILESTORE_ID, rs.getString("estimationfileStoreId"));
				addtionalDetails.put(SWConstants.SANCTION_LETTER_FILESTORE_ID, rs.getString("sanctionfileStoreId"));
				addtionalDetails.put(SWConstants.ESTIMATION_DATE_CONST, rs.getBigDecimal("estimationLetterDate"));
				addtionalDetails.put(SWConstants.LOCALITY, rs.getString("locality"));
				sewarageConnection.setAdditionalDetails(addtionalDetails);
				sewarageConnection.processInstance(ProcessInstance.builder().action((rs.getString("action"))).build());
				sewarageConnection.setApplicationType(rs.getString("applicationType"));
				sewarageConnection.setChannel(rs.getString("channel"));
				sewarageConnection.setDateEffectiveFrom(rs.getLong("dateEffectiveFrom"));
				sewarageConnection.setPropertyId(rs.getString("property_id"));

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("sw_createdBy"))
						.createdTime(rs.getLong("sw_createdTime")).lastModifiedBy(rs.getString("sw_lastModifiedBy"))
						.lastModifiedTime(rs.getLong("sw_lastModifiedTime")).build();
				sewarageConnection.setAuditDetails(auditdetails);

				// Add documents id's
				connectionListMap.put(Id, sewarageConnection);
			}
//			addDocumentToSewerageConnection(rs, sewarageConnection);
//			addPlumberInfoToSewerageConnection(rs, sewarageConnection);
//			addHoldersDeatilsToSewerageConnection(rs, sewarageConnection);
//            addRoadCuttingInfotToSewerageConnection(rs, sewarageConnection);
		}
		return new ArrayList<>(connectionListMap.values());
	}

}