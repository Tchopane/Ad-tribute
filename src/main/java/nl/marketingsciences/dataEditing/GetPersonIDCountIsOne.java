package nl.marketingsciences.dataEditing;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import java.util.List;

import nl.marketingsciences.beans.database.Interaction;
import nl.marketingsciences.beans.database.Activity;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.websocket.Globals;
import nl.marketingsciences.repositories.ActivityRepository;

@Service
public class GetPersonIDCountIsOne {
	
	@Inject
	private InteractionRepository interactionRepository;
	@Inject
	private ActivityRepository activityRepository;
	@Inject
	private Globals Globals;
	
	//When path length is 1:
		public int getPersonIDCountIsOne(Interaction interaction1){
			
			List <Interaction> list1 = interactionRepository.findByPlacementAndTimestampAndImpclickAndReportId(interaction1.getPlacement(), interaction1.getTimestamp(),interaction1.getImpclick(), Globals.getCurrentSessionReportId());
			int existingPersonID = list1.get(0).getPersonId();
			List <Interaction> list2 = interactionRepository.findByPersonIdAndReportIdAndTimestampGreaterThanEqual(existingPersonID, Globals.getCurrentSessionReportId(), interaction1.getTimestamp());
			int interactionsSize = list2.size();
			
			if(interactionsSize == 1){
				return existingPersonID;
			} else {
				System.out.println("#interactions >1 from existing person while a newer row in DCM has one");
				return interactionRepository.getMaxPersonId(Globals.getCurrentSessionReportId())+1;
			}
		}
		
		//When path length is larger than 1:
		public int getPersonIDCountIsOne(Interaction interaction1 ,Interaction interaction2){
			
			List <Interaction> list1 = interactionRepository.findByPlacementAndTimestampAndImpclickAndReportId(interaction1.getPlacement(), interaction1.getTimestamp(),interaction1.getImpclick(), Globals.getCurrentSessionReportId());
			int existingPersonID = list1.get(0).getPersonId();
			interaction2.setPersonId(existingPersonID);
			
			List <Interaction> list2 = interactionRepository.findByPlacementAndTimestampAndImpclickAndPersonIdAndReportId(interaction2.getPlacement(), interaction2.getTimestamp(),interaction2.getImpclick(), interaction2.getPersonId(), Globals.getCurrentSessionReportId());
			int checkExistence2 = list2.size();
			
			if(checkExistence2 == 0){
				//Or there is no second interaction yet? --> interactions.size() == 1
				
				List <Interaction> list3 = interactionRepository.findByPersonIdAndReportIdAndTimestampGreaterThanEqual(existingPersonID, Globals.getCurrentSessionReportId(), interaction1.getTimestamp());
				int interactionsSize = list3.size();
				if(interactionsSize == 1){
					//check if all the activities in db are prior to Interaction2
					List <Activity> list4 = activityRepository.findByPersonIdAndReportIdAndTimestampGreaterThanEqual(existingPersonID, Globals.getCurrentSessionReportId(), interaction2.getTimestamp());
					int activitiesSize = list4.size();
					
					if(activitiesSize > 0){
						System.out.println("count = 1, pathlength > 1, 1 existing interaction, activity to new!");
						return interactionRepository.getMaxPersonId(Globals.getCurrentSessionReportId())+1;
					} else{
						return existingPersonID;
					}
					
				} else {
					return interactionRepository.getMaxPersonId(Globals.getCurrentSessionReportId())+1;
				}
				
			} else {
				return existingPersonID;
			}
		}
}
