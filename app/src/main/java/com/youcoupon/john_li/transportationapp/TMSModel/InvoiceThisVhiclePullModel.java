package com.youcoupon.john_li.transportationapp.TMSModel;

import java.util.ArrayList;
import java.util.List;

public class InvoiceThisVhiclePullModel {
    /**
     * Header : {"CustomerID":"000119000","CustomerName":"田記","InvoiceNo":"1980476","Reference":"CD880000148320200914144426","DriverID":"D340","OperationID":"H574"}
     * Line : [{"MerchandiseID":"014","MerchandiseName":"飛雪5加侖吉膠桶","Quantity":-1}]
     */

    private HeaderBean Header;
    private List<LineBean> Line;

    public HeaderBean getHeader() {
        return Header;
    }

    public void setHeader(HeaderBean Header) {
        this.Header = Header;
    }

    public List<LineBean> getLine() {
        return Line;
    }

    public void setLine(List<LineBean> Line) {
        this.Line = Line;
    }

    public static class HeaderBean {
        /**
         * CustomerID : 000119000
         * CustomerName : 田記
         * InvoiceNo : 1980476
         * Reference : CD880000148320200914144426
         * DriverID : D340
         * OperationID : H574
         */

        private String CustomerID;
        private String CustomerName;
        private String InvoiceNo;
        private String Reference;
        private String DriverID;
        private String OperationID;

        public String getCustomerID() {
            return CustomerID;
        }

        public void setCustomerID(String CustomerID) {
            this.CustomerID = CustomerID;
        }

        public String getCustomerName() {
            return CustomerName;
        }

        public void setCustomerName(String CustomerName) {
            this.CustomerName = CustomerName;
        }

        public String getInvoiceNo() {
            return InvoiceNo;
        }

        public void setInvoiceNo(String InvoiceNo) {
            this.InvoiceNo = InvoiceNo;
        }

        public String getReference() {
            return Reference;
        }

        public void setReference(String Reference) {
            this.Reference = Reference;
        }

        public String getDriverID() {
            return DriverID;
        }

        public void setDriverID(String DriverID) {
            this.DriverID = DriverID;
        }

        public String getOperationID() {
            return OperationID;
        }

        public void setOperationID(String OperationID) {
            this.OperationID = OperationID;
        }
    }

    public static class LineBean {
        /**
         * MerchandiseID : 014
         * MerchandiseName : 飛雪5加侖吉膠桶
         * Quantity : -1
         */

        private String MerchandiseID;
        private String MerchandiseName;
        private int Quantity;

        public String getMerchandiseID() {
            return MerchandiseID;
        }

        public void setMerchandiseID(String MerchandiseID) {
            this.MerchandiseID = MerchandiseID;
        }

        public String getMerchandiseName() {
            return MerchandiseName;
        }

        public void setMerchandiseName(String MerchandiseName) {
            this.MerchandiseName = MerchandiseName;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int Quantity) {
            this.Quantity = Quantity;
        }
    }

    /**
     * 將獲取到的發票表體轉為數據庫樣式
     * @param line
     * @return
     */
    public List<DeliverInvoiceModel> transModelToDB(List<LineBean> line) {
        List<DeliverInvoiceModel> list = new ArrayList<>();
        for (LineBean bean: line) {
            DeliverInvoiceModel model = new DeliverInvoiceModel();
            model.setMaterialId(bean.MerchandiseID);
            model.setMaterialName(bean.MerchandiseName);
            if (bean.getQuantity() > 0) {
                model.setSendOutNum(bean.getQuantity());
            } else {
                model.setRecycleNum(bean.getQuantity() * -1);

            }
            list.add(model);
        }
        return list;
    }
}
