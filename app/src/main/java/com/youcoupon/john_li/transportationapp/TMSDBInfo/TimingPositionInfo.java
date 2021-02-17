package com.youcoupon.john_li.transportationapp.TMSDBInfo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "timing_position_tb",onCreated = "")
public class TimingPositionInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "time_id",isId = true,autoGen = true,property = "NOT NULL")
    private int _id;
    @Column(name = "userid")
    private String userid;
    @Column(name = "longtitude")
    private String longtitude;
    @Column(name = "lantitude")
    private String lantitude;
    @Column(name = "deviceNo")
    private String deviceNo;
    @Column(name = "creatTime")
    private String creatTime;
    @Column(name = "positionTime")
    private String positionTime;
    // 状态，0未提交，1提交成功，2提交失败
    @Column(name = "status")
    private int status;

    public String getPositionTime() {
        return positionTime;
    }

    public void setPositionTime(String positionTime) {
        this.positionTime = positionTime;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLantitude() {
        return lantitude;
    }

    public void setLantitude(String lantitude) {
        this.lantitude = lantitude;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
