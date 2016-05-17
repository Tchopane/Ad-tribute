package nl.marketingsciences.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.marketingsciences.beans.database.RecapInteraction;
import nl.marketingsciences.beans.database.TMS;
import nl.marketingsciences.dataEditing.SaveTMS;
import nl.marketingsciences.repositories.InteractionGroupRepository;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.repositories.RecapInteractionsRepository;
import nl.marketingsciences.repositories.StateRepository;
import nl.marketingsciences.repositories.TMSRepository;
import nl.marketingsciences.websocket.Globals;

@RestController
@RequestMapping("/RecapInteraction")
public class RecapInteractionController {

	@Inject
	private Globals Globals;

	@Inject
	private RecapInteractionsRepository recapInteractionsRepository;

	@Inject
	private InteractionGroupRepository interactionGroupRepository;

	@Inject
	private StateRepository stateRepository;
	
	@Inject
	private InteractionRepository interactionRepository;
	
	@Inject 
	private TMSRepository tmsRepository;

	@RequestMapping(value = "/SetWeights", method = RequestMethod.POST)
	public boolean setWeights(@RequestParam("startDate") String startDateString, @RequestParam("endDate") String endDateString) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date startDate = sdf.parse(startDateString);
		Date endDate = sdf.parse(endDateString);

		ArrayList<String> header = new ArrayList<String>();
		ArrayList<Integer> values = new ArrayList<Integer>();
		// This List contains the date ranges
		//From now on we fill that last ArrayList with times in ms
		for (int n = 0; n <= 30; n++) {
			values.add(n * 60);
		} // from 0 minute to 30 minutes
		for (int n = 4; n <= 6; n++) {
			values.add(n * 10 * 60);
		}
		for (int n = 2; n <= 48; n++) {
			values.add(n * 60 * 60);
		}

		List<String> beginList = recapInteractionsRepository.findDistinctBeginState(startDate, endDate, Globals.getCurrentSessionReportId());
		for (String bS : beginList) {
			List<String> endList = recapInteractionsRepository.findDistinctEndStateWhere(startDate, endDate, bS, Globals.getCurrentSessionReportId());
			for (String eS : endList) {
				if (!eS.equals(bS)) {
					header.add(bS + "," + eS);
				}
			}
		}

		float[][] weights = new float[header.size()][values.size()];

