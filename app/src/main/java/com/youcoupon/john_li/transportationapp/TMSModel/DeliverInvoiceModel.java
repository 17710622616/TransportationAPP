package com.youcoupon.john_li.transportationapp.TMSModel;

/**
 * Created by John_Li on 26/11/2018.
 */

public class DeliverInvoiceModel {
    private String materialId;
    private String materialName;
    private int sendOutNum;
    private int recycleNum;
    private int seq;

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
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
