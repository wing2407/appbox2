package cn.com.hewoyi.appbox;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.util.ArrayList;
import java.util.List;


public class DBHandler {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "apps";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static DBHandler DBHandler;
    private SQLiteDatabase db;

    private SharedPreferences shpre;

    /**
     * 将构造方法私有化
     */
    private DBHandler(Context context) {
        shpre = context.getSharedPreferences("dbTable", context.MODE_PRIVATE);

        DBHelper dbHelper = new DBHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取DB的实例。
     */
    public synchronized static DBHandler getInstance(Context context) {
        if (DBHandler == null) {
            DBHandler = new DBHandler(context);
        }
        return DBHandler;
    }

    //接口有新数据时候增加的表
    private String createTable(String tableName) {
        return "create table if not exists " + tableName + "(" +
                "id integer primary key autoincrement, " +
                "app_id text, " +
                "name text, " +
                "packagename text, " +
                "icon blob)";
    }


    /**
     * 数据库保存list
     *
     * @param appInfos
     */
    public synchronized void saveADList(List<AppInfo> appInfos) {
        //String newTable = shpre.getString("newVer", "");


        //不为空时操作，前面有!号
        if (!appInfos.isEmpty()) {
            String newTable = shpre.getString("newVer", "");
            String oldTable = shpre.getString("oldVer", "old");

            //表名不同的时候插入数据,前面有！号
            if (!newTable.equals(oldTable)) {
                db.beginTransaction();//开启事务操作

                //创建新表
                db.execSQL(createTable(newTable));
                //得到处理过的list则插入数据表
                ContentValues values = new ContentValues();
                for (AppInfo info : appInfos) {
                    values.put("app_id", info.get_id());
                    values.put("name", info.getName());
                    values.put("packagename", info.getPackageName());
                    values.put("icon", info.getApp_icon());
                    db.insert(newTable, null, values);
                    //循环使用values
                    values.clear();
                    //Log.i("DBHandler", info.get_id());
                }

                db.execSQL("drop table if exists " + oldTable);//删除旧表
                shpre.edit().putString("oldVer", newTable).apply();//执行完操作newVer变为oldVer
                db.setTransactionSuccessful();//事务成功
                db.endTransaction();
            }
        }
    }

    /**
     * 从数据库读取list
     *
     * @return
     */
    public synchronized List<AppInfo> loadADList() {

        String oldTable = shpre.getString("oldVer", "old");

        ArrayList<AppInfo> list = new ArrayList<AppInfo>();
        try {
            db.beginTransaction();//开启事务操作

            Cursor cursor = db.rawQuery("select distinct * from " + oldTable, null);
            if (cursor.moveToFirst()) {
                do {
                    AppInfo appInfo = new AppInfo();
                    appInfo.set_id(cursor.getString(cursor.getColumnIndex("app_id")));
                    appInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                    appInfo.setPackageName(cursor.getString(cursor.getColumnIndex("packagename")));
                    appInfo.setApp_icon(cursor.getBlob(cursor.getColumnIndex("icon")));
                    appInfo.setIsAD(true);//广告list
                    list.add(appInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();

            //过滤已安装的packageName
            Cursor cs_install = db.rawQuery("select * from installed", null);
            //查询到有数据，则去掉之前已安装的,获得新list
            if (cs_install.moveToFirst()) {
                do {
                    for (AppInfo info : list) {
                        if (cs_install.getString(cs_install.getColumnIndex("packagename")).equals(info.getPackageName())) {
                            list.remove(info);
                        }
                    }
                }
                while (cs_install.moveToNext());
            }
            cs_install.close();

            db.setTransactionSuccessful();//事务成功
            db.endTransaction();
        } catch (Exception e) {
            Log.e("DBHandler", e.toString());
        }
        return list;
    }

    /**
     * 保存已安装的packageName
     *
     * @param packageName
     */
    public synchronized void saveInstall(String packageName) {
        String oldTable = shpre.getString("oldVer", "old");
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        db.insert("installed", null, values);
        //删除已安装的表项
        db.delete(oldTable, "packagename = ?", new String[]{packageName});
    }


    //插入gridView表的数据
    public synchronized void saveGridList(List<AppInfo> gridList) {
        //不为空时操作，前面有!号
        if (!gridList.isEmpty()) {
            db.beginTransaction();//开启事务操作

            ContentValues values = new ContentValues();
            for (AppInfo info : gridList) {
               // values.put("app_id", info.get_id());
                values.put("name", info.getName());
                values.put("packagename", info.getPackageName());
                values.put("icon", info.getApp_icon());
                db.insert("gridlist", null, values);
                //循环使用values
                values.clear();
                //Log.i("DBHandler", info.get_id());
            }

            db.setTransactionSuccessful();//事务成功
            db.endTransaction();

        }
    }

    //加载gridView的数据
    public synchronized List<AppInfo> loadGridList() {
        List<AppInfo> gridList = new ArrayList<AppInfo>();

        db.beginTransaction();//开启事务操作

        Cursor cursor = db.rawQuery("select distinct * from gridlist", null);
        if (cursor.moveToFirst()) {
            do {
                AppInfo appInfo = new AppInfo();
               // appInfo.set_id(cursor.getString(cursor.getColumnIndex("app_id")));
                appInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                appInfo.setPackageName(cursor.getString(cursor.getColumnIndex("packagename")));
                appInfo.setApp_icon(cursor.getBlob(cursor.getColumnIndex("icon")));
                appInfo.setIsAD(false);//非广告list
                gridList.add(appInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.setTransactionSuccessful();//事务成功
        db.endTransaction();

        return gridList;
    }

    public synchronized void deleteGridList(AppInfo info) {
            //删除选中的表项
        db.delete("gridlist", "packagename = ?", new String[]{info.getPackageName()});
    }

    /**
     * 保存user安装的app信息
     * @param type
     * @param Name
     * @param PackageName
     */
    public synchronized void saveIn(int type,String Name,String PackageName){
        ContentValues values = new ContentValues();
        values.put("type",type);
        values.put("name",Name);
        values.put("packagename",PackageName);

        db.insert("userin", null, values);
    }

    /**
     * 加载user的app信息
     * @return
     */
    public synchronized String loadIn(){
        db.beginTransaction();//开启事务操作
        Cursor cursor = db.rawQuery("select distinct * from userin", null);

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (cursor.moveToFirst()) {
            do {
                jsonObject.put("type",cursor.getInt(cursor.getColumnIndex("type")));
                jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
                jsonObject.put("packagename", cursor.getString(cursor.getColumnIndex("packagename")));
                Log.i("DBHandler", jsonObject.toString());
                jsonArray.add(jsonObject.toString());
                jsonObject.clear();


            } while (cursor.moveToNext());
        }else {
            db.endTransaction();
            cursor.close();
            return "";
        }
        cursor.close();
        db.setTransactionSuccessful();//事务成功
        db.endTransaction();

        return jsonArray.toString();
    }

    /**
     * post数据后则删除全部
     */
    public synchronized void deleteUserIn(){
        //删除选中的表项
        db.delete("userin", null, null);
}

}
