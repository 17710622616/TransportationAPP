package com.youcoupon.john_li.transportationapp.TMSDBInfo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by John_Li on 14/5/2019.
 */

@Table(name = "clock_in_customer_tb",onCreated = "")
public class ClockInCustomerInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "_id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "customer_id")
    private String CustomerID;
    @Column(name = "customer_name")
    private String CustomerName;
    @Column(name = "customer_address")
    private String CustomerAddress;
    @Column(name = "contact")
    private String Contact;
    @Column(name = "telephone")
    private String Telephone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }
}
