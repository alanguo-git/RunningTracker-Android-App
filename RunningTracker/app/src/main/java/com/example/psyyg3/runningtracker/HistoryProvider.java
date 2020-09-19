package com.example.psyyg3.runningtracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class HistoryProvider extends ContentProvider {

    private DBHelper dbHelper = null;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(HistoryProviderContract.AUTHORITY, "myRunningHistory", 1);
    }

    @Override
    public boolean onCreate() {
        Log.d("g53mdp", "HistoryProvider onCreate");
        this.dbHelper = new DBHelper(this.getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        if (uri.getLastPathSegment() == null) {
            return HistoryProviderContract.CONTENT_TYPE_MULTIPLE;
        }
        else {
            return HistoryProviderContract.CONTENT_TYPE_SINGLE;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert("myRunningHistory", null, values);
        db.close();
        Uri newUri = ContentUris.withAppendedId(uri, id); //new Uri after inserting
        Log.d("g53mdp", newUri.toString());
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return  db.query("myRunningHistory", projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.update("myRunningHistory", values, selection, selectionArgs);
        db.close();
        return rowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete("myRunningHistory", selection, selectionArgs);
        db.close();
        return rowsDeleted;
    }
}
