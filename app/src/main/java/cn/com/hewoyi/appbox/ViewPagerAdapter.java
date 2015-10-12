package cn.com.hewoyi.appbox;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    //生成的fragment个数
    private int NUM_ITEMS;
    //每个Fragment容纳的图标数，主界面是12个，广告栏为4个
    private int GRID_ITEM_COUNT;

    private Context mContext;
    private LinearLayout group;//导航圆点布局
    private ViewPager mViewPager;//滑动布局
    private ImageView[] imageViews;//导航图片
    private ImageView imageView;
    private List<AppInfo> data;

    public ViewPagerAdapter(FragmentManager fm, Context context, LinearLayout group, ViewPager viewPager, List<AppInfo> list, int pagerLength) {
        super(fm);
        this.mContext = context;
        this.group = group;
        this.mViewPager = viewPager;
        this.data = list;
        this.GRID_ITEM_COUNT = pagerLength;

        NUM_ITEMS = list.size() / GRID_ITEM_COUNT +1;//计算产生Fragment的数量，一个fragment一个gridview，一个gridview有GRID_ITEM_COUNT个图标
        if (group != null) {
            initCirclePoint();
            mViewPager.setOnPageChangeListener(new AdPageChangeListener());//滑动监听
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        //new新的Fragment(当数量大于每一页的规定数),并传入相应的data
        return new GridFragment(position, data.subList(position * GRID_ITEM_COUNT,
                position * GRID_ITEM_COUNT + GRID_ITEM_COUNT > data.size() ? data.size() : position * GRID_ITEM_COUNT + GRID_ITEM_COUNT));

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;

    }

    //动态增长小圆点导航
    private void initCirclePoint() {
        imageViews = new ImageView[NUM_ITEMS];
        //小圆点图标
        for (int i = 0; i < NUM_ITEMS; i++) {
            //创建一个ImageView, 并设置宽高. 将该对象放入到数组中
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
            imageViews[i] = imageView;

            //初始值, 默认第0个选中
            if (i == 0) {
                imageViews[i]
                        .setBackgroundResource(android.R.drawable.presence_online);
            } else {
                imageViews[i]
                        .setBackgroundResource(android.R.drawable.presence_invisible);
            }
            //将小圆点放入到布局中
            group.addView(imageViews[i]);
        }
    }

    /**
     * ViewPager 页面改变监听器
     */
    private final class AdPageChangeListener implements ViewPager.OnPageChangeListener {

        /**
         * 页面滚动状态发生改变的时候触发
         */
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        /**
         * 页面滚动的时候触发
         */
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        /**
         * 页面选中的时候触发
         */
        @Override
        public void onPageSelected(int arg0) {
            //重新设置原点布局集合
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0]
                        .setBackgroundResource(android.R.drawable.presence_online);
                if (arg0 != i) {
                    imageViews[i]
                            .setBackgroundResource(android.R.drawable.presence_invisible);
                }
            }
        }
    }
}
