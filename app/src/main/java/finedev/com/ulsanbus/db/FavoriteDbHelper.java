package finedev.com.ulsanbus.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ram on 2015-04-29.
 */
public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "favorite.db";

    private SQLiteDatabase sqliteDatabase;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.sqliteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_BUS_TABLE = "CREATE TABLE " + FavoriteContract.BusEntry.TABLE_NAME + " (" +
                FavoriteContract.BusEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteContract.BusEntry.COLUMN_BUS_ID + " INTEGER UNIQUE NOT NULL " +
                " );";

        final String SQL_CREATE_STATION_TABLE = "CREATE TABLE " + FavoriteContract.StationEntry.TABLE_NAME + " (" +
                FavoriteContract.StationEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteContract.StationEntry.COLUMN_STATION_ID + " INTEGER UNIQUE NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_BUS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.BusEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.StationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean isExistBusFavorite(String busId) {
        Cursor cursor =
                sqliteDatabase.query(FavoriteContract.BusEntry.TABLE_NAME, // a. table
                        FavoriteContract.BusEntry.TABLE_COLUMNS, // b. column names
                        FavoriteContract.BusEntry.COLUMN_BUS_ID + "=?", // c. selections
                        new String[]{busId}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if ( cursor.getCount() <= 0 ) return false;
        return true;
    }

    public void deleteBusFavorite(String busId) {
        sqliteDatabase.delete(
                FavoriteContract.BusEntry.TABLE_NAME,
                FavoriteContract.BusEntry.COLUMN_BUS_ID + "=?",
                new String[]{busId});
    }

    public void insertBusFavorite(String busId) {
        ContentValues values = new ContentValues();
        values.put(FavoriteContract.BusEntry.COLUMN_BUS_ID, busId);
        sqliteDatabase.insert(FavoriteContract.BusEntry.TABLE_NAME, null, values);
    }

    public List<String> getBusFavorite() {
        List<String> result = new ArrayList<String>();
        Cursor cursor =
                sqliteDatabase.query(FavoriteContract.BusEntry.TABLE_NAME, // a. table
                        FavoriteContract.BusEntry.TABLE_COLUMNS, // b. column names
                        null, // c. selections
                        null, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String busId = cursor.getString(cursor.getColumnIndex(FavoriteContract.BusEntry.COLUMN_BUS_ID));
                result.add(busId);
                cursor.moveToNext();
            }
        }
        return result;
    }

    public boolean isExistStationFavorite(String stationId) {
        Cursor cursor =
                sqliteDatabase.query(FavoriteContract.StationEntry.TABLE_NAME, // a. table
                        FavoriteContract.StationEntry.TABLE_COLUMNS, // b. column names
                        FavoriteContract.StationEntry.COLUMN_STATION_ID + "=?", // c. selections
                        new String[]{stationId}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if ( cursor.getCount() <= 0 ) return false;
        return true;
    }

    public void deleteStationFavorite(String stationId) {
        sqliteDatabase.delete(
                FavoriteContract.StationEntry.TABLE_NAME,
                FavoriteContract.StationEntry.COLUMN_STATION_ID + "=?",
                new String[]{stationId});
    }

    public void insertStationFavorite(String stationId) {
        ContentValues values = new ContentValues();
        values.put(FavoriteContract.StationEntry.COLUMN_STATION_ID, stationId );
        sqliteDatabase.insert(FavoriteContract.StationEntry.TABLE_NAME, null, values);
    }

    public List<String> getStationFavorite() {
        List<String> result = new ArrayList<String>();
        Cursor cursor =
                sqliteDatabase.query(FavoriteContract.StationEntry.TABLE_NAME, // a. table
                        FavoriteContract.StationEntry.TABLE_COLUMNS, // b. column names
                        null, // c. selections
                        null, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String stationId = cursor.getString(cursor.getColumnIndex(FavoriteContract.StationEntry.COLUMN_STATION_ID));
                result.add(stationId);
                cursor.moveToNext();
            }
        }
        return result;
    }
    
}
