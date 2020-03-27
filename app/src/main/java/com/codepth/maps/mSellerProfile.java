package com.codepth.maps;

public class mSellerProfile { //obj of this class is used when creating sellers profile
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    private String sellerId;
    private String sellerName;
    private String shopName;
    private String sellerPhone;
    private String sellerLocality;

    public String getSellerLat() {
        return sellerLat;
    }

    public void setSellerLat(String sellerLat) {
        this.sellerLat = sellerLat;
    }

    public String getSellerLong() {
        return sellerLong;
    }

    public void setSellerLong(String sellerLong) {
        this.sellerLong = sellerLong;
    }

    private String sellerLat;
    private String sellerLong;

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerLocality() {
        return sellerLocality;
    }

    public void setSellerLocality(String sellerLocality) {
        this.sellerLocality = sellerLocality;
    }
}
