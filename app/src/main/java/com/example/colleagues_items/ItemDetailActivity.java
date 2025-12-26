package com.example.colleagues_items;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemDetailActivity extends AppCompatActivity {

    private ImageView ivItemImage;
    private TextView tvItemName;
    private TextView tvItemPrice;
    private TextView tvItemDescription;
    private TextView tvItemDate;
    private TextView tvSellerName;
    private TextView tvContactInfo;
    private Button btnContactSeller;
    private Button btnLike;
    private TextView tvLikesCount;
    private EditText etComment;
    private Button btnSubmitComment;
    private RecyclerView rvComments;

    private ItemDAO itemDAO;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private int currentItemId;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // 初始化控件
        ivItemImage = findViewById(R.id.iv_item_image);
        tvItemName = findViewById(R.id.tv_item_name);
        tvItemPrice = findViewById(R.id.tv_item_price);
        tvItemDescription = findViewById(R.id.tv_item_description);
        tvItemDate = findViewById(R.id.tv_item_date);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvContactInfo = findViewById(R.id.tv_contact_info);
        btnContactSeller = findViewById(R.id.btn_contact_seller);
        btnLike = findViewById(R.id.btn_like);
        tvLikesCount = findViewById(R.id.tv_likes_count);
        etComment = findViewById(R.id.et_comment);
        btnSubmitComment = findViewById(R.id.btn_submit_comment);
        rvComments = findViewById(R.id.rv_comments);

        // 初始化数据库
        itemDAO = new ItemDAO(this);

        // 获取Intent传递的物品ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("item_id")) {
            currentItemId = intent.getIntExtra("item_id", -1);
            if (currentItemId != -1) {
                // 初始化评论列表
                initCommentsRecyclerView();
                // 根据ID获取物品详情
                loadItemDetail(currentItemId);
            } else {
                Toast.makeText(this, "无法获取物品信息", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "无法获取物品信息", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 设置联系卖家按钮的点击事件
        btnContactSeller.setOnClickListener(v -> {
            // 这里可以实现联系卖家的逻辑，比如拨打电话或发送消息
            String contact = tvContactInfo.getText().toString();
            Toast.makeText(this, "联系卖家: " + contact, Toast.LENGTH_SHORT).show();
        });

        // 设置点赞按钮的点击事件
        btnLike.setOnClickListener(v -> {
            if (currentItem != null) {
                // 更新点赞数
                int newLikesCount = currentItem.getLikes() + 1;
                currentItem.setLikes(newLikesCount);
                // 更新数据库
                executorService.execute(() -> {
                    itemDAO.updateLikes(currentItem.getId(), newLikesCount);
                    // 在主线程中更新UI
                    handler.post(() -> {
                        tvLikesCount.setText("点赞数: " + newLikesCount);
                        Toast.makeText(ItemDetailActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });

        // 设置提交评论按钮的点击事件
        btnSubmitComment.setOnClickListener(v -> {
            String commentContent = etComment.getText().toString().trim();
            if (commentContent.isEmpty()) {
                Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                return;
            }

            // 简单起见，这里固定评论者名称为"用户"
            String commenter = "用户";
            // 获取当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String commentDate = sdf.format(new Date());

            // 添加评论到数据库
            executorService.execute(() -> {
                long commentId = itemDAO.addComment(currentItemId, commenter, commentContent, commentDate);
                // 在主线程中更新UI
                handler.post(() -> {
                    if (commentId != -1) {
                        // 添加成功，更新评论列表
                        Comment newComment = new Comment((int) commentId, currentItemId, commenter, commentContent, commentDate);
                        commentList.add(0, newComment); // 新评论添加到列表顶部
                        commentAdapter.notifyItemInserted(0);
                        rvComments.scrollToPosition(0); // 滚动到顶部显示新评论
                        etComment.setText(""); // 清空输入框
                        Toast.makeText(ItemDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ItemDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    // 初始化评论列表RecyclerView
    private void initCommentsRecyclerView() {
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
    }

    // 加载物品详情
    private void loadItemDetail(int itemId) {
        // 在后台线程中获取物品信息
        executorService.execute(() -> {
            currentItem = itemDAO.getItemById(itemId);

            // 在主线程中更新UI
            handler.post(() -> {
                if (currentItem != null) {
                    // 设置物品信息
                    tvItemName.setText(currentItem.getName());
                    tvItemPrice.setText(String.format("¥%.2f", currentItem.getPrice()));
                    tvItemDescription.setText(currentItem.getDescription());
                    tvItemDate.setText("发布日期: " + currentItem.getPublishDate());
                    tvSellerName.setText("卖家: " + currentItem.getSeller());
                    tvContactInfo.setText("联系方式: " + currentItem.getContact());
                    tvLikesCount.setText("点赞数: " + currentItem.getLikes());

                    // 设置物品图片
                    if (currentItem.getImagePath() != null && !currentItem.getImagePath().isEmpty()) {
                        if (currentItem.getImagePath().startsWith("content://")) {
                            // 从相册选择的图片，使用Uri加载
                            ivItemImage.setImageURI(Uri.parse(currentItem.getImagePath()));
                        } else {
                            // 拍照的图片，使用文件路径加载
                            File imageFile = new File(currentItem.getImagePath());
                            if (imageFile.exists()) {
                                // 压缩图片加载，提高性能
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                Bitmap bitmap = BitmapFactory.decodeFile(currentItem.getImagePath(), options);
                                ivItemImage.setImageBitmap(bitmap);
                            } else {
                                ivItemImage.setImageResource(R.drawable.ic_launcher_background);
                            }
                        }
                    } else {
                        ivItemImage.setImageResource(R.drawable.ic_launcher_background);
                    }

                    // 加载评论列表
                    loadComments();
                } else {
                    Toast.makeText(this, "无法找到该物品", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    // 加载评论列表
    private void loadComments() {
        executorService.execute(() -> {
            Cursor cursor = itemDAO.getCommentsByItemId(currentItemId);
            List<Comment> comments = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENT_ID));
                    int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ITEM_ID));
                    String commenter = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENTER));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENT_CONTENT));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENT_DATE));
                    
                    // 确保所有字符串字段不为null
                    commenter = commenter != null ? commenter : "";
                    content = content != null ? content : "";
                    date = date != null ? date : "";
                    
                    comments.add(new Comment(id, itemId, commenter, content, date));
                } while (cursor.moveToNext());
                cursor.close();
            }

            // 在主线程中更新UI
            handler.post(() -> {
                commentList.clear();
                commentList.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            });
        });
    }

    // 评论适配器
    private class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
        private List<Comment> comments;

        public CommentAdapter(List<Comment> comments) {
            this.comments = comments != null ? comments : new ArrayList<>();
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            if (comments == null || comments.isEmpty()) {
                return;
            }
            
            Comment comment = comments.get(position);
            if (comment != null) {
                String commenter = comment.getCommenter();
                String date = comment.getDate();
                String content = comment.getContent();
                
                // 确保所有字符串都不为null
                commenter = commenter != null ? commenter : "";
                date = date != null ? date : "";
                content = content != null ? content : "";
                
                holder.text1.setText(commenter + " (" + date + ")");
                holder.text2.setText(content);
            }
        }

        @Override
        public int getItemCount() {
            return comments != null ? comments.size() : 0;
        }

        class CommentViewHolder extends RecyclerView.ViewHolder {
            TextView text1;
            TextView text2;

            public CommentViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}