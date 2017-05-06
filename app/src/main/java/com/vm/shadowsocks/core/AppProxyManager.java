package com.vm.shadowsocks.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AppProxyManager {
    public static boolean isLollipopOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    public static AppProxyManager Instance;
    private static final String PROXY_APPS = "PROXY_APPS";
    private Context mContext;

    public List<AppInfo> mlistAppInfo = new ArrayList<AppInfo>();
    public List<AppInfo> proxyAppInfo = new ArrayList<AppInfo>();

    public AppProxyManager(Context context){
        this.mContext = context;
        Instance = this;
        readProxyAppsList();
    }

    public void removeProxyApp(String pkg){
        for (AppInfo app : this.proxyAppInfo) {
            if (app.getPkgName().equals(pkg)){
                proxyAppInfo.remove(app);
                break;
            }
        }
        writeProxyAppsList();
    }

    public void addProxyApp(String pkg){
        for (AppInfo app : this.mlistAppInfo) {
            if (app.getPkgName().equals(pkg)){
                proxyAppInfo.add(app);
                break;
            }
        }
        writeProxyAppsList();
    }

    public boolean isAppProxy(String pkg){
        for (AppInfo app : this.proxyAppInfo) {
            if (app.getPkgName().equals(pkg)){
                return true;
            }
        }
        return false;
    }

    private void readProxyAppsList() {
        SharedPreferences preferences = mContext.getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        String tmpString = preferences.getString(PROXY_APPS, "");
        try {
            if (proxyAppInfo != null){
                proxyAppInfo.clear();
            }
            if (tmpString.isEmpty()){
                return;
            }
            JSONArray jsonArray = new JSONArray(tmpString);
            for (int i = 0; i < jsonArray.length() ; i++){
                JSONObject object = jsonArray.getJSONObject(i);
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(object.getString("label"));
                appInfo.setPkgName(object.getString("pkg"));
                proxyAppInfo.add(appInfo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeProxyAppsList() {
        SharedPreferences preferences = mContext.getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < proxyAppInfo.size() ; i++){
                JSONObject object = new JSONObject();
                AppInfo appInfo = proxyAppInfo.get(i);
                object.put("label", appInfo.getAppLabel());
                object.put("pkg", appInfo.getPkgName());
                jsonArray.put(object);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PROXY_APPS, jsonArray.toString());
            editor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
