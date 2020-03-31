package com.fishinwater.a00data_questions.showpic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.alibaba.fastjson.JSONObject;
import com.fishinwater.a00data_questions.BR;
import com.fishinwater.a00data_questions.showpic.Utils.CommonUtil;
import com.fishinwater.a00data_questions.showpic.Utils.Info;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author fishinwater-1999
 * @date :2020/3/31 14:34
 */
public class PicViewModel extends BaseObservable {

    public final String TAG = getClass().getName();

    Info dataBean;

    private Context context;

    private PicViewModel() {
    }

    public PicViewModel(Info dataBean, Context context) {
        this.dataBean = dataBean;
        this.context = context;
    }

    public void init() {
        OkHttpClient okHttpClient = new OkHttpClient();
        //Form表单格式的参数传递
        FormBody formBody = new FormBody
                .Builder()
                //设置参数名称和参数值
                .add("m","nets_haojk")
                .build();
        Request request = new Request
                .Builder()
                //Post请求的参数传递
                .post(formBody)
                .url("https://tkmp.tmtreading.cn/app/index.php?i=2&t=2&v=1.0.0&from=wxapp&c=entry&a=wxapp&do=GetGoodPoster&sign=28ac5abff924bfa149a2e6343e173530")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("my_Test", e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try {
                    String jString = response.body().string();
                    String[] strings = jString.split(">");
                    dataBean = JSONObject.parseObject(strings[strings.length-1], Info.class);
                    setDataBean(dataBean);
                }catch (Exception e) {
                    Log.d(TAG,  e.getMessage() + "");
                }
            }
        });
    }

    @Bindable
    public String getDataCode() {
        return dataBean.getData().getCode();
    }

    @Bindable
    public String getDataMsg() {
        return dataBean.getData().getMsg();
    }

    @Bindable
    public String getDataRes() {
        return dataBean.getData().getRes();
    }

    @Bindable
    public void setDataCode(String code) {
        dataBean.getData().setCode(code);
        notifyPropertyChanged(BR.dataCode);
    }

    @Bindable
    public void setDataMsg(String msg) {
        dataBean.getData().setMsg(msg);
        notifyPropertyChanged(BR.dataMsg);
    }

    @Bindable
    public void setDataRes(String res) {
        dataBean.getData().setRes(res);
        notifyPropertyChanged(BR.dataRes);
    }

    public void setDataBean(Info dataBean) {
        this.dataBean = dataBean;
        setDataCode(dataBean.getData().getCode());
        setDataMsg(dataBean.getData().getMsg());
        setDataRes(dataBean.getData().getRes());
        downloadBitmap(dataBean.getData().getRes());
    }


    // 指纹图片存放路径
    public String sdCardDir = Environment.getExternalStorageDirectory() + "/fingerprintimages/";

    /**
     * 保存指纹图片
     *
     * @param url
     */
    private void downloadBitmap(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();//得到图片的流
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveBitmap(bitmap, "1_a_a_a_a.jpg");
                        //CommonUtil.saveBitmap2file(bitmap, context);
                    }
                }).start();

            }
        });
    }

    /*
     * 保存文件，文件名为当前日期
     */
    public void saveBitmap(Bitmap bitmap, String bitName){
        String fileName ;
        File file ;
        if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
        }else{  // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }
        file = new File(fileName);

        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
// 插入图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);

            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage() + "");
            e.printStackTrace();

        }
        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

    }

}
