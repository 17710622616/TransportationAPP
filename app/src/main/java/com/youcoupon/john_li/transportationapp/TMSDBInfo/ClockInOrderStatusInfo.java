package com.youcoupon.john_li.transportationapp.TMSDBInfo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by John_Li on 14/5/2019.
 */

@Table(name = "clock_in_order_status_tb",onCreated = "")
public class ClockInOrderStatusInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "_id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "operatorID")
    private String OperatorID;
    @Column(name = "customer_id")
    private String CustomerID;
    @Column(name = "seq_no")
    private int SeqNo;
    @Column(name = "weekday")
    private int Weekday;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(String operatorID) {
        OperatorID = operatorID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public int getSeqNo() {
        return SeqNo;
    }

    public void setSeqNo(int seqNo) {
        SeqNo = seqNo;
    }

    public int getWeekday() {
        return Weekday;
    }

    public void setWeekday(int weekday) {
        Weekday = weekday;
    }
}
