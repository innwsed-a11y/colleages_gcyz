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
        this.dbHelper = new DBHelper(context);
    }

    // 添加物品
    public long addItem(Item item) {
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

        long newRowId = db.insert(DBHelper.TABLE_ITEMS, null, values);
        db.close();
        return newRowId;
    }

    // 获取所有物品
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_ITEMS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = cursorToItem(cursor);
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    // 根据ID获取物品
    public Item getItemById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_ITEMS, null, selection, selectionArgs, null, null, null);

        Item item = null;
        if (cursor.moveToFirst()) {
            item = cursorToItem(cursor);
        }

        cursor.close();
        db.close();
        return item;
    }

    // 更新物品
    public int updateItem(Item item) {
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

        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(item.getId())};

        int count = db.update(DBHelper.TABLE_ITEMS, values, selection, selectionArgs);
        db.close();
        return count;
    }

    // 删除物品
    public int deleteItem(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        int count = db.delete(DBHelper.TABLE_ITEMS, selection, selectionArgs);
        db.close();
        return count;
    }

    // 将Cursor转换为Item对象
    private Item cursorToItem(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE));
        String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_PATH));
        String publishDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PUBLISH_DATE));
        String seller = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SELLER));
        String contact = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY));
        String tags = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TAGS));
        String condition = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONDITION));
        int likes = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIKES));
        String campus = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPUS));

        // 确保所有字符串字段不为null
        name = name != null ? name : "";
        description = description != null ? description : "";
        imagePath = imagePath != null ? imagePath : "";
        publishDate = publishDate != null ? publishDate : "";
        seller = seller != null ? seller : "";
        contact = contact != null ? contact : "";
        category = category != null ? category : "";
        tags = tags != null ? tags : "";
        condition = condition != null ? condition : "";
        campus = campus != null ? campus : "";

        return new Item(id, name, description, price, imagePath, publishDate, seller, contact, category, tags, condition, likes, campus);
    }

    // 搜索物品
    public List<Item> searchItems(String keyword) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DBHelper.COLUMN_NAME + " LIKE ? OR " + DBHelper.COLUMN_DESCRIPTION + " LIKE ? OR " + DBHelper.COLUMN_TAGS + " LIKE ?";
        String[] selectionArgs = {"%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%"};

        Cursor cursor = db.query(DBHelper.TABLE_ITEMS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = cursorToItem(cursor);
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    // 筛选物品
    public List<Item> filterItems(String category, String condition, String campus, double minPrice, double maxPrice, String sortBy) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder selection = new StringBuilder();
        ArrayList<String> selectionArgs = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            selection.append(DBHelper.COLUMN_CATEGORY).append(" = ?");
            selectionArgs.add(category);
        }

        if (condition != null && !condition.isEmpty()) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DBHelper.COLUMN_CONDITION).append(" = ?");
            selectionArgs.add(condition);
        }

        if (campus != null && !campus.isEmpty()) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DBHelper.COLUMN_CAMPUS).append(" = ?");
            selectionArgs.add(campus);
        }

        if (minPrice > 0) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DBHelper.COLUMN_PRICE).append(" >= ?");
            selectionArgs.add(String.valueOf(minPrice));
        }

        if (maxPrice > 0) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DBHelper.COLUMN_PRICE).append(" <= ?");
            selectionArgs.add(String.valueOf(maxPrice));
        }

        String orderBy = null;
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "price_asc":
                    orderBy = DBHelper.COLUMN_PRICE + " ASC";
                    break;
                case "price_desc":
                    orderBy = DBHelper.COLUMN_PRICE + " DESC";
                    break;
                case "newest":
                    orderBy = DBHelper.COLUMN_PUBLISH_DATE + " DESC";
                    break;
                case "oldest":
                    orderBy = DBHelper.COLUMN_PUBLISH_DATE + " ASC";
                    break;
                default:
                    orderBy = DBHelper.COLUMN_PUBLISH_DATE + " DESC";
            }
        }

        Cursor cursor = db.query(DBHelper.TABLE_ITEMS, null, 
                selection.length() > 0 ? selection.toString() : null, 
                selectionArgs.size() > 0 ? selectionArgs.toArray(new String[0]) : null, 
                null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                Item item = cursorToItem(cursor);
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    // 更新点赞数
    public int updateLikes(int itemId, int likes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_LIKES, likes);

        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};

        int count = db.update(DBHelper.TABLE_ITEMS, values, selection, selectionArgs);
        db.close();
        return count;
    }

    // 添加评论
    public long addComment(int itemId, String commenter, String content, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ITEM_ID, itemId);
        values.put(DBHelper.COLUMN_COMMENTER, commenter);
        values.put(DBHelper.COLUMN_COMMENT_CONTENT, content);
        values.put(DBHelper.COLUMN_COMMENT_DATE, date);

        long newRowId = db.insert(DBHelper.TABLE_COMMENTS, null, values);
        db.close();
        return newRowId;
    }

    // 获取物品的所有评论
    public Cursor getCommentsByItemId(int itemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBHelper.COLUMN_ITEM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};
        String orderBy = DBHelper.COLUMN_COMMENT_DATE + " DESC";

        return db.query(DBHelper.TABLE_COMMENTS, null, selection, selectionArgs, null, null, orderBy);
    }

    // 获取高赞物品
    public List<Item> getPopularItems(int limit) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = dbHelper.getReadableDatabase();
            if (db != null) {
                String orderBy = DBHelper.COLUMN_LIKES + " DESC LIMIT " + Math.max(limit, 1); // 确保limit至少为1
                cursor = db.query(DBHelper.TABLE_ITEMS, null, null, null, null, null, orderBy);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            Item item = cursorToItem(cursor);
                            if (item != null) {
                                itemList.add(item);
                            }
                        } while (cursor.moveToNext());
                    }
                }
            }
        } catch (Exception e) {
            // 捕获所有异常，避免崩溃
            e.printStackTrace();
        } finally {
            // 确保资源被释放
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return itemList;
    }
    // 获取指定卖家的物品
    public List<Item> getItemsBySeller(String seller) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DBHelper.COLUMN_SELLER + " = ?";
            String[] selectionArgs = {seller};
            cursor = db.query(DBHelper.TABLE_ITEMS, null, selection, selectionArgs, null, null, DBHelper.COLUMN_PUBLISH_DATE + " DESC");

            while (cursor.moveToNext()) {
                Item item = cursorToItem(cursor);
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return items;
    }
}