package nl.marketingsciences.controller;
import java.util.Date;

//import java.util.List;
import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.marketingsciences.beans.database.Interaction;
import nl.marketingsciences.repositories.InteractionRepository;
import nl.marketingsciences.websocket.Globals;



@RestController
@RequestMapping("/Test")
public class TestController {
	
	@Inject private InteractionRepository interactionRepository;
	@Inject
	private Globals Globals;
	
	@RequestMapping(value = "/Test1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public int Test1000() {
		
		int testInt = interactionRepository.getMaxPersonId( Globals.getCurrentSessionReportId());
		Interaction interaction = new Interaction();
		interaction.setPlacement("placement");
		
		Date date = new Date();
		
		interaction.setTimestamp(date);
		interaction.setImpclick("impclick");
		interaction.setPersonId(1);
		
		System.out.println(interactionRepository.findByPlacementAndTimestampAndImpclickAndPersonIdAndReportId(interaction.getPlacement(), interaction.getTimestamp(),interaction.getImpclick(), interaction.getPersonId(),Globals.getCurrentSessionReportId()));
		//List<String> testString = interactionRepository.findDistinctPlacements();
		
		return testInt;
	}
}
