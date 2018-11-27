package com.youcoupon.john_li.transportationapp.TMSModel;

/**
 * Created by John_Li on 26/11/2018.
 */

public class DeliverInvoiceModel {
    private int materialId;
    private String materialName;
    private int sendOutNum;
    private int recycleNum;

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getSendOutNum() {
        return sendOutNum;
    }

    public void setSendOutNum(int sendOutNum) {
        this.sendOutNum = sendOutNum;
    }

    public int getRecycleNum() {
        return recycleNum;
    }

    public void setRecycleNum(int recycleNum) {
        this.recycleNum = recycleNum;
    }
}
