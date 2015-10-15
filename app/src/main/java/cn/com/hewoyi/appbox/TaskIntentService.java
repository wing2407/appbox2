package cn.com.hewoyi.appbox;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class TaskIntentService extends IntentService {


    public TaskIntentService() {
        super("TaskIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        //读取配置文件中的rate频率
        SharedPreferences pref = getSharedPreferences("conService", MODE_PRIVATE);
        int rate = pref.getInt("rate", 5);//没有找到则传出默认值300
        //Alarm定时器
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int aTime = rate * 1000; // 默认，可以更改配置
        Intent i = new Intent(this, TaskIntentService.class);
        PendingIntent pi = PendingIntent.getService(this.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + aTime, pi);
        Log.i("TaskService","==============================>test");


    }
/*
    public JSONArray getTasks() {
        //从服务器获取任务列表
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://api.hewoyi.com.cn/v1/tasks?imei=" + telephonyManager.getDeviceId())
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            //设定频率，会写在配置文件里
            if (jsonObject.has("rate")) {
                int rate = jsonObject.getInt("rate");
                //Log.i("TaskService", rate + "");
                SharedPreferences.Editor editor = getSharedPreferences("conService", MODE_PRIVATE).edit();
                editor.putInt("rate", rate);
                editor.apply();
            }

            return jsonObject.getJSONArray("data");


    }*/

}
