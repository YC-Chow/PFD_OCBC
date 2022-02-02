package sg.edu.np.pfd_ocbc;

public class Business {
    private String uen;
    private  String name;
    private String acc_no;


    public String getUen() {
        return uen;
    }

    public void setUen(String uen) {
        this.uen = uen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcc_no() {
        return acc_no;
    }

    public void setAcc_no(String acc_no) {
        this.acc_no = acc_no;
    }

    public Business() {
    }

    public Business(String uen, String name, String acc_no) {
        this.uen = uen;
        this.name = name;
        this.acc_no = acc_no;
    }
}
