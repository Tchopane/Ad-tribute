package nl.marketingsciences.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nl.marketingsciences.beans.database.InteractionGroup;

@Repository
public interface InteractionGroupRepository extends JpaRepository<InteractionGroup, Integer> {

	@Query(value = "select group_number from interaction_group where interaction_id = :interaction_id AND report_id = :report_id LIMIT 1", nativeQuery = true)
	int findGroupNumberById(@Param("interaction_id") int interaction_id, @Param("report_id") long report_id);
	
	@Query(value = "select count(*) from interaction_group where interaction_id = :id AND report_id = :report_id", nativeQuery = true)
	Integer findIfAlreadyExists(@Param("id") int id, @Param("report_id") long report_id);
	
	@Query(value = "SELECT group_number FROM adtribute.interaction_group WHERE report_id = :report_id order by group_number DESC limit 1;", nativeQuery = true)
	Integer findMaxGroupNumber(@Param("report_id") long report_id);
	
	InteractionGroup findByInteractionIdAndReportId(int interactionId, long report_id);
	
	@Query(value = "SELECT group_number from interaction_group where groupname = :groupname AND report_id = :report_id LIMIT 1", nativeQuery = true)
	Integer findGroupNumberByName(@Param("groupname") String groupname, @Param("report_id") long report_id);
}
