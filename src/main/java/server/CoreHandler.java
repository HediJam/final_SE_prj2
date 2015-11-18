
package server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Hedieh Jam
 */
public class CoreHandler {

    private FileReader jsonFileReader;
    private int portFromFile;
    private String logFileName;
    private String jsonFilePath;

    public CoreHandler(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        try {
            jsonFileReader = new FileReader(jsonFilePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CoreHandler.class.getName()).log(Level.SEVERE, jsonFilePath + " not found!", ex);
        }
    }

    public void readJSONFile() {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(jsonFileReader);
        } catch (IOException ex) {
            Logger.getLogger(CoreHandler.class.getName()).log(Level.SEVERE, "json core file not found to parse", ex);
        } catch (ParseException ex) {
            Logger.getLogger(CoreHandler.class.getName()).log(Level.SEVERE, "there are some problems in parsing", ex);
        }
        JSONObject jsonObject = (JSONObject) obj;
        portFromFile = Integer.valueOf(((Long) jsonObject.get("port")).toString());
        JSONArray depositsArray = (JSONArray) jsonObject.get("deposits");
        Iterator<JSONObject> iteratorOverDeposits = depositsArray.iterator();
        while (iteratorOverDeposits.hasNext()) {
            JSONObject deposit = iteratorOverDeposits.next();
            Deposit curDeposit = createDepositFromJsonObject(deposit);
        }
        logFileName = (String) jsonObject.get("outLog");
    }

    private Deposit createDepositFromJsonObject(JSONObject deposit) {
        String customerName = (String) deposit.get("customer");
        String customerId = (String) deposit.get("id");
        String initialBalance = (String) deposit.get("initialBalance");
        String upperBound = (String) deposit.get("upperBound");
        Deposit curDeposit = new Deposit(customerName, customerId, initialBalance, upperBound);
        return curDeposit;
    }

    public int getPort() {
        return portFromFile;
    }

    public String getLogFile() {
        return logFileName;
    }

    public void writeToJsonFile() {
        JSONObject core = createCoreJsonFormat();
        try {
            FileWriter file = new FileWriter(jsonFilePath);
            file.write(core.toJSONString());
            file.flush();
            file.close();

        } catch (IOException e) {
            Logger.getLogger(CoreHandler.class.getName()).log(Level.SEVERE, jsonFilePath + " not found to write.", e);
        }
    }

    private JSONObject createCoreJsonFormat() {
        JSONObject coreJSONObj = new JSONObject();
        coreJSONObj.put("port", portFromFile);
        JSONArray depositsJSONlist = Deposit.depositsToJSON();
        coreJSONObj.put("deposits", depositsJSONlist);
        coreJSONObj.put("outLog", logFileName);
        return coreJSONObj;
    }
}
