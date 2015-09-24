package cn.com.hewoyi.appbox;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity {

    private GridView gridView;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        LinearLayout ad_main = (LinearLayout) findViewById(R.id.ad_main);
        ad_main.setVisibility(View.GONE);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        // 取控件aaa当前的布局参数
        linearParams.height = dip2px(this, 395); // 当控件的高强制设成适配父窗口
        frameLayout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件frameLayout


        GridView gridView = (GridView) findViewById(R.id.gridView_main);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, position + "!~" + id, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"click:"+position+" id:"+id,Toast.LENGTH_LONG).show();
            }
        });

        adapter = new ImageAdapter();
        // 设置一个适配器
        gridView.setAdapter(adapter);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int[] images = {R.drawable.abc_ab_share_pack_mtrl_alpha, R.drawable.abc_btn_borderless_material, R.drawable.abc_btn_check_material,
            R.drawable.abc_btn_check_to_on_mtrl_000, R.drawable.abc_btn_default_mtrl_shape};

    public class ImageAdapter extends BaseAdapter {

        public ImageAdapter() {
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return images[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
			 * 1.手工创建对象 2.加载xml文件
			 */
            ImageView imageView = null;
            if (convertView == null) {
                imageView = new ImageView(MainActivity.this);
            } else {
                imageView = (ImageView) convertView;
            }
            // 设置GridView的显示的个子的间距
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(30, 20, 20, 20);
            imageView.setImageResource(images[position]);
            return imageView;
        }
    }


}
