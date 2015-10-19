package cn.com.hewoyi.appbox;

import android.app.Application;
import android.content.Intent;

public class MyApplication extends Application{

    public static boolean NeedUpdata;//重启MainActivity的不再次开启服务的标志

    @Override
    public void onCreate() {
        super.onCreate();

            NeedUpdata = true;
    }
}
