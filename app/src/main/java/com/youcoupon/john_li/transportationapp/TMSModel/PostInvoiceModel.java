package com.youcoupon.john_li.transportationapp.TMSModel;

import java.util.List;

/**
 * Created by John on 29/11/2018.
 */

public class PostInvoiceModel {
    /**
     * Header : {"SalesmanID":"D307","Reference":"Test002","CustomerID":"006932008"}
     * Line : [{"MerchandiseID":"013A","Quantity":-5}]
     */

    private Header Header;
    private List<Line> Line;

    public Header getHeader() {
        return Header;
    }

    public void setHeader(Header Header) {
        this.Header = Header;
    }

    public List<Line> getLine() {
        return Line;
    }

    public void setLine(List<Line> Line) {
        this.Line = Line;
    }

    public static class Header {
        /**
         * SalesmanID : D307
         * Reference : Test002
         * CustomerID : 006932008
         */

        private String SalesmanID;
        private String Reference;
        private String CustomerID;

        public String getSalesmanID() {
            return SalesmanID;
        }

        public void setSalesmanID(String SalesmanID) {
            this.SalesmanID = SalesmanID;
        }

        public String getReference() {
            return Reference;
        }

        public void setReference(String Reference) {
            this.Reference = Reference;
        }

        public String getCustomerID() {
            return CustomerID;
        }

        public void setCustomerID(String CustomerID) {
            this.CustomerID = CustomerID;
        }
    }

    public static class Line {
        /**
         * MerchandiseID : 013A
         * Quantity : -5
         */

        private String MerchandiseID;
        private int Quantity;

        public String getMerchandiseID() {
            return MerchandiseID;
        }

        public void setMerchandiseID(String MerchandiseID) {
            this.MerchandiseID = MerchandiseID;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int Quantity) {
            this.Quantity = Quantity;
        }
    }
}
