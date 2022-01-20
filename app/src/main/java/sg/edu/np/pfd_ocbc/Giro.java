package sg.edu.np.pfd_ocbc;

import java.util.Date;

public class Giro {
    private int giro_id;
    private int biz_id;
    private String biz_name;
    private String giro_acc_no;
    private Date giro_date;
    private String description;
    private boolean verified;
    private double giro_amount;

    public int getGiro_id() {
        return giro_id;
    }

    public void setGiro_id(int giro_id) {
        this.giro_id = giro_id;
    }

    public int getBiz_id() {
        return biz_id;
    }

    public void setBiz_id(int biz_id) {
        this.biz_id = biz_id;
    }

    public String getGiro_acc_no() {
        return giro_acc_no;
    }

    public void setGiro_acc_no(String giro_acc_no) {
        this.giro_acc_no = giro_acc_no;
    }

    public Date getGiro_date() {
        return giro_date;
    }

    public void setGiro_date(Date giro_date) {
        this.giro_date = giro_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public double getGiro_amount() {
        return giro_amount;
    }

    public void setGiro_amount(double giro_amount) {
        this.giro_amount = giro_amount;
    }

    public String getBiz_name() { return biz_name; }

    public void setBiz_name(String biz_name) { this.biz_name = biz_name; }

    public Giro() {
    }
}
