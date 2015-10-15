package cn.com.hewoyi.appbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityAddapp extends AppCompatActivity {

    private static final int GET_ALL_APP_FINISH = 1;//加载列表
    private static final int GET_CHECKED_NUM = 2;//更新button上的数目
    private static final int DELETE_DONE = 3;//删除完毕，重新加载列表的标志

    private ListView lv_app_manager;//应用信息列表
    private LinearLayout ll_app_manager_progress; //进度条
    private AppInfoProvider provider;//获取App列表的对象
    private AddappAdapter adapter;
    private List<AppInfo> list;

    private List<AppInfo> delete_list = new ArrayList<AppInfo>();//删除列表
    private Button btn_home_delete;
    private Boolean firstEnter = true;//判断首次加载列表

    private Toolbar toolbar;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_ALL_APP_FINISH:
                    //进度条设置为不可见
                    ll_app_manager_progress.setVisibility(View.GONE);
                    adapter = new AddappAdapter(ActivityAddapp.this, lv_app_manager, list);
                    lv_app_manager.setAdapter(adapter);
                    lv_app_manager.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    break;
                case GET_CHECKED_NUM:
                    btn_home_delete.setText("添加(已选中" + msg.arg1 + "项)");
                    break;
                case DELETE_DONE:
                    btn_home_delete.setText("添  加");
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addapp);

        //实例化
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_app_manager_progress = (LinearLayout) findViewById(R.id.ll_app_manager_progress);
        btn_home_delete = (Button) findViewById(R.id.btn_home_delete);

        //标题栏toolbar
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("");//设置Toolbar标题
        //toolbar.setSubtitle("  应用删除工具");
        toolbar.setTitleTextColor(Color.parseColor("#000000")); //设置标题颜色
        //toolbar.setSubtitleTextColor(Color.parseColor("#999999"));
        //toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    protected void onStart() {
        super.onStart();

        ll_app_manager_progress.setVisibility(View.VISIBLE);

        /**
         * //开一个线程用于完成对所有应用程序信息的搜索
         * 当搜索完成之后，就把一个成功的消息发送给Handler，
         * 然后handler把搜索到的数据设置进入listview里面  .
         * */
        new Thread() {
            public void run() {
                //加入looper才能使用全局context
                Looper.prepare();
                provider = new AppInfoProvider(ActivityAddapp.this);
                //获取应用列表
                list = provider.getAllApps();
                Message msg = new Message();
                msg.what = GET_ALL_APP_FINISH;
                handler.sendMessage(msg);
                Looper.loop();

            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //实时更新button选择的项目数量
                Message msg = new Message();
                msg.what = GET_CHECKED_NUM;
                msg.arg1 = lv_app_manager.getCheckedItemCount();
                handler.sendMessage(msg);

                //更新checkBox
                adapter.notifyDataSetChanged();

            }
        });

        btn_home_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //循环获取被选中的list item加入最后的删除list
                int length = lv_app_manager.getCheckedItemIds().length;
                for (int i = 0; i < length; i++) {
                    delete_list.add(list.get((int) lv_app_manager.getCheckedItemIds()[i]));
                    //Log.d("System", delete_list.get(i).getPackageName());
                }

                //保存list到数据库
                DBHandler dbHandler = DBHandler.getInstance(getApplicationContext());
                dbHandler.saveGridList(delete_list);
                //dbHandler.saveADList(delete_list);

                //Intent intent =getIntent();
               // setResult(RESULT_OK,intent);
                startActivity(new Intent(ActivityAddapp.this, MainActivity.class).putExtra("addapp", "addapp"));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(this, MainActivity.class));
        finish();
        Toast.makeText(this,"你取消了添加应用",Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                Toast.makeText(this,"你取消了添加应用",Toast.LENGTH_LONG).show();
                //startActivity(new Intent(ActivityAddapp.this, MainActivity.class).putExtra("addapp", "addapp"));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
