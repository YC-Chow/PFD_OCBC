package sg.edu.np.pfd_ocbc;

import java.util.Date;

public class Card {
    private String nameOnCard;
    private String cardNo;
    private String cvv;
    private Date expiryDate;
    private String accountNum;
    private String IssuingNetwork;
    private Double cardBal;


    public String getCardNo() {
        return cardNo;
    }

    public String getAccountNum() {return accountNum;}

    public String getIssuingNetwork() { return IssuingNetwork;}

    public Double getCardBal() {return cardBal; }

    public String getCvv() {
        return cvv;
    }

    public Date getExpiryDate() {
        return expiryDate;
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

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public void setCardBal(Double cardBal) {
        this.cardBal = cardBal;
    }

    public void setIssuingNetwork(String issuingNetwork) {
        IssuingNetwork = issuingNetwork;
    }

    public Card() {
    }

    public Card(String cardNo,String accNum,String IssuingNetwork,Double cardBal) {
        this.cardNo = cardNo;
        this.accountNum = accNum;
        this.IssuingNetwork = IssuingNetwork;
        this.cardBal = cardBal;
    }
    @Override
    public String toString() {
        return cardNo;
    }
}
