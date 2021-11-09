package sg.edu.np.pfd_ocbc;

import java.util.Date;

public class Transaction {
    private String toPhoneNum;
    private String toIC;
    private String toBankNum;
    private double transactionAmt;
    private Date transactionDate;
    private String transactionId;

    public String getToPhoneNum() {
        return toPhoneNum;
    }

    public String getToIC() {
        return toIC;
    }

    public String getToBankNum() {
        return toBankNum;
    }

    public Date getTransactionDate() {
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

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getTransactionAmt() { return transactionAmt; }

    public void setTransactionAmt(double transactionAmt) { this.transactionAmt = transactionAmt; }

    public Transaction() {
    }

    public Transaction(String toPhoneNum, Date transactionDate, String transactionId, int transactionAmt) {
        this.toPhoneNum = toPhoneNum;
        this.transactionDate = transactionDate;
        this.transactionId = transactionId;
        this.transactionAmt = transactionAmt;
    }




}
