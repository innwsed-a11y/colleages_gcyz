package com.example.colleagues_items;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemDetailActivity extends AppCompatActivity {
    private TextView tvName, tvPrice, tvDescription, tvSeller, tvContact, tvDate, tvCategory, tvCondition, tvCampus, tvTags, tvLikes;
    private ImageView ivImage, ivLike;
    private EditText etComment;
    private RecyclerView rvComments;
    private LinearLayout llCommentInput;
    private ItemDAO itemDAO;
    private CommentDAO commentDAO;
    private ExecutorService executorService;
    private Handler handler;
    private int itemId;
    private Item currentItem;
    private UserPrefs userPrefs;
    private CommentAdapter commentAdapter;
    private boolean isLiked = false;

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
        commentDAO = new CommentDAO(this);
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // 加载物品详情
        loadItemDetail();
        // 加载评论列表
        loadComments();
        // 检查点赞状态
        checkLikeStatus();
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
        tvLikes = findViewById(R.id.tv_likes);
        ivImage = findViewById(R.id.iv_item_image);
        ivLike = findViewById(R.id.iv_like);
        etComment = findViewById(R.id.et_comment);
        rvComments = findViewById(R.id.rv_comments);
        llCommentInput = findViewById(R.id.ll_comment_input);

        // 初始化RecyclerView
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        
        // 设置点赞点击事件
        ivLike.setOnClickListener(v -> toggleLike());
        
        // 设置评论发送点击事件
        findViewById(R.id.btn_send_comment).setOnClickListener(v -> sendComment());
    }

    private void loadItemDetail() {
        executorService.execute(() -> {
            currentItem = itemDAO.getItemById(itemId);
            handler.post(() -> {
                if (currentItem != null) {
                    updateItemDetail(currentItem);
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
        tvLikes.setText(String.valueOf(item.getLikes()));

        // 使用Glide加载图片
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Glide.with(this)
                    .load(item.getImagePath())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void checkLikeStatus() {
        executorService.execute(() -> {
            isLiked = itemDAO.isItemLiked(userPrefs.getUsername(), itemId);
            handler.post(() -> updateLikeIcon());
        });
    }

    private void toggleLike() {
        executorService.execute(() -> {
            boolean success;
            if (isLiked) {
                // 取消点赞
                success = itemDAO.unlikeItem(userPrefs.getUsername(), itemId);
            } else {
                // 点赞
                success = itemDAO.likeItem(userPrefs.getUsername(), itemId);
            }
            
            if (success) {
                isLiked = !isLiked;
                // 重新加载物品详情以更新点赞数
                Item updatedItem = itemDAO.getItemById(itemId);
                currentItem = updatedItem;
                handler.post(() -> {
                    updateLikeIcon();
                    updateItemDetail(currentItem);
                    Toast.makeText(ItemDetailActivity.this, isLiked ? "点赞成功" : "取消点赞成功", Toast.LENGTH_SHORT).show();
                });
            } else {
                handler.post(() -> Toast.makeText(ItemDetailActivity.this, "操作失败，请重试", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateLikeIcon() {
        if (isLiked) {
            ivLike.setImageResource(R.drawable.ic_liked);
        } else {
            ivLike.setImageResource(R.drawable.ic_like);
        }
    }

    private void loadComments() {
        executorService.execute(() -> {
            List<Comment> comments = commentDAO.getCommentsByItemId(itemId);
            handler.post(() -> {
                commentAdapter = new CommentAdapter(ItemDetailActivity.this, comments);
                rvComments.setAdapter(commentAdapter);
            });
        });
    }

    private void sendComment() {
        String commentContent = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(commentContent)) {
            Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建评论对象
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setCommenter(userPrefs.getUsername());
        comment.setContent(commentContent);
        comment.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));

        // 发送评论
        executorService.execute(() -> {
            long commentId = commentDAO.addComment(comment);
            handler.post(() -> {
                if (commentId != -1) {
                    Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    // 重新加载评论列表
                    loadComments();
                } else {
                    Toast.makeText(this, "评论失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}