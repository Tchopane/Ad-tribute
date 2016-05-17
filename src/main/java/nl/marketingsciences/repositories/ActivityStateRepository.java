package nl.marketingsciences.repositories;

import java.util.List;

import nl.marketingsciences.beans.database.ActivityState;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityStateRepository extends JpaRepository<ActivityState,Integer>{
	List<ActivityState> findAllByActivityAndReportId(String activity, long report_id);
	
	ActivityState findTop1ByActivityAndReportId(String Activity, long report_id);

}
