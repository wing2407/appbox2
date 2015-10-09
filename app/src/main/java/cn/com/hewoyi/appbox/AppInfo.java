package cn.com.hewoyi.appbox;

import android.os.Parcel;
import android.os.Parcelable;


public class AppInfo implements Parcelable{

    private String name;
    private String _id;
    private String packageName;
    private byte[] app_icon;
    private boolean isAD;

    public AppInfo() {
        super();
    }

    public AppInfo(String name, String _id, String packageName, byte[] app_icon, boolean isAD) {
        this.name = name;
        this._id = _id;
        this.packageName = packageName;
        this.app_icon = app_icon;
        this.isAD = isAD;
    }

    protected AppInfo(Parcel in) {
        name = in.readString();
        _id = in.readString();
        packageName = in.readString();
        app_icon = in.createByteArray();
        isAD = in.readByte() != 0; //isAD == true if byte != 0
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

    public boolean isAD() {
        return isAD;
    }

    public void setIsAD(boolean isAD) {
        this.isAD = isAD;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(_id);
        dest.writeString(packageName);
        dest.writeByteArray(app_icon);
        dest.writeByte((byte) (isAD ? 1 : 0)); //if isAD == true, byte == 1
    }
}
