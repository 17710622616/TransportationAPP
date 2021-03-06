package com.youcoupon.john_li.transportationapp.TMSDBInfo;

/**
 * Created by John_Li on 26/11/2018.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * onCreated = "sql"：当第一次创建表需要插入数据时候在此写sql语句
 */
@Table(name = "material_number_tb",onCreated = "")
public class MaterialNumberInfo {
    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "material_number_id")
    private String MaterialID;
    @Column(name = "material_name")
    private String NameChinese;
    @Column(name = "short_material_name")
    private String ShortNameChinese;
    @Column(name = "material_deposite_num")
    private int MaterialDepositeNum;
    @Column(name = "material_refund_num")
    private int MaterialRefundNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterialID() {
        return MaterialID;
    }

    public void setMaterialID(String materialID) {
        MaterialID = materialID;
    }

    public String getMaterialName() {
        return NameChinese;
    }

    public void setMaterialName(String NameChinese) {
        NameChinese = NameChinese;
    }

    public String getNameChinese() {
        return NameChinese;
    }

    public void setNameChinese(String nameChinese) {
        NameChinese = nameChinese;
    }

    public int getMaterialDepositeNum() {
        return MaterialDepositeNum;
    }

    public void setMaterialDepositeNum(int materialDepositeNum) {
        MaterialDepositeNum = materialDepositeNum;
    }

    public int getMaterialRefundNum() {
        return MaterialRefundNum;
    }

    public void setMaterialRefundNum(int materialRefundNum) {
        MaterialRefundNum = materialRefundNum;
    }

    public String getShortNameChinese() {
        return ShortNameChinese;
    }

    public void setShortNameChinese(String shortNameChinese) {
        ShortNameChinese = shortNameChinese;
    }

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public MaterialNumberInfo() {
    }

    @Override
    public String toString() {
        return "submit_invoice_tb{"+"id="+id+'}';
    }
}
