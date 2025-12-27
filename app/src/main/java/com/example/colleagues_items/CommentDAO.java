package com.example.colleagues_items;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private DBHelper dbHelper;

    public CommentDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // 添加评论
    public long addComment(Comment comment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ITEM_ID, comment.getItemId());
        values.put(DBHelper.COLUMN_COMMENTER, comment.getCommenter());
        values.put(DBHelper.COLUMN_COMMENT_CONTENT, comment.getContent());
        values.put(DBHelper.COLUMN_COMMENT_DATE, comment.getDate());
        long id = db.insert(DBHelper.TABLE_COMMENTS, null, values);
        db.close();
        return id;
    }

    // 删除评论
    public boolean deleteComment(int commentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DBHelper.TABLE_COMMENTS, 
                DBHelper.COLUMN_COMMENT_ID + " = ?", 
                new String[]{String.valueOf(commentId)});
        db.close();
        return rowsAffected > 0;
    }

    // 获取物品的所有评论
    public List<Comment> getCommentsByItemId(int itemId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_COMMENTS, 
                null,
                DBHelper.COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)},
                null, null, DBHelper.COLUMN_COMMENT_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment();
                comment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENT_ID)));
                comment.setItemId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ITEM_ID)));
                comment.setCommenter(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENTER)));
                comment.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENT_CONTENT)));
                comment.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENT_DATE)));
                comments.add(comment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return comments;
    }

    // 获取物品的评论数量
    public int getCommentsCount(int itemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_COMMENTS, 
                new String[]{"COUNT(*)"},
                DBHelper.COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)},
                null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}