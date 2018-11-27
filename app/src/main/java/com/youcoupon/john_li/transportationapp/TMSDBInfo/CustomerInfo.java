package com.youcoupon.john_li.transportationapp.TMSDBInfo;

/**
 * Created by John_Li on 26/11/2018.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * onCreated = "sql"：当第一次创建表需要插入数据时候在此写sql语句
 */
@Table(name = "customer_tb",onCreated = "")
public class CustomerInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "customerID",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "customer_id")
    private String CustomerID;
    @Column(name = "customer_name")
    private String CustomerName;
    @Column(name = "customer_address")
    private String CustomerAddress;
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

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public CustomerInfo() {
    }

    @Override
    public String toString() {
        return "customer_tb{"+"id="+id+",customerID='"+CustomerID+",CustomerID='"+CustomerName+",customerAddress='"+CustomerAddress+",telephone='"+Telephone+'\''+'}';
    }
}
