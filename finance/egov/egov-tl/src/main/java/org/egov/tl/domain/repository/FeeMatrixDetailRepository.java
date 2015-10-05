package org.egov.tl.domain.repository;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.egov.tl.domain.entity.FeeMatrix;
import org.egov.tl.domain.entity.FeeMatrixDetail;
import org.egov.tl.domain.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository 
public interface FeeMatrixDetailRepository extends JpaRepository<FeeMatrixDetail,Long>,FeeMatrixDetailRepositoryCustom {

	List<FeeMatrixDetail> findFeeList(License license);
	



}