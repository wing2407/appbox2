package cn.com.hewoyi.appbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class AddappAdapter extends BaseAdapter
{
    private Context mContext;
    private ListView lvApp;
    private List<AppInfo> listApp = null;

    public AddappAdapter(Context context,ListView listView, List<AppInfo> list){
        mContext = context;
        listApp = list;
        lvApp = listView;
    }

    @Override
    public int getCount()
    {
        return listApp.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public Object getItem(int position)
    {
        return listApp.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        AppInfo info = listApp.get(position);
        if(convertView == null)
        {
            View view = View.inflate(mContext, R.layout.activity_addapp_item, null);
            AppManagerViews views = new AppManagerViews();
            views.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_manager_icon);
            views.tv_app_name = (TextView) view.findViewById(R.id.tv_app_manager_name);
            views.cb_app_select = (CheckBox)view.findViewById(R.id.item_home_checked);
            views.iv_app_icon.setImageBitmap(BitmapFactory.decodeByteArray(info.getApp_icon(), 0, info.getApp_icon().length));
            views.tv_app_name.setText(info.getName());
            view.setTag(views);
            return view;
        }
        else {
            AppManagerViews views = (AppManagerViews) convertView.getTag();
            views.iv_app_icon.setImageBitmap(BitmapFactory.decodeByteArray(info.getApp_icon(), 0, info.getApp_icon().length));
            views.tv_app_name.setText(info.getName());
            updateChecked(position, views.cb_app_select);
            return convertView;
        }
    }

    public void updateChecked(int position, CheckBox select){
        if(lvApp.isItemChecked(position)){
            select.setChecked(true);
        } else {
            select.setChecked(false);
        }

    }
    /**
     * 用来优化listview的类
     * */
    private class AppManagerViews
    {
        ImageView iv_app_icon;
        TextView tv_app_name;
        CheckBox cb_app_select;
    }

}
