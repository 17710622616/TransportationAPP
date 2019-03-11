package com.youcoupon.john_li.transportationapp.TMSDBInfo;

/**
 * Created by John_Li on 26/11/2018.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * onCreated = "sql"：当第一次创建表需要插入数据时候在此写sql语句
 */
@Table(name = "trains_tb",onCreated = "")
public class TrainsInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "trains_id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "trains_times")
    private int TrainsTimes;
    @Column(name = "today_date")
    private String TodayDate;
    @Column(name = "today_refund_body")
    private String TodayRefundBody;
    @Column(name = "today_deposit_body")
    private String TodayDepositBody;
    // 0未提交，1提交成功，2提交失敗
    @Column(name = "today_deposit_status")
    private int TodayDepositStatus;
    @Column(name = "today_refund_status")
    private int TodayRefundStatus;

    public int getTodayDepositStatus() {
        return TodayDepositStatus;
    }

    public void setTodayDepositStatus(int todayDepositStatus) {
        TodayDepositStatus = todayDepositStatus;
    }

    public int getTodayRefundStatus() {
        return TodayRefundStatus;
    }

    public void setTodayRefundStatus(int todayRefundStatus) {
        TodayRefundStatus = todayRefundStatus;
    }

    public String getTodayRefundBody() {
        return TodayRefundBody;
    }

    public void setTodayRefundBody(String todayRefundBody) {
        TodayRefundBody = todayRefundBody;
    }

    public String getTodayDepositBody() {
        return TodayDepositBody;
    }

    public void setTodayDepositBody(String todayDepositBody) {
        TodayDepositBody = todayDepositBody;
    }

    public String getTodayDate() {
        return TodayDate;
    }

    public void setTodayDate(String todayDate) {
        TodayDate = todayDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainsTimes() {
        return TrainsTimes;
    }

    public void setTrainsTimes(int trainsTimes) {
        TrainsTimes = trainsTimes;
    }

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public TrainsInfo() {
    }

    @Override
    public String toString() {
        return "submit_invoice_tb{"+"id="+id+'}';
    }
}
