/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
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
 */

package org.egov.works.elasticsearch.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.works.elasticsearch.model.WorksMilestoneIndexResponse;
import org.egov.works.elasticsearch.model.WorksTransactionIndex;
import org.egov.works.elasticsearch.model.WorksTransactionIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 * @author venki
 */
@Service
public class WorksTransactionIndexService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorksTransactionIndexService.class);

    public static final String WORKSTRANSACTION_INDEX_NAME = "workstransaction";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public void getWorksTransactionDetails(final WorksTransactionIndexRequest worksTransactionIndexRequest,
            final WorksMilestoneIndexResponse wmIndexResponse) {
        final BoolQueryBuilder boolQuery = prepareWhereClause(worksTransactionIndexRequest);
        Long startTime = System.currentTimeMillis();
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(WORKSTRANSACTION_INDEX_NAME)
                .withQuery(boolQuery)
                .build();
        final List<WorksTransactionIndex> worksTransactionIndexs = elasticsearchTemplate.queryForList(searchQuery,
                WorksTransactionIndex.class);
        for (final WorksTransactionIndex response : worksTransactionIndexs) {
            wmIndexResponse.setFund(response.getLineestimatefund());
            wmIndexResponse.setScheme(response.getLineestimatescheme());
            wmIndexResponse.setSubscheme(response.getLineestimatesubscheme());
            wmIndexResponse.setWard(response.getLineestimateboundary());
            wmIndexResponse.setEstimatenumber(response.getEstimatenumber());
            wmIndexResponse.setWin(response.getEstimatewin());
            wmIndexResponse.setNameofthework(response.getNameofthework());
            wmIndexResponse.setContractornamecode(response.getLoanameofagency() + "/" + response.getLoacontractorcode());
            wmIndexResponse.setAgreementnumber(response.getLoanumber());
            wmIndexResponse.setAgreementdate(response.getAgreementdate());
            wmIndexResponse.setWorkstatus(response.getWorkstatus());
            wmIndexResponse.setContractperiod(response.getLoacontractperiod());
        }
        Long timeTaken = System.currentTimeMillis() - startTime;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken by type of work WiseAggregations is : " + timeTaken + " (millisecs) ");

        startTime = System.currentTimeMillis();
        timeTaken = System.currentTimeMillis() - startTime;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken for setting values in returnUlbWiseAggregationResults() is : " + timeTaken
                    + " (millisecs) ");
    }

    private BoolQueryBuilder prepareWhereClause(final WorksTransactionIndexRequest worksTransactionIndexRequest) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(worksTransactionIndexRequest.getDistname()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery("distname", worksTransactionIndexRequest.getDistname()));
        if (StringUtils.isNotBlank(worksTransactionIndexRequest.getUlbname()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("ulbname", worksTransactionIndexRequest.getUlbname()));
        if (worksTransactionIndexRequest.getLineestimatedetailid() != null)
            boolQuery.filter(
                    QueryBuilders.matchQuery("lineestimatedetailid", worksTransactionIndexRequest.getLineestimatedetailid()));
        return boolQuery;
    }

}