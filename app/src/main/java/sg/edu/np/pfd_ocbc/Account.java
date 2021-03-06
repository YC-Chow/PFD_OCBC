package sg.edu.np.pfd_ocbc;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

public class Account implements Serializable{
    private String email;
    private String icNo;
    private String name;
    private Date startDate;
    private String phoneNo;
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

    public String getphoneNo() {
        return phoneNo;
    }

    public void setphoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public Account() {
    }


    public Account(String email, String icNo, String name, Date startDate, String phoneNo,ArrayList<Card> cardList) {
        this.email = email;
        this.icNo = icNo;
        this.name = name;
        this.startDate = startDate;
        this.phoneNo = phoneNo;
        this.card = card;
    }

}
