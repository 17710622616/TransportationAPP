package com.youcoupon.john_li.transportationapp.TMSUtils;

import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by John_Li on 27/7/2018.
 */

public class ToHtml {
    private static final String mHtmlHead1 = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>澳門可口可樂飲料有限公司</title></head><body></table><p> </p>"
        + "<table width=\"360\" height=\"46\" frame =\"void\"> <tr> <td><font size=\"5\"><strong>"+ "物料單" + "</strong>  <br/>No.";
    private static final String mHtmlHead2 = "<br /></font></td>  ";
    private static final String mHtmlHead9 = "<td><font size=\"5\"><img src=\"" ;
    private static final String mHtmlHead10 = "\"/></font></td> " ;
    private static final String mHtmlHead8 = " </tr> </table> <table width=\"360\" height=\"46\" frame=\"void\"><tr> <td align=\"center\"><font size=\"5\"><strong>澳門可口可樂飲料有限公司</strong></font></td> </tr><tr><td algin=\"left\"><font size=\"5\">" + "Reference：";
    private static final String mHtmlHead3 = "</font></td> </tr> <tr><tr><td algin=\"left\"><font size=\"5\">"+ "客戶編號：";
    private static final String mHtmlHead4 = "</font></td> </tr> <tr><td algin=\"left\"><font size=\"5\">客戶名稱：";
    private static final String mHtmlHead5 = "</font></td> </tr> <tr><td algin=\"left\"><font size=\"5\">業&nbsp;&nbsp;務&nbsp;&nbsp;員：";
    private static final String mHtmlHead6 = "</font></td> </tr> <tr><td algin=\"left\"><font size=\"5\">經&nbsp;&nbsp;手&nbsp;&nbsp;人：";
    private static final String mHtmlHead7 = "</font></td> </tr>  <tr><td algin=\"left\"><font size=\"5\">日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期：" + "2018-05-21" +
            "</font></td> </tr> </table> <table width=\"360\" height=\"46\" frame=\"void\"><tr><td height=\"46\"><font size=\"5\">物料</font></td>  <td align=\"center\"><font size=\"5\">送出</font></td><td align=\"center\"><font size=\"5\">回收</font></td></tr>" +
            "<tr><td height=\"46\"><font size=\"5\"></font></td>  <td align=\"center\"><font size=\"4\">(客戶收)</font></td><td align=\"center\"><font size=\"4\">(客戶退)</font></td><td align=\"center\"><font size=\"5\"></font></td></tr>";
    private static final String mHtmlItem = "<tr> <td height=\"46\"><font size=\"5\">name</font></td> <td align=\"center\"><font size=\"5\">sendOut</font></td>  <td align=\"center\"><font size=\"5\">recycle</font></td> </tr>";
    private static final String mHtmlEnd = "</table><table width=\"360\" height=\"46\" frame=\"void\"> <tr><td algin=\"left\"><font size=\"5\"><br/>客戶簽收及蓋章<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________</br></br></br></br></br></td> </tr></table></body></html>";

    public static void convert(String invoiceNo, String reference, String customerID, String customerName, String path, List<DeliverInvoiceModel> list, String barCodeImagePath) {//, String name[], int sendOut[], int recycle[]
        try {
            String result = "";
            if (invoiceNo.equals("")) {
                result = mHtmlHead1 + invoiceNo + mHtmlHead2 + mHtmlHead8 + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSShareInfo.mUserModelList.get(0).getNameChinese()+ mHtmlHead6 + TMSShareInfo.mUserModelList.get(0).getNameChinese() + mHtmlHead7;
            } else {
                result = mHtmlHead1 + invoiceNo + mHtmlHead2 + mHtmlHead9 + barCodeImagePath + mHtmlHead10 + mHtmlHead8 + reference + mHtmlHead3 + customerID + mHtmlHead4 + customerName + mHtmlHead5 + TMSShareInfo.mUserModelList.get(0).getNameChinese()+ mHtmlHead6 + TMSShareInfo.mUserModelList.get(0).getNameChinese() + mHtmlHead7;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getRecycleNum() > 0 || list.get(i).getSendOutNum() > 0) {
                    String mid = new String(mHtmlItem);
                    mid = mid.replace("name", list.get(i).getMaterialName());
                    mid = mid.replace("sendOut", "" + list.get(i).getSendOutNum());
                    mid = mid.replace("recycle", "" + list.get(i).getRecycleNum());
                    result += mid;
                }
            }
            result += mHtmlEnd;
            saveStringToFile(path, result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final String mHtmlHeada1 = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>倉庫結算單</title></head><body><p> </p>"
            + "<table width=\"360\" height=\"46\" frame=\"void\"><tr> <td align=\"center\"><font size=\"7\"><strong>物料結算單據</strong></font></td></tr>" +
            "<tr> <td align=\"center\"><font size=\"5\">時間：" + TMSCommonUtils.getTimeToday() + "</font></td></tr></table>"
            + "<table width=\"360\" height=\"46\" frame=\"void\"><tr><td height=\"46\"><font size=\"5\">物料</font></td>  <td align=\"center\"><font size=\"5\">送出</font></td><td align=\"center\"><font size=\"5\">回收</font></td></tr>"
            + "<tr><td height=\"46\"><font size=\"5\"></font></td>  <td align=\"center\"><font size=\"4\">(客戶收)</font></td><td align=\"center\"><font size=\"4\">(客戶退)</font></td><td align=\"center\"><font size=\"5\"></font></td></tr>";
    private static final String mHtmlItema1 = "<tr> <td height=\"46\"><font size=\"5\">name</font></td> <td align=\"center\"><font size=\"5\">sendOut</font></td>  <td align=\"center\"><font size=\"5\">recycle</font></td> </tr>";
    private static final String mHtmlEnda1 = "</table><table width=\"360\" height=\"46\" frame=\"void\"> <tr><td algin=\"left\"><font size=\"5\"><br/>司機簽名<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________" +
            "<tr><td algin=\"left\"><font size=\"5\"><br/>倉儲簽名<br/><br/><br/><br/><br/><br/></font></td> </tr> <tr><td algin=\"left\">________________________________________________</br></br></br></br></br></td> </tr></table></body></html>";
    public static void convertCloseccount(List<DeliverInvoiceModel> mDeliverInvoiceModelList, String path) {//, String name[], int sendOut[], int recycle[]
        try {
            String result = "";
            result = mHtmlHeada1;
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