		for (int i = 0; i < header.size(); i++) {
			String toSplit = header.get(i);
			String[] split = toSplit.split(",", 2);
			String beginState = split[0];
			String endState = split[1];
			List<RecapInteraction> recapInteractionList = recapInteractionsRepository.findByBeginEnd(startDate, endDate, beginState, endState, Globals.getCurrentSessionReportId());
			for (RecapInteraction temp : recapInteractionList) {
				int time = temp.getTimeToNextState();
				int j = 0;
				while (j < values.size() - 1 && time > values.get(j + 1)) {
					j++;
				}
				if (j < values.size()) {
					weights[i][j] = weights[i][j] + 1;
				}
			}
			for (int n = 0; n <= 30; n++) {
				weights[i][n] = weights[i][n] / recapInteractionList.size();
			}
			for (int n = 31; n <= 33; n++) {
				weights[i][n] = weights[i][n] / (10 * recapInteractionList.size());
			}
			for (int n = 34; n <= 80; n++) {
				weights[i][n] = weights[i][n] / (60 * recapInteractionList.size());
			}
		}
		//From now on, the array containing the weights is ready and we can use it
		//We now want to add to the interactions the value of the weights
		//We clean the database by removing the interactions outside the new date range
		recapInteractionsRepository.deleteOutdated(startDate, endDate, Globals.getCurrentSessionReportId());
		Integer maxPersonId = recapInteractionsRepository.maxPersonId(Globals.getCurrentSessionReportId());
		int j = 0;
		while (j <= maxPersonId) { //j is kind of the person_id
			j++;
			//We create two lists : one contains all the recap interaction for the current person id, and the other is empty and will be filled only 
			//with the placement between two same activities
			List<RecapInteraction> recapInteractionList = recapInteractionsRepository.findJourneyByPersonId(j, Globals.getCurrentSessionReportId());
			if (recapInteractionList.isEmpty()) {
				continue;
			}
			List<RecapInteraction> temp = new ArrayList<RecapInteraction>();
			temp.add(recapInteractionList.get(0));
			recapInteractionList.remove(0);
			if (temp.get(0).getTimeToNextState() == -1) {
				continue;
			}
			bigLoop: while (!recapInteractionList.isEmpty() || !temp.isEmpty()) {

				if (!temp.isEmpty() && !recapInteractionList.isEmpty()) {
					//We start by checking that the next element of recapInteractionList is not leading to nothing (same begin and end state)
					if (recapInteractionList.get(0).getTimeToNextState() == -1) {
						recapInteractionList.remove(0);
						continue;
					}
					long nextTimeStamp1 = temp.get(0).getTimestamp().getTime() + temp.get(0).getTimeToNextState() * 1000;
					long nextTimeStamp2 = recapInteractionList.get(0).getTimestamp().getTime() + recapInteractionList.get(0).getTimeToNextState() * 1000;
					if (temp.get(0).getBeginState().equals(recapInteractionList.get(0).getBeginState()) && temp.get(0).getEndState().equals(recapInteractionList.get(0).getEndState())
							&& nextTimeStamp1 == nextTimeStamp2) {// if all the conditions are met it means that the next element is between the two same activities
						temp.add(recapInteractionList.get(0));
						recapInteractionList.remove(0);
						continue;
					}
				}
				float totalWeight = 0;
				for (RecapInteraction rI : temp) {
					String forHeader = rI.getBeginState() + "," + rI.getEndState();
					int timeToNextState = rI.getTimeToNextState();
					int k = 0;
					int l = 0;
					while (!forHeader.equals(header.get(k))) {
						k++;
					}
					while (l < values.size() - 1 && timeToNextState > values.get(l + 1)) {
						l++;
					}
					if (l < values.size()) {
						rI.setWeight(weights[k][l]);
						totalWeight = totalWeight + rI.getWeight();
					}
				}
				//update the weights and save the stuff in db
				for (RecapInteraction rI : temp) {
					rI.setWeight(rI.getWeight() / totalWeight);
					recapInteractionsRepository.save(rI);
				}
				//empty the tempList and refill it with the first element of the recapInteractionList
				temp.clear();
				if (!recapInteractionList.isEmpty()) {
					temp.add(recapInteractionList.get(0));
					recapInteractionList.remove(0);
				} else {
					break bigLoop;
				}

			}

		}
		return true;
	}

	@RequestMapping(value = "/FillTransitionMatrices", method = RequestMethod.POST)
	public boolean fillTransitionMatrices() throws Exception {
		//Create all rows
		int numberOfGroups = interactionGroupRepository.findMaxGroupNumber(Globals.getCurrentSessionReportId());
		int numberOfStates = stateRepository.findMaxState(Globals.getCurrentSessionReportId());
		for (int i = 1; i <= numberOfGroups; i++) {
			for (int j = 0; j <= numberOfStates; j++) {
				for (int k = j; k <= numberOfStates; k++) {
					TMS newTMS = new TMS();
					newTMS.setImpclick("Imp");
					newTMS.setGroupNumber(i);
					newTMS.setStateFrom(j);
					newTMS.setStateTo(k);
					newTMS.setReportId(Globals.getCurrentSessionReportId());
					SaveTMS.saveTMS(newTMS);
					newTMS.setImpclick("Click");
					SaveTMS.saveTMS(newTMS);
				}
			}
		}

		//Fill the rows by adding content to the value_cell column
		//Loop on the recapInteractions table
		int minId = recapInteractionsRepository.minId(Globals.getCurrentSessionReportId());
		int maxId = recapInteractionsRepository.maxId(Globals.getCurrentSessionReportId());
		for (int i = minId; i <= maxId; i++) {
			if (recapInteractionsRepository.existId(i, Globals.getCurrentSessionReportId()) != null) { 
			//Check if that recapInteraction exists, since we just removed some of them in the previous step
				RecapInteraction tempRecapInteraction = recapInteractionsRepository.findById(i, Globals.getCurrentSessionReportId());
				int tempPersonId = tempRecapInteraction.getPersonId();
				Date tempDate = tempRecapInteraction.getTimestamp();
				//from these two infos we can retrieve the original id in the interactions database, and finally the imp_click value
				int originalId = interactionRepository.findRealIdByPersonIdAndTimestamp(tempPersonId, tempDate, Globals.getCurrentSessionReportId());
				String tempImpclick = interactionRepository.findImpclickById(originalId, Globals.getCurrentSessionReportId());
				int tempGroupNumber = recapInteractionsRepository.findGroupIdById(i, Globals.getCurrentSessionReportId());
				int tempFrom = stateRepository.findPositionByName(recapInteractionsRepository.findBeginStateById(i, Globals.getCurrentSessionReportId()), Globals.getCurrentSessionReportId());
				int tempTo = stateRepository.findPositionByName(recapInteractionsRepository.findEndStateById(i, Globals.getCurrentSessionReportId()), Globals.getCurrentSessionReportId());
				
				TMS currentTMS = tmsRepository.findByImpClickAndGroupNumberAndStateFromAndStateTo(tempImpclick, tempGroupNumber, tempFrom, tempTo, Globals.getCurrentSessionReportId());

				currentTMS.setCellValue(currentTMS.getCellValue()+tempRecapInteraction.getWeight());
				TMS currentTMSBis = tmsRepository.findByImpClickAndGroupNumberAndStateFromAndStateTo(tempImpclick, tempGroupNumber, tempFrom, tempFrom, Globals.getCurrentSessionReportId());
				currentTMSBis.setCellValue(currentTMSBis.getCellValue()+(1-tempRecapInteraction.getWeight()));
				
				tmsRepository.save(currentTMS);
				tmsRepository.save(currentTMSBis);
			}
		}
		return true;
	}
}
