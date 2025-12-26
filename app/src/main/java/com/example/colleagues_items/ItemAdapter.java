package com.example.colleagues_items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context context;
    private List<Item> itemList;
    private OnItemClickListener listener;

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(Item item);
        void onDeleteClick(Item item);
    }

    public ItemAdapter(Context context, List<Item> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            if (context == null) {
                context = parent.getContext();
            }
            View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
            return new ItemViewHolder(view);
        } catch (Exception e) {
            // 如果创建失败，返回一个空的ViewHolder
            return new ItemViewHolder(new View(parent.getContext()));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        try {
            if (holder == null || position < 0 || position >= itemList.size()) {
                return;
            }
            
            Item item = itemList.get(position);
            if (item != null) {
                // 设置物品名称
                if (holder.tvItemName != null) {
                    String name = item.getName();
                    holder.tvItemName.setText(name != null ? name : "");
                }
                
                // 设置物品价格
                if (holder.tvItemPrice != null) {
                    double price = item.getPrice();
                    holder.tvItemPrice.setText("¥" + (price >= 0 ? price : 0.0));
                }
                
                // 设置卖家名称
                if (holder.tvItemSeller != null) {
                    String seller = item.getSeller();
                    holder.tvItemSeller.setText(seller != null ? seller : "");
                }
                
                // 设置发布日期
                if (holder.tvItemDate != null) {
                    String date = item.getPublishDate();
                    holder.tvItemDate.setText(date != null ? date : "");
                }

                // 设置物品图片
                if (holder.ivItemImage != null) {
                    setItemImage(holder.ivItemImage, item.getImagePath());
                }

                // 设置点击事件
                if (holder.itemView != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                listener.onItemClick(item);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            // 捕获所有异常，避免崩溃
            e.printStackTrace();
        }
    }

    // 设置物品图片
    private void setItemImage(ImageView imageView, String imagePath) {
        try {
            if (imageView == null) {
                return;
            }
            
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    // 压缩图片加载，提高性能
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4; // 图片缩小为原来的1/4
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        // 设置默认图片
                        imageView.setImageResource(R.drawable.ic_launcher_background);
                    }
                } else {
                    // 设置默认图片
                    imageView.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                // 设置默认图片
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        } catch (Exception e) {
            // 捕获所有异常，避免崩溃
            if (imageView != null) {
                try {
                    imageView.setImageResource(R.drawable.ic_launcher_background);
                } catch (Exception ex) {
                    // 忽略进一步的异常
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    // ViewHolder类
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvItemSeller;
        TextView tvItemDate;
        ImageView ivItemImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvItemSeller = itemView.findViewById(R.id.tv_item_seller);
            tvItemDate = itemView.findViewById(R.id.tv_item_date);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
        }
    }
}