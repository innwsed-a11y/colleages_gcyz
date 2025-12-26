package com.example.colleagues_items;

public class Comment {
    private int id;
    private int itemId;
    private String commenter;
    private String content;
    private String date;

    // 构造方法
    public Comment() {
    }

    public Comment(int id, int itemId, String commenter, String content, String date) {
        this.id = id;
        this.itemId = itemId;
        this.commenter = commenter != null ? commenter : "";
        this.content = content != null ? content : "";
        this.date = date != null ? date : "";
    }

    // getter和setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}