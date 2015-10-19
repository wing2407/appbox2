package cn.com.hewoyi.appbox;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.List;


public class MainActivity extends AppCompatActivity {


    ViewPagerAdapter mAdapter;  //主列表适配器
    ViewPager mPager;           //主界面
    LinearLayout group;         //小圆点布局
    RelativeLayout relativeLayout;//广告布局
    ViewPagerAdapter adAdapter; //广告列表适配器
    ViewPager adPager;          //广告列表

    public boolean DELETE_MODE = false;//删除模式标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.container);
        mPager = (ViewPager) findViewById(R.id.viewpager);
        group = (LinearLayout) findViewById(R.id.viewGroup);
        adPager = (ViewPager) findViewById(R.id.ad_viewpager);

        //友盟自动更新
        UmengUpdateAgent.silentUpdate(this);
        UmengUpdateAgent.setDeltaUpdate(false);
        //友盟统计
        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        DBHandler dbHandler = DBHandler.getInstance(getApplicationContext());

        // //访问数据库加载主界面gridVIew数据,这里传入参数比较多，目的让Activity的操作代码尽量简洁
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext(), dbHandler.loadGridList(), group, mPager, 12);
        mPager.setAdapter(mAdapter);

        String ver = getSharedPreferences("dbTable", MODE_PRIVATE).getString("oldVer", "old");
        //ver为默认值,证明数据库还没有存在从接口拿下来的数据,否则访问数据库得到数据
        if (ver.equals("old")) {
            findViewById(R.id.ad_main).setVisibility(View.GONE);//隐藏广告栏
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
            // 取控件aaa当前的布局参数
            linearParams.height = dip2px(this, 395); // 当控件的高强制设成适配父窗口
            relativeLayout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件relatucelayout
        } else {
            //加载广告的数据list,不为空则显示，前面有！号
            List<AppInfo> adList = dbHandler.loadADList();
            if (!adList.isEmpty()) {
                findViewById(R.id.noAppTitle).setVisibility(View.GONE);//隐藏文本
                //这里传入参数比较多，目的让Activity的操作代码尽量简洁
                adAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext(), adList, null, adPager, 4);
                adPager.setAdapter(adAdapter);
            }else {
                findViewById(R.id.noAppTitle).setVisibility(View.VISIBLE);//显示文本（包罗万象）

            }

        }

        findViewById(R.id.nextAppBtn).setOnClickListener(new View.OnClickListener() {

            boolean flag = adPager.isShown() ? adPager.getCurrentItem() != adAdapter.getCount() : true;//滚动判断标志,别改成||，就这样写！

            @Override
            public void onClick(View v) {

                if (flag) {
                    adPager.setCurrentItem(adPager.getCurrentItem() + 1);
                    if (adPager.getCurrentItem() == (adAdapter.getCount() - 1)) {
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

        //确保启动的时候只拿一次广告列表数据
        if (MyApplication.NeedUpdata) {
            //从接口拿数据,从应用启动开始就获取，避免Activity的销毁创建而多次启动
            startService(new Intent(this, UpdataIntentService.class));
            //启动定时任务
            startService(new Intent(this, TaskIntentService.class));
        }
        //友盟启动次数统计
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);

    }

    @Override
    public void onBackPressed() {
        if (DELETE_MODE) {
            //删除模式重启activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        super.onBackPressed();
    }

    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}
