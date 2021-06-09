package com.youcoupon.john_li.transportationapp.TMSUtils;

import android.content.Context;
import android.os.Environment;

import com.youcoupon.john_li.transportationapp.TMSActivity.TestPrintWebActivity;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by John_Li on 27/7/2018.
 */

public class ToHtml {
    private static final String mDepositHtmlHead7 = ("</font></td> </tr>  <tr><td algin=\"left\"><font size=\"5\">日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期：" + TMSCommonUtils.getTimeToday() + "</font></td> </tr> </table> <table width=\"360\" height=\"46\" frame=\"void\"><tr><td height=\"46\"><font size=\"5\">物料</font></td>  <td align=\"center\"><font size=\"5\">送出</font></td> </tr><tr><td height=\"46\"><font size=\"5\"></font></td>  <td align=\"center\"><font size=\"4\">(客戶收)</font></td> <td align=\"center\"><font size=\"5\"></font></td></tr>");
    private static final String mDepositHtmlItem = "<tr> <td height=\"46\"><font size=\"5\">name</font></td> <td align=\"center\"><font size=\"5\">sendOut</font></td> </tr>";
    private static final String mHtmlEnd = "</table><table width=\"360\" height=\"46\" frame=\"void\"> <tr><td algin=\"left\"><font size=\"5\"><br/>客戶簽收及蓋章<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________</br></br></br></br></br></td> </tr></table></body></html>";
    private static final String mHtmlEnda1 = "</table><table width=\"360\" height=\"46\" frame=\"void\"> <tr><td algin=\"left\"><font size=\"5\"><br/>司機簽名<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________<tr><td algin=\"left\"><font size=\"5\"><br/>倉儲簽名<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________</br></br></br></br></br></td> </tr></table></body></html>";
    private static final String mHtmlHead1 = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>澳門可口可樂飲料有限公司</title></head><body></table><p> </p><table width=\"360\" height=\"46\" frame =\"void\"> <tr> <td><font size=\"5\"><strong>";
    private static final String mHtmlHead10 = "\"/></font></td> ";
    private static final String mHtmlHead11 = "</strong>  <br/>No.";
    private static final String mHtmlHead2 = "<br /></font></td>  ";
    private static final String mHtmlHead3 = "</font></td> </tr> <tr><tr><td algin=\"left\"><font size=\"5\">客戶編號：";
    private static final String mHtmlHead4 = "</font></td> </tr> <tr><td algin=\"left\"><font size=\"5\">客戶名稱：";
    private static final String mHtmlHead5 = "</font></td> </tr> <tr><td algin=\"left\"><font size=\"5\">業&nbsp;&nbsp;務&nbsp;&nbsp;員：";
    private static final String mHtmlHead6 = "</font></td> </tr> <tr><td algin=\"left\"><font size=\"5\">經&nbsp;&nbsp;手&nbsp;&nbsp;人：";
    private static final String mHtmlHead8 = " </tr> </table> <table width=\"360\" height=\"46\" frame=\"void\" style=\"word-break:break-all; word-wrap:break-all;\"><tr> <td align=\"center\"><font size=\"5\"><strong>澳門可口可樂飲料有限公司</strong></font></td> </tr><tr><td algin=\"left\"><font size=\"5\">Reference：";
    private static final String mHtmlHead9 = "<td><font size=\"5\"><img src=\"";
    private static final String mHtmlHeada1 = ("<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>倉庫結算單</title></head><body><p> </p><table width=\"360\" height=\"46\" frame=\"void\"><tr> <td align=\"center\"><font size=\"7\"><strong>物料結算單據</strong></font></td></tr><tr> <td align=\"center\"><font size=\"5\">時間：" + TMSCommonUtils.getTimeNow() + "</font></td></tr><tr> <td align=\"center\"><font size=\"5\">車次：");
    private static final String mHtmlHeada2 = "</font></td></tr><tr> <td align=\"center\"><font size=\"5\">司機：";
    private static final String mHtmlHeada3 = "</font></td></tr></table><table width=\"360\" height=\"46\" frame=\"void\"><tr><td height=\"46\"><font size=\"5\">物料</font></td>  <td align=\"center\"><font size=\"5\">送出</font></td><td align=\"center\"><font size=\"5\">回收</font></td></tr><tr><td height=\"46\"><font size=\"5\"></font></td>  <td align=\"center\"><font size=\"4\">(客戶收)</font></td><td align=\"center\"><font size=\"4\">(客戶退)</font></td><td align=\"center\"><font size=\"5\"></font></td></tr>";
    private static final String mHtmlItema1 = "<tr> <td height=\"46\"><font size=\"5\">name</font></td> <td align=\"center\"><font size=\"5\">sendOut</font></td>  <td align=\"center\"><font size=\"5\">recycle</font></td> </tr>";
    private static final String mRefundHtmlHead7 = ("</font></td> </tr>  <tr><td algin=\"left\"><font size=\"5\">日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期：" + TMSCommonUtils.getTimeToday() + "</font></td> </tr> </table> <table width=\"360\" height=\"46\" frame=\"void\"><tr><td height=\"46\"><font size=\"5\">物料</font></td> <td align=\"center\"><font size=\"5\">回收</font></td></tr><tr><td height=\"46\"><font size=\"5\"></font></td> <td align=\"center\"><font size=\"4\">(客戶退)</font></td><td align=\"center\"><font size=\"5\"></font></td></tr>");
    private static final String mRefundHtmlItem = "<tr> <td height=\"46\"><font size=\"5\">name</font></td> <td align=\"center\"><font size=\"5\">recycle</font></td> </tr>";

