package nl.marketingsciences.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Strings;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.Advertiser;
import com.google.api.services.dfareporting.model.AdvertisersListResponse;
import com.google.api.services.dfareporting.model.FloodlightActivitiesListResponse;
import com.google.api.services.dfareporting.model.FloodlightActivity;

import nl.marketingsciences.beans.database.AdvertiserFloodlight;
import nl.marketingsciences.beans.database.DCMApiSelection;
import nl.marketingsciences.beans.database.GroupProperties;
import nl.marketingsciences.beans.database.GroupingChoice;
import nl.marketingsciences.dcmapi.CreateDcmReport;
import nl.marketingsciences.dcmapi.DfaReportingFactory;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.websocket.Globals;

@RestController
@RequestMapping("/DcmApi")
public class DcmApiController {

	private static List<AdvertiserFloodlight> adFlList = new ArrayList<AdvertiserFloodlight>();

	@Inject
	private CreateDcmReport createDcmReport;

	@Inject
	private DCMApiSelection dcmApiSelection;

	@Inject
	private InteractionRepository interactionRepository;

	@Inject
	private Globals Globals;

	@RequestMapping(value = "/GetReport", method = RequestMethod.GET)
	@ResponseBody
	public void getReport(long fileID, long reportID) throws Exception {
		Dfareporting reporting = DfaReportingFactory.getInstance();
		// long fileId = 230963329; // 232515420
		// long reportId = 40775607; // 34264274
		createDcmReport.downloadReport(reporting, 1610416, reportID, fileID);
	}

	@RequestMapping(value = "/CleanReport", method = RequestMethod.GET)
	public void getReport() throws Exception {
		Dfareporting reporting =  DfaReportingFactory.getInstance();
		long profileId = Long.parseLong(Globals.getUSER_PROFILE_ID());
		reporting.reports().delete(profileId, Globals.getREPORTID()).execute();
	}
	
	
	@RequestMapping(value = "/SetFloodLights", method = RequestMethod.POST)
	public boolean setFloodLights(@RequestBody List<Long> floodlightIds) throws Exception {
		dcmApiSelection.setSelectedFloodlights(floodlightIds);
		return true;
	}

