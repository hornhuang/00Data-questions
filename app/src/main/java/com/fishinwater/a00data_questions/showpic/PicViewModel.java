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
    }
}
