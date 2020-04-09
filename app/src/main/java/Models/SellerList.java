package Models;

public class SellerList {
    String name,ph,desc,uid;

    public String getName() {
        return name;
    }

    public String getPh() {
        return ph;
    }

    public String getDesc() {
        return desc;
    }

    public String getUid() {
        return uid;
    }

    public SellerList(String name, String ph, String desc, String uid) {
        this.name = name;
        this.ph = ph;
        this.desc = desc;
        this.uid=uid;
    }
}
