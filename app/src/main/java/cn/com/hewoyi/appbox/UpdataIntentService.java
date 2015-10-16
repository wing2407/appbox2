package cn.com.hewoyi.appbox;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UpdataIntentService extends IntentService {


    public UpdataIntentService() {
        super("UpdataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        List<AppInfo> appList = new ArrayList<AppInfo>();
        String url = "http://192.168.1.8/json";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            //获取首次response然后json解析
            Response response = client.newCall(request).execute();
            //这里的response.body().string()似乎只能使用一次，第二次会出现问题
            String responseText = response.body().string();
            //如果接口没有任何数据文本，则不操作，以免报错，注意前面的！号
            if (!responseText.isEmpty()&&responseText!=null) {
                JSONObject jsonObject = JSON.parseObject(responseText);
                //Log.i("MainActivity", "response-->" + jsonObject.toString());

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                //Log.i("MainActivity", "Array-->" + jsonArray.toString());

                //数据库的表名
                String newTable = jsonObject.getString("ver");
                Log.i("MainActivity", "json_ver-->" + newTable);

                SharedPreferences prefer = getSharedPreferences("dbTable", MODE_PRIVATE);
                String oldTable = prefer.getString("oldVer", "old");//没有则读出默认值 空字符串
                //如果获取到的ver和本地的不符，则从服务器拿数据,前面有!号  //存储表名,不要改为apply()
                if ((!newTable.equals(oldTable))&&(prefer.edit().putString("newVer", newTable).commit())) {

                    for (int i = 0; i < jsonArray.size(); i++) {
                        //Log.i("MainActivity", "info_array-->" + jsonArray.getString(i));
                        //循环获取apps_info
                        Request request_info = new Request.Builder().url("http://192.168.1.8/" + jsonArray.getString(i) + "/info").build();
                        //根据每个ID的json接口获取对应的信息
                        Response info_app = client.newCall(request_info).execute();
                        JSONObject info_json = JSON.parseObject(info_app.body().string());
                        JSONObject info_data = info_json.getJSONObject("data");
                        //Log.i("MainActivity", "info_json-->id:" + info_data.getString("_id") + ",   name:" + info_data.getString("name") + ",   pack:" + info_data.getString("packagename"));

                        //获取泛型appinfo的各项信息
                        AppInfo appInfo = new AppInfo();
                        appInfo.set_id(info_data.getString("_id"));
                        appInfo.setName(info_data.getString("name"));
                        appInfo.setPackageName(info_data.getString("packagename"));
                        //Log.i("MainActivity", "appInfo-->id:" + appInfo.get_id() + ",   name:" + appInfo.getName() + ",   pack:" + appInfo.getPackageName());

                        //根据每个ID的json接口获取对应的信息
                        Request request_icon = new Request.Builder().url("http://192.168.1.8/" + jsonArray.getString(i) + "/download.png").build();
                        Response info_icon = client.newCall(request_icon).execute();
                        appInfo.setApp_icon(info_icon.body().bytes());
                        //Log.i("MainActivity", "app_icon-->length:" + appInfo.getApp_icon().length + "");
                        //加入list
                        appList.add(appInfo);
                    }
                    /*SharedPreferences.Editor editor = getSharedPreferences("dbTable", MODE_PRIVATE).edit();
                    editor.putString();
                    editor.commit();*/
                    for(AppInfo info:appList){
                        Log.i("IntentService","info-->"+info.get_id()+info.getName()+info.getPackageName()+info.getApp_icon().toString());
                    }
                    //解析得到list存入数据库
                    DBHandler handlerDB = DBHandler.getInstance(getApplicationContext());
                    handlerDB.saveADList(appList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
