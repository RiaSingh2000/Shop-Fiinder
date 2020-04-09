package Models;

public class mBuyerProfile {

        private String name;
        private String phone;
        private String Street;
        private String Locality;
        private String House;
        private String uid;

    public void setName(String name) {
        this.name = name;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public void setStreet(String Street) {
        this.Street = Street;
    }

    public void setLocaity(String Locality) {
        this.Locality = Locality;
    }

    public void setHouse(String House) {
        this.House = House;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getphone() {
        return phone;
    }

    public String getStreet() {
        return Street;
    }

    public String getLocality() {
        return Locality;
    }

    public String getHouse() {
        return House;
    }

    public String getUid() {
        return uid;
    }
}
