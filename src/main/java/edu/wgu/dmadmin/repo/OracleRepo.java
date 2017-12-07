package edu.wgu.dmadmin.repo;

import java.sql.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.wgu.dmadmin.repo.oracle.DRF;

@Repository
public interface OracleRepo extends CrudRepository<DRF, Long> {
	List<DRF> findByWguainfSpridenBannerIdAndTitle(String bannerId, String title);
	List<DRF> findByTitleIn(List<String> title);
	List<DRF> findByVendorIdAndTasksActivityDateGreaterThanEqual(Long vendorId, Date activityDate);
}
