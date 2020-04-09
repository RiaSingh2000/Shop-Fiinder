package Models;

public class mSellerProfile { //obj of this class is used when creating sellers profile

    private String selname;
    private String shopname;
    private String custcare;
    private String loc;
    private String lat;
    private String lng;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getSelname() {
        return selname;
    }

    public void setSelname(String selname) {
        this.selname = selname;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getCustcare() {
        return custcare;
    }

    public void setCustcare(String custcare) {
        this.custcare = custcare;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
