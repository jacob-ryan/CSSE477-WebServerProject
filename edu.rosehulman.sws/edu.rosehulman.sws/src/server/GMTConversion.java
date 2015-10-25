package server;

import java.text.*;
import java.util.*;

public class GMTConversion
{
	public static final String GMT_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";

	public static String toGMTString(Date date)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		DateFormat gmtFormat = new SimpleDateFormat(GMTConversion.GMT_FORMAT);
		gmtFormat.setCalendar(calendar);

		return gmtFormat.format(date);
	}

	public static Date fromGMTString(String dateString) throws ParseException
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		DateFormat gmtFormat = new SimpleDateFormat(GMTConversion.GMT_FORMAT);
		gmtFormat.setCalendar(calendar);

		return gmtFormat.parse(dateString);
	}

	public static void main(String[] args) throws Exception
	{
		Date date = new Date();
		System.out.println("From Any Timezone Date:\t" + date);
		String newTime = toGMTString(date);
		System.out.println("To GMT Timezone Date:\t" + newTime);
	}
}
