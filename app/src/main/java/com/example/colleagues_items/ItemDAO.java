package com.example.colleagues_items;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private DBHelper dbHelper;

    public ItemDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // ========== 原有方法（保留） ==========
    // 插入物品
    public long insertItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, item.getName());
        values.put(DBHelper.COLUMN_DESCRIPTION, item.getDescription());
        values.put(DBHelper.COLUMN_PRICE, item.getPrice());
        values.put(DBHelper.COLUMN_IMAGE_PATH, item.getImagePath());
        values.put(DBHelper.COLUMN_PUBLISH_DATE, item.getPublishDate());
        values.put(DBHelper.COLUMN_SELLER, item.getSeller());
        values.put(DBHelper.COLUMN_CONTACT, item.getContact());
        values.put(DBHelper.COLUMN_CATEGORY, item.getCategory());
        values.put(DBHelper.COLUMN_TAGS, item.getTags());
        values.put(DBHelper.COLUMN_CONDITION, item.getCondition());
        values.put(DBHelper.COLUMN_LIKES, item.getLikes());
        values.put(DBHelper.COLUMN_CAMPUS, item.getCampus());

        long id = db.insert(DBHelper.TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    // 根据ID查询物品
    public Item getItemById(int itemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Item item = null;
        Cursor cursor = db.query(
                DBHelper.TABLE_ITEMS,
                null,
                DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(itemId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)));
            item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_PATH)));
            item.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PUBLISH_DATE)));
            item.setSeller(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SELLER)));
            item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT)));
            item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY)));
            item.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TAGS)));
            item.setCondition(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONDITION)));
            item.setLikes(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIKES)));
            item.setCampus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPUS)));
        }
        cursor.close();
        db.close();
        return item;
    }

    // 获取高赞物品
    public List<Item> getPopularItems(int limit) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DBHelper.TABLE_ITEMS,
                null,
                null, null, null, null,
                DBHelper.COLUMN_LIKES + " DESC",
                String.valueOf(limit)
        );

        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)));
            item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_PATH)));
            item.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PUBLISH_DATE)));
            item.setSeller(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SELLER)));
            item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT)));
            item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY)));
            item.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TAGS)));
            item.setCondition(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONDITION)));
            item.setLikes(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIKES)));
            item.setCampus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPUS)));
            items.add(item);
        }
        cursor.close();
        db.close();
        return items;
    }

    // 查询所有物品
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DBHelper.TABLE_ITEMS,
                null,
                null, null, null, null,
                DBHelper.COLUMN_PUBLISH_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)));
            item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_PATH)));
            item.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PUBLISH_DATE)));
            item.setSeller(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SELLER)));
            item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT)));
            item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY)));
            item.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TAGS)));
            item.setCondition(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONDITION)));
            item.setLikes(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIKES)));
            item.setCampus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPUS)));
            items.add(item);
        }
        cursor.close();
        db.close();
        return items;
    }

    // ========== 新增缺失的4个方法 ==========
    // 1. 搜索物品（按名称/描述/标签模糊匹配）
    public List<Item> searchItems(String keyword) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 模糊查询：名称、描述、标签包含关键词
        String selection = DBHelper.COLUMN_NAME + " LIKE ? OR " +
                DBHelper.COLUMN_DESCRIPTION + " LIKE ? OR " +
                DBHelper.COLUMN_TAGS + " LIKE ?";
        String[] selectionArgs = {"%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%"};

        Cursor cursor = db.query(
                DBHelper.TABLE_ITEMS,
                null,
                selection,
                selectionArgs,
                null, null,
                DBHelper.COLUMN_PUBLISH_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)));
            item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_PATH)));
            item.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PUBLISH_DATE)));
            item.setSeller(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SELLER)));
            item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT)));
            item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY)));
            item.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TAGS)));
            item.setCondition(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONDITION)));
            item.setLikes(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIKES)));
            item.setCampus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPUS)));
            items.add(item);
        }
        cursor.close();
        db.close();
        return items;
    }

    // 2. 按卖家查询物品
    public List<Item> getItemsBySeller(String seller) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBHelper.COLUMN_SELLER + " = ?";
        String[] selectionArgs = {seller};

        Cursor cursor = db.query(
                DBHelper.TABLE_ITEMS,
                null,
                selection,
                selectionArgs,
                null, null,
                DBHelper.COLUMN_PUBLISH_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)));
            item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_PATH)));
            item.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PUBLISH_DATE)));
            item.setSeller(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SELLER)));
            item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT)));
            item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY)));
            item.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TAGS)));
            item.setCondition(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONDITION)));
            item.setLikes(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIKES)));
            item.setCampus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPUS)));
            items.add(item);
        }
        cursor.close();
        db.close();
        return items;
    }

    // 3. 删除物品（按ID）
    public int deleteItem(int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 返回删除的行数（成功返回1，失败返回0）
        int deletedRows = db.delete(
                DBHelper.TABLE_ITEMS,
                DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );
        db.close();
        return deletedRows;
    }

    // 4. 筛选物品（分类/新旧/校区/价格区间/排序）
    public List<Item> filterItems(String category, String condition, String campus,
                                  double minPrice, double maxPrice, String sortBy) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 拼接筛选条件
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        // 分类筛选（非空时）
        if (category != null && !category.isEmpty() && !category.equals("全部")) {
            selection.append(DBHelper.COLUMN_CATEGORY).append(" = ?");
            selectionArgs.add(category);
        }
        // 新旧程度筛选（非空时）
        if (condition != null && !condition.isEmpty() && !condition.equals("全部")) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DBHelper.COLUMN_CONDITION).append(" = ?");
            selectionArgs.add(condition);
        }
        // 校区筛选（非空时）
        if (campus != null && !campus.isEmpty() && !campus.equals("全部")) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DBHelper.COLUMN_CAMPUS).append(" = ?");
            selectionArgs.add(campus);
        }
        // 价格区间筛选
        if (minPrice >= 0 || maxPrice > 0) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DBHelper.COLUMN_PRICE).append(" BETWEEN ? AND ?");
            selectionArgs.add(String.valueOf(minPrice));
            selectionArgs.add(String.valueOf(maxPrice));
        }

        // 排序规则
        String orderBy;
        switch (sortBy) {
            case "price_asc": // 价格升序
                orderBy = DBHelper.COLUMN_PRICE + " ASC";
                break;
            case "price_desc": // 价格降序
                orderBy = DBHelper.COLUMN_PRICE + " DESC";
                break;
            case "likes_desc": // 点赞数降序
                orderBy = DBHelper.COLUMN_LIKES + " DESC";
                break;
            default: // 默认按发布时间降序
                orderBy = DBHelper.COLUMN_PUBLISH_DATE + " DESC";
                break;
        }

        // 执行查询
        Cursor cursor = db.query(
                DBHelper.TABLE_ITEMS,
                null,
                selection.length() > 0 ? selection.toString() : null,
                selectionArgs.toArray(new String[0]),
                null, null,
                orderBy
        );

        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)));
            item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_PATH)));
            item.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PUBLISH_DATE)));
            item.setSeller(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SELLER)));
            item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT)));
            item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY)));
            item.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TAGS)));
            item.setCondition(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONDITION)));
            item.setLikes(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIKES)));
            item.setCampus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPUS)));
            items.add(item);
        }
        cursor.close();
        db.close();
        return items;
    }
}