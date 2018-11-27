package com.youcoupon.john_li.transportationapp.TMSDBInfo;

/**
 * Created by John_Li on 26/11/2018.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * onCreated = "sql"：当第一次创建表需要插入数据时候在此写sql语句
 */
@Table(name = "submit_invoice_tb",onCreated = "")
public class SubmitInvoiceInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "submit_invoice_id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "customer_id")
    private String CustomerID;
    @Column(name = "invoice_no")
    private String InvoiceNo;
    @Column(name = "refrence")
    private String Refrence;
    @Column(name = "salesman_id")
    private String SalesmanId;
    @Column(name = "order_body")
    private String OrderBody;
    @Column(name = "status")
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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

    public String getRefrence() {
        return Refrence;
    }

    public void setRefrence(String refrence) {
        Refrence = refrence;
    }

    public String getSalesmanId() {
        return SalesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        SalesmanId = salesmanId;
    }

    public String getOrderBody() {
        return OrderBody;
    }

    public void setOrderBody(String orderBody) {
        OrderBody = orderBody;
    }

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public SubmitInvoiceInfo() {
    }

    @Override
    public String toString() {
        return "submit_invoice_tb{"+"id="+id+",CustomerID='"+CustomerID+",InvoiceNo='"+InvoiceNo+",Refrence='"+Refrence+",SalesmanId='"+SalesmanId+",OrderBody='"+OrderBody+'\''+'}';
    }
}
