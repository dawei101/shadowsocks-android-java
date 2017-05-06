package com.vm.shadowsocks.core;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private Drawable appIcon;
    private String appLabel;
    private String pkgName;

    public AppInfo() {
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public String getAppLabel() {
        return this.appLabel;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setAppIcon(Drawable var1) {
        this.appIcon = var1;
    }

    public void setAppLabel(String var1) {
        this.appLabel = var1;
    }

    public void setPkgName(String var1) {
        this.pkgName = var1;
    }
}
