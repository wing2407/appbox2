package cn.com.hewoyi.appbox;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;

import java.util.List;

public class GridFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    int mNum;
    List<AppInfo> gridData;//一个Fragment的数据

    public GridFragment(int num, List<AppInfo> data) {
        //Log.i("ViewPagerFragment", num + "");
        gridData = data;
        mNum = num;
    }
    
 /*   public static GridFragment newInstance(int num, List<String> data) {
        gridData = data;
        GridFragment f = new GridFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putParcelable();
        f.setArguments(args);

        return f;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_list, container, false);


        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(new GridViewAdapter(getActivity(), gridData));
        gridView.setOnItemClickListener(this);//点击事件
        gridView.setOnItemLongClickListener(this);//长按事件


       /* TextView tv = (TextView)v.findViewById(R.id.text);
        tv.setText("Fragment #" + mNum);*/
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (gridData.get(position).isAD()) {
            //下载并安装
            NetworkInfo activeNetInfo = ((ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //wifi环境下载
                Toast.makeText(getActivity(), "广告列表" + gridData.get(position).getPackageName() + "开启下载", Toast.LENGTH_SHORT).show();
                HttpUtils httpUtils;
            } else {
                Toast.makeText(getActivity(), "广告列表" + gridData.get(position).getPackageName() + "请先连接wifi", Toast.LENGTH_SHORT).show();
            }

        } else if (gridData.get(position).getPackageName().equals("add")) {
            //弹出已安装应用列表，并想办法获取返回数据
            Toast.makeText(getActivity(), gridData.get(position).getPackageName() + "弹出添加已安装应用的列表", Toast.LENGTH_SHORT).show();
        } else {
            //打开已安装应用（对应packageName的应用）
            Toast.makeText(getActivity(), gridData.get(position).getPackageName() + "主界面", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (gridData.get(position).isAD()) {
            //不做操作
            Toast.makeText(getActivity(), "广告列表长按不做操作", Toast.LENGTH_SHORT).show();
        } else {
            //进入删除模式
            Toast.makeText(getActivity(), "长按" + gridData.get(position).getPackageName() + "删除模式", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
