package com.youcoupon.john_li.transportationapp.TMSModel;

import java.util.List;

public class MaterialCorrespondenceModel {
    private String MerchandiseID;
    private List<CorrespondingMaterial> material;

    public String getMerchandiseID() {
        return MerchandiseID;
    }

    public void setMerchandiseID(String merchandiseID) {
        MerchandiseID = merchandiseID;
    }

    public List<CorrespondingMaterial> getMaterial() {
        return material;
    }

    public void setMaterial(List<CorrespondingMaterial> material) {
        this.material = material;
    }

    public class CorrespondingMaterial
    {
        private String MaterialID;
        private int Quantity;

        public String getMaterialID() {
            return MaterialID;
        }

        public void setMaterialID(String materialID) {
            MaterialID = materialID;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int quantity) {
            Quantity = quantity;
        }
    }
}
