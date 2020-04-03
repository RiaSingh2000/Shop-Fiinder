package com.codepth.maps;

public class mShops {
    private String name;
    private String longitude;
    private String latitude;

    public void setuId(String uId) {
        this.uId = uId;
    }

    private String uId;
    public String getuId() {
        return uId;
    }

    public void setLatitude(String mLatitude) {
        this.latitude = mLatitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String mLongitude) {
        this.longitude = mLongitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
