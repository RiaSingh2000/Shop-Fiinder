package Models;

public class BuyerList {
    String name,buid,phone;

    public BuyerList(String name, String buid,String phone) {
        this.name = name;
        this.buid = buid;
        this.phone=phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getBuid() {
        return buid;
    }
}
