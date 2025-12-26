package com.example.colleagues_items;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPrefs {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_REMEMBER_USER = "remember_user";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UserPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // 保存用户名
    public void saveUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // 获取用户名
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    // 保存联系方式
    public void saveContact(String contact) {
        editor.putString(KEY_CONTACT, contact);
        editor.apply();
    }

    // 获取联系方式
    public String getContact() {
        return sharedPreferences.getString(KEY_CONTACT, "");
    }

    // 保存是否记住用户的设置
    public void saveRememberUser(boolean remember) {
        editor.putBoolean(KEY_REMEMBER_USER, remember);
        editor.apply();
    }

    // 获取是否记住用户的设置
    public boolean getRememberUser() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_USER, false);
    }

    // 保存用户信息（包含记住用户选项）
    public void saveUserInfo(String username, String contact, boolean remember) {
        saveUsername(username);
        saveContact(contact);
        saveRememberUser(remember);
    }

    // 清除用户信息
    public void clearUserInfo() {
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_CONTACT);
        editor.remove(KEY_REMEMBER_USER);
        editor.apply();
    }
}