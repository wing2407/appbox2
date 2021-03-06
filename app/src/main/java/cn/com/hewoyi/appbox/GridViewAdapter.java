package cn.com.hewoyi.appbox;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    List<AppInfo> gridData;


    public GridViewAdapter(Context c,List<AppInfo> data) {
        mContext = c;
        gridData =data;
    }

    @Override
    public int getCount() {
        return gridData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        //ImageView imageView;
        ViewHolder viewHolder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item,null);
            viewHolder  = new ViewHolder();
            viewHolder.appName_grid_item = (TextView)convertView.findViewById(R.id.appName_grid_item);
            viewHolder.delete_grid_item = (Button)convertView.findViewById(R.id.delete_grid_item);
            viewHolder.pb_text_grid_item = (TextView)convertView.findViewById(R.id.pb_text_grid_item);
            viewHolder.pb_grid_item= (ProgressBar)convertView.findViewById(R.id.pb_grid_item);
            viewHolder.iv_grid_item =(ImageView)convertView.findViewById(R.id.iv_grid_item);

            viewHolder.pb_grid_item.setVisibility(View.GONE);
            viewHolder.pb_text_grid_item.setVisibility(View.GONE);
            viewHolder.delete_grid_item.setVisibility(View.GONE);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder)convertView.getTag();

            //imageView = (ImageView) convertView;
        }

        viewHolder.appName_grid_item.setText(gridData.get(position).getName()+"");
        viewHolder.iv_grid_item.setImageBitmap(BitmapFactory.decodeByteArray(gridData.get(position).getApp_icon(),0,gridData.get(position).getApp_icon().length));
        return convertView;
       /* imageView.setImageResource(mThumbIds[position]);
        return imageView;*/

    }

    public class ViewHolder{
        ImageView iv_grid_item;
        ProgressBar pb_grid_item;
        TextView pb_text_grid_item;
        TextView appName_grid_item;
        Button delete_grid_item;

    }
}

