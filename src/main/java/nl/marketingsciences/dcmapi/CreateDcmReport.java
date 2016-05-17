package nl.marketingsciences.dcmapi;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Charsets;
import com.google.api.client.util.DateTime;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.DateRange;
import com.google.api.services.dfareporting.model.DimensionValue;
import com.google.api.services.dfareporting.model.Report;
import com.google.api.services.dfareporting.model.Report.PathToConversionCriteria;
import com.google.api.services.dfareporting.model.Report.PathToConversionCriteria.ReportProperties;

import nl.marketingsciences.websocket.Globals;


@Service
public class CreateDcmReport {
	
	@Inject
	private Globals Globals;

	private static final long REPORT_ID = 41277180;

	public InputStream createReport(Dfareporting reporting, long profileId, String floodlightConfigurationId, Date startDate,
			Date endDate, List<Long> floodlightIds) throws Exception {
		// Retrieve the specified report.
		Report templateReport = reporting.reports().get(profileId, REPORT_ID).execute();
		Report newReport = new Report();
		// Update the report name.
		newReport.setName(floodlightConfigurationId + "_REPORT");
		newReport.setType("PATH_TO_CONVERSION");
		
		// Get the Criteria
		PathToConversionCriteria crit = templateReport.getPathToConversionCriteria();
		DateRange dateRange = new DateRange();

		// Get the date range
		String dateFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		DateTime startDateTime = new DateTime(sdf.format(startDate));
		DateTime endDateTime = new DateTime(sdf.format(endDate));
		dateRange.setStartDate(startDateTime);
		dateRange.setEndDate(endDateTime);
		dateRange.setKind("dfareporting#dateRange");
		crit.setDateRange(dateRange);
	    
		// Select the right Floodlight Configuration ID
		DimensionValue advertiserID = new DimensionValue();
		advertiserID.setValue(floodlightConfigurationId);
		advertiserID.setDimensionName("dfa:floodlightConfigId");
		
		crit.setFloodlightConfigId(advertiserID);
		
		// Select the right Floodlight Activities
		List<DimensionValue> activityFilters = new ArrayList<DimensionValue>();
		for (Long floodlightId : floodlightIds) {
			DimensionValue activityFilter = new DimensionValue();
			activityFilter.setDimensionName("dfa:activity");
			activityFilter.setId(String.valueOf(floodlightId));
			activityFilters.add(activityFilter);
		}
		crit.setActivityFilters(activityFilters);
		ReportProperties reportProperties = crit.getReportProperties();
		try {
		reportProperties.setMaximumClickInteractions(100);
		reportProperties.setMaximumImpressionInteractions(100);
		crit.setReportProperties(reportProperties);
		newReport.setPathToConversionCriteria(crit);
		reporting.reports().insert(profileId, newReport).execute();
		} catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
			reportProperties.setMaximumClickInteractions(5);
			reportProperties.setMaximumImpressionInteractions(5);
			crit.setReportProperties(reportProperties);
			newReport.setPathToConversionCriteria(crit);
		}
		Report insertReport = reporting.reports().insert(profileId, newReport).execute();
		Globals.REPORTID=insertReport.getId();
		return runReport(reporting, profileId, insertReport.getId());

	}

	
	
	public InputStream runReport(Dfareporting reporting, long profileId, long reportId)
			throws InterruptedException, IOException {
		com.google.api.services.dfareporting.model.File file = reporting.reports().run(profileId, reportId).execute();

		while (!file.getStatus().equals("REPORT_AVAILABLE")) {
			TimeUnit.SECONDS.sleep(2);
			file = reporting.reports().files().get(profileId, reportId, file.getId()).execute();
			System.out.printf("Report file with ID %d is in status \"%s\".%n", file.getId(), file.getStatus());
		}
		Globals.FILEID=file.getId();
		return downloadReport(reporting, profileId, reportId, file.getId());
	}

	public InputStream downloadReport(Dfareporting reporting, long profileId, long reportId, long fileId)
			throws IOException {
		HttpResponse fileContents = reporting.files().get(reportId, fileId).executeMedia();
		try {
			FileWriter writer = new FileWriter("Report.csv");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(fileContents.getContent(), Charsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				writer.append(line);
				writer.append('\n');
			}
			writer.flush();
			writer.close();
		} finally {
			fileContents.disconnect();
		}
		reporting.reports().delete(profileId, reportId).execute();
		return fileContents.getContent();
	}

}
