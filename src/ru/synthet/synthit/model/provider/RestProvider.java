package ru.synthet.synthit.model.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class RestProvider extends ContentProvider {
    final String TAG = getClass().getSimpleName();

    private static final String TABLE_USERS = "users";
    private static final String TABLE_COMPS = "comps";

    private static final String DB_NAME = "synthit.db";
    private static final int DB_VERSION = 1;

    private static final UriMatcher sUriMatcher;

    private static final int PATH_ROOT = 0;
    private static final int PATH_USERS = 1;
    private static final int PATH_COMPS = 2;

    static {
        sUriMatcher = new UriMatcher(PATH_ROOT);
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.Users.CONTENT_PATH, PATH_USERS);
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.Comps.CONTENT_PATH, PATH_COMPS);
    }

    private DatabaseHelper mDatabaseHelper;

    class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql =
                    "create table " + TABLE_USERS + " (" +
                            Contract.Users._ID + " integer primary key autoincrement, " +
                            Contract.Users.UID + " text , " +
                            Contract.Users.DN + " text, " +
                            Contract.Users.DISPLAY_NAME + " text, " +
                            Contract.Users.DISPLAY_NAME_UP + " text, " +
                            Contract.Users.DESCRIPTION + " text, " +
                            Contract.Users.PASSWORD + " text, " +
                            Contract.Users.PASSWORD_AD + " text" +
                            ")";
            db.execSQL(sql);

            sql =
                    "create table " + TABLE_COMPS + " (" +
                            Contract.Comps._ID + " integer primary key autoincrement, " +
                            Contract.Comps.ID + " integer , " +
                            Contract.Comps.DEFAULTGATEWAY + " text, " +
                            Contract.Comps.DESC + " text, " +
                            Contract.Comps.DNS + " text, " +
                            Contract.Comps.IPADDR + " text, " +
                            Contract.Comps.IPGATEWAY + " text, " +
                            Contract.Comps.IPMASK + " text, " +
                            Contract.Comps.MACADDR + " text, " +
                            Contract.Comps.MEMORY + " text, " +
                            Contract.Comps.MEMORYTYPE + " text, " +
                            Contract.Comps.MEMORYSIZE + " text, " +
                            Contract.Comps.MEMORYH + " text, " +
                            Contract.Comps.NAME + " text, " +
                            Contract.Comps.USERID + " text, " +
                            Contract.Comps.UID + " text, " +
                            Contract.Comps.TAG + " text, " +
                            Contract.Comps.PROCESSORT + " text, " +
                            Contract.Comps.PROCESSORS + " text, " +
                            Contract.Comps.PROCESSORN + " text, " +
                            Contract.Comps.OSNAME + " text, " +
                            Contract.Comps.OSCOMMENTS + " text, " +
                            Contract.Comps.OS + " text " +
                            ")";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext(), DB_NAME, null, DB_VERSION);
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case PATH_USERS: {
                Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_USERS, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), Contract.Users.CONTENT_URI);
                return cursor;
            }
            case PATH_COMPS: {
                Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_COMPS, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), Contract.Comps.CONTENT_URI);
                return cursor;
            }
            default:
                return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PATH_USERS:
                return Contract.Users.CONTENT_TYPE;
            case PATH_COMPS:
                return Contract.Comps.CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case PATH_USERS: {
                mDatabaseHelper.getWritableDatabase().insert(TABLE_USERS, null, values);
                getContext().getContentResolver().notifyChange(Contract.Users.CONTENT_URI, null);
                return Contract.Users.CONTENT_URI;
            }
            case PATH_COMPS: {
                mDatabaseHelper.getWritableDatabase().insert(TABLE_COMPS, null, values);
                getContext().getContentResolver().notifyChange(Contract.Comps.CONTENT_URI, null);
                return Contract.Comps.CONTENT_URI;
            }
            default:
                return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case PATH_USERS:
                return mDatabaseHelper.getWritableDatabase().delete(TABLE_USERS, selection, selectionArgs);
            case PATH_COMPS:
                return mDatabaseHelper.getWritableDatabase().delete(TABLE_COMPS, selection, selectionArgs);
            default:
                return 0;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case PATH_USERS:
                return mDatabaseHelper.getWritableDatabase().update(TABLE_USERS, values, selection, selectionArgs);
            case PATH_COMPS:
                return mDatabaseHelper.getWritableDatabase().update(TABLE_COMPS, values, selection, selectionArgs);
            default:
                return 0;
        }
    }

}
