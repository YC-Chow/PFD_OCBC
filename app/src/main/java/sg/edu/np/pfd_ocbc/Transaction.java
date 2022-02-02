package sg.edu.np.pfd_ocbc;

import java.util.Date;

public class Transaction {
    private String senderAccNo;
    private String senderName;
    private String recipientName;
    private String recipientAccNo;
    private Double transactionAmt;
    private String transactionDate;
    private String transactionId;
    private String DebitOrCredit;
    private String uniqueCode;

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    private long hours;



    public String getUniqueCode() { return uniqueCode; }

    public void setUniqueCode(String uniqueCode) { this.uniqueCode = uniqueCode; }

    public String getSenderAccNo() { return senderAccNo; }

    public void setSenderAccNo(String senderAccNo) { this.senderAccNo = senderAccNo; }

    public String getDebitOrCredit() {
        return DebitOrCredit;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
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

    public String getRecipientAccNo() {
        return recipientAccNo;
    }

    public void setRecipientAccNo(String recipientAccNo) {
        this.recipientAccNo = recipientAccNo;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public Transaction() {
    }

}
