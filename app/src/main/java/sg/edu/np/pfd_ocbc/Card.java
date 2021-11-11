package sg.edu.np.pfd_ocbc;

import java.util.Date;
import java.io.Serializable;

public class Card implements Serializable{
    private String nameOnCard;
    private String cardNo;
    private String issuingNetwork;
    private String balance;
    private  String accNo;

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getIssuingNetwork() {
        return issuingNetwork;
    }

    public void setIssuingNetwork(String issuingNetwork) {
        this.issuingNetwork = issuingNetwork;
    }

    public String getCardNo() {
        return cardNo;
    }



    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }



    public Card(String cardNo) {
        this.cardNo = cardNo;
    }

    public Card(String cardNo, String nameOnCard, String issuingNetwork, String balance, String accNo) {
        this.cardNo = cardNo;
        this.nameOnCard = nameOnCard;
        this.issuingNetwork = issuingNetwork;
        this.balance = balance;
        this.accNo = accNo;
    }
    @Override
    public String toString() {
        return cardNo;
    }
}
