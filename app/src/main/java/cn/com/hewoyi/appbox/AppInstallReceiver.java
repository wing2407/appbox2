package cn.com.hewoyi.appbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class AppInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();
        String packageName = intent.getData().getSchemeSpecificPart();
        String appName = "";
        DBHandler dbHandler = DBHandler.getInstance(context.getApplicationContext());
        try {
            appName = manager.getPackageInfo(packageName, 0).applicationInfo.loadLabel(manager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            //代号1是指新增的app
            dbHandler.saveIn(1,appName,packageName);
           // Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            //代号2是指更新的app
            dbHandler.saveIn(2,appName,packageName);
            //Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();
        }

        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            //代号3是指卸载的app
            dbHandler.saveIn(3,appName,packageName);
            //Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
        }


    }
}
