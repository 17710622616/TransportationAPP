package com.youcoupon.john_li.transportationapp.TMSDBInfo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by John_Li on 14/5/2019.
 */

@Table(name = "clock_in_photo_tb",onCreated = "")
public class ClockInPhotoInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "_id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "image_url")
    private String ImageUrl;
    @Column(name = "time")
    private String Time;
    @Column(name = "remark")
    private String Remark;
    @Column(name = "customer_id")
    private String CustomerID;
    @Column(name = "type")
    private String Type;
    @Column(name = "status")
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
