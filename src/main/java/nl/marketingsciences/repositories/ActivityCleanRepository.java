package nl.marketingsciences.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import nl.marketingsciences.beans.database.ActivityClean;


@Repository
public interface ActivityCleanRepository extends JpaRepository<ActivityClean, Integer> {

	@Query(value = "SELECT * FROM activity_clean WHERE person_id = :personId AND report_id = :report_id ORDER BY timestamp ASC", nativeQuery = true)
	List<ActivityClean> findByPersonId(@Param("personId") Integer personId, @Param("report_id") long report_id);
	
	@Query(value = "SELECT * FROM activity_clean WHERE person_id = :personId AND report_id = :report_id ORDER BY timestamp ASC", nativeQuery = true)
	List<ActivityClean> findByPersonIdTimeOrdered(@Param("personId") Integer personId, @Param("report_id") long report_id);

	@Query(value = "SELECT timestamp FROM activity_clean WHERE person_id = :personId AND id!= :id AND report_id = :report_id ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
	Date findFirstByPersonIdByOrderByTimestampDesc(@Param("personId") Integer personId, @Param("id") int id, @Param("report_id") long report_id);
	
	@Query(value = "SELECT * FROM activity_clean WHERE person_id = :personId AND id!= :id AND report_id = :report_id ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
	ActivityClean findLastFromExistingActivityClean(@Param("personId") Integer personId, @Param("id") int id, @Param("report_id") long report_id);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM activity_clean WHERE person_id = :personId AND timestamp = :timestamp AND report_id = :report_id", nativeQuery = true)
	void deleteByPersonIdAndTimestamp(@Param("personId") Integer personId, @Param("timestamp") Date timestamp, @Param("report_id") long report_id);

	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM activity_clean WHERE person_id = :personId AND report_id = :report_id", nativeQuery = true)
	void deleteByPersonId(@Param("personId") Integer personId, @Param("report_id") long report_id);

}