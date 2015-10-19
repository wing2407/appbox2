package cn.com.hewoyi.appbox;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GridFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    int mNum;
    List<AppInfo> gridData;//一个Fragment的数据
    GridView gridView;
    DBHandler dbHandler = DBHandler.getInstance(getActivity());

    public static final int REQUEST_INSTALL = 1;//安装成功标志


    public static GridFragment newInstance(int num, List<AppInfo> data) {


        ArrayList<AppInfo> list = new ArrayList<AppInfo>();
        for (AppInfo info : data) {
            if (info != null) {
                list.add(info);
            }
        }
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putParcelableArrayList("f_list", list);
        GridFragment f = new GridFragment();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNum = getArguments().getInt("num");
            gridData = getArguments().getParcelableArrayList("f_list");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_list, container, false);


        gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(new GridViewAdapter(getActivity(), gridData));
        gridView.setOnItemClickListener(this);//点击事件
        gridView.setOnItemLongClickListener(this);//长按事件
        return v;
    }

    /*
    启动的Activity返回时的处理函数，主要是根据requestCode和resultCode进行install成功
    或者失败的信息展示
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL) {
            if (resultCode == Activity.RESULT_OK) {
                //安装成功则获取包名,并清除相应缓存
                AppInfo info = install_list.get(install_list.size() - 1);
                Toast.makeText(getActivity(), info.getName() + "  安装成功，正在启动..", Toast.LENGTH_SHORT).show();
                install_list.remove(info);
                //保存到数据库
                dbHandler.saveInstall(info.getPackageName());

                List<AppInfo> saveinstall = new ArrayList<AppInfo>();
                saveinstall.add(info);
                dbHandler.saveGridList(saveinstall);


                startActivity(new Intent(getActivity(), MainActivity.class));
                startActivity(new Intent(getActivity().getPackageManager().getLaunchIntentForPackage(info.getPackageName())));
                getActivity().finish();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "你取消了安装!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "安装错误!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (gridData.get(position).isAD()) {
            //下载并安装
            NetworkInfo activeNetInfo = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //wifi环境下载并安装
                downAndInstall(gridData.get(position), view);
                Toast.makeText(getActivity(), gridData.get(position).getPackageName() + "开启下载", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "请先连接wifi", Toast.LENGTH_SHORT).show();
            }

        } else if (gridData.get(position).getPackageName().equals("add")) {
            //弹出已安装应用的Acitivity(返回数据在ActivityAddapp中的点击事件里，保存到数据库)
            getActivity().startActivity(new Intent(getActivity(), ActivityAddapp.class));
            getActivity().finish();
        } else {
            //打开已安装应用（对应packageName的应用）
            startActivity(new Intent(getActivity().getPackageManager().getLaunchIntentForPackage(gridData.get(position).getPackageName())));
            //Toast.makeText(getActivity(), gridData.get(position).getPackageName() + "主界面", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (gridData.get(position).isAD()) {
            //不做操作
            //Toast.makeText(getActivity(), "广告列表长按不做操作", Toast.LENGTH_SHORT).show();
        } else if (gridData.get(position).getPackageName().equals("add")) {
            //添加图标。。。不做操作，且不出现delete按钮
        } else {
            //进入删除模式
            //Toast.makeText(getActivity(), "长按删除模式", Toast.LENGTH_SHORT).show();
            deleteMode();
        }
        return true;
    }

    List<AppInfo> install_list = new ArrayList<AppInfo>();

    private void downAndInstall(final AppInfo info, View view) {
        HttpUtils httpUtils = new HttpUtils();
        final GridViewAdapter.ViewHolder holder = (GridViewAdapter.ViewHolder) view.getTag();
        HttpHandler handler = httpUtils.download("http://192.168.1.10/" + info.get_id() + "/download", "/sdcard/Download/" + info.getName() + ".apk",
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                        holder.pb_grid_item.setVisibility(View.VISIBLE);
                        holder.pb_text_grid_item.setVisibility(View.VISIBLE);
                        changeLight(holder.iv_grid_item, -80);
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        holder.pb_text_grid_item.setText((int) current / total + "%");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        //下载完成后隐藏进度条
                        holder.pb_grid_item.setVisibility(View.GONE);
                        holder.pb_text_grid_item.setVisibility(View.GONE);
                        changeLight(holder.iv_grid_item, 0);
                        install_list.add(info);//加入缓存list
                        //执行安装
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.setData(Uri.fromFile(new File("/sdcard/Download/" + info.getName() + ".apk")));
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, info.getPackageName());
                        startActivityForResult(intent, REQUEST_INSTALL);
                    }


                    @Override
                    public void onFailure(HttpException error, String msg) {

                        holder.pb_grid_item.setVisibility(View.GONE);
                        holder.pb_text_grid_item.setVisibility(View.GONE);
                        changeLight(holder.iv_grid_item, 0);
                        if (msg.equals("maybe the file has downloaded completely")) {
                            install_list.add(info);

                            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(Uri.fromFile(new File("/sdcard/Download/" + info.getName() + ".apk")));
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, info.getPackageName());
                            startActivityForResult(intent, REQUEST_INSTALL);

                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteMode() {
        View animView;
        Button deleteBtn;
        ((MainActivity) getActivity()).DELETE_MODE = true;
        for (int i = 0; i < gridView.getChildCount(); i++) {
            animView = gridView.getChildAt(i);
            AppInfo info = gridData.get(i);
            if (!info.getPackageName().equals("add")) {
                animView.startAnimation(createFastRotateAnimation());
                deleteBtn = (Button) animView.findViewById(R.id.delete_grid_item);
                gridView.setOnItemClickListener(null);
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setTag(i);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i("GridFragment", (int) v.getTag() + "");
                        ((View) v.getParent()).setVisibility(View.INVISIBLE);
                        dbHandler.deleteGridList(gridData.get((int) v.getTag()));

                    }
                });
            }
        }
    }

    private Animation createFastRotateAnimation() {
        Animation rotate = new RotateAnimation(-2.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setDuration(60);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        return rotate;
    }

    //改变亮度，方便进度显示
    private void changeLight(ImageView imageview, int brightness) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0,
                brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        imageview.setColorFilter(new ColorMatrixColorFilter(matrix));
    }
}
