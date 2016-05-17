package nl.marketingsciences.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.marketingsciences.beans.database.Activity;
import nl.marketingsciences.beans.database.ActivityClean;
import nl.marketingsciences.beans.database.ActivityState;
import nl.marketingsciences.beans.database.Interaction;
import nl.marketingsciences.dataEditing.SaveActivityClean;
import nl.marketingsciences.repositories.ActivityCleanRepository;
import nl.marketingsciences.repositories.ActivityRepository;
import nl.marketingsciences.repositories.ActivityStateRepository;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.repositories.StateRepository;
import nl.marketingsciences.websocket.Globals;

@RestController
@RequestMapping("/CleanActivities")
public class ActivitiesCleanController {

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private ActivityStateRepository activityStateRepository;

	@Inject
	private StateRepository stateRepository;

	@Inject
	private ActivityCleanRepository activityCleanRepository;

	@Inject
	private InteractionRepository interactionRepository;

	@Inject
	private Globals Globals;

	@RequestMapping(value = "/Fill", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean fill() {

		ActivityClean activityClean = new ActivityClean();
		Integer maxPersonId = activityRepository.CountPersonId(Globals.getCurrentSessionReportId());

		mainLoop: for (int i = 1; i <= maxPersonId; i++) {
			List<Activity> activityList = activityRepository.findByPersonIdAndReportIdOrderByTimestampAsc(i, Globals.getCurrentSessionReportId());
			activityClean.setPersonId(i);
			secondLoop: for (Activity a : activityList) {

				activityClean.setTimestamp(a.getTimestamp());
				ActivityState tempActivityState = activityStateRepository.findTop1ByActivityAndReportId(a.getActivity(), Globals.getCurrentSessionReportId());
				int tempStateId = stateRepository.findStateIdByName(tempActivityState.getState(), Globals.getCurrentSessionReportId());
				activityClean.setStateId(tempStateId);
				activityClean.setReportId(Globals.getCurrentSessionReportId());
				if (i == 816) {
					System.out.println("on y est");
				}
				List<ActivityClean> existingActivityClean = activityCleanRepository.findByPersonId(i, Globals.getCurrentSessionReportId());
				
				//FROM HERE ON WE CHOOSE THE CONSTRAINTS THAT ALLOW OR NOT AN ACTIVITYCLEAN TO BE REGISTERED IN THE DB
				//FIRST CONDITION : A JOURNEY CAN'T GO BACK IN STATE
				if (!existingActivityClean.isEmpty()) {
					if (stateRepository.findFirstPositionById(activityClean.getStateId(), Globals.getCurrentSessionReportId()) <= stateRepository
							.findFirstPositionById(existingActivityClean.get(existingActivityClean.size() - 1).getStateId(), Globals.getCurrentSessionReportId())) {
						continue secondLoop;
					} else {						
						SaveActivityClean.saveActivityClean(activityClean);
					}
				} else {
					SaveActivityClean.saveActivityClean(activityClean);
				}
				//SECOND CONDITION : When there are no placements between the timestamps of two states (based on floodlights) then the first activity is removed.
				Date timestampTemp = activityCleanRepository.findFirstByPersonIdByOrderByTimestampDesc(i, activityClean.getId(), Globals.getCurrentSessionReportId());
				if (timestampTemp != null) { //If it is null, it means it is the first activity in the person's journey
					List<Interaction> testList = interactionRepository.findByPersonIdAndTimestampGreaterThanAndTimestampLessThan(i, timestampTemp, activityClean.getTimestamp(),
							Globals.getCurrentSessionReportId());
					if (testList.size() == 0) { //We Have to delete the previous activityClean 
						ActivityClean toDelete = activityCleanRepository.findLastFromExistingActivityClean(i, activityClean.getId(), Globals.getCurrentSessionReportId());
						activityCleanRepository.delete(toDelete);
					}
				}
				//THIRD CONDITION ABOUT ACTION
				if (stateRepository.findFirstById(activityClean.getStateId(), Globals.getCurrentSessionReportId()).equals("Action")) {
					if (Globals.actionBehavior == 1) {
						ActivityClean activityClean2 = new ActivityClean();
						activityClean2.setStateId(stateRepository.findStateIdByName("Consideration", Globals.getCurrentSessionReportId()));
						Calendar cal = Calendar.getInstance();
						cal.setTime(a.getTimestamp());
						cal.add(Calendar.SECOND, +1);
						activityClean2.setTimestamp(cal.getTime());
						activityClean2.setPersonId(activityClean.getPersonId());
						activityClean2.setReportId(Globals.getCurrentSessionReportId());
						SaveActivityClean.saveActivityClean(activityClean2);
					} else if (Globals.actionBehavior == 2) {
						ActivityClean activityClean2 = new ActivityClean();
						activityClean2.setStateId(stateRepository.findStateIdByName("Awareness", Globals.getCurrentSessionReportId()));
						Calendar cal = Calendar.getInstance();
						cal.setTime(a.getTimestamp());
						cal.add(Calendar.SECOND, +1);
						activityClean2.setTimestamp(cal.getTime());
						activityClean2.setPersonId(activityClean.getPersonId());
						activityClean2.setReportId(Globals.getCurrentSessionReportId());
						SaveActivityClean.saveActivityClean(activityClean2);
					} else {
						continue mainLoop;
					}
				}
				//FOURTH CONDITION ABOUT EXCLUDE
				if (stateRepository.findFirstById(activityClean.getStateId(), Globals.getCurrentSessionReportId()).equals("Exclude")) {
					if (Globals.excludeBehavior) {
						continue mainLoop;
					} else {
						activityCleanRepository.deleteByPersonId(i, Globals.getCurrentSessionReportId());
						continue mainLoop;
					}
				}
			}
		}
		return true;
	}

	@RequestMapping(value = "/SetExcludeAndAction", method = RequestMethod.GET)
	public void setExcludeAndAction(@RequestParam("exclude") boolean exclude, @RequestParam("action") int action) throws Exception {
		Globals.actionBehavior = action;
		Globals.excludeBehavior = exclude;
	}
}
