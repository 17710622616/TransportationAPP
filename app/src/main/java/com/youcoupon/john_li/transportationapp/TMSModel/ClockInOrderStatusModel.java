package com.youcoupon.john_li.transportationapp.TMSModel;

/**
 * Created by John_Li on 20/5/2019.
 */

public class ClockInOrderStatusModel {
    private String CustomerID;
    private String CustomerName;
    private String CustomerAddress;
    private String Contact;
    private String Telephone;
    private String OperatorID;
    private int SeqNo;
    private int Weekday;
    private boolean isClockIn;

    public boolean isClockIn() {
        return isClockIn;
    }

    public void setClockIn(boolean clockIn) {
        isClockIn = clockIn;
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

    public String getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(String operatorID) {
        OperatorID = operatorID;
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
