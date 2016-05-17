package nl.marketingsciences.dataEditing;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import nl.marketingsciences.beans.database.Interaction;
import nl.marketingsciences.repositories.ActivityRepository;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.websocket.Globals;

@Service
public class GetPersonIDCountLargerOne {
	
	@Inject
	InteractionRepository interactionRepository;
	@Inject
	ActivityRepository activityRepository;
	@Inject
	private Globals Globals;
	
	//When path length is 1:
		public int getPersonIDCountLargerOne(Interaction interaction1, List<Integer> possiblePersonIDs){
			for(Integer personIDx: possiblePersonIDs){
				if(interactionRepository.findByPersonIdAndReportIdAndTimestampGreaterThanEqual(personIDx, Globals.getCurrentSessionReportId(), interaction1.getTimestamp()).size() == 1){
					return personIDx;
				}
			}
			
			return interactionRepository.getMaxPersonId(Globals.getCurrentSessionReportId())+1;
		}
		
		//When path length is larger than 1:
		public int getPersonIDCountLargerOne(Interaction interaction1, Interaction interaction2, List<Integer> possiblePersonIDs){
			
			for(Integer personIDx: possiblePersonIDs){
				Interaction interactionTemp = new Interaction();
				interactionTemp = interaction2;
				interactionTemp.setPersonId(personIDx);
				if(interactionRepository.findByPlacementAndTimestampAndImpclickAndPersonIdAndReportId(interactionTemp.getPlacement(), interactionTemp.getTimestamp(),interactionTemp.getImpclick(), interactionTemp.getPersonId(), Globals.getCurrentSessionReportId()).size()>0){
					return personIDx;
				}
			}
			
			for(Integer personIDx: possiblePersonIDs){
				if(interactionRepository.findByPersonIdAndReportIdAndTimestampGreaterThanEqual(personIDx, Globals.getCurrentSessionReportId(), interaction1.getTimestamp()).size() == 1){
					int activitiesSize = activityRepository.findByPersonIdAndReportIdAndTimestampGreaterThanEqual(personIDx, Globals.getCurrentSessionReportId(), interaction2.getTimestamp()).size();
					if(activitiesSize == 0){
						return personIDx;
					}
				}
			}
			
			return interactionRepository.getMaxPersonId(Globals.getCurrentSessionReportId())+1;
		}
		}
