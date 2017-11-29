package edu.wgu.dmadmin.repo;

import java.sql.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.wgu.dmadmin.repo.oracle.DRF;

@Repository
public interface OracleRepo extends CrudRepository<DRF, Long> {
	List<DRF> findByAssessmentCode(String assessmentCode);
	List<DRF> findByTitle(String title);
	List<DRF> findByVendorIdAndActivityDateGreaterThanEqual(Long vendorId, Date activityDate);
}
