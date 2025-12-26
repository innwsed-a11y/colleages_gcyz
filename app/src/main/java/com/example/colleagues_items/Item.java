package com.example.colleagues_items;

public class Item {
    private int id;
    private String name = "";
    private String description = "";
    private double price = 0.0;
    private String imagePath = "";
    private String publishDate = "";
    private String seller = "";
    private String contact = "";
    private String category = "";
    private String tags = "";
    private String condition = "";
    private int likes = 0;
    private String campus = "";

    // 构造方法
    public Item() {
    }

    public Item(int id, String name, String description, double price, String imagePath, String publishDate, String seller, String contact, String category, String tags, String condition, int likes, String campus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.publishDate = publishDate;
        this.seller = seller;
        this.contact = contact;
        this.category = category;
        this.tags = tags;
        this.condition = condition;
        this.likes = likes;
        this.campus = campus;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }
}