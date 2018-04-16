/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.ptis.service.es;

import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import static org.egov.ptis.constants.PropertyTaxConstants.DATE_FORMAT_YYYYMMDD;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_APPROVED;
import static org.egov.ptis.constants.PropertyTaxConstants.WF_STATE_CLOSED;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.utils.DateUtils;
import org.egov.ptis.domain.entity.es.PTGISIndex;
import org.egov.ptis.domain.entity.property.survey.SearchSurveyRequest;
import org.egov.ptis.domain.entity.property.survey.SearchSurveyResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
@Service
public class SurveyApplicationSearchService {
    private static final String CITY_CODE = "cityCode";
    private static final String APP_COUNT = "AppCount";
    private static final String APP_NO = "applicationNo";
    private static final String SURVEY_INDEX = "propertysurveydetails";
    private static final String APP_DATE = "applicationDate";
    private static final String APP_VIEW_URL = "/ptis/view/viewProperty-viewForm.action?applicationNo=%s&applicationType=%s";
    private static final String STATUS_CANCELLED = "Cancelled";
    private static final String APPLICATION_STATUS = "applicationStatus";
    private static final String ISAPPROVED = "isApproved";
    private static final String ISCANCELLED = "isCancelled";
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public List<SearchSurveyResponse> getDetailsBasedOnRequest(final SearchSurveyRequest searchSurveyRequest) {
        List<SearchSurveyResponse> responselist = new ArrayList<>();
        Date fromDate;
        Date toDate;
        if (StringUtils.isNotBlank(searchSurveyRequest.getFromDate()))
            fromDate = DateUtils.getDate(searchSurveyRequest.getFromDate(),DATE_FORMAT_YYYYMMDD);
        else
            fromDate = new Date(2018 - 01 - 01);
        if (StringUtils.isNotBlank(searchSurveyRequest.getToDate()))
            toDate = DateUtils.getDate(searchSurveyRequest.getToDate(), DATE_FORMAT_YYYYMMDD);
        else
            toDate = new Date();
        NativeSearchQuery search = new NativeSearchQueryBuilder()
                .addAggregation(AggregationBuilders.count(APP_COUNT).field(APP_NO))
                .withIndices(SURVEY_INDEX).withQuery(prepareQuery(searchSurveyRequest, fromDate, toDate))
                .build();
        final Aggregations applicationCountAggr = elasticsearchTemplate.query(search, SearchResponse::getAggregations);
        final ValueCount aggr = applicationCountAggr.get(APP_COUNT);
        search = new NativeSearchQueryBuilder().withIndices(SURVEY_INDEX)
                .withQuery(prepareQuery(searchSurveyRequest, fromDate, toDate))
                .addAggregation(AggregationBuilders.count(APP_COUNT).field(APP_NO))
                .withPageable(new PageRequest(0, toIntExact(aggr.getValue() == 0 ? 1 : aggr.getValue())))
                .build();
        List<PTGISIndex> searchList = elasticsearchTemplate.queryForList(search, PTGISIndex.class);
        for (PTGISIndex index : searchList) {
            SearchSurveyResponse surveyResponse = new SearchSurveyResponse();
            surveyResponse.setApplicationType(index.getApplicationType());
            surveyResponse.setApplicationNo(index.getApplicationNo());
            surveyResponse.setApplicationDate(DateUtils.getFormattedDate(index.getApplicationDate(), "dd/MM/yyyy"));
            surveyResponse.setAssessmentNo(index.getAssessmentNo().equals(StringUtils.EMPTY) ? "NA" : index.getAssessmentNo());
            surveyResponse.setAddress(index.getDoorNo().concat(",").concat(index.getLocalityName()).concat(",")
                    .concat(index.getBlockName().concat(",")).concat(index.getElectionWard()).concat(",")
                    .concat(index.getRevenueWard()).concat(",").concat(index.getCityName()));
            surveyResponse.setApplicationStatus(index.getApplicationStatus());
            surveyResponse
                    .setAppViewUrl(format(APP_VIEW_URL, surveyResponse.getApplicationNo(), surveyResponse.getApplicationType()));
            surveyResponse.setFunctionaryName(index.getFunctionaryName() == null ? "NA" : index.getFunctionaryName());
            responselist.add(surveyResponse);
        }
        return responselist;
    }

    private BoolQueryBuilder prepareQuery(SearchSurveyRequest searchSurveyRequest, Date fromDate, Date toDate) {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery = boolQuery.filter(QueryBuilders.rangeQuery(APP_DATE).gte(fromDate).lte(toDate))
                .filter(QueryBuilders.matchQuery(CITY_CODE, ApplicationThreadLocals.getCityCode()));
        if (StringUtils.isNotBlank(searchSurveyRequest.getApplicationType()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("applicationType", searchSurveyRequest.getApplicationType()));
        if (StringUtils.isNotBlank(searchSurveyRequest.getApplicationNo()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(APP_NO, searchSurveyRequest.getApplicationNo()));
        if (StringUtils.isNotBlank(searchSurveyRequest.getApplicationStatus())) {
            if (searchSurveyRequest.getApplicationStatus().equals(WF_STATE_CLOSED))
                boolQuery = boolQuery
                        .filter(QueryBuilders.matchQuery(APPLICATION_STATUS, searchSurveyRequest.getApplicationStatus()));
            else if (searchSurveyRequest.getApplicationStatus().equals(STATUS_APPROVED))
                boolQuery = boolQuery.filter(QueryBuilders.matchQuery(ISAPPROVED, true));
            else if (searchSurveyRequest.getApplicationStatus().equals(STATUS_CANCELLED))
                boolQuery = boolQuery.filter(QueryBuilders.matchQuery(ISCANCELLED, true));
            else
                boolQuery = boolQuery.mustNot(QueryBuilders.matchQuery(APPLICATION_STATUS,WF_STATE_CLOSED))
                        .must(QueryBuilders.matchQuery(ISAPPROVED, false)).must(QueryBuilders.matchQuery(ISCANCELLED, false));
        }
        if (StringUtils.isNotBlank(searchSurveyRequest.getAssessmentNo()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("assessmentNo", searchSurveyRequest.getAssessmentNo()));

        return boolQuery;
    }

}
