package com.youcoupon.john_li.transportationapp.TMSModel;

import java.util.List;
import java.util.Objects;

public class CarSplitInvoiceVM {
    private Header header;
    private List<Line> line;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<Line> getLine() {
        return line;
    }

    public void setLine(List<Line> line) {
        this.line = line;
    }

    public static class Header {
        private long InvoiceNo;
        private String InvoiceDate;
        private char DocumentType;
        private String District;
        private String CustomerID;
        private String CustomerName;
        private String CustomerAddress;
        private String Contact;
        private int TruckNo;
        private String TruckID;
        private String DriverID;
        private double Amount;
        private int DeliveryTime;
        private String PresellerID;
        private String PresellerTel;
        private String ShipConfirmedDate;

        public long getInvoiceNo() {
            return InvoiceNo;
        }

        public void setInvoiceNo(long invoiceNo) {
            InvoiceNo = invoiceNo;
        }

        public String getInvoiceDate() {
            return InvoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            InvoiceDate = invoiceDate;
        }

        public char getDocumentType() {
            return DocumentType;
        }

        public void setDocumentType(char documentType) {
            DocumentType = documentType;
        }

        public String getDistrict() {
            return District;
        }

        public void setDistrict(String district) {
            District = district;
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

        public int getTruckNo() {
            return TruckNo;
        }

        public void setTruckNo(int truckNo) {
            TruckNo = truckNo;
        }

        public String getTruckID() {
            return TruckID;
        }

        public void setTruckID(String truckID) {
            TruckID = truckID;
        }

        public String getDriverID() {
            return DriverID;
        }

        public void setDriverID(String driverID) {
            DriverID = driverID;
        }

        public double getAmount() {
            return Amount;
        }

        public void setAmount(double amount) {
            Amount = amount;
        }

        public int getDeliveryTime() {
            return DeliveryTime;
        }

        public void setDeliveryTime(int deliveryTime) {
            DeliveryTime = deliveryTime;
        }

        public String getPresellerID() {
            return PresellerID;
        }

        public void setPresellerID(String presellerID) {
            PresellerID = presellerID;
        }

        public String getPresellerTel() {
            return PresellerTel;
        }

        public void setPresellerTel(String presellerTel) {
            PresellerTel = presellerTel;
        }

        public String getShipConfirmedDate() {
            return ShipConfirmedDate;
        }

        public void setShipConfirmedDate(String shipConfirmedDate) {
            ShipConfirmedDate = shipConfirmedDate;
        }
    }
    public class Line {
        private String MerchandiseID;
        private String MerchandiseName;
        private int Qty;
        private double Price;
        private char SalesType;
        private String BrandName;
        private String PackageName;
        private String LoadingClassifyID;
        private String LoadingclassifyName;
        private long Packing;

        public long getPacking() {
            return Packing;
        }

        public void setPacking(long packing) {
            Packing = packing;
        }

        public String getMerchandiseID() {
            return MerchandiseID;
        }

        public void setMerchandiseID(String merchandiseID) {
            MerchandiseID = merchandiseID;
        }

        public String getMerchandiseName() {
            return MerchandiseName;
        }

        public void setMerchandiseName(String merchandiseName) {
            MerchandiseName = merchandiseName;
        }

        public int getQty() {
            return Qty;
        }

        public void setQty(int qty) {
            Qty = qty;
        }

        public double getPrice() {
            return Price;
        }

        public void setPrice(double price) {
            Price = price;
        }

        public char getSalesType() {
            return SalesType;
        }

        public void setSalesType(char salesType) {
            SalesType = salesType;
        }

        public String getBrandName() {
            return BrandName;
        }

        public void setBrandName(String brandName) {
            BrandName = brandName;
        }

        public String getPackageName() {
            return PackageName;
        }

        public void setPackageName(String packageName) {
            PackageName = packageName;
        }

        public String getLoadingClassifyID() {
            return LoadingClassifyID;
        }

        public void setLoadingClassifyID(String loadingClassifyID) {
            LoadingClassifyID = loadingClassifyID;
        }

        public String getLoadingclassifyName() {
            return LoadingclassifyName;
        }

        public void setLoadingclassifyName(String loadingclassifyName) {
            LoadingclassifyName = loadingclassifyName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarSplitInvoiceVM vm = (CarSplitInvoiceVM) o;
        if (vm.header == null) return false;
        return  header.getInvoiceNo() == vm.getHeader().getInvoiceNo();
    }
}
