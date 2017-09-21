/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emedicoz.app.Utils.OfflineData;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class StoreProvider extends ContentProvider {

    private static final String eMedicoz_TAG = "StoreProvider";
    public static String eMedicoz_AUTHORITY = "com.emedicoz.app.Utils.OfflineData.StoreProvider";
    private static final String eMedicoz_DATABASE_NAME = "emedicozdata.db";
    public static int eMedicoz_DATABASE_VERSION = 22;
    private static final String eMedicoz_DATA_TABLE_NAME = "emedicozdatastore";
    private static HashMap<String, String> sDataProjectionMap;

    private static final int eMedicoz_DATA = 1;
    private static final int eMedicoz_DATA_ID = 2;
    private static final int eMedicoz_DATA_APP_KEY = 3;
    private static final int eMedicoz_DATA_KEY = 4;
    private static final int eMedicoz_DATA_APP= 5;
    public static boolean isUpgrade = false;

    private static final UriMatcher sUriMatcher;

    public static final class StoreData implements BaseColumns {
        private StoreData() {}
        public static final Uri CONTENT_URI = Uri.parse("content://" + eMedicoz_AUTHORITY + "/data");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.emedicoz.storedata";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.emedicoz.storedata";
        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String KEY = "key";
        public static final String DATA = "data";
        public static final String CREATED_DATE = "created";
        public static final String MODIFIED_DATE = "modified";
		public static final String APP = "app";
    }
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, eMedicoz_DATABASE_NAME, null, eMedicoz_DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + eMedicoz_DATA_TABLE_NAME + " ("
                    + StoreData._ID + " INTEGER PRIMARY KEY,"
                    + StoreData.KEY + " TEXT,"
                    + StoreData.APP + " TEXT,"
                    + StoreData.DATA + " BLOB,"
                    + StoreData.CREATED_DATE + " INTEGER,"
                    + StoreData.MODIFIED_DATE + " INTEGER,"
                    + " UNIQUE("+ StoreData.APP + "," + StoreData.KEY + ") ON CONFLICT REPLACE"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(eMedicoz_TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
//            Util.onUpgrade();
           isUpgrade = true;
//            Util.getUtil(null).onUpgrade();
//            db.execSQL("DROP TABLE IF EXISTS "+eMedicoz_DATABASE_NAME);
//            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case eMedicoz_DATA:
            qb.setTables(eMedicoz_DATA_TABLE_NAME);
            qb.setProjectionMap(sDataProjectionMap);
            break;

        case eMedicoz_DATA_ID:
            qb.setTables(eMedicoz_DATA_TABLE_NAME);
            qb.setProjectionMap(sDataProjectionMap);
            qb.appendWhere(StoreData._ID + "=" + uri.getPathSegments().get(1));
            break;

        case eMedicoz_DATA_KEY:
        case eMedicoz_DATA_APP_KEY:
        	String appKey= "";
        	String dataKey;
        	if (uri.getPathSegments().size() > 3) {
        		appKey = uri.getPathSegments().get(2);
        		dataKey= uri.getPathSegments().get(3);
        	} else {
        		dataKey= uri.getPathSegments().get(2);
        	}
            qb.setTables(eMedicoz_DATA_TABLE_NAME);
            qb.setProjectionMap(sDataProjectionMap);
            qb.appendWhere(StoreData.APP+ "=\"" + appKey +"\" AND " + StoreData.KEY + "=\"" + dataKey + "\"");
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
     String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = StoreData.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case eMedicoz_DATA:
            return StoreData.CONTENT_TYPE;

        case eMedicoz_DATA_ID:
            return StoreData.CONTENT_ITEM_TYPE;
        case eMedicoz_DATA_KEY: //FALL Through
        case eMedicoz_DATA_APP_KEY:
            return StoreData.CONTENT_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != eMedicoz_DATA_APP_KEY) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        if (values.containsKey(StoreData.CREATED_DATE) == false) {
            values.put(StoreData.CREATED_DATE, now);
        }

        if (values.containsKey(StoreData.MODIFIED_DATE) == false) {
            values.put(StoreData.MODIFIED_DATE, now);
        }

        if (values.containsKey(StoreData.KEY) == false || values.containsKey(StoreData.APP) == false) {
        	throw new SQLException("Failed to insert row into must specify both app name and key");
        }

        if (values.containsKey(StoreData.DATA) == false) {
            values.remove(StoreData.DATA);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(eMedicoz_DATA_TABLE_NAME, StoreData.DATA, values);
        if (rowId > 0) {
            Uri dataUri = ContentUris.withAppendedId(StoreData.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(dataUri, null);
            return dataUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case eMedicoz_DATA:
            count = db.delete(eMedicoz_DATA_TABLE_NAME, where, whereArgs);
            break;

        case eMedicoz_DATA_ID:
            String dataId = uri.getPathSegments().get(1);
            count = db.delete(eMedicoz_DATA_TABLE_NAME, StoreData._ID + "=" + dataId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case eMedicoz_DATA_APP: //FALL Through
        case eMedicoz_DATA_APP_KEY:
        	String appKey= "";
        	String dataKey;
        	if (uri.getPathSegments().size() > 3) {
        		appKey = uri.getPathSegments().get(2);
        		dataKey= uri.getPathSegments().get(3);
        	} else {
        		appKey= uri.getPathSegments().get(2);
        		dataKey = null;
        	}
        	if (dataKey == null) {
        		count = db.delete(eMedicoz_DATA_TABLE_NAME, StoreData.APP + "=\"" + appKey + "\""
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
        	} else {
        		count = db.delete(eMedicoz_DATA_TABLE_NAME, StoreData.KEY + "=\"" + dataKey + "\" AND " + StoreData.APP + "=\"" + appKey + "\""
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
        	}

            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case eMedicoz_DATA:
            count = db.update(eMedicoz_DATA_TABLE_NAME, values, where, whereArgs);
            break;

        case eMedicoz_DATA_ID:
            String dataId = uri.getPathSegments().get(1);
            count = db.update(eMedicoz_DATA_TABLE_NAME, values, StoreData._ID + "=" + dataId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case eMedicoz_DATA_KEY: //FALL Through
        case eMedicoz_DATA_APP_KEY:
        	String appKey= "";
        	if (uri.getPathSegments().size() > 1) {
        		appKey = uri.getPathSegments().get(2);
        	}
            String dataKey= uri.getPathSegments().get(1);
            count = db.update(eMedicoz_DATA_TABLE_NAME, values, StoreData.KEY + "=\"" + dataKey + "\" AND " + StoreData.APP + "=\"" + appKey + "\""
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(eMedicoz_AUTHORITY, "data", eMedicoz_DATA);
        sUriMatcher.addURI(eMedicoz_AUTHORITY, "data/#", eMedicoz_DATA_ID);
        sUriMatcher.addURI(eMedicoz_AUTHORITY, "data/appkey/*/*", eMedicoz_DATA_APP_KEY);
        sUriMatcher.addURI(eMedicoz_AUTHORITY, "data/app/*",eMedicoz_DATA_APP);
        sUriMatcher.addURI(eMedicoz_AUTHORITY, "data/key/*", eMedicoz_DATA_KEY);

        sDataProjectionMap = new HashMap<String, String>();
        sDataProjectionMap.put(StoreData._ID, StoreData._ID);
        sDataProjectionMap.put(StoreData.KEY, StoreData.KEY);
        sDataProjectionMap.put(StoreData.DATA, StoreData.DATA);
        sDataProjectionMap.put(StoreData.CREATED_DATE, StoreData.CREATED_DATE);
        sDataProjectionMap.put(StoreData.MODIFIED_DATE, StoreData.MODIFIED_DATE);
    }
}
