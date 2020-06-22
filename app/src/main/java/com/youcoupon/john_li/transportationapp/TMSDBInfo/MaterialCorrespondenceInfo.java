package com.youcoupon.john_li.transportationapp.TMSDBInfo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * onCreated = "sql"：当第一次创建表需要插入数据时候在此写sql语句
 */
@Table(name = "material_correspondenceInfo_tb",onCreated = "")
public class MaterialCorrespondenceInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "correspondenceID",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "MerchandiseID")
    private String MerchandiseID;
    @Column(name = "materialListJson")
    private String materialListJson;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMerchandiseID() {
        return MerchandiseID;
    }

    public void setMerchandiseID(String merchandiseID) {
        MerchandiseID = merchandiseID;
    }

    public String getMaterialListJson() {
        return materialListJson;
    }

    public void setMaterialListJson(String materialListJson) {
        this.materialListJson = materialListJson;
    }
}
