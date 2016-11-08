/* eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.mrs.application.reports.repository;

import java.util.List;

import org.egov.mrs.domain.entity.MarriageRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarriageRegistrationReportsRepository extends JpaRepository<MarriageRegistration, Long> {//, QueryDslPredicateExecutor<Registration> {
    
    @Query(value="select ap.ageInYearsAsOnMarriage , count(*) from MarriageRegistration rg, MrApplicant ap where rg.husband=ap.id and rg.status.code='REGISTERED' and YEAR(rg.dateOfMarriage)=:year group by ap.ageInYearsAsOnMarriage order by ap.ageInYearsAsOnMarriage")
    String[] getHusbandCountAgeWise( @Param("year") int year);
    
    @Query(value="select ap.ageInYearsAsOnMarriage, count(*) from MarriageRegistration rg, MrApplicant ap where rg.wife=ap.id and rg.status.code='REGISTERED' and YEAR(rg.dateOfMarriage)=:year group by ap.ageInYearsAsOnMarriage order by ap.ageInYearsAsOnMarriage")
    String[] getWifeCountAgeWise( @Param("year") int year);
    
    @Query(value="select app.maritalStatus,to_char(app.createdDate,'Mon'),count(*) from MrApplicant as app ,MarriageRegistration as reg where YEAR(app.createdDate)=:year and reg.husband = app.id  group by app.maritalStatus, to_char(app.createdDate,'Mon') order by to_char(app.createdDate,'Mon') desc")
    List<String[]> getHusbandCountByMaritalStatus( @Param("year") int year);
    
    @Query(value="select app.maritalStatus,to_char(app.createdDate,'Mon'),count(*) from MrApplicant as app ,MarriageRegistration as reg where YEAR(app.createdDate)=:year and reg.wife = app.id  group by app.maritalStatus, to_char(app.createdDate,'Mon') order by to_char(app.createdDate,'Mon') desc")
    List<String[]> getWifeCountByMaritalStatus( @Param("year") int year);
}
