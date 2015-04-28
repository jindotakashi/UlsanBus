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
public class RecentHistoryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "recent_history.db";

    private SQLiteDatabase sqliteDatabase;

    public RecentHistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.sqliteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_BUS_TABLE = "CREATE TABLE " + RecentHistoryContract.BusEntry.TABLE_NAME + " (" +
                RecentHistoryContract.BusEntry._ID + " INTEGER PRIMARY KEY," +
                RecentHistoryContract.BusEntry.COLUMN_BUS_ID + " VARCHAR(10) UNIQUE NOT NULL " +
                " );";

        final String SQL_CREATE_STATION_TABLE = "CREATE TABLE " + RecentHistoryContract.StationEntry.TABLE_NAME + " (" +
                RecentHistoryContract.StationEntry._ID + " INTEGER PRIMARY KEY," +
                RecentHistoryContract.StationEntry.COLUMN_STATION_ID + " VARCHAR(6) UNIQUE NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_BUS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecentHistoryContract.BusEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecentHistoryContract.StationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertBusRecentHistory(String busId) {
        ContentValues values = new ContentValues();
        values.put(RecentHistoryContract.BusEntry.COLUMN_BUS_ID, busId );
        sqliteDatabase.insert(RecentHistoryContract.BusEntry.TABLE_NAME, null, values);
    }

    public List<String> getBusRecentHistory() {
        List<String> result = new ArrayList<String>();
        Cursor cursor =
                sqliteDatabase.query(RecentHistoryContract.BusEntry.TABLE_NAME, // a. table
                        RecentHistoryContract.BusEntry.TABLE_COLUMNS, // b. column names
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
                String busId = cursor.getString(cursor.getColumnIndex(RecentHistoryContract.BusEntry.COLUMN_BUS_ID));
                result.add(busId);
                cursor.moveToNext();
            }

        }
        return result;
    }
}
