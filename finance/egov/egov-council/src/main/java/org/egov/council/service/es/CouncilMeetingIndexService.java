/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *     accountability and the service delivery of the government  organizations.
 *
 *      Copyright (C) 2016  eGovernments Foundation
 *
 *      The updated version of eGov suite of products as by eGovernments Foundation
 *      is available at http://www.egovernments.org
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program. If not, see http://www.gnu.org/licenses/ or
 *      http://www.gnu.org/licenses/gpl.html .
 *
 *      In addition to the terms of the GPL license to be adhered to in using this
 *      program, the following additional terms are to be complied with:
 *
 *          1) All versions of this program, verbatim or modified must carry this
 *             Legal Notice.
 *
 *          2) Any misrepresentation of the origin of the material is prohibited. It
 *             is required that all modified versions of this material be marked in
 *             reasonable ways as different from the original version.
 *
 *          3) This license does not grant any rights to any user of the program
 *             with regards to rights under trademark law for use of the trade names
 *             or trademarks of eGovernments Foundation.
 *
 *    In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.council.service.es;

import static org.egov.council.utils.constants.CouncilConstants.PREAMBLE_STATUS_ADJOURNED;
import static org.egov.council.utils.constants.CouncilConstants.PREAMBLE_STATUS_APPROVED;
import static org.egov.council.utils.constants.CouncilConstants.REJECTED;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.entity.MeetingAttendence;
import org.egov.council.entity.MeetingMOM;
import org.egov.council.entity.es.CouncilMeetingDetailsSearchRequest;
import org.egov.council.entity.es.CouncilMeetingIndex;
import org.egov.council.repository.es.CouncilMeetingIndexRepository;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CouncilMeetingIndexService {

    @Autowired
    private CityService cityService;
   
    @Autowired
    private CouncilMeetingIndexRepository councilMeetingIndexRepository;
    
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    
    public CouncilMeetingIndex createCouncilMeetingIndex(final CouncilMeeting councilMeeting) throws ParseException{
        int noOfMembersPresent = 0;
        int noOfMembersAbsent = 0;
        int noOfPreamblesApproved= 0;
        int noOfPreamblesPostponed =0;
        int noOfPreamblesRejected =0;
        final City cityWebsite = cityService.getCityByURL(ApplicationThreadLocals.getDomainName());
        CouncilMeetingIndex meetingIndex = new CouncilMeetingIndex();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        if(councilMeeting != null){
            meetingIndex.setUlbName(cityWebsite.getName());
            meetingIndex.setId(cityWebsite.getCode().concat("-").concat(councilMeeting.getMeetingNumber()));
            meetingIndex.setCommitteeType(councilMeeting.getCommitteeType().getName()!= null ?councilMeeting.getCommitteeType().getName():"");
           String meetingDate = dateFormat.format(councilMeeting.getMeetingDate());
            meetingIndex.setMeetingDate(dateFormat.parse(meetingDate));
            meetingIndex.setMeetingLocation(councilMeeting.getMeetingLocation()!= null ?councilMeeting.getMeetingLocation():"");
            meetingIndex.setMeetingNumber(councilMeeting.getMeetingNumber() != null ?councilMeeting.getMeetingNumber():"");
            meetingIndex.setMeetingTime(councilMeeting.getMeetingTime() != null ?councilMeeting.getMeetingTime():"");
            meetingIndex.setStatus(councilMeeting.getStatus() != null ?councilMeeting.getStatus().getCode():"");
            meetingIndex.setTotalNoOfCommitteMembers(councilMeeting.getCommitteeType() != null && councilMeeting.getCommitteeType().getCommiteemembers()!= null ?councilMeeting.getCommitteeType().getCommiteemembers().size():0);
            if(councilMeeting.getMeetingAttendence() != null){
                for (MeetingAttendence attendence : councilMeeting.getMeetingAttendence()) {
                    if(attendence.getAttendedMeeting()) {
                        noOfMembersPresent++;
                    } else {
                        noOfMembersAbsent++;
                    }
                }
            }
            meetingIndex.setNoOfCommitteMembersPresent(noOfMembersPresent);
            meetingIndex.setNoOfCommitteMembersAbsent(noOfMembersAbsent);
            
            if(!councilMeeting.getMeetingMOMs().isEmpty()){
                for(MeetingMOM mom : councilMeeting.getMeetingMOMs()){
                   if(PREAMBLE_STATUS_APPROVED.equals(mom.getResolutionStatus().getCode())){
                       noOfPreamblesApproved++;
                   } else if(PREAMBLE_STATUS_ADJOURNED.equals(mom.getResolutionStatus().getCode())){
                       noOfPreamblesPostponed++;
                   }else if(REJECTED.equals(mom.getResolutionStatus().getCode())){
                       noOfPreamblesRejected++;
                    }
                }
            }
           meetingIndex.setTotalNoOfPreamblesUsed(councilMeeting.getMeetingMOMs().size());
           meetingIndex.setNoOfPreamblesApproved(noOfPreamblesApproved);
           meetingIndex.setNoOfPreamblesPostponed(noOfPreamblesPostponed);
           meetingIndex.setNoOfPreamblesRejected(noOfPreamblesRejected);
        }
        councilMeetingIndexRepository.save(meetingIndex);
        return meetingIndex;
    }
    
    public List<CouncilMeetingIndex> getQueryFilterForMeetingDetails(final CouncilMeetingDetailsSearchRequest searchRequest){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (searchRequest.getFrom() != null && searchRequest.getTo() != null){
        boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("meetingDate")
                .from(searchRequest.getFrom())
                .to(searchRequest.getTo()));
        }
        if (StringUtils.isNotBlank(searchRequest.getCommitteeType()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("committeeType", searchRequest.getCommitteeType()));
        if (StringUtils.isNotBlank(searchRequest.getMeetingNumber()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("meetingNumber", searchRequest.getMeetingNumber()));
        final FieldSortBuilder sort = new FieldSortBuilder("committeeType").order(SortOrder.DESC);
        
        return getSearchResultByBoolQuery(boolQuery,sort);
    }

    public List<CouncilMeetingIndex> getSearchResultByBoolQuery(final BoolQueryBuilder boolQuery, final FieldSortBuilder sort) {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("councilmeeting").withQuery(boolQuery)
                .withSort(sort).build();
        return elasticsearchTemplate.queryForList(searchQuery, CouncilMeetingIndex.class);
    }
    
    public BoolQueryBuilder prepareWhereClause(final CouncilMeetingDetailsSearchRequest searchRequest){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery("ulbName", searchRequest.getUlbName()));
        if (searchRequest.getFrom() != null && searchRequest.getTo() != null){
         boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("meetingDate")
                .from(searchRequest.getFrom())
                .to(searchRequest.getTo()));
        }
        if (StringUtils.isNotBlank(searchRequest.getCommitteeType()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("committeeType", searchRequest.getCommitteeType()));
        if (StringUtils.isNotBlank(searchRequest.getMeetingNumber()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("meetingNumber", searchRequest.getMeetingNumber()));
        
        return boolQuery;
    }
}