package com.example.colleagues_items;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private CheckBox cbRemember;
    private UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化用户偏好设置
        userPrefs = new UserPrefs(this);

        // 初始化控件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        cbRemember = findViewById(R.id.cb_remember);

        // 设置登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的账号和密码
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 简单的登录验证（实际项目中应该连接服务器验证）
                if (validateLogin(username, password)) {
                    // 保存用户信息，根据记住登录信息的选择决定是否记住
                    boolean remember = cbRemember.isChecked();
                    userPrefs.saveUserInfo(username, "", remember);

                    // 跳转到首页
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 结束当前Activity
                } else {
                    // 显示错误信息
                    Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 验证登录信息
    private boolean validateLogin(String username, String password) {
        // 简单的非空验证，实际项目中应该有更复杂的验证逻辑
        return !username.isEmpty() && !password.isEmpty();
    }
}