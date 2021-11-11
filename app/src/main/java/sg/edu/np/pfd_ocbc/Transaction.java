package sg.edu.np.pfd_ocbc;

import java.util.Date;

public class Transaction {
    private String toPhoneNum;
    private String toIC;
    private String toBankNum;
    private Double transactionAmt;
    private String transactionDate;
    private String transactionId;
    private String DebitOrCredit;

    public String getToPhoneNum() {
        return toPhoneNum;
    }

    public String getDebitOrCredit() {
        return DebitOrCredit;
    }

    public String getToIC() {
        return toIC;
    }

    public String getToBankNum() {
        return toBankNum;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setToPhoneNum(String toPhoneNum) {
        this.toPhoneNum = toPhoneNum;
    }

    public void setToIC(String toIC) {
        this.toIC = toIC;
    }

    public void setToBankNum(String toBankNum) {
        this.toBankNum = toBankNum;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getTransactionAmt() { return transactionAmt; }

    public void setTransactionAmt(Double transactionAmt) { this.transactionAmt = transactionAmt; }

    public void setDebitOrCredit(String debitOrCredit) {
        DebitOrCredit = debitOrCredit;
    }

    public Transaction() {
    }


    public Transaction(String toPhoneNum, String transactionDate, String transactionId, Double transactionAmt) {
        this.toPhoneNum = toPhoneNum;
        this.transactionDate = transactionDate;
        this.transactionId = transactionId;
        this.transactionAmt = transactionAmt;
    }
}
