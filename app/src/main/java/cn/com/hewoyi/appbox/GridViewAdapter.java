package cn.com.hewoyi.appbox;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2015/9/22.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mData;

    public GridViewAdapter(Context c,List<String> data) {
        mContext = c;
        mData =data;
    }

    public int getCount() {
        return mData.size();
        //return mThumbIds.length;
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
        TextView textView;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item,null);
            textView = (TextView)convertView.findViewById(R.id.appName_grid_item);
            convertView.setTag(textView);

            // if it's not recycled, initialize some attributes
           /* imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);*/
        } else {
            textView = (TextView)convertView.getTag();

            //imageView = (ImageView) convertView;
        }

        textView.setText(mData.get(position)+"");
        return convertView;
       /* imageView.setImageResource(mThumbIds[position]);
        return imageView;*/

    }

    // references to our images
    private Integer[] mThumbIds = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,


    };
}

