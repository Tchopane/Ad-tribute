package nl.marketingsciences.repositories;

import java.util.Date;
import java.util.List;

import nl.marketingsciences.beans.database.Activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
	
	Activity findTop1ByConversionIdAndReportId(String conversionId, long report_id);
	
	Activity findFirstByOrderByPersonIdDesc();
	
	List<Activity> findByPersonIdAndReportIdAndTimestampGreaterThanEqual(int existingPersonID, long report_id, Date timestamp);
	
	Activity findFirstByOrderByTimestampAsc();
	
	Activity findFirstByOrderByTimestampDesc();
	
	@Query(value = "select distinct activity from activities WHERE report_id = :report_id order by activity ASC", nativeQuery = true)
	List<String> findDistinctActivities(@Param("report_id") long report_id);
	
	List<Activity> findByPersonIdAndReportIdOrderByTimestampAsc(int personId, long report_id);
	
	@Query(value = "SELECT person_id FROM activities WHERE report_id = :report_id ORDER By person_id DESC LIMIT 1", nativeQuery = true)
	Integer CountPersonId(@Param("report_id") long report_id);
	
	@Query(value = "SELECT * FROM adtribute.activities WHERE report_id = :report_id order by timestamp ASC", nativeQuery = true)
	List<Activity> findAll(@Param("report_id") long report_id);
	
	@Query(value = "SELECT report_id FROM adtribute.activities order by report_id DESC LIMIT 1", nativeQuery = true)
	Long getMaxReportId();
	
}