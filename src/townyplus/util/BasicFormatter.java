package townyplus.util;

// This custom formatter formats parts of a log record to a single line

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class BasicFormatter extends Formatter {

	// This method is called for every log records
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer();
        // Bold any levels >= WARNING
		if (rec.getLevel() == Level.SEVERE) {
			buf.append("*ERROR* ");
		}
        buf.append(formatMessage(rec)+"\n");
        return buf.toString();
    }

    // This method is called just after the handler using this
    // formatter is created
	@Override
    public String getHead(Handler h) {
        return "";
    }

    // This method is called just after the handler using this
    // formatter is closed
	@Override
    public String getTail(Handler h) {
        return "";
    }
}