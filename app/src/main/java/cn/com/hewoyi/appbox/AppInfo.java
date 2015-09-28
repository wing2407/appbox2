package cn.com.hewoyi.appbox;

import android.os.Parcel;
import android.os.Parcelable;


public class AppInfo implements Parcelable{

    private String name;
    private String _id;
    private String packageName;
    private byte[] app_icon;


    protected AppInfo(Parcel in) {
        name = in.readString();
        _id = in.readString();
        packageName = in.readString();
        app_icon = in.createByteArray();
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

    public AppInfo() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public byte[] getApp_icon() {
        return app_icon;
    }

    public void setApp_icon(byte[] app_icon) {
        this.app_icon = app_icon;
    }

    public static Creator<AppInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(_id);
        dest.writeString(packageName);
        dest.writeByteArray(app_icon);
    }


}
