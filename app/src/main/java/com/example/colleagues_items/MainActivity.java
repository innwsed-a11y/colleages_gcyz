package com.example.colleagues_items;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView recyclerViewPopularItems;
    private ItemAdapter adapter;
    private List<Item> popularItemsList;
    private ItemDAO itemDAO;
    private ExecutorService executorService;
    private Handler handler;

    // 底部导航栏按钮
    private Button btnNavHome, btnNavItems, btnNavPublish, btnNavProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化组件
        etSearch = findViewById(R.id.et_search);
        Button btnSearch = findViewById(R.id.btn_search);
        recyclerViewPopularItems = findViewById(R.id.recycler_view_popular_items);

        // 初始化底部导航栏
        btnNavHome = findViewById(R.id.nav_home);
        btnNavItems = findViewById(R.id.nav_items);
        btnNavPublish = findViewById(R.id.nav_publish);
        btnNavProfile = findViewById(R.id.nav_profile);

        // 初始化数据库和线程池
        itemDAO = new ItemDAO(this);
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // 初始化物品列表
        popularItemsList = new ArrayList<>();
        adapter = new ItemAdapter(this, popularItemsList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                // 跳转到物品详情页面
                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                intent.putExtra("item_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Item item) {
                // 首页不显示删除按钮，所以这个方法可以留空
            }
        });
        recyclerViewPopularItems.setAdapter(adapter);
        recyclerViewPopularItems.setLayoutManager(new LinearLayoutManager(this));

        // 加载高赞物品
        loadPopularItems();

        // 设置搜索按钮点击事件
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, ItemsListActivity.class);
            intent.putExtra("search_keyword", keyword);
            startActivity(intent);
        });

        // 设置底部导航栏点击事件
        btnNavHome.setOnClickListener(v -> {
            // 已经在首页，不需要操作
            Toast.makeText(MainActivity.this, "当前已在首页", Toast.LENGTH_SHORT).show();
        });

        btnNavItems.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ItemsListActivity.class);
            startActivity(intent);
        });

        btnNavPublish.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PublishItemActivity.class);
            startActivity(intent);
        });

        btnNavProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    // 加载高赞物品
    private void loadPopularItems() {
        try {
            executorService.execute(() -> {
                try {
                    List<Item> items = itemDAO.getPopularItems(10); // 获取前10个高赞物品
                    handler.post(() -> {
                        try {
                            updatePopularItemsList(items);
                        } catch (Exception e) {
                            // 捕获主线程更新列表时的异常
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "加载物品列表失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    // 捕获后台线程查询数据库时的异常
                    e.printStackTrace();
                    handler.post(() -> {
                        Toast.makeText(MainActivity.this, "加载物品列表失败", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            // 捕获线程池执行任务时的异常
            e.printStackTrace();
            Toast.makeText(this, "加载物品列表失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 更新高赞物品列表
    private void updatePopularItemsList(List<Item> items) {
        try {
            if (items == null) {
                items = new ArrayList<>();
            }
            popularItemsList.clear();
            popularItemsList.addAll(items);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            // 捕获更新列表时的异常
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭线程池
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}