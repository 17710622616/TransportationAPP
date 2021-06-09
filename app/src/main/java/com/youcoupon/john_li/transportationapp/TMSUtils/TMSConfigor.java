package com.youcoupon.john_li.transportationapp.TMSUtils;

/**
 * Created by John_Li on 26/11/2018.
 */


public class TMSConfigor {
    // 測試url
    //public final static String BASE_URL = "http://202.100.100.151/";
    public final static String BASE_URL = "http://202.100.100.33:10090/";
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
    // 獲取物料列表接口
    public final static String GET_MATERIAL_LIST = "api/Basic/GetMaterialList?";
    // 獲取物料關係列表接口
    public final static String GET_MATERIAL_CORRESPONDENCE_LIST = "api/Basic/GetMaterialCorrespondence?";
    // 獲取當日發票列表接口
    public final static String GET_TODAY_INVOICE_LIST = "api/Basic/GetTodayInvoiceList?";
    // 提交發票接口
    public final static String SUBMIT_DELEIVER_INVOICE = "api/Business/PostInvoice?";
    // 提交發票状态接口
    public final static String SUBMIT_INVOICE_STATE = "api/Business/PostInvoiceStatistic?";
    // 提交物料结算接口
    public final static String SUBMIT_MATERIALS_SETTLEMENT = "api/Business/PostStockMovement?";
    // 拉取当前车次接口
    public final static String GET_TRUNKNO_NOW = "api/Business/GetCurrentTruckNo?";
    // 拉取車隊本車次所有發票
    public final static String GET_TRUNK_INVOICE = "api/Business/GetTruckFDInvoice?";
    // 檢查用戶是否登陸
    public final static String CHECK_ONLINE_USER = "api/Tools/CheckOnlineUser?";
    // 拉取分車發票列表
    public final static String GET_SPLIT_INVOICE = "api/Basic/GetInvoiceList?";
    // 退出分車操作
    public final static String CHECK_OUT_OCCUPY = "api/Tools/CheckOutUser?";
    // 提交分車數據
    public static final String POST_TRUNK_SPLIT = "api/Business/PostTruckSplitInvoice?";
    // 提交PDA定位
    public static final  String POST_LOCATION = "/api/Business/PutPDALocation?";

    // OK打卡
    // 获取OK签到路线接口
    public final static String GET_CIRCLE_ORDER_STATUS = "api/Basic/GetCircleOperatorCustomer?";
    // 提交OK签到照片接口
    public final static String POST_PHOTO = "api/Business/PostPhoto?";
}
