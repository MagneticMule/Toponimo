package org.toponimo.client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.toponimo.client.R;

public class MillisToDate {

    public static String millisecondsToDate(long milliseconds) {
	SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy,  HH:mm");
	Date resultdate = new Date(milliseconds);
	return (sdf.format(resultdate));

    }
}
