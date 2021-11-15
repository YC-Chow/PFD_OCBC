package sg.edu.np.pfd_ocbc;

import java.util.Date;

public class Transaction {
    private String senderAccNo;
    private String senderName;
    private String recipientName;
    private String recipientAccNo;
    private String toPhoneNum;
    private String toIC;
    private String toBankNum;
    private Double transactionAmt;
    private String transactionDate;
    private String transactionId;
    private String DebitOrCredit;
    private String ReceivedOrSent;

    public String getSenderAccNo() { return senderAccNo; }

    public void setSenderAccNo(String senderAccNo) { this.senderAccNo = senderAccNo; }

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

    public String getReceivedOrSent() {
        return ReceivedOrSent;
    }

    public void setReceivedOrSent(String receivedOrSent) {
        ReceivedOrSent = receivedOrSent;
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
