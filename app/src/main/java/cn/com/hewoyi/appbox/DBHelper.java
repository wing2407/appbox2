package cn.com.hewoyi.appbox;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static int bookName;

    public static final String APP_LIST = "create table "+ bookName +"(" +
            "_id text, " +
            "name text, " +
            "packagename text, " +
            "icon blob)";

    private static final String INSTALLED = "create table installed(" +
            "_id text, " +
            "name text, " +
            "packagename text)";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext =context;
        bookName =version;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(APP_LIST);
        db.execSQL(INSTALLED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //存在旧表则删除
        db.execSQL("drop table if exists "+oldVersion);

    }
}
