package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import org.xutils.http.RequestParams;
import org.xutils.x;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        if (crop != null && userName != null && pwd != null) {
            if (!crop.equals("") && !userName.equals("") && !pwd.equals("")) {
                doLogin(crop, userName, pwd);
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
        TMSCommonUtils.checkAPPVersion(this);
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
                    UserModel model = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData()), UserModel.class);
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
                            mUserModelList.add(model);
                            TMSShareInfo.mUserModelList.add(model);
                        } else {
                            model.setLoginTime(TMSCommonUtils.getTimeToday());
                            mUserModelList.add(model);
                            TMSShareInfo.mUserModelList.add(model);
                        }
                        SpuUtils.put(LoginActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                    } else {
                        List<UserModel> mUserModelList = new ArrayList<UserModel>();
                        model.setLoginTime(TMSCommonUtils.getTimeToday());
                        mUserModelList.add(model);
                        TMSShareInfo.mUserModelList.add(model);
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
