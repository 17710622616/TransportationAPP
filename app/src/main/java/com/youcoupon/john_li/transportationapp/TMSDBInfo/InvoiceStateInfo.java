package com.youcoupon.john_li.transportationapp.TMSDBInfo;

/**
 * Created by John_Li on 26/11/2018.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * onCreated = "sql"：当第一次创建表需要插入数据时候在此写sql语句
 */
@Table(name = "invoice_state_tb",onCreated = "")
public class InvoiceStateInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "invoiceStateID",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "corp")
    private String Corp;
    @Column(name = "user_id")
    private String UserID;
    @Column(name = "user_name")
    private String UserName;
    @Column(name = "bill_no")
    private String BillNo;
    @Column(name = "static_type")
    private String StaticType;
    @Column(name = "static_code")
    private String StaticCode;
    @Column(name = "resultReson")
    private String result_reason;
    // 0提交中，1提交成功，2提交失败
    @Column(name = "status")
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorp() {
        return Corp;
    }

    public void setCorp(String corp) {
        Corp = corp;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBillNo() {
        return BillNo;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    public String getStaticType() {
        return StaticType;
    }

    public void setStaticType(String staticType) {
        StaticType = staticType;
    }

    public String getStaticCode() {
        return StaticCode;
    }

    public void setStaticCode(String staticCode) {
        StaticCode = staticCode;
    }

    public String getResult_reason() {
        return result_reason;
    }

    public void setResult_reason(String result_reason) {
        this.result_reason = result_reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public InvoiceStateInfo() {
    }

    @Override
    public String toString() {
        return "invoice_state_tb{"+"id="+id+",Corp='"+Corp+",UserID='"+UserID+",BillNo='"+BillNo+",StaticCode='"+StaticCode+",StaticType='"+StaticType+",status='"+status+'\''+'}';
    }
}
