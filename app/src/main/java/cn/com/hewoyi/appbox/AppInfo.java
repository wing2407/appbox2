package cn.com.hewoyi.appbox;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/9/25.
 */
public class AppInfo implements Parcelable{

    private String name;
    private String _id;
    private String packname;
    private Bitmap app_icon;


    protected AppInfo(Parcel in) {
        name = in.readString();
        _id = in.readString();
        packname = in.readString();
        type = in.readString();
        app_icon = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(_id);
        dest.writeString(packname);
        dest.writeString(type);
        dest.writeParcelable(app_icon, flags);
    }
}
