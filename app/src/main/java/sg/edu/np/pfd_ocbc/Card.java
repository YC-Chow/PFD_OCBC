package sg.edu.np.pfd_ocbc;

import java.util.Date;
import java.io.Serializable;

public class Card implements Serializable{
    private String nameOnCard;
    private String cardNo;
    private String issuingNetwork;
    private Double balance;
    private String accNo;

    public Card() {

    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
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

    }

    public Card(String cardNo, String nameOnCard, String issuingNetwork, Double balance, String accNo) {
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