    // 仅一张客户物料回收单的时候
    private static final String mOneResult1 = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />  <title>澳門可口可樂飲料有限公司</title>  </head>  <body>    <p> </p>    <table width=\"360\" height=\"46\" frame =\"void\">      <tr>         <td style=\"text-align: center;\">          <font size=\"6\">            <strong>客戶物料回收單</strong><br/>          </font>        </td>      </tr>     </table>    <table width=\"360\" height=\"46\" frame=\"void\" style=\"word-break:break-all; word-wrap:break-all;\">      <tr>        <td align=\"center\">          <font size=\"5\">            <strong>澳門可口可樂飲料有限公司</strong>          </font>        </td>       </tr>      <tr>        <td algin=\"left\">          <font size=\"5\">Reference：";
    private static final String mOneResult2 = ("</font></td> </tr>  <tr><td algin=\"left\"><font size=\"5\">日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期：" + TMSCommonUtils.getTimeToday() + "</font></td> </tr> </table>     <table width=\"360\" height=\"46\" frame=\"void\">      <tr>        <td>          <font size=\"5\">收入客戶：</font>        </td>       </tr>      <tr>        <td height=\"46\">          <font size=\"5\">物料</font>        </td>          <td align=\"center\">          <font size=\"5\">數量</font>        </td>       </tr>    </table>            <table width=\"360\" height=\"46\" frame=\"void\">      <tr>        <td>          <font size=\"5\"><br/>送至客戶：</font>        </td>       </tr>      <tr>        <td height=\"46\">          <font size=\"5\">物料</font>        </td>          <td align=\"center\">          <font size=\"5\">數量</font>        </td>       </tr>");

