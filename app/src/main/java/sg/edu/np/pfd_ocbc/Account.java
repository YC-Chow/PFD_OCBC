package sg.edu.np.pfd_ocbc;

import java.util.ArrayList;
import java.util.Date;

public class Account {
    private String email;
    private String icNo;
    private String name;
    private Date startDate;
    private String uid;
    private Card card;
    private ArrayList<Transaction> transactions;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcNo() {
        return icNo;
    }

    public void setIcNo(String icNo) {
        this.icNo = icNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Account() {
    }

    public Account(String email, String icNo, String name, Date startDate, String uid, Card card) {
        this.email = email;
        this.icNo = icNo;
        this.name = name;
        this.startDate = startDate;
        this.uid = uid;
        this.card = card;
    }
}
