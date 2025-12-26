package com.example.colleagues_items;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemDetailActivity extends AppCompatActivity {
    private TextView tvName, tvPrice, tvDescription, tvSeller, tvContact, tvDate, tvCategory, tvCondition, tvCampus, tvTags;
    private ImageView ivImage;
    private ItemDAO itemDAO;
    private ExecutorService executorService;
    private Handler handler;
    private int itemId;

    private UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // 初始化用户偏好设置
        userPrefs = new UserPrefs(this);
        
        // 检查用户是否已登录
        if (!userPrefs.getRememberUser()) {
            // 用户未登录，跳转到登录页面
            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 初始化控件
        initViews();

        // 获取传递的物品ID
        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId == -1) {
            Toast.makeText(this, "物品信息错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化数据库和线程池
        itemDAO = new ItemDAO(this);
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // 加载物品详情
        loadItemDetail();
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_item_name);
        tvPrice = findViewById(R.id.tv_item_price);
        tvDescription = findViewById(R.id.tv_item_description);
        tvSeller = findViewById(R.id.tv_seller);
        tvContact = findViewById(R.id.tv_contact);
        tvDate = findViewById(R.id.tv_publish_date);
        tvCategory = findViewById(R.id.tv_category);
        tvCondition = findViewById(R.id.tv_condition);
        tvCampus = findViewById(R.id.tv_campus);
        tvTags = findViewById(R.id.tv_tags);
        ivImage = findViewById(R.id.iv_item_image);
    }

    private void loadItemDetail() {
        executorService.execute(() -> {
            Item item = itemDAO.getItemById(itemId);
            handler.post(() -> {
                if (item != null) {
                    updateItemDetail(item);
                } else {
                    Toast.makeText(this, "未找到物品信息", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void updateItemDetail(Item item) {
        tvName.setText(item.getName());
        tvPrice.setText("价格: " + item.getPrice() + "元");
        tvDescription.setText("描述: " + item.getDescription());
        tvSeller.setText("卖家: " + item.getSeller());
        tvContact.setText("联系方式: " + item.getContact());
        tvDate.setText("发布日期: " + item.getPublishDate());
        tvCategory.setText("分类: " + item.getCategory());
        tvCondition.setText("新旧程度: " + item.getCondition());
        tvCampus.setText("校区: " + item.getCampus());
        tvTags.setText("标签: " + item.getTags());

        // 加载图片（使用Glide库）
        if (!item.getImagePath().isEmpty()) {
            Glide.with(this)
                    .load(item.getImagePath())
                    .error(R.drawable.ic_launcher_background) // 替换为你的默认图片
                    .into(ivImage);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}