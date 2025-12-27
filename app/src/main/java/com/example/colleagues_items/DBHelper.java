package com.example.colleagues_items;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // 数据库名称
    private static final String DATABASE_NAME = "campus_items.db";
    // 数据库版本 - 从4升级到5以修复点赞表结构问题
    private static final int DATABASE_VERSION = 5;

    // 物品表
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE_PATH = "imagePath";
    public static final String COLUMN_PUBLISH_DATE = "publishDate";
    public static final String COLUMN_SELLER = "seller";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_CONDITION = "item_condition";
    public static final String COLUMN_LIKES = "likes";
    public static final String COLUMN_CAMPUS = "campus";

    // 评论表
    public static final String TABLE_COMMENTS = "comments";
    public static final String COLUMN_COMMENT_ID = "comment_id";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_COMMENTER = "commenter";
    public static final String COLUMN_COMMENT_CONTENT = "content";
    public static final String COLUMN_COMMENT_DATE = "comment_date";

    // 点赞表
    public static final String TABLE_LIKES = "likes";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_LIKE_ITEM_ID = "item_id";

    // 创建物品表的SQL语句
    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_PRICE + " REAL, " +
            COLUMN_IMAGE_PATH + " TEXT, " +
            COLUMN_PUBLISH_DATE + " TEXT, " +
            COLUMN_SELLER + " TEXT, " +
            COLUMN_CONTACT + " TEXT, " +
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_TAGS + " TEXT, " +
            COLUMN_CONDITION + " TEXT, " +
            COLUMN_LIKES + " INTEGER DEFAULT 0, " +
            COLUMN_CAMPUS + " TEXT" +
            ");";

    // 创建评论表的SQL语句
    private static final String CREATE_TABLE_COMMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_COMMENTS + "(" +
            COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ITEM_ID + " INTEGER, " +
            COLUMN_COMMENTER + " TEXT, " +
            COLUMN_COMMENT_CONTENT + " TEXT, " +
            COLUMN_COMMENT_DATE + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
            ");";

    // 创建点赞表的SQL语句
    private static final String CREATE_TABLE_LIKES = "CREATE TABLE IF NOT EXISTS " + TABLE_LIKES + "(" +
            COLUMN_USER_ID + " TEXT, " +
            COLUMN_LIKE_ITEM_ID + " INTEGER, " +
            "PRIMARY KEY(" + COLUMN_USER_ID + ", " + COLUMN_LIKE_ITEM_ID + "), " +
            "FOREIGN KEY(" + COLUMN_LIKE_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
            ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建物品表
        db.execSQL(CREATE_TABLE_ITEMS);
        // 创建评论表
        db.execSQL(CREATE_TABLE_COMMENTS);
        // 创建点赞表
        db.execSQL(CREATE_TABLE_LIKES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 旧版本小于2时，添加新字段
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_TAGS + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_CONDITION + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_LIKES + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_CAMPUS + " TEXT");
            // 创建评论表（使用IF NOT EXISTS避免表已存在时的错误）
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COMMENTS + "(" +
                    COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ITEM_ID + " INTEGER, " +
                    COLUMN_COMMENTER + " TEXT, " +
                    COLUMN_COMMENT_CONTENT + " TEXT, " +
                    COLUMN_COMMENT_DATE + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ");");
        }
        if (oldVersion < 4) {
            // 旧版本小于4时，创建点赞表（使用IF NOT EXISTS避免表已存在时的错误）
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIKES + "(" +
                    COLUMN_USER_ID + " TEXT, " +
                    COLUMN_LIKE_ITEM_ID + " INTEGER, " +
                    "PRIMARY KEY(" + COLUMN_USER_ID + ", " + COLUMN_LIKE_ITEM_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_LIKE_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ");");
        }
        if (oldVersion < 5) {
            // 旧版本小于5时，重新创建点赞表以修复结构问题
            // 首先删除现有的点赞表（如果存在）
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES);
            // 重新创建点赞表
            db.execSQL("CREATE TABLE " + TABLE_LIKES + "(" +
                    COLUMN_USER_ID + " TEXT, " +
                    COLUMN_LIKE_ITEM_ID + " INTEGER, " +
                    "PRIMARY KEY(" + COLUMN_USER_ID + ", " + COLUMN_LIKE_ITEM_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_LIKE_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ");");
        }
    }
}