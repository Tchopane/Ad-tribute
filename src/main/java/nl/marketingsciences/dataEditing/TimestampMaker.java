package nl.marketingsciences.dataEditing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampMaker {
	
	public static Date Timestamp(String timestampString){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date timestamp = null;
		try {
			timestamp = formatter.parse(timestampString);
		} catch (ParseException e) {e.printStackTrace();}
		
		return timestamp;
	}
}
