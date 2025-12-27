package com.example.colleagues_items;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername;
    private TextView tvContact;
    private UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 初始化用户偏好设置
        userPrefs = new UserPrefs(this);
        
        // 检查用户是否已登录
        if (!userPrefs.getRememberUser()) {
            // 用户未登录，跳转到登录页面
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 初始化控件
        tvUsername = findViewById(R.id.tv_username);
        tvContact = findViewById(R.id.tv_contact);

        // 加载用户信息
        loadUserInfo();

        // 设置底部导航栏点击事件
        setupBottomNavigation();
    }

    // 加载用户信息
    private void loadUserInfo() {
        String username = userPrefs.getUsername();
        String contact = userPrefs.getContact();

        if (!username.isEmpty()) {
            tvUsername.setText(username);
        }

        if (!contact.isEmpty()) {
            tvContact.setText(contact);
        }
    }

    // 设置底部导航栏
    private void setupBottomNavigation() {
        Button btnNavHome = findViewById(R.id.nav_home);
        Button btnNavItems = findViewById(R.id.nav_items);
        Button btnNavPublish = findViewById(R.id.nav_publish);
        Button btnNavProfile = findViewById(R.id.nav_profile);

        btnNavHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnNavItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ItemsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnNavPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PublishItemActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnNavProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "已在个人中心", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 已发布商品点击事件
    public void onPublishedItemsClick(View view) {
        // 跳转到已发布商品列表页面
        Intent intent = new Intent(ProfileActivity.this, ItemsListActivity.class);
        intent.putExtra("show_type", "published");
        intent.putExtra("seller", tvUsername.getText().toString());
        startActivity(intent);
    }

    // 我点赞的商品点击事件
    public void onLikedItemsClick(View view) {
        // 跳转到我点赞的商品列表页面
        Intent intent = new Intent(ProfileActivity.this, ItemsListActivity.class);
        intent.putExtra("show_type", "liked");
        intent.putExtra("username", tvUsername.getText().toString());
        startActivity(intent);
    }

    // 联系方式点击事件
    public void onContactClick(View view) {
        // 显示联系方式
        Toast.makeText(this, "联系方式：13800138000", Toast.LENGTH_SHORT).show();
    }
}