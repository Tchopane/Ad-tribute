package nl.marketingsciences.repositories;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nl.marketingsciences.beans.database.RecapInteraction;

@Repository
public interface RecapInteractionsRepository extends JpaRepository<RecapInteraction, Integer> {

	@Query(value = "select distinct begin_state from recap_interactions WHERE report_id = :report_id AND timestamp> :startDate AND timestamp< :endDate", nativeQuery = true)
	List<String> findDistinctBeginState(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("report_id") long report_id);

	@Query(value = "select distinct end_state from recap_interactions WHERE begin_state = :begin_state AND report_id = :report_id AND timestamp> :startDate AND timestamp< :endDate", nativeQuery = true)
	List<String> findDistinctEndStateWhere(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("begin_state") String begin_state, @Param("report_id") long report_id);

	@Query(value = "select * from recap_interactions WHERE begin_state = :begin_state AND end_state = :end_state AND report_id = :report_id AND timestamp> :startDate AND timestamp< :endDate", nativeQuery = true)
	List<RecapInteraction> findByBeginEnd(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("begin_state") String begin_state, @Param("end_state") String end_state,
			@Param("report_id") long report_id);

	@Modifying
	@Transactional
	@Query(value = "delete from recap_interactions where (timestamp< :startDate OR timestamp> :endDate) AND report_id = :report_id", nativeQuery = true)
	void deleteOutdated(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("report_id") long report_id);
	
	@Query(value = "select * from recap_interactions WHERE person_id = :person_id AND report_id = :report_id ORDER BY timestamp ASC", nativeQuery = true)
	List<RecapInteraction> findJourneyByPersonId(@Param("person_id") int person_id, @Param("report_id") long report_id);

	@Query(value = "select person_id from recap_interactions where report_id = :report_id order by person_id DESC LIMIT 1", nativeQuery = true)
	Integer maxPersonId(@Param("report_id") long report_id);
	
	@Query(value = "select distinct begin_state from recap_interactions WHERE report_id = :report_id", nativeQuery = true)
	List<String> findDistinctBeginState(@Param("report_id") long report_id);
	
	@Query(value = "select distinct end_state from recap_interactions WHERE begin_state = :begin_state AND report_id = :report_id", nativeQuery = true)
	List<String> findDistinctEndStateWhere(@Param("begin_state") String begin_state, @Param("report_id") long report_id);
	
	@Query(value = "select id from recap_interactions where report_id = :report_id order by id ASC LIMIT 1", nativeQuery = true)
	Integer minId(@Param("report_id") long report_id);
	
	@Query(value = "select id from recap_interactions where report_id = :report_id order by id DESC LIMIT 1", nativeQuery = true)
	Integer maxId(@Param("report_id") long report_id);
	
	@Query(value = "select id from recap_interactions where report_id = :report_id and id= :id limit 1", nativeQuery = true)
	Integer existId(@Param("id") int id, @Param("report_id") long report_id);
	
	@Query(value = "select * from recap_interactions where report_id = :report_id and id= :id limit 1", nativeQuery = true)
	RecapInteraction findById(@Param("id") int id, @Param("report_id") long report_id);
	
	@Query(value = "select group_id from recap_interactions where report_id = :report_id and id= :id limit 1", nativeQuery = true)
	Integer findGroupIdById(@Param("id") int id, @Param("report_id") long report_id);
	
	@Query(value = "select begin_state from recap_interactions where report_id = :report_id and id= :id limit 1", nativeQuery = true)
	String findBeginStateById(@Param("id") int id, @Param("report_id") long report_id);
	
	@Query(value = "select end_state from recap_interactions where report_id = :report_id and id= :id limit 1", nativeQuery = true)
	String findEndStateById(@Param("id") int id, @Param("report_id") long report_id);
}
