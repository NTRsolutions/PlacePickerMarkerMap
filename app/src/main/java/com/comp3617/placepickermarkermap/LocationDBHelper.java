package com.comp3617.placepickermarkermap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * FinalProject : com.comp3617.placepickermarkermap
 * File: LocationDBHelper.java
 * Created by G.E. Eidsness on 11/30/2016.
 */
class LocationDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = LocationDBHelper.class.getSimpleName();
    private static final String DB_NAME = "scooch_locations.db";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_NAME = "locations";

    private static final String DB_COL_ID = BaseColumns._ID; //same as setting to "_id"
    private static final String DB_COL_GOOGLE_ID = "google_id";
    private static final String DB_COL_LATITUDE = "latitude";
    private static final String DB_COL_LONGITUDE = "longitude";
    private static final String DB_COL_NAME = "name";
    private static final String DB_COL_ADDRESS = "address";
    private static final String DB_COL_REMARKS = "remarks";
    private static final String[] DB_ALL_COLUMNS;

    static {
        DB_ALL_COLUMNS = new String[]{DB_COL_ID, DB_COL_GOOGLE_ID, DB_COL_LATITUDE,
                DB_COL_LONGITUDE, DB_COL_NAME, DB_COL_ADDRESS, DB_COL_REMARKS};
    }

    private static final String DB_TABLE_CREATE =
            "CREATE TABLE " + DB_TABLE_NAME + "(" +
                    DB_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DB_COL_GOOGLE_ID + " TEXT NOT NULL UNIQUE, " +
                    DB_COL_LATITUDE + " DOUBLE NOT NULL, " +
                    DB_COL_LONGITUDE + " DOUBLE NOT NULL, " +
                    DB_COL_NAME + " TEXT NOT NULL, " +
                    DB_COL_ADDRESS + " TEXT NOT NULL, " +
                    DB_COL_REMARKS + " TEXT  )";

    private static LocationDBHelper INSTANCE;

    static LocationDBHelper getInstance(Context ctx) {
        Log.d(LOG_TAG, "getInstance():" + ctx);
        if (INSTANCE == null)
            INSTANCE = new LocationDBHelper(ctx.getApplicationContext());
        return INSTANCE;
    }

    private LocationDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        Log.d(LOG_TAG, "onCreate():" + DB_TABLE_CREATE);
        try {
            db.execSQL(DB_TABLE_CREATE);
            Log.d(LOG_TAG, " onCreate(): " + DB_TABLE_NAME);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, " Error: " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }

        try {
            ContentValues cv = new ContentValues();
            cv.put(DB_COL_GOOGLE_ID, "ChIJwXz9f39xhlQRT3qxXAPDlbU");
            cv.put(DB_COL_LATITUDE, 49.2829607);
            cv.put(DB_COL_LONGITUDE, -123.1204715);
            cv.put(DB_COL_NAME, "Vancouver Art Gallery");
            cv.put(DB_COL_ADDRESS, "750 Hornby St, Vancouver,BC, V6Z2H7, Canada");
            cv.put(DB_COL_REMARKS, "Default Marker!!");
            db.insert(DB_TABLE_NAME, null, cv);
            cv.clear();
            Log.d(LOG_TAG, " Created : " + cv.toString());
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, " Error: " + e.getLocalizedMessage());
        }
    }

    public boolean dropTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        Log.d(LOG_TAG, "dropTable(): " + DB_TABLE_NAME);
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(db);
        Log.d(LOG_TAG, "Created: " + DB_TABLE_NAME);
     }

    public Location createLocation(Location location) {
        Log.d(LOG_TAG, "Calling createLocation()");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        long id;
        try {
            // The location might already exist
            //long localId = addOrUpdateLocation(location);
            cv.put(DB_COL_GOOGLE_ID, location.getGoogleId());
            cv.put(DB_COL_LATITUDE, location.getLatitude());
            cv.put(DB_COL_LONGITUDE, location.getLongitude());
            cv.put(DB_COL_NAME, location.getName());
            cv.put(DB_COL_ADDRESS, location.getAddress());
            cv.put(DB_COL_REMARKS, location.getRemarks());
            //long id = db.insertWithOnConflict(DB_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            id = db.insertOrThrow(DB_TABLE_NAME, null, cv);
            Log.d(LOG_TAG, "Location created : " + id);
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Insert Exception: " + e.getLocalizedMessage());
            //String sql = String.format("UPDATE %s SET %s='%s' WHERE %s='%s'", DB_TABLE_NAME, DB_COL_REMARKS,
            //        location.getRemarks(), DB_COL_GOOGLE_ID, location.getGoogleId());
            try {
                //db.execSQL(sql);
                //Log.d(LOG_TAG, "Update sql: " + sql);
                String selection = DB_COL_GOOGLE_ID + " =?";
                String[] selectionArgs = {String.valueOf(location.getGoogleId())};
                id = db.update(DB_TABLE_NAME, cv, selection, selectionArgs);
                Log.d(LOG_TAG, "Location updated : " + id);
            } catch (SQLiteException ex) {
                Log.d(LOG_TAG, "Update Exception: " + ex.getLocalizedMessage());
            }
        } finally {
            cv.clear();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
        return location;
    }

    public long addOrUpdateLocation(Location location) {
        Log.d(LOG_TAG, "Calling addOrUpdateLocation()");
        SQLiteDatabase db = getWritableDatabase();
        long localId = -1;
        Log.d(LOG_TAG, "Attribs:" + location.toString());
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DB_COL_GOOGLE_ID, location.getGoogleId());
            values.put(DB_COL_LATITUDE, location.getLatitude());
            values.put(DB_COL_LONGITUDE, location.getLongitude());
            values.put(DB_COL_NAME, location.getName());
            values.put(DB_COL_ADDRESS, location.getAddress());
            values.put(DB_COL_REMARKS, location.getRemarks());
            // First try to update the location in case already exists
            String selection = DB_COL_ID + " =?";
            String[] selectionArgs = {String.valueOf(location.getId())};
            int rows = db.update(DB_TABLE_NAME, values, selection, selectionArgs);
            //int rows = db.updateWithOnConflict(DB_TABLE_NAME, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_IGNORE);
            // Check if update succeeded
            Log.d(LOG_TAG, "row=" + rows);
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String sql = String.format("SELECT %s FROM %s WHERE %s = ?", DB_COL_ID, DB_TABLE_NAME, DB_COL_ID);
                Log.d(LOG_TAG, "SQL: " + sql);
                Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(location.getId())});
                //Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        localId = cursor.getLong(0);
                        Log.d(LOG_TAG, "Location updated: " + localId);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // location did not already exist, so insert new location
                localId = db.insertOrThrow(DB_TABLE_NAME, null, values);
                Log.d(LOG_TAG, "Location created: " + localId);
                //localId = db.insertWithOnConflict(DB_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                db.setTransactionSuccessful();
            }

        } catch (SQLiteException ex) {
            Log.d(LOG_TAG, "Update Error: " + ex.getMessage());
        } finally {
            db.endTransaction();
        }

        return localId;
    }

    public List<Location> getLocations() {
        Log.d(LOG_TAG, "Calling getLocations()");
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        Cursor c = null;
        List<Location> Locations = new LinkedList<>();
        try {
            String query;
            query = String.format("SELECT _id, %s, %s, %s, %s, %s, %s FROM %s",
                    DB_COL_GOOGLE_ID,
                    DB_COL_LATITUDE,
                    DB_COL_LONGITUDE,
                    DB_COL_NAME,
                    DB_COL_ADDRESS,
                    DB_COL_REMARKS,
                    DB_TABLE_NAME);
            Log.d(LOG_TAG, "sql: " + query);
            c = db.rawQuery(query, null);
            if ((c != null) && c.getCount() > 0) {
                while (c.moveToNext()) {
                    Locations.add(getLocationFromCursor(c));
                }
                db.setTransactionSuccessful();
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.endTransaction();
            //db.close();
            Log.d(LOG_TAG, "Closed");
        }
        return Locations;
    }

    private static Location getLocationFromCursor(Cursor c) {
        if ((c == null) || (c.getCount() == 0))
            return null;
        else {
            Location Location = new Location();
            Location.setId(c.getLong(c.getColumnIndex(DB_COL_ID)));
            Location.setGoogleId(c.getString(c.getColumnIndex(DB_COL_GOOGLE_ID)));
            Location.setLatitude(c.getDouble(c.getColumnIndex(DB_COL_LATITUDE)));
            Location.setLongitude(c.getDouble(c.getColumnIndex(DB_COL_LONGITUDE)));
            Location.setName(c.getString(c.getColumnIndex(DB_COL_NAME)));
            Location.setAddress(c.getString(c.getColumnIndex(DB_COL_ADDRESS)));
            Location.setRemarks(c.getString(c.getColumnIndex(DB_COL_REMARKS)));
            return Location;
        }
    }

    public String getLocationByGoogleId(String googleId) {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        boolean exist = false;
        String attribs = "Empty";
        Cursor c = null;
        final String query = "SELECT " + DB_COL_REMARKS + " FROM " + DB_TABLE_NAME +
                " WHERE " + DB_COL_GOOGLE_ID + " ='" + googleId + "' LIMIT 1";
        Log.d(LOG_TAG, "Exists sql: " + query);
        try {
            c = db.rawQuery(query, null);
            if ((c != null) && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    attribs = c.getString(c.getColumnIndex(DB_COL_REMARKS));
                    exist = true;
                }
                db.setTransactionSuccessful();
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.endTransaction();
            //db.close();
            Log.d(LOG_TAG, "getLocationByGoogleId(): " + exist);
        }
        return attribs;
    }

//    if (cursor.moveToFirst()) {
//        // record exists
//    } else {
//        // record not found
//    }

    public int updateLocation(Location location) {
        Log.d(LOG_TAG, "Calling updateLocation(): " + location.getGoogleId());
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        int id = -1;
        ContentValues cv = new ContentValues();
        try {
            cv.put(DB_COL_REMARKS, location.getRemarks());
            id = db.update(DB_TABLE_NAME, cv, DB_COL_GOOGLE_ID + "=" + location.getGoogleId(), null);
            db.setTransactionSuccessful();
            Log.d(LOG_TAG, "Location updated : " + location.getGoogleId());
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error while trying to update location");
        } finally {
            db.endTransaction();
            db.close();
        }
        return id;
    }

    public boolean doesLocationExist(String googleId) {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        boolean exist = false;
        Cursor c = null;
        final String query = "SELECT " + DB_COL_GOOGLE_ID + " FROM " + DB_TABLE_NAME +
                " WHERE " + DB_COL_GOOGLE_ID + " ='" + googleId + "' LIMIT 1";
        Log.d(LOG_TAG, "Exists sql: " + query);
        try {
            c = db.rawQuery(query, null);
            if ((c != null) && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    exist = true;
                }
                db.setTransactionSuccessful();
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.endTransaction();
            db.close();
            Log.d(LOG_TAG, "doesLocationExist(): " + exist);
        }
        return exist;
    }

    public boolean deleteLocationWithGoogleId(String googleId) {
        boolean success = false;
        SQLiteDatabase db = getWritableDatabase();
        String strSQL = "DELETE FROM " + DB_TABLE_NAME + " WHERE " + DB_COL_GOOGLE_ID + " = ?";
        Log.d(LOG_TAG, "SQL:" + strSQL);
        SQLiteStatement stmt = db.compileStatement(strSQL);
        stmt.bindString(1, googleId);
        db.beginTransaction();
        try {
            stmt.execute();
            db.setTransactionSuccessful();
            success = true;
            Log.d(LOG_TAG, "Deleted: " + googleId);
        } catch (SQLiteException ex) {
            Log.d(LOG_TAG, "Delete Exception: " + ex.getLocalizedMessage());
        } finally {
            db.endTransaction();
            stmt.close();
            db.close();
        }
        return success;
    }

    public boolean deleteLocationWithRowId(long rowId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(DB_TABLE_NAME, DB_COL_ID + "=" + rowId, null) > 0;
    }

}

