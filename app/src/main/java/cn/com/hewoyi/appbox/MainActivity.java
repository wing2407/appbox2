package cn.com.hewoyi.appbox;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    ViewPagerAdapter mAdapter;
    ViewPager mPager;
    List<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

      /*  LinearLayout ad_main = (LinearLayout) findViewById(R.id.ad_main);
        ad_main.setVisibility(View.GONE);*/

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.container);
       /* LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
        // 取控件aaa当前的布局参数
        linearParams.height = dip2px(this, 395); // 当控件的高强制设成适配父窗口
        relativeLayout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件relatucelayout
*/
        LinearLayout group = (LinearLayout) findViewById(R.id.viewGroup);
        mPager = (ViewPager) findViewById(R.id.viewpager);
        data = initData();
        //这里传入参数比较多，目的让Activity的操作代码尽量简洁
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, group, mPager, data);
        mPager.setAdapter(mAdapter);


    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppInfo> appList = new ArrayList<AppInfo>();


                String url = "http://192.168.1.10/test.json";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try {
                    //获取首次response然后json解析
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = JSON.parseObject(response.body().string());//这里的response.body().string()似乎只能使用一次，第二次会出现问题
                    Log.i("MainActivity", "response-->" + jsonObject.toString());

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.i("MainActivity", "Array-->" + jsonArray.toString());
                    if (jsonArray != null) {
                        //日期作为数据库的表名
                        int day = jsonObject.getIntValue("ver");
                        Log.i("MainActivity", "ver-->" + day);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            Log.i("MainActivity", "info_array-->" + jsonArray.getString(i));
                            //循环获取apps_info
                            Request request_info = new Request.Builder().url("http://192.168.1.10/" + jsonArray.getString(i) + "/info").build();
                            //根据每个ID的json接口获取对应的信息
                            Response info_app = client.newCall(request_info).execute();
                            JSONObject info_json = JSON.parseObject(info_app.body().string());
                            JSONObject info_data = info_json.getJSONObject("data");
                            Log.i("MainActivity", "info_json-->id:" + info_data.getString("_id") + ",   name:" + info_data.getString("name") + ",   pack:" + info_data.getString("packagename"));

                            //获取泛型appinfo的各项信息
                            AppInfo appInfo = new AppInfo();
                            appInfo.set_id(info_data.getString("_id"));
                            appInfo.setName(info_data.getString("name"));
                            appInfo.setPackageName(info_data.getString("packagename"));
                            Log.i("MainActivity", "appInfo-->id:" + appInfo.get_id() + ",   name:" + appInfo.getName() + ",   pack:" + appInfo.getPackageName());

                            //根据每个ID的json接口获取对应的信息
                            Request request_icon = new Request.Builder().url("http://192.168.1.10/" + jsonArray.getString(i) + "/download.png").build();
                            Response info_icon = client.newCall(request_icon).execute();
                            appInfo.setApp_icon(info_icon.body().bytes());
                            Log.i("MainActivity", "app_icon-->length:" + appInfo.getApp_icon().length + "");
                            //加入list
                            appList.add(appInfo);
                        }
                        //解析得到list存入数据库
                        DBHandler dbHandler = DBHandler.getInstance(getBaseContext(), day);
                        dbHandler.saveList(appList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();


      /*  //if数据库中,该日期作为version的表存在则不创建，否则创建
        DBHelper dbHelper = new DBHelper(getBaseContext(), "apps.db", null, 150929);
        dbHelper.getWritableDatabase();*/

    }

    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private List<String> initData() {
        List<String> init = new ArrayList<String>();
        for (int i = 0; i < 25; i++) {
            init.add("" + i);
        }
        return init;
    }

}
