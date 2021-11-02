package sg.edu.np.pfd_ocbc;

import java.util.Date;

public class Card {
    private String nameOnCard;
    private String cardNo;
    private String cvv;
    private Date expiryDate;


    public String getCardNo() {
        return cardNo;
    }

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

    public Card() {
    }

    public Card(String nameOnCard, String cardNo, String cvv, Date expiryDate) {
        this.nameOnCard = nameOnCard;
        this.cardNo = cardNo;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }
}
