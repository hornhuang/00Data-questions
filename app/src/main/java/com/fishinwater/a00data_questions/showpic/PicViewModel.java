package com.fishinwater.a00data_questions.showpic;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.alibaba.fastjson.JSONObject;
import com.fishinwater.a00data_questions.BR;
import com.fishinwater.a00data_questions.showpic.Utils.Info;

import java.io.IOException;

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

    private PicViewModel() {

    }

    public PicViewModel(Info dataBean) {
        this.dataBean = dataBean;
    }

    public void init() {
        OkHttpClient okHttpClient = new OkHttpClient();
        //Form表单格式的参数传递
        FormBody formBody = new FormBody
                .Builder()
                //设置参数名称和参数值
                .add("i","2")
                .add("t","2")
                .add("v","1.0.0")
                .add("from","wxapp")
                .add("c","entry")
                .add("a","wxapp")
                .add("do","GetGoodPoster")
                .add("sign","28ac5abff924bfa149a2e6343e173530")
                .build();
        Request request = new Request
                .Builder()
                //Post请求的参数传递
                .post(formBody)
                .url("https://tkmp.tmtreading.cn/app/index.php")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("my_Test", e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try {
                    dataBean = (Info) JSONObject.parse(response.body().string());
                    setDataBean(dataBean);
                }catch (Exception e) {
                    Log.d(TAG,  e.getMessage() + "");
                    setDataRes("https://tkmp.tmtreading.cn/addons/nets_haojk/cache/71c13eb73bc5bb9e3f8c1657f4190de0.jpg");
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
    }
}
