package nl.marketingsciences.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nl.marketingsciences.beans.database.TMS;

@Repository
public interface TMSRepository extends JpaRepository<TMS, Integer>{
	@Query(value = "SELECT * FROM adtribute.tms where imp_click = :imp_click and group_number = :group_number and state_from = :state_from and state_to = :state_to AND report_id = :report_id limit 1", nativeQuery = true)
	TMS findByImpClickAndGroupNumberAndStateFromAndStateTo(@Param("imp_click") String imp_click, @Param("group_number") int group_number, @Param("state_from") int state_from, @Param("state_to") int state_to, @Param("report_id") long report_id);
}
