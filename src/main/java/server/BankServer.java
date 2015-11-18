package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hedieh Jam
 */
public class BankServer {

    private String coreFilePath = "core.json";
    private int port;
    private Logger serverLogger;
    private static CoreHandler jsonCoreHandler;

    private BankServer() {
        jsonCoreHandler = new CoreHandler(coreFilePath);
        jsonCoreHandler.readJSONFile();
        port = jsonCoreHandler.getPort();
        setLogger(jsonCoreHandler.getLogFile());
    }

    private void setLogger(String logFileName) {
        try {
            serverLogger = Logger.getLogger("serverLogger");
            FileHandler logFileHandler = null;
            logFileHandler = new FileHandler(logFileName);
            serverLogger.addHandler(logFileHandler);
            ServerFormatter formatter = new ServerFormatter();
            logFileHandler.setFormatter(formatter);
        } catch (IOException ex) {
            Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, "can not find log file : " + logFileName, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, "can not open " + logFileName + " because of security issues.", ex);
        }

    }

    public static void main(String[] args) throws Exception {
        BankServer bs = new BankServer();
        new ServerCommandLine(bs).start();
    }

    private void serverRunNormally() {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(port);
            while (true) {
                new TransactionServiceProvider(listener.accept(), serverLogger).start();
            }
        } catch (IOException ex) {
            serverLogger.info(BankServer.class.getName() + " can not listen on port : " + port);
        }
        try {
            listener.close();
        } catch (IOException ex) {
            serverLogger.info(BankServer.class.getName() + " can not close listener on port : " + port);
        }
    }

    private void serverTestMood(boolean sync) {

        for (int i = 0; i < 1000; i++) {
            new TransactionServiceProviderTest(sync, serverLogger).start();
        }
    }

    private static class TransactionServiceProvider extends Thread {

        private Socket socket;
        private Logger serverLogger;

        public TransactionServiceProvider(Socket socket, Logger serverLogger) {
            this.socket = socket;
            this.serverLogger = serverLogger;

        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        break;
                    }
                    serverLogger.info("client request : " + input);
                    TransactionExecuter executer = new TransactionExecuter(input);
                    String transactionResult = executer.execute(true);
                    out.println(transactionResult);
                    serverLogger.info(executer.getOriginTerminal() + ":" + transactionResult);
                }
            } catch (IOException e) {

            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    serverLogger.info("can not close server socket");
                }
            }
        }
    }

    private static class TransactionServiceProviderTest extends Thread {

        private Logger serverLogger;
        private boolean sync;

        public TransactionServiceProviderTest(boolean sync, Logger serverLogger) {
            this.serverLogger = serverLogger;
            this.sync = sync;
        }

        public void run() {
            for (int i = 0; i < 1000; i++) {
                TransactionExecuter executer = new TransactionExecuter("33227781;withdraw;1;1;21374;ATM");
                String transactionResult = executer.execute(sync);
                serverLogger.info(executer.getOriginTerminal() + ":" + transactionResult);
            }
        }
    }

    private static class ServerCommandLine extends Thread {

        private BankServer bs;

        public ServerCommandLine(BankServer bs) {
            this.bs = bs;
        }

        public void run() {
            System.out.println("The Banking server is running.");
            System.out.println("available commands are :");
            System.out.println("run normally / sync / run successful test scenario / run unsuccessful test scenario");
            try {
                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String command = bufferRead.readLine();
                    if (command.equals("sync")) {
                        bs.serverLogger.info("sync command enterned");
                        jsonCoreHandler.writeToJsonFile();
                        bs.serverLogger.info("sync command finished");
                    } else if (command.equals("run normally")) {
                        bs.serverLogger.info("The Banking server is running normally.");
                        bs.serverRunNormally();
                    } else if (command.equals("run successful test scenario")) {
                        bs.serverLogger.info("server is running in test mood (successful scenario).");
                        boolean sync = true;
                        bs.serverTestMood(sync);
                    } else if (command.equals("run unsuccessful test scenario")) {
                        bs.serverLogger.info("server is running in test mood (unsuccessful scenario).");
                        boolean sync = false;
                        bs.serverTestMood(sync);
                    } else {
                        bs.serverLogger.info("invalid command entered :  " + command);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, null, ex);
                bs.serverLogger.info(BankServer.class.getName() + " can not initiate buffer reader.");
            }
        }
    }

}
