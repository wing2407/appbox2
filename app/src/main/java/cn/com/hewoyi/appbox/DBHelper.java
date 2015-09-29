package cn.com.hewoyi.appbox;


import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;
    private int date;


    //根据传入的ver号作为表名
    private String tableName(int date) {
        return "create table list_" + date + "(" +
                "_id text primary key, " +
                "name text, " +
                "packagename text, " +
                "icon blob)";
    }

    private static final String INSTALLED = "create table installed(" +
            "_id integer primary key autoincrement, " +
            "packagename text)";



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        date = version;//作为表名...
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(tableName(date));
        db.execSQL(INSTALLED);
        Log.i("DBHelper", tableName(date));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //存在旧表则删除
        db.execSQL("drop table if exists list_" + oldVersion);
        db.execSQL(tableName(newVersion));
    }

}


