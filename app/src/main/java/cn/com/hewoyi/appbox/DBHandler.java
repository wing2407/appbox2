package cn.com.hewoyi.appbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBHandler {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "apps";
    /**
     * 数据库版本作为表名
     */
    private String tableName;
    private static DBHandler DBHandler;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private DBHandler(Context context, int ver) {
        tableName = "list_" + ver;
        DBHelper dbHelper = new DBHelper(context,
                DB_NAME, null, ver);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取DB的实例。
     */
    public synchronized static DBHandler getInstance(Context context, int ver) {
        if (DBHandler == null) {
            DBHandler = new DBHandler(context, ver);
        }
        return DBHandler;
    }

    /**
     * 数据库保存list
     *
     * @param appInfos
     */
    public void saveList(List<AppInfo> appInfos) {
        //不为空时操作，前面有!号
        if (!appInfos.isEmpty()) {
            Cursor cursor = db.rawQuery("select * from installed", null);
            //查询到有数据，则去掉之前已安装的,获得新list
            if (cursor.moveToFirst()) {
                do {
                    for (AppInfo info : appInfos) {
                        if (cursor.getString(cursor.getColumnIndex("packagename")).equals(info.getPackageName())) {
                            appInfos.remove(info);
                        }
                    }
                }
                while (cursor.moveToNext());
            }
            cursor.close();


            //得到处理过的list则插入数据表
            db.beginTransaction();//开启事务操作
            ContentValues values = new ContentValues();
            for (AppInfo info : appInfos) {
                values.put("app_id", info.get_id());
                values.put("name", info.getName());
                values.put("packagename", info.getPackageName());
                values.put("icon", info.getApp_icon());
                db.insert(tableName, null, values);
                //循环使用values
                values.clear();
                //Log.i("DBHandler", info.get_id());
            }
            db.setTransactionSuccessful();//事务成功
            db.endTransaction();
        }

    }

    /**
     * 从数据库读取list
     *
     * @return
     */
    public List<AppInfo> loadList() {
        List<AppInfo> list = new ArrayList<AppInfo>();
        try {
            Cursor cursor = db.rawQuery("select distinct * from " + tableName, null);
            if (cursor.moveToFirst()) {
                do {
                    AppInfo appInfo = new AppInfo();
                    appInfo.set_id(cursor.getString(cursor.getColumnIndex("app_id")));
                    appInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                    appInfo.setPackageName(cursor.getString(cursor.getColumnIndex("packagename")));
                    appInfo.setApp_icon(cursor.getBlob(cursor.getColumnIndex("icon")));
                    list.add(appInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e("DBHandler", e.toString());
        } finally {
            return list;
        }

    }

    /**
     * 保存已安装的packageName
     *
     * @param packageName
     */
    public void saveInstall(String packageName) {

        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        db.insert("installed", null, values);

        db.delete(tableName, "packagename = ?", new String[]{packageName});
    }

    public void closeDB() {
        db.close();
    }

}
