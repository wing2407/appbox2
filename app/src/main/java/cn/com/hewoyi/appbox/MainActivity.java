package cn.com.hewoyi.appbox;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    ViewPagerAdapter mAdapter;
    ViewPager mPager;

    ViewPagerAdapter adAdapter;
    ViewPager adPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.container);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        //访问数据库加载主界面gridVIew数据
        DBHandler dbHandler = DBHandler.getInstance(this);
        List<AppInfo> gridList = dbHandler.loadGridList();
        //加入额外的addApp图标
        gridList.add(new AppInfo("添加应用", "add", "add", bitmapToBytes(BitmapFactory.decodeResource(getResources(), R.drawable.addapp)), false));
        LinearLayout group = (LinearLayout) findViewById(R.id.viewGroup);
        //这里传入参数比较多，目的让Activity的操作代码尽量简洁
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, group, mPager, gridList, 12);
        mPager.setAdapter(mAdapter);


        adPager = (ViewPager) findViewById(R.id.ad_viewpager);
        String ver = getSharedPreferences("dbTable", MODE_PRIVATE).getString("oldVer", "old");
        //ver为默认值,证明数据库还没有存在从接口拿下来的数据,否则访问数据库得到数据
        if (ver.equals("old")) {
            findViewById(R.id.ad_main).setVisibility(View.GONE);//隐藏广告栏
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
            // 取控件aaa当前的布局参数
            linearParams.height = dip2px(this, 395); // 当控件的高强制设成适配父窗口
            relativeLayout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件relatucelayout
        } else {
            //加载广告的数据list
            List<AppInfo> adList = dbHandler.loadADList();
            //这里传入参数比较多，目的让Activity的操作代码尽量简洁
            if (adList != null) {
                findViewById(R.id.noAppTitle).setVisibility(View.GONE);//隐藏文本
                adAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, null, adPager, adList, 4);
                adPager.setAdapter(adAdapter);
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        findViewById(R.id.nextAppBtn).setOnClickListener(new View.OnClickListener() {
            boolean flag = true;//滚动判断标志
            @Override
            public void onClick(View v) {

                if (flag) {
                    adPager.setCurrentItem(adPager.getCurrentItem() + 1);
                    if (adPager.getCurrentItem() == (adAdapter.getCount()-1)) {
                        flag = false;
                    }
                } else {
                    adPager.setCurrentItem(adPager.getCurrentItem() - 1);
                    if (adPager.getCurrentItem() == 1) {
                        flag = true;
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //从接口拿数据
        startService(new Intent(this, UpdataIntentService.class));

    }

    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
