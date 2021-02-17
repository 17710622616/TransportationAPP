package com.youcoupon.john_li.transportationapp.TMSDBInfo;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

//@SmartTable(name="展示發票表")
public class CarSplitInvoiceInfo {
    //@SmartColumn(id =1, name = "發票號")
    private String invoiceNo;
    //@SmartColumn(id =2, name = "發票類型")
    private String invoiceType;
    //@SmartColumn(id =3,name = "數量")
    private String qty;
    //@SmartColumn(id =4,name = "客戶名")
    private String CustomerName;
    //@SmartColumn(id =5,name = "客戶地址")
    private String CustomerAddress;
    //@SmartColumn(id =6,name = "用戶記錄是否被選中")
    private Boolean operation;
    //@SmartColumn(id =7,name = "区域")
    private String district;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
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
}
