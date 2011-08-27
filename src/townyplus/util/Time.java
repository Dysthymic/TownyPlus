//Time Utils designed by spathwalker.
//v1.0.0
package townyplus.util;

import java.text.DateFormat;
import java.util.Date;

public class Time {
	public static final long second = 1000;
	public static final long minute = 60000;
	public static final long hour = 3600000;
	public static final long day = 86400000;
	public static final long week = 604800000;
	public static final long month = 2629800000L;
	public static final long year = 31557600000L;
	
	public static String toStr(long time) {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(time));
	}
	
	public static String getTime(long time) {
		return DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(time));
	}	
	
	public static long now() {
		return System.currentTimeMillis();
	}

    public static String countDown(long time) {
        long result;
        if (time > day) {
            return (time/day)+" day"+((time/day>1) ?  "s" : "");
        }
        if (time > hour) {
            return (time/hour)+" hour"+((time/hour>1) ?  "s" : "");
        }
        return (time/minute)+" minute"+((time/minute>1) ?  "s" : "");
    }	
}
