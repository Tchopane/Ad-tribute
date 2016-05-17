package nl.marketingsciences.repositories;

import java.util.List;

import nl.marketingsciences.beans.database.PlacementGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementGroupRepository extends JpaRepository<PlacementGroup, Integer> {
	List<PlacementGroup> findAllByPlacementAndReportId(String placement, long report_id);
	
	@Query(value = "select distinct groupname from placement_group WHERE report_id = :report_id order by groupname ASC", nativeQuery = true)
	List<String> findDistinctGroups(@Param("report_id") long report_id);
	
	@Query(value = "Select groupname from placement_group where placement = :placement and report_id = :report_id", nativeQuery = true)
	String findGroupByPlacement(@Param("placement") String placement, @Param("report_id") long report_id);
	
	@Query(value = "Select placement_group_id from placement_group where placement = :placement and report_id = :report_id", nativeQuery = true)
	Integer findGroupNumberByPlacement(@Param("placement") String placement, @Param("report_id") long report_id);
}
