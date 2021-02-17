package com.youcoupon.john_li.transportationapp.TMSDBInfo;

import java.io.Serializable;
import java.util.Objects;

public class TrainsAnalysisInfo implements Serializable {
    private String merchandiseCode;
    private String merchandiseName;
    private String type;
    private String qty;

    public String getMerchandiseCode() {
        return merchandiseCode;
    }

    public void setMerchandiseCode(String merchandiseCode) {
        this.merchandiseCode = merchandiseCode;
    }

    public String getMerchandiseName() {
        return merchandiseName;
    }

    public void setMerchandiseName(String merchandiseName) {
        this.merchandiseName = merchandiseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainsAnalysisInfo that = (TrainsAnalysisInfo) o;
        return Objects.equals(merchandiseCode, that.merchandiseCode) &&
                Objects.equals(merchandiseName, that.merchandiseName) &&
                Objects.equals(type, that.type);
    }
}
