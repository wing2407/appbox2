package cn.com.hewoyi.appbox;


import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Stack;

public class DBHelper extends SQLiteOpenHelper {


    //表存在则不创建（如果出现重复创建会报错）
    private static final String INSTALLED = "create table if not exists installed(" +
            "_id integer primary key autoincrement, " +
            "packagename text)";


    private static final String GRIDLIST = "create table if not exists gridlist(" +
            "id integer primary key autoincrement, " +
            "app_id text, " +
            "name text, " +
            "packagename text, " +
            "icon blob)";

    private static final String UserIn = "create table if not exists userin(" +
            "id integer primary key autoincrement, " +
            "packagename text, " +
            "name text, " +
            "type int)";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(GRIDLIST);
        db.execSQL(INSTALLED);
        db.execSQL(UserIn);
        //Log.i("DBHelper", tableName(date));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("DBHelper","onUpgrade-->newVersion:"+newVersion+"-->oldVersion:"+oldVersion);
    }

}


