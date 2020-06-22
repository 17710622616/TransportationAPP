package com.youcoupon.john_li.transportationapp.TMSModel;

import java.util.List;

public class InvoiceViewModel {
    private InviceHeader Header;
    private List<InvoiceLine> LineList;

    public InviceHeader getHeader() {
        return Header;
    }

    public void setHeader(InviceHeader header) {
        Header = header;
    }

    public List<InvoiceLine> getLineList() {
        return LineList;
    }

    public void setLineList(List<InvoiceLine> lineList) {
        LineList = lineList;
    }

    public class InviceHeader
    {
        private String InvoiceNo;
        private String CustomerID;
        private String Remark;

        public String getInvoiceNo() {
            return InvoiceNo;
        }

        public void setInvoiceNo(String invoiceNo) {
            InvoiceNo = invoiceNo;
        }

        public String getCustomerID() {
            return CustomerID;
        }

        public void setCustomerID(String customerID) {
            CustomerID = customerID;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
        }
    }
    public class InvoiceLine {
        private String MerchandiseID;
        private int Quantity;
        private double Price;
        private int Packing;
        private int SeqNo;

        public String getMerchandiseID() {
            return MerchandiseID;
        }

        public void setMerchandiseID(String merchandiseID) {
            MerchandiseID = merchandiseID;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int quantity) {
            Quantity = quantity;
        }

        public double getPrice() {
            return Price;
        }

        public void setPrice(double price) {
            Price = price;
        }

        public int getPacking() {
            return Packing;
        }

        public void setPacking(int packing) {
            Packing = packing;
        }

        public int getSeqNo() {
            return SeqNo;
        }

        public void setSeqNo(int seqNo) {
            SeqNo = seqNo;
        }
    }
}
