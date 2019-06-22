package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInPhotoInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.ClockInOrderStatusModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.PostPhotoService;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.ex.DbException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by John_Li on 20/5/2019.
 */

public class ClockInCustomerDetialActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private TextView noTv, nameTv, addressTv, contactTv, telTv, startTv, deliverTv, circleclockin;
    private ImageView hasClockInIv;
    private Dialog d;

    private ClockInOrderStatusModel mClockInOrderStatusModel;
    private File file;
    private File dir;
    private List<ClockInPhotoInfo> mClockInPhotoInfos;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_customer_detial);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.ci_customer_detial_head);
        noTv = findViewById(R.id.ci_customer_detial_no);
        nameTv = findViewById(R.id.ci_customer_detial_name);
        addressTv = findViewById(R.id.ci_customer_detial_address);
        contactTv = findViewById(R.id.ci_customer_detial_contact);
        telTv = findViewById(R.id.ci_customer_detial_tel);
        startTv = findViewById(R.id.ci_customer_detial_start);
        hasClockInIv = findViewById(R.id.ci_customer_detial_has_clock_in);
        deliverTv = findViewById(R.id.shortcut_customer_deliver);
        circleclockin = findViewById(R.id.shortcut_customer_invoice);
    }

    @Override
    public void setListener() {
        startTv.setOnClickListener(this);
        deliverTv.setOnClickListener(this);
        circleclockin.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mClockInOrderStatusModel = new Gson().fromJson(getIntent().getStringExtra("ClockInOrderStatusModel"), ClockInOrderStatusModel.class);
        headView.setLeft(this);
        headView.setTitle(String.valueOf(mClockInOrderStatusModel.getCustomerName()));

        noTv.setText("客戶編碼：" + String.valueOf(mClockInOrderStatusModel.getCustomerID()));
        nameTv.setText("客戶名稱：" + String.valueOf(mClockInOrderStatusModel.getCustomerName()));
        addressTv.setText("客戶地址：" + String.valueOf(mClockInOrderStatusModel.getCustomerAddress()));
        contactTv.setText("聯  絡  人：" + String.valueOf(mClockInOrderStatusModel.getContact()));
        telTv.setText("客戶電話：" + String.valueOf(mClockInOrderStatusModel.getTelephone()));
        try {
            mClockInPhotoInfos = TMSApplication.db.selector(ClockInPhotoInfo.class).where("customer_id","=",mClockInOrderStatusModel.getCustomerID()).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (IsThereAnAppToTakePictures()) {      // 判斷是否掛起相機
            // 創建相冊
            CreateDirectoryForPictures();
        } else {
            Toast.makeText(this, "您的手機暫未開啟拍照權限，請先開啟！", Toast.LENGTH_SHORT).show();
        }

        checkHasClockIn();
    }

    /**
     * 判斷是否可以正常叫起相機
     **/
    private boolean IsThereAnAppToTakePictures() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
        PackageManager packageManager = this.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    /**
     * 在外部儲存裝置中建立起檔案夾並且使用當作暫存
     **/
    private void CreateDirectoryForPictures() {
        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PSAForMaterialAlbum");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.head_left:
                intent = new Intent();
                intent.putExtra("result", "1");
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.ci_customer_detial_start:
                // 未拜訪過的客戶強制要求拍簽到照片
                //showSingInDialog();
                openCamare(10);
                break;
            case R.id.shortcut_customer_deliver:
                intent = new Intent();
                intent.putExtra("result", "2");
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.shortcut_customer_invoice:
                intent = new Intent();
                intent.putExtra("result", "3");
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void showSingInDialog() {
        View view = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        view = LayoutInflater.from(this).inflate(R.layout.dialog_sign_in, null);
        builder.setView(view);
        d = builder.create();
        d.requestWindowFeature(6);
        d.setCancelable(false);
        // 初始化控件
        LinearLayout sign_in_take_photo = view.findViewById(R.id.sign_in_take_photo);
        TextView sign_in_cancel = view.findViewById(R.id.sign_in_cancel);
        sign_in_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamare(10);
            }
        });
        sign_in_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    private void checkHasClockIn() {
        if (mClockInPhotoInfos != null) {
            if(mClockInPhotoInfos.size() > 0) {
                // 將之前的數據加載入視圖
                hasClockInIv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 打開相機
     **/
    private void openCamare(int requestCode) {
        //使用intent 叫起拍照動作
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //回存的檔名
        file = new File(dir, String.format("Material" + Calendar.getInstance().getTimeInMillis() + ".jpg", UUID.randomUUID().toString()));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        //等待結果的呼叫Activity
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return ;
        }

        // 讓此可以在圖片庫中被使用
        // 這一段不寫不會影響功能只是在圖片庫中，並不會顯示此張照片
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);

        switch (requestCode) {
            case 10:
                saveInDB("00011", file.getPath());
                break;
        }
    }

    /**
     * 将照片存入数据库
     **/
    private void saveInDB(String type, String filePath) {
        ClockInPhotoInfo info = new ClockInPhotoInfo();
        info.setImageUrl(filePath);;
        info.setRemark("");
        info.setType(type);
        info.setStatus(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        info.setTime(sdf.format(new Date()));
        info.setCustomerID(mClockInOrderStatusModel.getCustomerID());
        try {
            TMSApplication.db.save(info);
            // 加入IntentService队列
            try {
                List<ClockInPhotoInfo> clockInPhotoInfoList = TMSApplication.db.selector(ClockInPhotoInfo.class).where("status", "!=", "1").findAll();
                for (ClockInPhotoInfo clockInPhotoInfo : clockInPhotoInfoList) {
                    Intent intent = new Intent(this, PostPhotoService.class);
                    intent.putExtra("ClockInPhotoInfo", new Gson().toJson(clockInPhotoInfo));
                    this.startService(intent);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            try {
                mClockInPhotoInfos = TMSApplication.db.selector(ClockInPhotoInfo.class).where("customer_id","=",mClockInOrderStatusModel.getCustomerID()).findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }
            checkHasClockIn();
            if (d != null) {
                d.dismiss();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
