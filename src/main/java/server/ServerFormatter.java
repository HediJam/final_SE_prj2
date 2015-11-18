
package server;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author Hedieh Jam
 */
public class ServerFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String msg = String.valueOf(System.currentTimeMillis());
        msg += "\r\n" + record.getLevel() + ":" +record.getMessage() + "\r\n";
        return msg;
    }
    
}