    public static void convert(String invoiceNo, String reference, String customerID, String customerName, String path, List<DeliverInvoiceModel> list, String barCodeImagePath, Context context, boolean isMother) {//, String name[], int sendOut[], int recycle[]
        try {
            String result;
            String result2;
            String result3;
            String result4 = "";
            boolean isdeposit = true;
            int totalDepositNum = 0;
            int totalRefundNum = 0;
            int i = 0;
            while (i < list.size()) {
                try {
                    totalDepositNum += ((DeliverInvoiceModel) list.get(i)).getSendOutNum();
                    totalRefundNum += ((DeliverInvoiceModel) list.get(i)).getRecycleNum();
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：ToHtml.convert():" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
                    return;
                }
            }
            if (totalDepositNum <= 0) {
                result = result4 + "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>澳門可口可樂飲料有限公司</title></head><body></table><p> </p><table width=\"360\" height=\"46\" frame =\"void\"> <tr> <td><font size=\"5\"><strong>回樽單</strong>  <br/>No.";
                isdeposit = false;
            } else if (totalRefundNum <= 0) {
                result = result4 + "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>澳門可口可樂飲料有限公司</title></head><body></table><p> </p><table width=\"360\" height=\"46\" frame =\"void\"> <tr> <td><font size=\"5\"><strong>按金單</strong>  <br/>No.";
            } else if (isMother) {
                result = result4 + "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>澳門可口可樂飲料有限公司</title></head><body></table><p> </p><table width=\"360\" height=\"46\" frame =\"void\"> <tr> <td><font size=\"5\"><strong>按金單</strong>  <br/>No.";
            } else {
                result = result4 + "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>澳門可口可樂飲料有限公司</title></head><body></table><p> </p><table width=\"360\" height=\"46\" frame =\"void\"> <tr> <td><font size=\"5\"><strong>回樽單</strong>  <br/>No.";
                isdeposit = false;
            }
            if (invoiceNo == null) {
                result2 = result + "<br /></font></td>   </tr> </table> <table width=\"360\" height=\"46\" frame=\"void\" style=\"word-break:break-all; word-wrap:break-all;\"><tr> <td align=\"center\"><font size=\"5\"><strong>澳門可口可樂飲料有限公司</strong></font></td> </tr><tr><td algin=\"left\"><font size=\"5\">Reference：" + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead6 + TMSCommonUtils.getUserFor40(context).getNameChinese();
            } else if (!invoiceNo.equals("")) {
                result2 = result + invoiceNo + mHtmlHead2 + mHtmlHead9 + barCodeImagePath + mHtmlHead10 + mHtmlHead8 + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead6 + TMSCommonUtils.getUserFor40(context).getNameChinese();
            } else {
                result2 = result + "<br /></font></td>   </tr> </table> <table width=\"360\" height=\"46\" frame=\"void\" style=\"word-break:break-all; word-wrap:break-all;\"><tr> <td align=\"center\"><font size=\"5\"><strong>澳門可口可樂飲料有限公司</strong></font></td> </tr><tr><td algin=\"left\"><font size=\"5\">Reference：" + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead6 + TMSCommonUtils.getUserFor40(context).getNameChinese();
            }
            if (isdeposit) {
                result3 = result2 + mDepositHtmlHead7;
                for (int i2 = 0; i2 < list.size(); i2++) {
                    if (((DeliverInvoiceModel) list.get(i2)).getSendOutNum() > 0) {
                        result3 = result3 + new String(mDepositHtmlItem).replace("name", ((DeliverInvoiceModel) list.get(i2)).getMaterialName()).replace("sendOut", "" + ((DeliverInvoiceModel) list.get(i2)).getSendOutNum());
                    }
                }
            } else {
                result3 = result2 + mRefundHtmlHead7;
                for (int i3 = 0; i3 < list.size(); i3++) {
                    if (((DeliverInvoiceModel) list.get(i3)).getRecycleNum() > 0) {
                        result3 = result3 + new String(mRefundHtmlItem).replace("name", ((DeliverInvoiceModel) list.get(i3)).getMaterialName()).replace("recycle", "" + ((DeliverInvoiceModel) list.get(i3)).getRecycleNum());
                    }
                }
            }
            saveStringToFile(path, result3 + mHtmlEnd);
//            if (invoiceNo != null) {
//                if (!invoiceNo.equals("")) {
//                    result = mHtmlHead1 + invoiceNo + mHtmlHead2 + mHtmlHead9 + barCodeImagePath + mHtmlHead10 + mHtmlHead8 + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSCommonUtils.getUserFor40(context).getNameChinese()+ mHtmlHead6 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead7;
//                } else {
//                    result = mHtmlHead1 + mHtmlHead2 + mHtmlHead8 + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead6 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead7;
//                }
//            } else {
//                result = mHtmlHead1 + mHtmlHead2 + mHtmlHead8 + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead6 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead7;
//            }
//            for (int i = 0; i < list.size(); i++) {
//                if (list.get(i).getRecycleNum() > 0 || list.get(i).getSendOutNum() > 0) {
//                    String mid = new String(mHtmlItem);
//                    mid = mid.replace("name", list.get(i).getMaterialName());
//                    mid = mid.replace("sendOut", "" + list.get(i).getSendOutNum());
//                    mid = mid.replace("recycle", "" + list.get(i).getRecycleNum());
//                    result += mid;
//                }
//            }
//
//            result += mHtmlEnd;
//            saveStringToFile(path, result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：ToHtml.convert():" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
        }
    }

    /**
     * 有仅一张客戶物料回收單时创建网页
     * @param invoiceNo
     * @param refrence
     * @param customerID
     * @param customerName
     * @param path
     * @param list
     * @param context
     */
    public static void convertOneSheet(String invoiceNo, String refrence, String customerID, String customerName, String path, List<DeliverInvoiceModel> list, Context context) {
        try {
            String result;
            String headResult;
            String result2;
            String result3 = "";
            boolean isdeposit = true;
            int totalDepositNum = 0;
            int totalRefundNum = 0;
            int i = 0;
            while (i < list.size()) {
                try {
                    totalDepositNum += ((DeliverInvoiceModel) list.get(i)).getSendOutNum();
                    totalRefundNum += ((DeliverInvoiceModel) list.get(i)).getRecycleNum();
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：ToHtml.convert():" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
                    return;
                }
            }
            result = mOneResult1 + refrence + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHead6 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mOneResult2;
            if (totalDepositNum <= 0) {
                for (int i2 = 0; i2 < list.size(); i2++) {
                    if (((DeliverInvoiceModel) list.get(i2)).getSendOutNum() > 0) {
                        result3 = result3 + new String(mDepositHtmlItem).replace("name", ((DeliverInvoiceModel) list.get(i2)).getMaterialName()).replace("sendOut", "" + ((DeliverInvoiceModel) list.get(i2)).getSendOutNum());
                    }
                }
            } else if (totalRefundNum <= 0) {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    if (((DeliverInvoiceModel) list.get(i3)).getRecycleNum() > 0) {
                        result3 = result3 + new String(mRefundHtmlItem).replace("name", ((DeliverInvoiceModel) list.get(i3)).getMaterialName()).replace("recycle", "" + ((DeliverInvoiceModel) list.get(i3)).getRecycleNum());
                    }
                }
            }

            saveStringToFile(path, result3 + mHtmlEnd);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：ToHtml.convert():" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
        }
    }

