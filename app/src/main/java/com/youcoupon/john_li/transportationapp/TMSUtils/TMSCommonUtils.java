package com.youcoupon.john_li.transportationapp.TMSUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import hardware.print.BarcodeUtil;

/**
 * 常用工具類
 * Created by John_Li on 20/7/2018.
 */

public class TMSCommonUtils {
    /**
     * 图片两端所保留的空白的宽度
     */
    private static int marginW=20;

    /**
     * 条形码的编码类型
     */
    private static BarcodeFormat barcodeFormat= BarcodeFormat.EAN_8;
    /**
     * 条形码编码
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    public static Bitmap encode(String contents, int width, int height, Context context) {
        Bitmap bm = null;
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.EAN_8, codeWidth, height, null);
            //MatrixToImageWriter.writeToFile(bitMatrix, "png", new File(imgPath));
            bm = BarcodeUtil.creatBarcode(context.getApplicationContext(), BarcodeFormat.EAN_8, contents, codeWidth, height, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 生成条形码
     * @param context
     * @param contents  需要生成的内容
     * @param desiredWidth 生成条形码的宽带
     * @param desiredHeight 生成条形码的高度
     * @param displayCode 是否在条形码下方显示内容
     * @return
     */
    public static Bitmap creatBarcode(Context context, String contents, int desiredWidth, int desiredHeight, boolean displayCode){
        Bitmap ruseltBitmap=null;
        if (displayCode) {
            Bitmap barcodeBitmap=encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
            Bitmap codeBitmap=creatCodeBitmap(contents, desiredWidth+2*marginW, desiredHeight, context);
            ruseltBitmap=mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0, desiredHeight));
        } else {
            ruseltBitmap=encodeAsBitmap(contents,barcodeFormat, desiredWidth, desiredHeight);
        }

        return ruseltBitmap;
    }

    public static UserModel getUserFor40(Context context) {
        UserModel userModel = null;
        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(context, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        for (UserModel model : userModelList) {
            if (model.getCorp().equals("40")) {
                userModel = model;
            }

            if (model.getCorp().equals("XX")) {
                userModel = model;
            }
        }
        return userModel;
    }

    public static UserModel getUserFor72(Context context) {
        UserModel userModel = null;
        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(context, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        for (UserModel model : userModelList) {
            if (model.getCorp().equals("72")) {
                userModel = model;
            }

            if (model.getCorp().equals("XX")) {
                userModel = model;
            }
        }
        return userModel;
    }

    public static UserModel getUserForXX(Context context) {
        UserModel userModel = null;
        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(context, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        for (UserModel model : userModelList) {
            if (model.getCorp().equals("XX")) {
                userModel = model;
            }
        }
        return userModel;
    }

    /**
     * 生成EAN-8 验证码
     */
    public static String ean8(String code) {
        /*例如要算出*471002110526*此筆資料的檢查碼，其計算過程如下:
        (1)	將偶位數值相加乘3 。
        7+0+2+1+5+6=21 , 21*3=63
        (2)	將奇位數值相加。
        4+1+0+1+0+2=8
        (3)	將步驟1.2中所求得的值相加，取其個位數之值。
        63+8=71
        (4)	以10減去步驟3中所求得的值，即為該EAN條碼之檢查碼。
        若步驟3求得的個位數為0， 檢查碼應為0。
        10-1=9........檢查碼
        EAN 8的檢查碼計算方式與EAN13相同。*/
        int c1 = 0; // 奇數位
        int c2 = 0; // 偶數位
        for (int i = 0; i <= 6; i += 2) { // i=0,2,4,6,..
            c1 += (code.charAt(i) - '0');
            if (i != 6) {
                c2 += (code.charAt(i + 1) - '0');
            }
        }
        int c = (c1 + c2 * 3) % 10;
        int cc = 0;
        if (c != 0) {
            cc = (10 - c);
        }
        return code + cc;
    }

    /**
     * 生成显示编码的Bitmap
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    protected static Bitmap creatCodeBitmap(String contents,int width,int height,Context context) {
        TextView tv=new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(),
                tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode=tv.getDrawingCache();
        return bitmapCode;
    }

    /**
     * 生成条形码的Bitmap
     * @param contents  需要生成的内容
     * @param format    编码格式
     * @param desiredWidth
     * @param desiredHeight
     * @return
     * @throws WriterException
     */
    protected  static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight){
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result=null;
        try {
            result = writer.encode(contents, format, desiredWidth,
                    desiredHeight, null);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 将两个Bitmap合并成一个
     * @param first
     * @param second
     * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     * @return
     */
    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth()+second.getWidth()+marginW, first.getHeight()+second.getHeight()
                , Bitmap.Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first,marginW,0,null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        //cv.save(Canvas.ALL_SAVE_FLAG);
        cv.save();
        cv.restore();

        return newBitmap;
    }

    /**
     * 保存bitmap返回文件路徑
     * @param bitmap
     * @return
     */
    public static String saveBitmap(Bitmap bitmap) {
        String path = "";
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "BarCode.jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            path = file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static int searchTrainsInfoMaxTimes() {
        int times = 0;
        try {
            List<TrainsInfo> list = new ArrayList<>();
            list.addAll(TMSApplication.db.findAll(TrainsInfo.class));
            for (TrainsInfo info : list) {
                if (info.getTrainsTimes() > times) {
                    times = info.getTrainsTimes();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 獲取今日時間
     * @return
     */
    public static String getTimeToday() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(now);
    }

    /**
     * 獲取今日時間
     * @return
     */
    public static String getTimeNow() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(now);
    }

    public static String createLinkStringByGet(Map<String, String> params) {
        String prestr = "";
        try {
            List<String> keys = new ArrayList<String>(params.keySet());
            Collections.sort(keys);
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = params.get(key);
                value = URLEncoder.encode(value, "UTF-8");

                if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                    prestr = prestr + key + "=" + value;
                } else {
                    prestr = prestr + key + "=" + value + "&";
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return prestr;
    }

    /**
     * 获取软件版本号
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            //注意："com.example.try_downloadfile_progress"对应AndroidManifest.xml里的package="……"部分
            verCode = context.getPackageManager().getPackageInfo(
                    "com.youcoupon.john_li.transportationapp", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg",e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取版本名称
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "com.youcoupon.john_li.transportationapp", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg",e.getMessage());
        }
        return verName;
    }

    /**
     * 參數加密方法
     **/
    public static String encryptDES(String encryptString){
        try {

            IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
            SecretKeySpec key = new SecretKeySpec(DESKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

            return Base64.encodeToString(encryptedData, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptString;
    }

    /**
     * data解密方法
     **/
    public static String decryptDES(String decryptString){
        try {
            byte[] byteMi = Base64.decode(decryptString, Base64.DEFAULT);
            IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
            SecretKeySpec key = new SecretKeySpec(DESKey.getBytes(), "DES");
            Cipher cipher = null;

                cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte decryptedData[] = cipher.doFinal(byteMi);
            return new String(decryptedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取编码后的值
     * @param data
     * @return
     */
    public static String decode(String data) {
        return decode(Base64.decode(data, Base64.DEFAULT));
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @return 解密后的字节数组
     */
    public static String decode(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec iv = new IvParameterSpec(DESIV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, getRawKey(DESKey), iv);
            byte[] original = cipher.doFinal(data);
            String originalString = new String(original);
            return originalString;
        } catch (Exception e) {
            return null;
        }
    }
    // 对密钥进行处理
    private static Key getRawKey(String key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }

    /**
     * string List去重
     * @param list
     */
    public static List<String> removeDuplicate(List<String> list) {
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }


    // 加解密参数
    public final static String SHAKey = "m@ccbM0CCB";
    public final static String DESKey = "m0CCbK1y";
    public final static String DESIV = "M@CCbMi5";
    private final static String HEX = "0123456789ABCDEF";
    private final static String TRANSFORMATION = "DES/CBC/PKCS5Padding";//DES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private final static String IVPARAMETERSPEC = "01020304";////初始化向量参数，AES 为16bytes. DES 为8bytes.
    private final static String ALGORITHM = "DES";//DES是加密方式
    private static final String SHA1PRNG = "SHA1PRNG";//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法


    private static String m_newVerCode; //最新版的版本号
    private static String m_newVerName; //最新版的版本名
    private static String m_newApkUrl;//新的apk下载地址
    private static String m_appNameStr; //下载到本地要给这个APP命的名字
    private String m_versionRemark; //新版本的備註
    private static Callback.Cancelable cancelable;// 短點續傳的回調
    private static ProgressDialog m_progressDlg;

    /**
     * 从服务器获取当前最新版本号
     */
    public static void checkAPPVersion(final Context context) {
        m_progressDlg = new ProgressDialog(context);
        m_progressDlg.setTitle("提示");
        m_progressDlg.setMessage("檢查版本號中......");
        m_progressDlg.setCancelable(false);
        m_progressDlg.setCanceledOnTouchOutside(false);
        m_progressDlg.show();
        m_appNameStr = "PSAForMaterial.apk";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("apkname", "PSAForMaterial.apk");
        paramsMap.put("IMEI", TMSShareInfo.IMEI);
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_APK_VER + TMSCommonUtils.createLinkStringByGet(paramsMap));
        String url = params.getUri();
        params.setConnectTimeout(30 * 1000);
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String verCode = new Gson().fromJson(result, String.class);
                if (verCode != null) {
                    m_newVerCode = verCode;
                } else {
                    m_newVerCode = "-1";
                    Toast.makeText(context, "獲取版本號失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            //请求异常后的回调方法
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(context,  "獲取版本號失敗！" + ex.getMessage(), Toast.LENGTH_LONG).show();
                m_newVerCode = "-1";
            }

            //主动调用取消请求的回调方法
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
                m_progressDlg.dismiss();
                int vercode = TMSCommonUtils.getVerCode(context.getApplicationContext());
                if (Integer.parseInt(m_newVerCode) > vercode) {
                    doNewVersionUpdate(context); // 更新新版本
                } else {
                    notNewVersionDlgShow(context); // 提示当前为最新版本
                }
            }
        });
    }

    private static void doNewVersionUpdate(final Context context) {
        int verCode = getVerCode(context.getApplicationContext());
        String verName = getVerName(context.getApplicationContext());

        String str= "當前版本："+verName+" Code:"+verCode+" ,發現新版本："+
                " Code:"+m_newVerCode+" ,是否更新？";
        Dialog dialog = new AlertDialog.Builder(context).setTitle("軟件更新").setMessage(str)
                // 设置内容
                .setPositiveButton("更新",// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                m_progressDlg.setTitle("正在下載");
                                m_progressDlg.setMessage("請稍後...");
                                downFile(m_newApkUrl, context);  //开始下载
                            }
                        })
                .setNegativeButton("暫不更新",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // 点击"取消"按钮之后退出程序
                                System.exit(0);
                            }
                        }).create();// 创建

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        // 显示对话框
        dialog.show();
    }

    /**
     *  提示当前为最新版本
     * @param context
     */
    private static void notNewVersionDlgShow(Context context) {
        int verCode = getVerCode(context.getApplicationContext());
        String verName = getVerName(context.getApplicationContext());
        if(m_newVerCode.equals("-1")) {
            //Toast.makeText(context, "獲取版本號失敗，請重試！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "當前版本:"+verName+" Code:"+verCode+",已經是最新版本!", Toast.LENGTH_SHORT).show();
        }
    }


    private static long downloadTaskID;                //下载任务的唯一编号标示
    private static DownloadManager downloadManager;

    private static void downFile(String m_newApkUrl, final Context context) {
        /*initProgressDialog(context);
        // 開始下載
        //设置请求参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("apkname", "PSAForMaterial");
        paramsMap.put("IMEI", TMSShareInfo.IMEI);
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_NEW_APK + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setAutoResume(true);//设置是否在下载是自动断点续传
        params.setAutoRename(false);//设置是否根据头信息自动命名文件
        params.setSaveFilePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/TMSFolder/" + m_appNameStr);
        params.setExecutor(new PriorityExecutor(2, true));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
        params.setCancelFast(true);//是否可以被立即停止.
        //下面的回调都是在主线程中运行的,这里设置的带进度的回调
        *//**
         * 可取消的任务
         *//*
        cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onCancelled(CancelledException arg0) {
                Log.i("tag", "取消"+Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Log.i("tag", "onError: 失败"+Thread.currentThread().getName() + "------Throwable:" + arg0.getMessage());
                m_progressDlg.dismiss();
            }

            @Override
            public void onFinished() {
                Log.i("tag", "完成,每次取消下载也会执行该方法"+Thread.currentThread().getName());
                m_progressDlg.dismiss();
            }

            @RequiresApi(api = 26)
            @Override
            public void onSuccess(File arg0) {
                Log.i("tag", "下载成功的时候执行"+Thread.currentThread().getName());
                // 下載完成
                down(context);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (isDownloading) {
                    m_progressDlg.setProgress((int) (current*100/total));
                    Log.i("tag", "下载中,会不断的进行回调:"+Thread.currentThread().getName());
                }
            }

            @Override
            public void onStarted() {
                Log.i("tag", "开始下载的时候执行"+Thread.currentThread().getName());
                m_progressDlg.show();
            }

            @Override
            public void onWaiting() {
                Log.i("tag", "等待,在onStarted方法之前执行"+Thread.currentThread().getName());
            }
        });*/
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo("com.android.providers.downloads", 0);

            //当系统Downloader可用时才进行下载操作
            if (applicationInfo.enabled) {
                downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(m_newApkUrl));
                downloadTaskID = downloadManager.enqueue(request);
            } else {
                Toast.makeText(context, "系统下载工具不可用", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下載完成關閉進度條
     */
    @RequiresApi(api = 26)
    private static void down(Context context) {
        m_progressDlg.dismiss();
        //update();
        openAPKFile(context, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/TMSFolder", m_appNameStr).getPath());
    }

    /**
     * 打开安装包
     *
     * @param mContext
     * @param fileUri
     */
    @RequiresApi(api = 26)
    public static void openAPKFile(Context mContext, String fileUri) {
        //DataEmbeddingUtil.dataEmbeddingAPPUpdate(fileUri);
        // 核心是下面几句代码
        if (null != fileUri) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File apkFile = new File(fileUri);
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(mContext, "com.youcoupon.john_li.transportationapp" + ".fileprovider", apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    //兼容8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean hasInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
                        if (!hasInstallPermission) {
                            Toast.makeText(mContext, "hasInstallPermission=" + hasInstallPermission, Toast.LENGTH_LONG);
                            startInstallPermissionMainActivity(mContext);
                            return;
                        }
                    }
                } else {
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    mContext.startActivity(intent);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                //DataEmbeddingUtil.dataEmbeddingAPPUpdate(e.toString());
                //CommonUtils.makeEventToast(MyApplication.getContext(), MyApplication.getContext().getString(R.string.download_hint), false);
                Toast.makeText(mContext, "版本更新失敗！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionMainActivity(Context context) {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 更新APP
     */
    private void update(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/TMSFolder", m_appNameStr)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /*初始化短點續傳的对话框*/
    private static void initProgressDialog(Context context) {
        //创建进度条对话框
        m_progressDlg = new ProgressDialog(context);
        //设置标题
        m_progressDlg.setTitle("下載安裝包");
        //设置信息
        m_progressDlg.setMessage("玩命下載中...");
        //设置显示的格式
        m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //设置按钮
        m_progressDlg.setButton(ProgressDialog.BUTTON_NEGATIVE, "暫停",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击取消正在下载的操作
                cancelable.cancel();
            }});

        m_progressDlg.show();
    }

    /**
     * 根据公司获取用户信息
     */
    public static UserModel getUserInfoByCorp(Context context, String corp) {
        UserModel userModel = null;
        List<UserModel> list = new Gson().fromJson(String.valueOf(SpuUtils.get(context, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        for (UserModel model : list) {
            userModel = model;
        }

        return userModel;
    }

    public static String compressImageFromFile(String path) {
        //进行大小缩放来达到压缩的目的
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true; //只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        //根据原始图片的宽高比和期望的输出图片的宽高比计算最终输出的图片的宽和高
        // 图片的宽高
        int width = opts.outWidth;
        int height = opts.outHeight;
        // 预期的图片宽高
        float scaleWidth = 1000f, scaleHeight = 1333f;
        int be = 1;
        if (width > height | width > scaleWidth) {
            be = (int)(opts.outWidth / scaleWidth);
        } else if (width < height && height > scaleHeight) {
            be = (int)(opts.outHeight / scaleHeight);
        }
        if (be <= 0) {
            be = 1;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = be;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        opts.inPurgeable = true;// 同时设置才会有效
        opts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
        Bitmap bm = BitmapFactory.decodeFile(path, opts);
        //进行有损压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
        long l = baos.toByteArray().length;
        while (l / 1024 > 180) {    //循环判断如果压缩后图片是否大于maxMemmorrySize,大于继续压缩
            baos.reset();  //重置baos即让下一次的写入覆盖之前的内容
            options = Math.max(0, options - 10);//图片质量每次减少10
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
            l = baos.toByteArray().length;
            if (options == 0) {   //如果图片的质量已降到最低则，不再进行压缩
                break;
            }
        }
        bm.recycle();
        String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return base64;
    }

    public static byte[] IntArrayToByteArray(byte[] Iarr){
        byte[] bytes = new byte[Iarr.length];
        for (int i = 0; i < Iarr.length; i++) {
            bytes[i] = (byte)  (Iarr[i] & 0xFF);
        }
        return bytes;
    }

    private static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readByte();
        } catch (Exception e) {
            Log.e("{}", e.getStackTrace().toString());
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                Log.e("{}", e.getStackTrace().toString());
            }
        }
        return null;
    }

    /**
     * 删除相对路径
     * */
    public static void deletePath() {
        isHaveSDCard();
        File file;
        if (isHaveSDCard()) {
            file = Environment.getExternalStorageDirectory();
        } else {
            file = Environment.getDataDirectory();
        }
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder");
        RecursionDeleteFile(file);
    }

    /**
     * 删除绝对路径
     * */
    public static void deleteCache() {
        File deleteFile = new File("/sdcard/Android/data/com.eCell.eCellStudy/cache");
        RecursionDeleteFile(deleteFile);
        if (!deleteFile.exists()) {
            deleteFile.mkdirs();
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file
     *            要删除的根目录
     */
    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    /** 是否有SD卡 */
    public static boolean isHaveSDCard() {
        String SDState = android.os.Environment.getExternalStorageState();
        if (SDState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    //生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }
}
