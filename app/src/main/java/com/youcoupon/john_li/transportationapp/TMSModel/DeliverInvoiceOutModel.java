package com.youcoupon.john_li.transportationapp.TMSModel;

/**
 * Created by John_Li on 27/11/2018.
 */

public class DeliverInvoiceOutModel {
    private String customerId;
    private String customerName;
    private String salesmanId;
    private String invoiceNo;
    private String reference;
    private int status;

    private DeliverInvoiceModel deliverInvoiceModel;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        this.salesmanId = salesmanId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public DeliverInvoiceModel getDeliverInvoiceModel() {
        return deliverInvoiceModel;
    }

    public void setDeliverInvoiceModel(DeliverInvoiceModel deliverInvoiceModel) {
        this.deliverInvoiceModel = deliverInvoiceModel;
    }
}
