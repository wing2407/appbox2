package cn.com.hewoyi.appbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //开机启动task服务
       context.startService(new Intent(context,TaskIntentService.class));
    }
}
