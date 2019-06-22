package com.youcoupon.john_li.transportationapp.TMSUtils;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInPhotoInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.PostPhoto;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by John_Li on 20/5/2019.
 */

public class PostPhotoService extends IntentService {

    public PostPhotoService()
    {
        super("PostPhotoService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ClockInPhotoInfo info = new Gson().fromJson(intent.getStringExtra("ClockInPhotoInfo"), ClockInPhotoInfo.class);
        if (info != null) {
            sendPhotoToServer(info);
        }
    }

    private void sendPhotoToServer(final ClockInPhotoInfo info) {
        PostPhoto photo = new PostPhoto();
        photo.setImageType(info.getType());
        photo.setPhotoTime(info.getTime());
        photo.setRemark(info.getRemark());
        photo.setPhoto(TMSCommonUtils.compressImageFromFile(info.getImageUrl()));

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor72(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor72(this).getID());
        paramsMap.put("salesmanid", TMSCommonUtils.getUserFor72(this).getSalesmanID());
        paramsMap.put("customerid", info.getCustomerID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.POST_PHOTO + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setAsJsonContent(true);
        params.setConnectTimeout(100 * 1000);

        params.setCacheSize(3000 * 1000);
        params.setBodyContent(new Gson().toJson(photo));
        String uri = params.getUri();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    info.setStatus(1);
                    TMSApplication.db.saveOrUpdate(info);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Toast.makeText(PostPhotoService.this, "照片提交成！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                try {
                    info.setStatus(2);
                    TMSApplication.db.saveOrUpdate(info);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Toast.makeText(PostPhotoService.this, "照片提交失敗！請重試", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
