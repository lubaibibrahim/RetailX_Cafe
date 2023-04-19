package com.hadaba.retailxcafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
public class AppPreference {

    private Context context;

    private static final String orientation = "orientation";
    private static final String Initially = "INITIALLY";
    private static final String Logout = "LOGOUT";
    private static final String UserName = "UserName";
    private static final String Password = "Password";
    private static final String url = "url";
    private static final String tab_name = "tab_name";
    private static final String employee_id = "employee_id";

    public AppPreference(Context context) {

        this.context = context;
    }

    public void updateContext(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("com.hadaba", context.getApplicationContext().MODE_PRIVATE);
        return pref;
    }


    public  String getOrientation() { return getSharedPreferences().getString(orientation, "LANDSCAPE"); }
    public void setOrientation(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(orientation, data);
        editor.apply();
        editor.commit();
    }
    public  String getInitially() { return getSharedPreferences().getString(Initially, ""); }
    public void setInitially(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Initially, data);
        editor.apply();
        editor.commit();
    }
    public  String getLogout(String default_val) { return getSharedPreferences().getString(Logout, default_val); }
    public void setLogout(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Logout, data);
        editor.apply();
        editor.commit();
    }
    public  String getUserName(String default_val) { return getSharedPreferences().getString(UserName, default_val); }
    public void setUserName(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(UserName, data);
        editor.apply();
        editor.commit();
    }

    public  String getPassword() { return getSharedPreferences().getString(Password, ""); }
    public void setPassword(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Password, data);
        editor.apply();
        editor.commit();
    }
    public  String getUrl() { return getSharedPreferences().getString(url, ""); }
    public void setUrl(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(url, data);
        editor.apply();
        editor.commit();
    }
    public  String getTab_name() { return getSharedPreferences().getString(tab_name, ""); }
    public void setTab_name(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(tab_name, data);
        editor.apply();
        editor.commit();
    }
    public  String getEmployee_id() { return getSharedPreferences().getString(employee_id, ""); }
    public void setEmployee_id(String data) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(employee_id, data);
        editor.apply();
        editor.commit();
    }
}
