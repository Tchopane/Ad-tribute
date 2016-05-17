package nl.marketingsciences.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nl.marketingsciences.beans.database.State;

@Repository
public interface StateRepository extends JpaRepository<State, Integer>{
	
	@Query(value="select id from state where name= :name and report_id = :report_id LIMIT 1", nativeQuery = true)
	Integer findStateIdByName(@Param("name") String name, @Param("report_id") long report_id);
	
	@Query(value="select name from state where id= :id and report_id = :report_id LIMIT 1", nativeQuery = true)
	String findFirstById(@Param("id") int id, @Param("report_id") long report_id);
	
	@Query(value = "Select position from state where id = :id and report_id = :report_id LIMIT 1", nativeQuery = true)
	Integer findFirstPositionById(@Param("id") Integer id, @Param("report_id") long report_id);
	
	@Query(value = "Select id from state where name = :name and report_id = :report_id LIMIT 1", nativeQuery = true)
	Integer findIdByName(@Param("name") String name, @Param("report_id") long report_id);
	
	@Query(value = "SELECT position FROM adtribute.state where report_id = :report_id order by position DESC limit 1;", nativeQuery = true)
	Integer findMaxState(@Param("report_id") long report_id);
	
	@Query(value = "Select position from state where name = :name and report_id = :report_id LIMIT 1", nativeQuery = true)
	Integer findPositionByName(@Param("name") String name, @Param("report_id") long report_id);
}
