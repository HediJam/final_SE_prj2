package client;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author Hedieh Jam
 */
public class TerminalFormatter extends  Formatter{
    String terminalType;
    String terminalId;
    TerminalFormatter(String id, String type){
        terminalType = type;
        terminalId = id;
    }
  @Override
    public String format(LogRecord record) {
        String msg = String.valueOf(System.currentTimeMillis());
        msg += "\r\n" + record.getLevel() + ";" + terminalType + ";" + terminalId +";"+record.getMessage() + "\r\n";
        return msg;
    }
    
}
