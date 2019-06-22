package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登錄界面
 * Created by John_Li on 20/7/2018.
 */

public class LoginActivity extends BaseActivity {
    private EditText cropEt, userNameEt, pwdEt;
    private TextView submitTv;
    private LinearLayout progressLL;

    private IntentFilter intentFilter;
    private String startWay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startWay = getIntent().getStringExtra("startWay");
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        cropEt = findViewById(R.id.login_crop);
        userNameEt = findViewById(R.id.login_username);
        pwdEt = findViewById(R.id.login_password);
        submitTv = findViewById(R.id.login_submit);
        progressLL = findViewById(R.id.login_progress);
    }

    @Override
    public void setListener() {
        submitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
    }

    private void checkData() {
        progressLL.setVisibility(View.VISIBLE);
        String crop = cropEt.getText().toString();
        String userName = userNameEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        if (crop != null && userName != null && pwd != null) {
            if (!crop.equals("") && !userName.equals("") && !pwd.equals("")) {
                if (userModelList != null) {
                    if (userModelList.size() > 0) {
                        // 判斷是否和已登錄賬戶公司相同
                        boolean b = true;
                        for (int i = 0; i < userModelList.size(); i++) {
                            if (userModelList.get(i).getCorp().equals(crop)) {
                                b = false;
                            }
                        }

                        if (b) {
                            doLogin(crop, userName, pwd);
                        } else {
                            progressLL.setVisibility(View.GONE);
                            Toast.makeText(this, "同一公司的賬號不能重複登錄系統！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        doLogin(crop, userName, pwd);
                    }
                } else {
                    doLogin(crop, userName, pwd);
                }
            } else {
                progressLL.setVisibility(View.GONE);
                Toast.makeText(this, "請填寫全登錄信息！", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressLL.setVisibility(View.GONE);
            Toast.makeText(this, "請填寫全登錄信息！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //TMSCommonUtils.checkAPPVersion(this);
        checkAPPVersion(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁Activity时取消注册广播监听器；
        unregisterReceiver(receiver);
    }

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
        int verCode = TMSCommonUtils.getVerCode(context.getApplicationContext());
        String verName = TMSCommonUtils.getVerName(context.getApplicationContext());

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
                                Map<String, String> paramsMap = new HashMap<>();
                                paramsMap.put("apkname", "PSAForMaterial.apk");
                                paramsMap.put("IMEI", TMSShareInfo.IMEI);
                                m_newApkUrl = TMSConfigor.BASE_URL + TMSConfigor.GET_NEW_APK + TMSCommonUtils.createLinkStringByGet(paramsMap);
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
        int verCode = TMSCommonUtils.getVerCode(context.getApplicationContext());
        String verName = TMSCommonUtils.getVerName(context.getApplicationContext());
        if(m_newVerCode.equals("-1")) {
            //Toast.makeText(context, "獲取版本號失敗，請重試！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "當前版本:"+verName+" Code:"+verCode+",已經是最新版本!", Toast.LENGTH_SHORT).show();
        }
    }


    private static long downloadTaskID;                //下载任务的唯一编号标示
    private static DownloadManager downloadManager;

    private static void downFile(String m_newApkUrl, final Context context) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo("com.android.providers.downloads", 0);

            // 清空旧版DB，防止新旧版DB冲突
            TMSCommonUtils.deletePath();

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

    //重写广播的接收事件相应
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = 26)
        @Override
        public void onReceive(Context context, Intent intent) {
            //只把用户在该Activity中新建的下载任务筛选出来，仅限一个
            //如果是多个还得把downloadTaskID保存到一个List当中再进行筛选
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadTaskID);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToNext() && DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                //获得下载文件存储的本地路径
                String localFileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                //接下来要进行的操作可自行定义
                //可以根据文件类型进行打开，编辑操作等
                openAPKFile(LoginActivity.this, localFileName);
            } else {
                Toast.makeText(context, "66", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 打开安装包
     *
     * @param mContext
     * @param fileUri
     */
    @RequiresApi(api = 26)
    public void openAPKFile(Activity mContext, String fileUri) {
        //DataEmbeddingUtil.dataEmbeddingAPPUpdate(fileUri);
        // 核心是下面几句代码
        if (null != fileUri) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File apkFile = new File(fileUri);
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(LoginActivity.this, "com.youcoupon.john_li.transportationapp" + ".fileprovider", apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    //兼容8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
                        if (!hasInstallPermission) {
                            Toast.makeText(LoginActivity.this, "hasInstallPermission=" + hasInstallPermission, Toast.LENGTH_LONG);
                            startInstallPermissionMainActivity();
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
                Toast.makeText(LoginActivity.this, "版本更新失敗，請聯繫工作人員，謝謝！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionMainActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 登錄
     * @param crop
     * @param userName
     * @param pwd
     */
    private void doLogin(String crop, String userName, String pwd) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", crop);
        paramsMap.put("userid", userName);
        paramsMap.put("password", pwd);
        paramsMap.put("IMEI", TMSShareInfo.IMEI);
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.LOGIN_API + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(10 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    String json = TMSCommonUtils.decode(commonModel.getData());
                    UserModel model = new Gson().fromJson(json, UserModel.class);
                    boolean b = false;
                    String loginMsg = String.valueOf(SpuUtils.get(LoginActivity.this, "loginMsg", ""));
                    if (!loginMsg.equals("") && !loginMsg.equals("null")) {
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (UserModel model1 : mUserModelList) {
                            if (model1.getID().equals(model.getID())) {
                                b = true;
                            }
                        }

                        if (!b) {
                            model.setLoginTime(TMSCommonUtils.getTimeToday());
                            mUserModelList.add(0,model);
                            TMSShareInfo.mUserModelList.add(0,model);
                        } else {
                            model.setLoginTime(TMSCommonUtils.getTimeToday());
                            mUserModelList.add(0,model);
                            TMSShareInfo.mUserModelList.add(0,model);
                        }
                        SpuUtils.put(LoginActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                        if (model.getCorp().equals("xx")) {
                            TMSApplication.setDebug(true);
                        }
                    } else {
                        List<UserModel> mUserModelList = new ArrayList<UserModel>();
                        model.setLoginTime(TMSCommonUtils.getTimeToday());
                        mUserModelList.add(0,model);
                        TMSShareInfo.mUserModelList.add(0,model);
                        SpuUtils.put(LoginActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                    }
                    progressLL.setVisibility(View.GONE);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    progressLL.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "登錄失敗，請重新提交", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressLL.setVisibility(View.GONE);
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(LoginActivity.this, "網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登錄失敗，請重新提交", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            final AlertDialog alertDialog = builder.create();
            if (startWay != null) {
                if (startWay.equals("1")) {
                    finish();
                }
            }
            builder.setTitle("系統提示")
                    .setMessage("是否退出系統？")
                    .setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 關閉視窗
                                    alertDialog.dismiss();
                                }
                            })
                    .setNegativeButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 退出APP
                                    TMSApplication.exit();
                                }
                            });
            alertDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
