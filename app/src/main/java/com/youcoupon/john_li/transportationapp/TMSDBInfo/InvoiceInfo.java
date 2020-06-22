package com.youcoupon.john_li.transportationapp.TMSDBInfo;

/**
 * Created by John_Li on 26/11/2018.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * onCreated = "sql"：当第一次创建表需要插入数据时候在此写sql语句
 */
@Table(name = "invoice_tb",onCreated = "")
public class InvoiceInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "invoiceID",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "customer_id")
    private String CustomerID;
    @Column(name = "invoice_no")
    private String InvoiceNo;
    @Column(name = "remark")
    private String remark;
    @Column(name = "lines")
    private String lines;

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

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public InvoiceInfo() {
    }

    @Override
    public String toString() {
        return "customer_tb{"+"id="+id+",CustomerID='"+CustomerID+",InvoiceNo='"+InvoiceNo+'\''+'}';
    }
}
