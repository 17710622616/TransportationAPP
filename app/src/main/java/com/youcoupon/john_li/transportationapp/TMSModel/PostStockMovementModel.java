package com.youcoupon.john_li.transportationapp.TMSModel;

import java.util.List;

/**
 * Created by John_Li on 6/3/2019.
 */

public class PostStockMovementModel {
    private Header header;
    private List<Line> line;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<Line> getLines() {
        return line;
    }

    public void setLines(List<Line> line) {
        this.line = line;
    }

    public static class  Header {
        private String SalesmanID;
        private String Reference;
        private int TruckNo;

        public String getSalesmanID() {
            return SalesmanID;
        }

        public void setSalesmanID(String salesmanID) {
            SalesmanID = salesmanID;
        }

        public String getReference() {
            return Reference;
        }

        public void setReference(String reference) {
            Reference = reference;
        }

        public int getTruckNo() {
            return TruckNo;
        }

        public void setTruckNo(int truckNo) {
            TruckNo = truckNo;
        }
    }

    public static class Line {
        private String MerchandiseID;
        private int Quantity;

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
    }
}
