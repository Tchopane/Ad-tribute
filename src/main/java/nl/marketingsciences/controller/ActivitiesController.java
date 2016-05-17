package nl.marketingsciences.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nl.marketingsciences.beans.database.ActivityState;
import nl.marketingsciences.beans.database.State;
//import nl.marketingsciences.beans.AdwordsCampaignGroup;
import nl.marketingsciences.repositories.ActivityRepository;
import nl.marketingsciences.repositories.ActivityStateRepository;
import nl.marketingsciences.repositories.StateRepository;
import nl.marketingsciences.websocket.Globals;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Activities")
public class ActivitiesController {
	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private ActivityStateRepository activityStateRepository;
	
	@Inject
	private StateRepository stateRepository;

	@Inject
	private Globals Globals;

	@RequestMapping(value = "/UniqueActivities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> uniqueActivities() {

		List<String> uniqueActivities = activityRepository.findDistinctActivities(Globals.getCurrentSessionReportId());

		return uniqueActivities;
	}

	@RequestMapping(value = "/States", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<ActivityState> States() {
		List<String> uniqueActivities = activityRepository.findDistinctActivities(Globals.getCurrentSessionReportId());

		List<ActivityState> states = new ArrayList<ActivityState>();

		for (int i = 0; i < uniqueActivities.size(); i++) {
			List<ActivityState> result = activityStateRepository.findAllByActivityAndReportId(uniqueActivities.get(i), Globals.getCurrentSessionReportId());
			if (result.size() > 0) {
				states.add(i, result.get(0));
			} else {
				states.add(i, new ActivityState());
			}
		}
		return states;
	}

	@RequestMapping(value = "/UniqueStates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> uniqueStates() {

		List<String> uniqueStates = new ArrayList<String>();
		uniqueStates.add("Awareness");
		uniqueStates.add("Consideration");
		uniqueStates.add("Action");
		uniqueStates.add("Exclude");
		return uniqueStates;
	}

	@RequestMapping(value = "/PostActivityState", method = RequestMethod.POST)
	public boolean PostActivityState(@RequestBody List<ActivityState> states) {
		for (ActivityState aS : states) {
			aS.setReportId(Globals.getCurrentSessionReportId());
		}
		activityStateRepository.save(states);
		return true;
	}

	@RequestMapping(value = "/PostState", method = RequestMethod.POST)
	public boolean PostState(@RequestBody List<String> states) {
		State dormant = new State();
		dormant.setName("Dormant");
		dormant.setPosition(0);
		dormant.setReportId(Globals.getCurrentSessionReportId());
		Integer id0 = stateRepository.findIdByName("Dormant", Globals.getCurrentSessionReportId());
		if (id0 != null) {
			dormant.setId(id0);
		}
		stateRepository.save(dormant);
		int i = 1;
		for (String state : states) {
			State newState = new State();
			newState.setName(state);
			newState.setPosition(i);
			newState.setReportId(Globals.getCurrentSessionReportId());
			Integer id = stateRepository.findIdByName(state, Globals.getCurrentSessionReportId());
			if (id != null) {
				newState.setId(id);
			}
			stateRepository.save(newState);
			i++;
		}
		return true;
	}
}
