
package server;

import utility.Transaction;

/**
 *
 * @author Hedieh Jam
 */
public class TransactionExecuter {
    private Transaction transaction;
    private String originTerminal;
    public TransactionExecuter(String clientMessage) {
        String[] parts = clientMessage.split(";");
        transaction = new Transaction();
        transaction.setDepositId(parts[0]);
        transaction.setType(parts[1]);
        transaction.setAmount(parts[2]);
        transaction.setTransactionId(parts[3]);
        originTerminal = parts[5] + ":"+ parts[4];
        
    }
    public String execute(boolean sync){
        if(transaction.getType().equals("deposit")){
            String operationResult = Deposit.depositIn(transaction.getDepositId(), transaction.getAmount());
            operationResult = attachTransactionId(transaction) + operationResult;
            return operationResult;
        }
        else if(transaction.getType().equals("withdraw")){
            String operationResult = Deposit.withdraw(transaction.getDepositId(), transaction.getAmount(),sync);
            operationResult = attachTransactionId(transaction) + operationResult;
            return operationResult;            
        }
        return "invalid command\r\n";
    }
    public String getOriginTerminal(){
        return originTerminal;
    }
    private String attachTransactionId(Transaction tr){
        return " transaction Id : " + tr.getTransactionId() + ";" +" type : " + transaction.getType() + ";";
    }
    
    
}
