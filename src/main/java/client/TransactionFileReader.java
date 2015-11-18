package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Hedieh Jam
 */
public class TransactionFileReader {

    private XMLInputFactory factory;
    private InputStream xmlInputStream;

    private String serverIpAddress;
    private String serverPort;
    private String terminalId;
    private String logFileName;
    private String terminalType;

    public TransactionFileReader() {

        factory = XMLInputFactory.newInstance();
        try {
            xmlInputStream = new FileInputStream("terminal.xml");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TransactionFileReader.class.getName()).log(Level.SEVERE, "can not find terminal.xml", ex);
        }
    }

    public void parseTransactionsFile() {
        XMLEventReader eventReader = null;
        try {
            eventReader = factory.createXMLEventReader(xmlInputStream);
        } catch (XMLStreamException ex) {
            Logger.getLogger(TransactionFileReader.class.getName()).log(Level.SEVERE, "can not event reader", ex);
        }
        while (eventReader.hasNext()) {
            XMLEvent event = null;
            try {
                event = eventReader.nextEvent();
            } catch (XMLStreamException ex) {
                Logger.getLogger(TransactionFileReader.class.getName()).log(Level.SEVERE, "parser does not have any event.", ex);
            }
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals("terminal")) {
                    setTerminalAttribute(startElement);
                } else if (event.asStartElement().getName().getLocalPart().equals("transaction")) {
                    try {
                        event = eventReader.nextEvent();
                    } catch (XMLStreamException ex) {
                        Logger.getLogger(TransactionFileReader.class.getName()).log(Level.SEVERE, "parser does not have any event.", ex);
                    }
                    Transaction currentTransaction = setTransactionAttribute(startElement);
                    currentTransaction.addTransaction();
                } else if (event.asStartElement().getName().getLocalPart().equals("outLog")) {
                    setLogAttribute(startElement);
                } else if (event.asStartElement().getName().getLocalPart().equals("server")) {
                    setServerAttribute(startElement);
                }

            }
        }
    }

    private void setTerminalAttribute(StartElement startElement) {
        Iterator<Attribute> attributes = startElement.getAttributes();
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            if (attribute.getName().toString().equals("id")) {
                terminalId = attribute.getValue();
            } else if (attribute.getName().toString().equals("type")) {
                terminalType = attribute.getValue();
            }
        }
    }
    
    private Transaction setTransactionAttribute(StartElement startElement) {
        Iterator<Attribute> attributes = startElement.getAttributes();
        Transaction currentTransaction = new Transaction();
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            switch (attribute.getName().toString()) {
                case "id":
                    currentTransaction.setTransactionId(attribute.getValue());
                    break;
                case "type":
                    currentTransaction.setType(attribute.getValue());
                    break;
                case "amount":
                    currentTransaction.setAmount(attribute.getValue());
                    break;
                case "deposit":
                    currentTransaction.setDepositId(attribute.getValue());
                    break;
            }
        }
        return currentTransaction;
    }

    private void setServerAttribute(StartElement startElement) {
        Iterator<Attribute> attributes = startElement.getAttributes();
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            if (attribute.getName().toString().equals("ip")) {
                serverIpAddress = attribute.getValue();
            }
            if (attribute.getName().toString().equals("port")) {
                serverPort = attribute.getValue();
            }
        }
    }

    private void setLogAttribute(StartElement startElement) {
        Iterator<Attribute> attributes = startElement.getAttributes();
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            if (attribute.getName().toString().equals("path")) {
                logFileName = attribute.getValue();
            }
        }
    }

    public String getServerIp() {
        return serverIpAddress;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public String getTerminalId() {
        return terminalId;
    }
}
