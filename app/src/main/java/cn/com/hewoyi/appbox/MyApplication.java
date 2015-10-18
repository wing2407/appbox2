package cn.com.hewoyi.appbox;

import android.app.Application;
import android.content.Intent;

public class MyApplication extends Application{

    public static boolean NeedUpdata;

    @Override
    public void onCreate() {
        super.onCreate();

        NeedUpdata = true;
    }
}