//    private static final String mHtmlHeada1 = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>倉庫結算單</title></head><body><p> </p>"
//            + "<table width=\"360\" height=\"46\" frame=\"void\"><tr> <td align=\"center\"><font size=\"7\"><strong>物料結算單據</strong></font></td></tr><tr> <td align=\"center\"><font size=\"5\">時間：" + TMSCommonUtils.getTimeNow() + "</font></td></tr><tr> <td align=\"center\"><font size=\"5\">車次：";
//    private static final String mHtmlHeada2 = "</font></td></tr><tr> <td align=\"center\"><font size=\"5\">司機：";
//    private static final String mHtmlHeada3 = "</font></td></tr></table>"
//            + "<table width=\"360\" height=\"46\" frame=\"void\"><tr><td height=\"46\"><font size=\"5\">物料</font></td>  <td align=\"center\"><font size=\"5\">送出</font></td><td align=\"center\"><font size=\"5\">回收</font></td></tr>"
//            + "<tr><td height=\"46\"><font size=\"5\"></font></td>  <td align=\"center\"><font size=\"4\">(客戶收)</font></td><td align=\"center\"><font size=\"4\">(客戶退)</font></td><td align=\"center\"><font size=\"5\"></font></td></tr>";
//    private static final String mHtmlItema1 = "<tr> <td height=\"46\"><font size=\"5\">name</font></td> <td align=\"center\"><font size=\"5\">sendOut</font></td>  <td align=\"center\"><font size=\"5\">recycle</font></td> </tr>";
//    private static final String mHtmlEnda1 = "</table><table width=\"360\" height=\"46\" frame=\"void\"> <tr><td algin=\"left\"><font size=\"5\"><br/>司機簽名<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________" +
//            "<tr><td algin=\"left\"><font size=\"5\"><br/>倉儲簽名<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________</br></br></br></br></br></td> </tr></table></body></html>";
    public static void convertCloseccount(List<DeliverInvoiceModel> mDeliverInvoiceModelList, String path, Context context) {//, String name[], int sendOut[], int recycle[]
        try {
            String result = "";
            TrainsInfo first = TMSApplication.db.findFirst(TrainsInfo.class);
            if (first != null) {
                int times = TMSCommonUtils.searchTrainsInfoMaxTimes();
                if (times == 0) {
                    times = 1;
                }
                result = mHtmlHeada1 + times + mHtmlHeada2 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHeada3;
            } else {
                result = mHtmlHeada1 + 1 + mHtmlHeada2 + TMSCommonUtils.getUserFor40(context).getNameChinese() + mHtmlHeada3;
            }

            for (int i = 0; i < mDeliverInvoiceModelList.size(); i++) {
                if (mDeliverInvoiceModelList.get(i).getRecycleNum() > 0 || mDeliverInvoiceModelList.get(i).getSendOutNum() > 0) {
                    String mid = new String(mHtmlItema1);
                    mid = mid.replace("name", mDeliverInvoiceModelList.get(i).getMaterialName());
                    mid = mid.replace("sendOut", "" + mDeliverInvoiceModelList.get(i).getSendOutNum());
                    mid = mid.replace("recycle", "" + mDeliverInvoiceModelList.get(i).getRecycleNum());
                    result += mid;
                }
            }
            result += mHtmlEnda1;
            saveStringToFile(path, result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean saveStringToFile(String path, String content) {
        // FileWriter fw = new FileWriter(path);
        // MTDebug.startCount();
        // ByteBuffer dst = ByteBuffer.allocate(content.length() * 4);

        try {
            FileOutputStream fos = new FileOutputStream(path);
            // 把长宽写入头部
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
}
