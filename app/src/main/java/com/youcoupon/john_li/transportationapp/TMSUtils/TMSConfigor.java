package com.youcoupon.john_li.transportationapp.TMSUtils;

/**
 * Created by John_Li on 26/11/2018.
 */

public class TMSConfigor {
    // 測試url
    public final static String BASE_URL = "http://202.100.100.151/";
    // 正式url
    //public final static String BASE_URL = "http://psamaterial.moccb.com/";
    // 登錄接口
    public final static String LOGIN_API = "api/Tools/CheckUser?";
    // 登出接口
    public final static String LOGIN_OUT_API = "api/Tools/LogoutUser?";
    // 獲取版本號接口
    public final static String GET_APK_VER = "api/Tools/GetAPKVer?";
    // 獲取新版本APK接口
    public final static String GET_NEW_APK = "api/Tools/GetAPK?";
    // 獲取客戶列表接口
    public final static String GET_CUSTOMER_LIST = "api/Basic/GetCustomerList?";
    // 獲取當日發票列表接口
    public final static String GET_TODAY_INVOICE_LIST = "api/Basic/GetTodayInvoiceList?";
    // 提交發票接口
    public final static String SUBMIT_DELEIVER_INVOICE = "api/Business/PostInvoice?";
    // 提交發票状态接口
    public final static String SUBMIT_INVOICE_STATE = "api/Business/PostInvoiceStatistic?";
    // 提交物料结算接口
    public final static String SUBMIT_MATERIALS_SETTLEMENT = "api/Business/PostStockMovement?";

}
