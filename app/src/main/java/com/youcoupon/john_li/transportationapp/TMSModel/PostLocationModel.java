package com.youcoupon.john_li.transportationapp.TMSModel;

public class PostLocationModel {
    private double Long;
    private double Lat;
    private String CreateDate;

    public PostLocationModel(double aLong, double lat, String createDate) {
        Long = aLong;
        Lat = lat;
        CreateDate = createDate;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }
}
