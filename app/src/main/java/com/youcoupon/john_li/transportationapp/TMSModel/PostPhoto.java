package com.youcoupon.john_li.transportationapp.TMSModel;

/**
 * Created by John_Li on 21/5/2019.
 */

public class PostPhoto {
    public String imageType;
    //public byte[] photo;
    public String photo;
    public String photoTime;
    public String remark;

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(String photoTime) {
        this.photoTime = photoTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
