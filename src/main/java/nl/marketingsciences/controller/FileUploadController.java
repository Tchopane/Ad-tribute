package nl.marketingsciences.controller;

import java.io.BufferedReader;
//import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.dfareporting.Dfareporting;

import nl.marketingsciences.beans.database.Activity;
import nl.marketingsciences.beans.database.Interaction;
//import nl.marketingsciences.dataEditing.Chronometer;
import nl.marketingsciences.dataEditing.GetPersonIDCountIsOne;
import nl.marketingsciences.dataEditing.GetPersonIDCountLargerOne;
import nl.marketingsciences.dataEditing.SaveActivityDCM;
import nl.marketingsciences.dataEditing.SaveInteractionsDCM;
import nl.marketingsciences.dataEditing.TimestampMaker;
import nl.marketingsciences.dcmapi.DfaReportingFactory;
import nl.marketingsciences.repositories.ActivityRepository;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.websocket.Globals;

@RestController
@RequestMapping("/FileUpload")
public class FileUploadController {

	@Inject
	private GetPersonIDCountIsOne getPersonIDCountIsOne;
	@Inject
	private GetPersonIDCountLargerOne getPersonIDCountLargerOne;

	@Inject
	private InteractionRepository interactionRepository;
	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private Globals Globals;

	@RequestMapping(value = "/File", method = RequestMethod.POST)
	public @ResponseBody void handleFileUpload() throws Exception {

		Dfareporting reporting = DfaReportingFactory.getInstance();
		InputStream fileContent2 = reporting.files().get(Globals.REPORTID, Globals.FILEID).executeMedia().getContent();
		BufferedReader reader2 = new BufferedReader(new InputStreamReader(fileContent2));
		int lines = 0;
		while (reader2.readLine() != null)
			lines++;
		reader2.close();

		InputStream fileContent = reporting.files().get(Globals.REPORTID, Globals.FILEID).executeMedia().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent));

		String inputLine;
		int deletedlines = 1;
		while ((inputLine = reader.readLine()) != null) {
			List<String> values = Arrays.asList(inputLine.split(","));
			deletedlines++;
			if (values.get(0).toString().equals("Report Fields")) {
				break;
			}
		}
		Globals.setUsedLines(lines - deletedlines - 1);
		inputLine = reader.readLine();

		List<String> headers = Arrays.asList(inputLine.split(","));

		int pathLengthPosition = headers.indexOf("Path Length");
		int conversionIDPosition = headers.indexOf("Conversion ID");
		int activityPosition = headers.indexOf("Activity");
		int activityDateTimePosition = headers.indexOf("Activity Date/Time");

		int interaction1TypePosition = headers.indexOf("Interaction 1: Interaction Type");
		int interaction1DateTimePosition = headers.indexOf("Interaction 1: Interaction Date/Time");
		int interaction1PlacementPosition = headers.indexOf("Interaction 1: Placement");
		int interaction2PlacementPosition = headers.indexOf("Interaction 2: Placement");
		int interaction1PSCPosition = headers.indexOf("Interaction 1: Paid Search Campaign");
		int interaction1PSKPosition = headers.indexOf("Interaction 1: Paid Search Keyword");
		int interaction1PSMTPosition = headers.indexOf("Interaction 1: Paid Search Match Type");
		int differenceBetweenInteractions = interaction2PlacementPosition - interaction1PlacementPosition;
		
		
		long report_id_value = 1;
		try {
			report_id_value = activityRepository.getMaxReportId() + 1;
		} catch (java.lang.NullPointerException ex) {
			report_id_value = 1;
		}
		Globals.setCurrentSessionReportId(report_id_value);
		Globals.setI(0);

		while (Globals.getI() < Globals.getUsedLines()) {
			inputLine = reader.readLine();
			Globals.setI(Globals.getI() + 1);
			List<String> values = Arrays.asList(inputLine.split(","));
			String id = values.get(conversionIDPosition).toString();
			int pathLength = Integer.parseInt(values.get(pathLengthPosition).toString());

			Activity activityCheck = activityRepository.findTop1ByConversionIdAndReportId(id, Globals.getCurrentSessionReportId());

			if (activityCheck == null && pathLength > 0) {
				Activity activity = new Activity();
				Interaction interaction1 = new Interaction();

				// Interaction 1
				interaction1.setPlacement(values.get(interaction1PlacementPosition + (pathLength - 1) * differenceBetweenInteractions).toString());
				Date timestampI1 = TimestampMaker.Timestamp(values.get(interaction1DateTimePosition + (pathLength - 1) * differenceBetweenInteractions).toString());
				interaction1.setTimestamp(timestampI1);
				interaction1.setImpclick(values.get(interaction1TypePosition + (pathLength - 1) * differenceBetweenInteractions).toString());
				// Interaction 1

				int count = interactionRepository.countByPlacementAndImpclickAndTimestampAndReportId(interaction1.getPlacement(), interaction1.getImpclick(), interaction1.getTimestamp(),
						Globals.getCurrentSessionReportId());
				int personID = 0;

				if (count == 0) {
					personID = interactionRepository.getMaxPersonId(Globals.getCurrentSessionReportId()) + 1;
				} else if (count == 1) {
					if (pathLength == 1) {
						personID = getPersonIDCountIsOne.getPersonIDCountIsOne(interaction1);
					} else {
						// Interaction 2
						Interaction interaction2 = new Interaction();
						interaction2.setPlacement(values.get(interaction1PlacementPosition + (pathLength - 2) * differenceBetweenInteractions).toString());
						Date timestampI2 = TimestampMaker.Timestamp(values.get(interaction1DateTimePosition + (pathLength - 2) * differenceBetweenInteractions).toString());
						interaction2.setTimestamp(timestampI2);
						interaction2.setImpclick(values.get(interaction1TypePosition + (pathLength - 2) * differenceBetweenInteractions).toString());
						// Interaction 2

						personID = getPersonIDCountIsOne.getPersonIDCountIsOne(interaction1, interaction2);
					}
				} else {
					List<Integer> possiblePersonIDs = new ArrayList<Integer>();
					List<Interaction> interactions = interactionRepository.findByPlacementAndTimestampAndImpclickAndReportId(interaction1.getPlacement(), interaction1.getTimestamp(),
							interaction1.getImpclick(), Globals.getCurrentSessionReportId());
					for (Interaction interaction : interactions) {
						possiblePersonIDs.add(interaction.getPersonId());
					}

					if (pathLength == 1) {

						personID = getPersonIDCountLargerOne.getPersonIDCountLargerOne(interaction1, possiblePersonIDs);
					} else {
						// Interaction 2
						Interaction interaction2 = new Interaction();
						interaction2.setPlacement(values.get(interaction1PlacementPosition + (pathLength - 2) * differenceBetweenInteractions).toString());
						Date timestampI2 = TimestampMaker.Timestamp(values.get(interaction1DateTimePosition + (pathLength - 2) * differenceBetweenInteractions).toString());
						interaction2.setTimestamp(timestampI2);
						interaction2.setImpclick(values.get(interaction1TypePosition + (pathLength - 2) * differenceBetweenInteractions).toString());
						// Interaction 2
						personID = getPersonIDCountLargerOne.getPersonIDCountLargerOne(interaction1, interaction2, possiblePersonIDs);
					}
				}

				for (int j = 1; j <= pathLength; j++) {

					Interaction finteraction = new Interaction();

					finteraction.setPlacement(values.get(interaction1PlacementPosition + (pathLength - j) * differenceBetweenInteractions).toString());
					Date timestamp = TimestampMaker.Timestamp(values.get(interaction1DateTimePosition + (pathLength - j) * differenceBetweenInteractions).toString());
					finteraction.setTimestamp(timestamp);
					finteraction.setImpclick(values.get(interaction1TypePosition + (pathLength - j) * differenceBetweenInteractions).toString());
					finteraction.setPersonId(personID);
					finteraction.setPaidSearchCampaign(values.get(interaction1PSCPosition + (pathLength - j) * differenceBetweenInteractions).toString());
					finteraction.setPaidSearchKeyword(values.get(interaction1PSKPosition + (pathLength - j) * differenceBetweenInteractions).toString());
					finteraction.setPaidSearchMatchType(values.get(interaction1PSMTPosition + (pathLength - j) * differenceBetweenInteractions).toString());
					finteraction.setReportId(Globals.getCurrentSessionReportId());
					int checkExistence = 0;
					checkExistence = interactionRepository.findByPlacementAndTimestampAndImpclickAndPersonIdAndReportId(finteraction.getPlacement(), finteraction.getTimestamp(),
							finteraction.getImpclick(), finteraction.getPersonId(), Globals.getCurrentSessionReportId()).size();
					if (checkExistence == 0) {
						SaveInteractionsDCM.saveInteraction(finteraction);
						// interactionList.add(finteraction);
					}

				}

				String activityName = values.get(activityPosition).toString();

				Date timestamp = TimestampMaker.Timestamp(values.get(activityDateTimePosition).toString());
				activity.setConversionId(id);
				activity.setActivity(activityName);
				activity.setTimestamp(timestamp);
				activity.setPersonId(personID);
				activity.setReportId(Globals.getCurrentSessionReportId());

				// activityList.add(activity);
				SaveActivityDCM.saveActivity(activity);

			}
		}
	}
}
