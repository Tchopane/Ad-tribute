package nl.marketingsciences.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

import nl.marketingsciences.beans.database.Interaction;

public interface InteractionRepository extends JpaRepository<Interaction, Integer> {
	
	// getCountInteraction
	int countByPlacementAndImpclickAndTimestampAndReportId(String placement, String impclick, Date timestamp, long reportId);

	// GetUniquePersonId or PossiblePersonID depending on the number of elements
	// in the returned list
	@Query(value="SELECT person_id FROM adtribute.interactions WHERE placement= :p"
			+ "AND imp_click= :ic" 
			+ "AND timestamp= :t AND report_id = :report_id", nativeQuery = true)
	List<Integer> findPersonId(@Param("p") String p, @Param("ic") String ic, @Param("t") Date t, @Param("report_id") long report_id);
	
	@Query(value = "select DISTINCT(placement) FROM interactions where report_id = :report_id order by placement ASC", nativeQuery = true)
	List<String> findDistinctPlacements(@Param("report_id") long report_id);
	
	@Query(value = "select distinct paid_search_campaign from interactions where paid_search_campaign != '' AND placement = 'De Lage Landen - Freo : 1042000' AND report_id = :report_id order by paid_search_campaign ASC", nativeQuery = true)
	List<String> findDistinctBingPaidSearchCampaign(@Param("report_id") long report_id);
	
	@Query(value = "select distinct paid_search_campaign from interactions where paid_search_campaign != '' AND placement = :placement AND report_id = :report_id order by paid_search_campaign ASC", nativeQuery = true)
	List<String> findDistinctPaidSearchCampaigns(@Param("placement") String placement, @Param("report_id") long report_id);
	
	@Query(value = "select distinct paid_search_keyword from interactions where paid_search_keyword != '' AND placement = :placement AND report_id = :report_id order by paid_search_keyword ASC", nativeQuery = true)
	List<String> findDistinctPaidSearchKeywords(@Param("placement") String placement, @Param("report_id") long report_id);

	List<Interaction> findByPlacementAndTimestampAndImpclickAndReportId(String placement,Date timestamp, String impclick, long report_id);

	List<Interaction> findByPersonIdAndReportIdAndTimestampGreaterThanEqual(int personId, long report_id, Date timestamp);
	
	@Query(value = "SELECT * FROM interactions WHERE person_id = :personId AND timestamp > :timestamp1 AND timestamp <= :timestamp2 AND report_id = :report_id", nativeQuery = true)
	List<Interaction> findByPersonIdAndTimestampGreaterThanAndTimestampLessThan(@Param("personId") int personId, @Param("timestamp1") Date timestamp1, @Param("timestamp2") Date timestamp2, @Param("report_id") long report_id);
	
	//@Transactional
	List<Interaction> findByPlacementAndTimestampAndImpclickAndPersonIdAndReportId(String placement, Date timestamp,String impclick ,int personId, long report_id);
	
	@Query(value="SELECT COALESCE(MAX(person_id),0) FROM interactions WHERE report_id = :report_id", nativeQuery = true)
	int getMaxPersonId(@Param("report_id") long report_id); 
	
	@Query(value="SELECT COUNT(*) from interactions WHERE report_id = :report_id", nativeQuery = true)
	int getNumberOfRows(@Param("report_id") long report_id); 
	
	@Query(value="SELECT interaction_id from interactions where report_id = :report_id order by interaction_id ASC LIMIT 1", nativeQuery = true)
	int getFirstInteractionId(@Param("report_id") long report_id);
	
	@Query(value="SELECT * from interactions where interaction_id = :interactionId AND report_id = :report_id", nativeQuery = true)
	Interaction findByPKey(@Param("interactionId") int interactionId, @Param("report_id") long report_id); 
	
	@Query(value="SELECT distinct interaction_id from interactions where paid_search_campaign = :paid_search_campaign AND report_id = :report_id", nativeQuery = true)
	List<Integer> findIdByCampaign(@Param("paid_search_campaign") String paid_search_campaign, @Param("report_id") long report_id); 
	
	@Query(value="SELECT distinct interaction_id from interactions where paid_search_keyword = :paid_search_keyword AND report_id = :report_id", nativeQuery = true)
	List<Integer> findIdByKeyword(@Param("paid_search_keyword") String paid_search_keyword, @Param("report_id") long report_id); 
	
	@Query(value="SELECT distinct paid_search_keyword from interactions where placement = :placement and paid_search_campaign= :paid_search_campaign AND report_id = :report_id", nativeQuery = true)
	List<String> findKeywordByCampaignAndPlacement(@Param("paid_search_campaign") String paid_search_campaign, @Param("placement") String placement, @Param("report_id") long report_id); 
	
	@Query(value="SELECT distinct interaction_id from interactions where placement = :placement and paid_search_campaign= :paid_search_campaign AND report_id = :report_id", nativeQuery = true)
	List<Integer> findIdByCampaignAndPlacement(@Param("paid_search_campaign") String paid_search_campaign, @Param("placement") String placement, @Param("report_id") long report_id); 
	
	@Query(value="SELECT distinct interaction_id from interactions where placement = :placement and paid_search_keyword= :paid_search_keyword AND report_id = :report_id", nativeQuery = true)
	List<Integer> findIdByKeywordAndPlacement(@Param("paid_search_keyword") String paid_search_keyword, @Param("placement") String placement, @Param("report_id") long report_id); 
	
	@Query(value="SELECT distinct interaction_id from interactions where placement = :placement and paid_search_keyword= :paid_search_keyword and paid_search_campaign = :paid_search_campaign AND report_id = :report_id", nativeQuery = true)
	List<Integer> findIdByKeywordAndPlacementAndCampaign(@Param("paid_search_keyword") String paid_search_keyword, @Param("placement") String placement, @Param("paid_search_campaign") String paid_search_campaign, @Param("report_id") long report_id);
	
	@Query(value="SELECT interaction_id from interactions where person_id = :person_id and timestamp = :timestamp and report_id = :report_id limit 1", nativeQuery = true)
	int findRealIdByPersonIdAndTimestamp(@Param("person_id") int person_id, @Param("timestamp") Date timestamp, @Param("report_id") long report_id);
	
	@Query(value="SELECT imp_click from interactions where interaction_id = :interaction_id and report_id = :report_id limit 1", nativeQuery = true)
	String findImpclickById(@Param("interaction_id") int interaction_id, @Param("report_id") long report_id);
}
