package nl.marketingsciences.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.marketingsciences.beans.database.ActivityClean;
import nl.marketingsciences.beans.database.GroupProperties;
import nl.marketingsciences.beans.database.Interaction;
import nl.marketingsciences.beans.database.InteractionGroup;
import nl.marketingsciences.beans.database.PlacementGroup;
import nl.marketingsciences.beans.database.RecapInteraction;
import nl.marketingsciences.dataEditing.SaveInteractionGroup;
import nl.marketingsciences.dataEditing.SaveRecapInteractions;
import nl.marketingsciences.repositories.ActivityCleanRepository;
import nl.marketingsciences.repositories.InteractionGroupRepository;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.repositories.PlacementGroupRepository;
import nl.marketingsciences.repositories.StateRepository;
import nl.marketingsciences.websocket.Globals;

@RestController
@RequestMapping("/PlacementGroup")
public class PlacementGroupController {
	@Inject
	private InteractionRepository interactionRepository;

	@Inject
	private PlacementGroupRepository placementGroupRepository;

	@Inject
	private ActivityCleanRepository activityCleanRepository;

	@Inject
	private StateRepository stateRepository;

	@Inject
	private InteractionGroupRepository interactionGroupRepository;

	@Inject
	private Globals Globals;

	@RequestMapping(value = "/UniquePlacements", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> uniquePlacements() {

		List<String> uniquePlacements = interactionRepository.findDistinctPlacements(Globals.getCurrentSessionReportId());
		for (String placement : Globals.getChosenPlacements()) {
			uniquePlacements.remove(placement);
		}
		return uniquePlacements;
	}

	/**
	 * @return
	 */
	@RequestMapping(value = "/Groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	// public List<String> Groups() {
	public List<PlacementGroup> Groups() {
		List<String> uniquePlacements = interactionRepository.findDistinctPlacements(Globals.getCurrentSessionReportId());
		for (String placement : Globals.getChosenPlacements()) {
			uniquePlacements.remove(placement);
		}

		List<PlacementGroup> groups = new ArrayList<PlacementGroup>();

		for (int i = 0; i < uniquePlacements.size(); i++) {
			List<PlacementGroup> result = placementGroupRepository.findAllByPlacementAndReportId(uniquePlacements.get(i), Globals.getCurrentSessionReportId());
			if (result.size() > 0) {
				groups.add(i, result.get(0));
			} else {
				groups.add(i, new PlacementGroup());
			}
		}
		return groups;
	}

	@RequestMapping(value = "/UniqueGroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> uniqueGroups() {
		List<String> uniqueGroups = placementGroupRepository.findDistinctGroups(Globals.getCurrentSessionReportId());

		return uniqueGroups;
	}

	@RequestMapping(value = "/PostPlacementGroup", method = RequestMethod.POST)
	public boolean PostPlacementGroup(@RequestBody List<PlacementGroup> groups) {
		for (PlacementGroup pG : groups) {
			pG.setReportId(Globals.getCurrentSessionReportId());
			placementGroupRepository.save(pG);
		}
		//it's time to set the group for every other interaction
		Integer firstInteractionId = interactionRepository.getFirstInteractionId(Globals.getCurrentSessionReportId());
		Integer numberOfInteractions = interactionRepository.getNumberOfRows(Globals.getCurrentSessionReportId());
		for (int i = firstInteractionId; i < numberOfInteractions + firstInteractionId - 1; i++) {
			int alreadyThere = interactionGroupRepository.findIfAlreadyExists(i, Globals.getCurrentSessionReportId());
			if (alreadyThere > 0) { // this means the interaction has already been set
				continue;
			}
			Interaction tempInteraction = interactionRepository.findByPKey(i, Globals.getCurrentSessionReportId());

			InteractionGroup temp = new InteractionGroup();
			temp.setGroupName(placementGroupRepository.findGroupByPlacement(tempInteraction.getPlacement(), Globals.getCurrentSessionReportId()));

			Integer exist = interactionGroupRepository.findGroupNumberByName(temp.getGroupName(), Globals.getCurrentSessionReportId());
			//We need to give a group number to that group name to we check if the group already exists or not : if it
			//does, then the group number is found, else we just give a new group number to that new group
			if (exist != null) {
				temp.setGroupNumber(exist);
			} else {
				if (interactionGroupRepository.findMaxGroupNumber(Globals.getCurrentSessionReportId()) != null) {
					temp.setGroupNumber(interactionGroupRepository.findMaxGroupNumber(Globals.getCurrentSessionReportId()) + 1);
				} else {
					temp.setGroupNumber(1);
				}
			}
			temp.setInteractionId(i);
			temp.setReportId(Globals.getCurrentSessionReportId());
			SaveInteractionGroup.saveInteractionGroup(temp);
		}
		return true;
	}

	@RequestMapping(value = "/PreparePlacements", method = RequestMethod.POST)
	public boolean preparePlacements() {

		Integer firstInteractionId = interactionRepository.getFirstInteractionId(Globals.getCurrentSessionReportId());
		Integer numberOfInteractions = interactionRepository.getNumberOfRows(Globals.getCurrentSessionReportId());

		for (int i = firstInteractionId; i < numberOfInteractions + firstInteractionId - 1; i++) { //i is kind of the interaction_id
			RecapInteraction recapInteraction = new RecapInteraction();
			Interaction tempInteraction = interactionRepository.findByPKey(i, Globals.getCurrentSessionReportId());
			recapInteraction.setPersonId(tempInteraction.getPersonId());
			recapInteraction.setReportId(Globals.getCurrentSessionReportId());
			recapInteraction.setGroup(interactionGroupRepository.findGroupNumberById(i, Globals.getCurrentSessionReportId()));
			recapInteraction.setTimestamp(tempInteraction.getTimestamp());
			List<ActivityClean> activityCleanList = activityCleanRepository.findByPersonIdTimeOrdered(tempInteraction.getPersonId(), Globals.getCurrentSessionReportId());
			if (!activityCleanList.isEmpty()) {
				for (int j = 0; j < activityCleanList.size(); j++) {
					ActivityClean activityClean = activityCleanList.get(j);
					if (tempInteraction.getTimestamp().after(activityClean.getTimestamp())) {
						if (j == activityCleanList.size() - 1) {
							recapInteraction.setBeginState(stateRepository.findFirstById(activityClean.getStateId(), Globals.getCurrentSessionReportId()));
						} else {
							continue;
						}
					} else {
						if (j >= 1) {
							recapInteraction.setBeginState(stateRepository.findFirstById(activityCleanList.get(j - 1).getStateId(), Globals.getCurrentSessionReportId()));
						}
						recapInteraction.setEndState(stateRepository.findFirstById(activityClean.getStateId(), Globals.getCurrentSessionReportId()));
						long seconds = (activityClean.getTimestamp().getTime() - recapInteraction.getTimestamp().getTime()) / 1000;
						int seconds1 = (int) seconds;
						recapInteraction.setTimeToNextState(seconds1);
						break;

					}
				}
				if (recapInteraction.getBeginState() == null) {
					recapInteraction.setBeginState("Dormant");
				}
				if (recapInteraction.getEndState() == null) {
					recapInteraction.setEndState(recapInteraction.getBeginState());
					recapInteraction.setTimeToNextState(-1);
				}
				// Placements at the beginning and at the end of the journey still
				// don't have any endState/beginState, so we give them one now
				if (recapInteraction.getBeginState().equals("Exclude") || (Globals.getActionBehavior() == 3 && recapInteraction.getBeginState().equals("Action"))) {
					//Whatever the choice concerning Exclude is, it means we don't wan't to keep this placement in the journey
					//And in the second condition, it means we want to remove every placement after the action occurs so we don't save this interaction
					continue;
				}
				SaveRecapInteractions.saveRecapInteractions(recapInteraction);
			} else { // the activityCleanList is empty so it means the choice was made to exclude the whole journey due to Exclude behavior choice
				continue;
			}
		}
		return true;
	}

	@RequestMapping(value = "/CheckIfPossibleCampaign", method = RequestMethod.GET)
	public @ResponseBody Integer checkPossibleCampaign(@RequestParam("campaign") String campaign) throws Exception {
		String placement = Globals.getGroupingChoices().get(0).getPlacement();
		int amount = interactionRepository.findKeywordByCampaignAndPlacement(campaign, placement, Globals.getCurrentSessionReportId()).size();
		int nbAttr = 0;
		for (GroupProperties gP : Globals.getAllGroupsProperties()) {
			if (gP.getCampaignsList().contains(campaign)) {
				nbAttr++;
			}
		}
		if (nbAttr >= amount) {
			return 0;
		} else {
			return 1;
		}
	}

	@RequestMapping(value = "/UpdateGroupName", method = RequestMethod.GET)
	public @ResponseBody void checkPossibleCampaign(@RequestParam("oldname") String oldname, @RequestParam("newname") String newname) throws Exception {
		if (Globals.getAllGroupsProperties().size() > 0) {
			for (GroupProperties gP : Globals.getAllGroupsProperties()) {
				if (gP.getGroupId().equals(oldname)) {
					gP.setGroupId(newname);
					break;
				}
			}
		}
	}

	@RequestMapping(value = "/SetGroups", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer setGroups(@RequestBody String[] dataToSend) throws Exception {
		if (dataToSend.length == 0) {
			return 0;
		} else {
			boolean found = false;
			for (GroupProperties gC : Globals.getAllGroupsProperties()) {
				if (gC.getGroupId().equals(dataToSend[0])) {
					found = true; //The group corresponding to the element we want to add is already in the allgroupproperties list
					if (!dataToSend[1].equals("DONOTCONSIDER")) { //the element we are going to add is not a keyword, but a campaign
						if (!gC.getCampaignsList().contains(dataToSend[1])) { //This group properties hasn't got this campaign yet
							List<String> tempo = gC.getCampaignsList();
							tempo.add(dataToSend[1]);
							gC.setCampaignsList(tempo);
						} else {
							return 2;
						}
					} else {
						if (gC.getKeywordList() != null && !gC.getKeywordList().isEmpty()) {
							List<String> temp = new ArrayList<String>(Arrays.asList(dataToSend));
							temp.remove(1);
							temp.remove(0); //temp est desormais la liste des keywords
							for (String kw : temp) {

								if (!gC.getKeywordList().contains(kw)) { //This group properties hasn't got this keyword yet
									List<String> tempo = gC.getKeywordList();
									tempo.add(kw);
									gC.setKeywordList(tempo);
								}
							}
						} else {
							List<String> temp = new ArrayList<String>(Arrays.asList(dataToSend));
							temp.remove(1);
							temp.remove(0); //temp est desormais la liste des keywords
							gC.setKeywordList(temp);

						}
					}
				}
			}

			if (!found) { // We create another group property
				GroupProperties tempGroup = new GroupProperties();
				tempGroup.setGroupId(dataToSend[0]);
				if (!dataToSend[1].equals("DONOTCONSIDER")) {
					List<String> campaignsList = new ArrayList<String>();
					campaignsList.add(dataToSend[1]);
					tempGroup.setCampaignsList(campaignsList);
				} else {
					List<String> kwList = new ArrayList<String>();
					kwList.add(dataToSend[2]);
					tempGroup.setCampaignsList(kwList);
				}
				List<GroupProperties> list = Globals.getAllGroupsProperties();
				list.add(tempGroup);
				Globals.setAllGroupsProperties(list);
			}
			return 1;
		}

	}

	@RequestMapping(value = "/SetGroups2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody boolean setGroups2(@RequestBody String[] dataToSend) throws Exception {
		String oldGroup = dataToSend[0];
		String newGroup = dataToSend[1];
		boolean found = false;
		for (GroupProperties gP : Globals.getAllGroupsProperties()) {
			if (gP.getGroupId().equals(oldGroup)) { //We update the old group by removing the campaign from him
				List<String> temp = gP.getCampaignsList();
				temp.remove(dataToSend[2]);
				gP.setCampaignsList(temp);
			}
			if (gP.getGroupId().equals(newGroup)) { //We update the new group by adding to him the new campaign
				found = true;
				List<String> temp = gP.getCampaignsList();
				temp.add(dataToSend[2]);
				gP.setCampaignsList(temp);
			}
		}
		if (!found) {
			GroupProperties tempGroup = new GroupProperties();
			tempGroup.setGroupId(newGroup);
			List<String> campaignsList = new ArrayList<String>();
			campaignsList.add(dataToSend[2]);
			tempGroup.setCampaignsList(campaignsList);
			List<GroupProperties> list = Globals.getAllGroupsProperties();
			list.add(tempGroup);
			Globals.setAllGroupsProperties(list);
		}
		return true;
	}
	
	@RequestMapping(value = "/SetGroups3", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody boolean setGroups3(@RequestBody String[] dataToSend) throws Exception {
		String oldGroup = dataToSend[0];
		String newGroup = dataToSend[1];
		boolean found = false;
		for (GroupProperties gP : Globals.getAllGroupsProperties()) {
			if (gP.getGroupId().equals(oldGroup)) { //We update the old group by removing the keyword from him
				List<String> temp = gP.getKeywordList();
				temp.remove(dataToSend[3]);
				gP.setKeywordList(temp);
			}
			if (gP.getGroupId().equals(newGroup)) { //We update the new group by adding to him the new keyword
				found = true;
				List<String> temp = gP.getKeywordList();
				temp.add(dataToSend[3]);
				gP.setKeywordList(temp);
			}
		}
		if (!found) {
			GroupProperties tempGroup = new GroupProperties();
			tempGroup.setGroupId(newGroup);
			List<String> keywordList = new ArrayList<String>();
			keywordList.add(dataToSend[3]);
			tempGroup.setCampaignsList(keywordList);
			List<GroupProperties> list = Globals.getAllGroupsProperties();
			list.add(tempGroup);
			Globals.setAllGroupsProperties(list);
		}
		return true;
	}

	@RequestMapping(value = "/RemoveGroup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> removeGroup(@RequestBody String[] groupToRemove) {
		GroupProperties toRemove = new GroupProperties();
		List<String> campaigns = new ArrayList<String>();
		for (GroupProperties gP : Globals.getAllGroupsProperties()) {
			if (gP.getGroupId().equals(groupToRemove[0])) {
				toRemove = gP;
				campaigns.addAll(gP.getCampaignsList());
				break;
			}
		}
		List<GroupProperties> cleanList = Globals.getAllGroupsProperties();
		cleanList.remove(toRemove);
		Globals.setAllGroupsProperties(cleanList);
		return campaigns;
	}

	@RequestMapping(value = "/SavePlacementGroups", method = RequestMethod.GET)
	public boolean savePlacementGroups() throws Exception {
		int index;
		try{
			int max = getMaxGroup();
			index = 1 + max;
		} catch (java.lang.NullPointerException ex) {
			index = 1;
		}
		
	
		String placement = Globals.getGroupingChoices().get(0).getPlacement();
		for (GroupProperties gP : Globals.getAllGroupsProperties()) {
			List<Integer> interactionList = new ArrayList<Integer>();
			if ((gP.getKeywordList() != null) && !gP.getKeywordList().isEmpty()) {
				for (int i = 0; i < gP.getKeywordList().size(); i++) {
					if (gP.getKeywordList().get(i).contains("/_")) {
						String campaign = gP.getKeywordList().get(i).split("/_")[1];
						String keyword = gP.getKeywordList().get(i).split("/_")[0];
						List<Integer> idInteractionList = interactionRepository.findIdByKeywordAndPlacementAndCampaign(keyword, placement, campaign, Globals.getCurrentSessionReportId());
						interactionList.addAll(idInteractionList);
					} else {
						List<Integer> idInteractionList = interactionRepository.findIdByKeywordAndPlacement(gP.getKeywordList().get(i), placement, Globals.getCurrentSessionReportId());
						interactionList.addAll(idInteractionList);
					}
				}
			}
			if (gP.getCampaignsList() != null && !gP.getCampaignsList().isEmpty()) {
				for (int i = 0; i < gP.getCampaignsList().size(); i++) {
					List<Integer> idInteractionList = interactionRepository.findIdByCampaignAndPlacement(gP.getCampaignsList().get(i), placement, Globals.getCurrentSessionReportId());
					interactionList.addAll(idInteractionList);
				}
			}
			Set<Integer> hs = new HashSet<>(); //We do this in order to remove duplicates
			hs.addAll(interactionList);
			interactionList.clear();
			interactionList.addAll(hs);
			// Let's now save all the interactions with their group name and id in the database using Hibernate
			InteractionGroup interactionGroup = new InteractionGroup();
			interactionGroup.setGroupName(gP.getGroupId());
			interactionGroup.setReportId(Globals.getCurrentSessionReportId());
			for (int i = 0; i < interactionList.size(); i++) {
				interactionGroup.setInteractionId(interactionList.get(i));
				interactionGroup.setGroupNumber(index);
				SaveInteractionGroup.saveInteractionGroup(interactionGroup);
			}
			index++;
		}
		List<GroupProperties> empty = new ArrayList<GroupProperties>();
		Globals.setAllGroupsProperties(empty);
		return true;
	}

	@RequestMapping(value = "/GetMultipleGroupsAndKeywords", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> getMultipleGroups() {
		List<String> listToDelete = new ArrayList<String>();
		List<String> multipleCampaign = new ArrayList<String>();
		String placement = Globals.getGroupingChoices().get(0).getPlacement();
		for (GroupProperties gP : Globals.getAllGroupsProperties()) {
			for (String campaign : gP.getCampaignsList()) {

				for (GroupProperties groupP : Globals.getAllGroupsProperties()) {
					if (gP.getGroupId().equals(groupP.getGroupId())) {
						continue;
					}
					if (groupP.getCampaignsList().contains(campaign)) { //campaign was assigned to groupP and gP
						//In order to not have corrupted Globals, we need to delete the campaign from groupP and gP. 
						//The corresponding interactions will be found using keywords anyway.
						//For the moment we will just fill a list containing all the campaigns we want to delete
						listToDelete.add(campaign);
						List<String> tempKWList = interactionRepository.findKeywordByCampaignAndPlacement(campaign, placement, Globals.getCurrentSessionReportId());

						for (String keyword : tempKWList) {
							String toAdd = campaign.concat("/");
							toAdd = toAdd.concat(gP.getGroupId());
							toAdd = toAdd.concat("/");
							toAdd = toAdd.concat(keyword);
							multipleCampaign.add(toAdd);
							String toAdd2 = campaign.concat("/");
							toAdd2 = toAdd2.concat(groupP.getGroupId());
							toAdd2 = toAdd2.concat("/");
							toAdd2 = toAdd2.concat(keyword);
							multipleCampaign.add(toAdd2);
							//Basically the elements of multipleCampaign will look like this : campaign2_group7_kw1
							//with the names being the ones displayed in the database
						}
					}
				}
			}
		}
		Set<String> hs = new HashSet<>();
		hs.addAll(multipleCampaign);
		multipleCampaign.clear();
		multipleCampaign.addAll(hs);
		java.util.Collections.sort(multipleCampaign);
		//Before ending, we remove the concerned campaigns from the globals 
		for (GroupProperties gP : Globals.getAllGroupsProperties()) {
			List<String> list2 = gP.getCampaignsList();
			for (String campaign : listToDelete) {
				if (list2.contains(campaign)) {
					list2.remove(list2.indexOf(campaign));
				}
				gP.setCampaignsList(list2);
			}
		}
		return multipleCampaign;
	}

	@RequestMapping(value = "/GetMaxGroup", method = RequestMethod.GET)
	public @ResponseBody Integer getMaxGroup() {
		try {
			Integer intToReturn = interactionGroupRepository.findMaxGroupNumber(Globals.getCurrentSessionReportId());
			return intToReturn;
		} catch (org.springframework.aop.AopInvocationException e) {
			return 0;
		} catch (java.lang.NullPointerException e) {
			return 0;
		}
	}

	@RequestMapping(value = "/SetGroupsMatrix", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer setGroupsMatrix(@RequestBody String[][] matrixToSend) throws Exception {
		for (String[] dataToSend : matrixToSend) {
			boolean found = false;
			for (GroupProperties gC : Globals.getAllGroupsProperties()) {
				if (gC.getGroupId().equals(dataToSend[0])) {
					found = true; //The group corresponding to the element we want to add is already in the allGroupProperties list
					if (!dataToSend[1].equals("DONOTCONSIDER")) { //the element we are going to add is not a keyword, but a campaign
						if (!gC.getCampaignsList().contains(dataToSend[1])) { //This group properties hasn't got this campaign yet
							List<String> tempo = gC.getCampaignsList();
							tempo.add(dataToSend[1]);
							gC.setCampaignsList(tempo);
						}
					} else { //This is a keyword
						if (gC.getKeywordList() != null && !gC.getKeywordList().isEmpty()) {
							List<String> temp = new ArrayList<String>(Arrays.asList(dataToSend));
							temp.remove(1);
							temp.remove(0); //temp is now the list of keywords
							for (String kw : temp) {
								if (!gC.getKeywordList().contains(kw)) { //This group properties hasn't got this keyword yet
									List<String> tempo = gC.getKeywordList();
									tempo.add(kw);
									gC.setKeywordList(tempo);
								}
							}
						} else {// this groupProperties hasn't got any keyword for the moment
							List<String> temp = new ArrayList<String>(Arrays.asList(dataToSend));
							temp.remove(1);
							temp.remove(0); //temp is now the list of keywords
							gC.setKeywordList(temp);

						}
					}
				}
			}

			if (!found) { // We create another group property
				GroupProperties tempGroup = new GroupProperties();
				tempGroup.setGroupId(dataToSend[0]);
				if (!dataToSend[1].equals("DONOTCONSIDER")) {
					List<String> campaignsList = new ArrayList<String>();
					campaignsList.add(dataToSend[1]);
					tempGroup.setCampaignsList(campaignsList);
				} else {
					List<String> kwList = new ArrayList<String>();
					kwList.add(dataToSend[2]);
					tempGroup.setCampaignsList(kwList);
				}
				List<GroupProperties> list = Globals.getAllGroupsProperties();
				list.add(tempGroup);
				Globals.setAllGroupsProperties(list);
			}
		}
		return 1;
	}

}
