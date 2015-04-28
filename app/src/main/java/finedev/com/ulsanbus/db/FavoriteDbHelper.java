package finedev.com.ulsanbus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ram on 2015-04-29.
 */
public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "favorite.db";

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
}