	@RequestMapping(value = "/ListAllAdvertisers", method = RequestMethod.GET)
	public HashMap<Long, String> listAllAdvertisers() throws Exception {

		Dfareporting reporting = DfaReportingFactory.getInstance();
		long profileId = Long.parseLong(Globals.getUSER_PROFILE_ID());

		AdvertisersListResponse response;
		String nextPageToken = null;
		HashMap<Long, String> result = new HashMap<Long, String>();
		do {
			// Create and execute the advertiser list request.
			response = reporting.advertisers().list(profileId).setPageToken(nextPageToken).execute();

			for (Advertiser advertiser : response.getAdvertisers()) {
				result.put(advertiser.getId(), advertiser.getName() + "      " + advertiser.getFloodlightConfigurationId());
				adFlList.add(new AdvertiserFloodlight(advertiser.getId(), advertiser.getName(), advertiser.getFloodlightConfigurationId()));
			}

			// Update the next page token.
			nextPageToken = response.getNextPageToken();
		} while (!response.getAdvertisers().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));
		return result;
	}

	@RequestMapping(value = "/ListAllPlacements", method = RequestMethod.GET)
	public List<String> listAllPlacements() throws Exception {
		List<String> result1 = interactionRepository.findDistinctPlacements(Globals.getCurrentSessionReportId());
		return result1;
	}

	@RequestMapping(value = "/SetGroupingChoices", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean setGroupingChoices(@RequestBody Integer[] toSplit) throws Exception {


		int length = toSplit.length / 2;
		Integer[] part1 = new Integer[length];
		Integer[] part2 = new Integer[length];
		part1 = Arrays.copyOfRange(toSplit, 0, length);
		part2 = Arrays.copyOfRange(toSplit, length, length * 2);
		List<String> allPlacements = listAllPlacements();
		List<String> chosenPlacements = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			GroupingChoice temp = new GroupingChoice();
			temp.setKey(part2[i]);
			temp.setPlacement(allPlacements.get(part1[i]));
			chosenPlacements.add(allPlacements.get(part1[i]));
			List<GroupingChoice> list = Globals.getGroupingChoices();
			list.add(temp);
			Globals.setGroupingChoices(list);
		}
		//We keep in the globals the differents chosen placements
		Globals.setChosenPlacements(chosenPlacements);
		return true;
	}

	
	//This method gives back the choice made for each placement.
	@RequestMapping(value = "/GetGroupingParameters", method = RequestMethod.GET)
	@ResponseBody
	public Integer getGroupingChoices() throws Exception {
		
		//Then we can retrieve the group parameters
		if (!Globals.getGroupingChoices().isEmpty()) {
			//Since we are starting to elaborate the groups from now on, let's clean the globals grouping parameters first
			List<GroupProperties> listnull = new ArrayList<GroupProperties>();
			Globals.setAllGroupsProperties(listnull);
			int intToReturn = Globals.getGroupingChoices().get(0).getKey();
			return intToReturn;

		} else {
			return 0;
		}
	}

	//This method gives back the choice made for each placement.
	@RequestMapping(value = "/GetGroupingParametersAndUpdateGlobals", method = RequestMethod.GET)
	public @ResponseBody Integer getGroupingChoicesAndUpdate() throws Exception {
		if (Globals.getGroupingChoices().size() > 1) {
			Integer intToReturn = Globals.getGroupingChoices().get(1).getKey();
			List<GroupingChoice> list = Globals.getGroupingChoices();
			list.remove(0);
			Globals.setGroupingChoices(list);
			return intToReturn;
		} else {
			return 0;
		}
	}

	//This method gives back each campaign
	@RequestMapping(value = "/GetGroupingPlacements", method = RequestMethod.GET)
	public @ResponseBody List<String> getGroupingPlacements() throws Exception {
		if (!Globals.getGroupingChoices().isEmpty()) {
			List<String> stringList = interactionRepository.findDistinctPaidSearchCampaigns(Globals.getGroupingChoices().get(0).getPlacement(), Globals.getCurrentSessionReportId());
			//			Globals.groupingChoices.remove(0);

			return stringList;
		} else {
			return new ArrayList<String>();
		}
	}

	@RequestMapping(value = "/GetGroupingKeywords", method = RequestMethod.GET)
	public @ResponseBody List<String> getGroupingKeywords() throws Exception {
		if (!Globals.getGroupingChoices().isEmpty()) {
			List<String> stringList = interactionRepository.findDistinctPaidSearchKeywords(Globals.getGroupingChoices().get(0).getPlacement(), Globals.getCurrentSessionReportId());

			//System.out.println(Globals.groupingChoices.get(0).getPlacement());
			return stringList;
		} else {
			return new ArrayList<String>();
		}
	}

	@RequestMapping(value = "/GetPageTitle", method = RequestMethod.GET)
	public @ResponseBody String getPageTitle() throws Exception {
		if (!Globals.getGroupingChoices().isEmpty()) {
			return Globals.getGroupingChoices().get(0).getPlacement();
		} else {
			return "noMorePlacement";
		}
	}

	@RequestMapping(value = "/SetAdvertiserAndStartEndDate", method = RequestMethod.POST)
	public void setStartEndDate(@RequestParam("clientId") Long advertiserId, @RequestParam("startDate") String startDateString, @RequestParam("endDate") String endDateString) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date startDate = sdf.parse(startDateString);
		Date endDate = sdf.parse(endDateString);
		dcmApiSelection.setStartDate(startDate);
		dcmApiSelection.setEndDate(endDate);
		dcmApiSelection.setAdvertiserId(advertiserId);
	}

	@RequestMapping(value = "/GetAllDcmFloodlights", method = RequestMethod.POST)
	public @ResponseBody HashMap<Long, String> getAllDcmFloodlights() throws Exception {
		HashMap<Long, String> result = new HashMap<Long, String>();
		Dfareporting reporting = DfaReportingFactory.getInstance();
		FloodlightActivitiesListResponse acts;
		long floodlightConfigurationId = 0;
		for (AdvertiserFloodlight adFl : adFlList) {
			if (adFl.getAdvertiserId() == dcmApiSelection.getAdvertiserId()) {
				floodlightConfigurationId = adFl.getFloodlightConfigurationId();
				dcmApiSelection.setFloodlightConfigurationId(floodlightConfigurationId);
			}
		}
		acts = reporting.floodlightActivities().list(new Long(Globals.USER_PROFILE_ID)).setFloodlightConfigurationId(floodlightConfigurationId).execute();
		List<FloodlightActivity> actList = acts.getFloodlightActivities();
		for (FloodlightActivity act : actList) {
			result.put(act.getId(), act.getName());
		}
		return result;
	}

	@RequestMapping(value = "/RunDcmFile", method = RequestMethod.POST)
	public boolean runDcmFile(HttpServletResponse response) throws Exception {
		Dfareporting reporting = DfaReportingFactory.getInstance();
		long profileId = Long.parseLong(Globals.getUSER_PROFILE_ID());
		createDcmReport.createReport(reporting, profileId, dcmApiSelection.getFloodlightConfigurationId().toString(), dcmApiSelection.getStartDate(), dcmApiSelection.getEndDate(),
				dcmApiSelection.getSelectedFloodlights());
		return true;
	}

	@RequestMapping(value = "/CleanGlobals", method = RequestMethod.GET)
	public void cleanGlobals() throws Exception {
		//Since we are starting to elaborate the groups from now on, let's clean the globals grouping parameters first
		List<GroupProperties> listnull = new ArrayList<GroupProperties>();
		Globals.setAllGroupsProperties(listnull);
	}
}