package com.youcoupon.john_li.transportationapp.TMSModel;

/**
 * 用戶信息類
 * Created by John_Li on 20/7/2018.
 */

public class UserModel {
    // 用戶名
    private String ID;
    // 中文名
    private String NameChinese;
    //
    private String SalesmanID;
    // 公司
    private String Corp;
    // 密碼
    private String passWord;
    // 車隊id
    private String trunkId;
    // 登錄時間
    private String loginTime;
    // 客戶表更新狀態
    private boolean customerTbStatus;
    // 今日發票表更新狀態
    private boolean invoiceTbStatus;
    // 物料表更新狀態
    private boolean materialTbStatus;
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNameChinese() {
        return NameChinese;
    }

    public void setNameChinese(String nameChinese) {
        NameChinese = nameChinese;
    }

    public String getSalesmanID() {
        return SalesmanID;
    }

    public void setSalesmanID(String salesmanID) {
        SalesmanID = salesmanID;
    }

    public String getCorp() {
        return Corp;
    }

    public void setCorp(String corp) {
        Corp = corp;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getTrunkId() {
        return trunkId;
    }

    public void setTrunkId(String trunkId) {
        this.trunkId = trunkId;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isCustomerTbStatus() {
        return customerTbStatus;
    }

    public void setCustomerTbStatus(boolean customerTbStatus) {
        this.customerTbStatus = customerTbStatus;
    }

    public boolean isInvoiceTbStatus() {
        return invoiceTbStatus;
    }

    public void setInvoiceTbStatus(boolean invoiceTbStatus) {
        this.invoiceTbStatus = invoiceTbStatus;
    }

    public boolean isMaterialTbStatus() {
        return materialTbStatus;
    }

    public void setMaterialTbStatus(boolean materialTbStatus) {
        this.materialTbStatus = materialTbStatus;
    }
}
