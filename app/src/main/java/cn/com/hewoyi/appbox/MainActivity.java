package cn.com.hewoyi.appbox;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        SharedPreferences prefer = getSharedPreferences("dbVersion", MODE_PRIVATE);
        int ver = prefer.getInt("ver", 1);//没有则读出默认值1
        //根据ver访问数据库
        if (ver != 1) {
            DBHandler dbHandler = DBHandler.getInstance(this, ver);
            List<AppInfo> appsList = dbHandler.loadList();
            for (AppInfo info : appsList) {
                Log.i("MainActivity", info.getPackageName());
            }
            Log.i("MainActivity", "shpre-->ver:" + ver);
            dbHandler.closeDB();
        }
        startService(new Intent(this, UpdataIntentService.class));
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
