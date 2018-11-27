package com.youcoupon.john_li.transportationapp.TMSModel;

/**
 * Created by John_Li on 23/7/2018.
 */

public class TestMaterialModel {
    private String materialName;
    private int materialNum;

    public TestMaterialModel(String materialName, int materialNum) {
        this.materialName = materialName;
        this.materialNum = materialNum;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getMaterialNum() {
        return materialNum;
    }

    public void setMaterialNum(int materialNum) {
        this.materialNum = materialNum;
    }
}
