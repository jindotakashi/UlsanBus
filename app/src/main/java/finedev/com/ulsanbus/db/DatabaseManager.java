package finedev.com.ulsanbus.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import finedev.com.ulsanbus.AppConstant;
import finedev.com.ulsanbus.bus.BusInfo;
import finedev.com.ulsanbus.station.StationInfo;

/**
 * Created by ramju on 4/18/15.
 */
public class DatabaseManager {

    private final String LOG_TAG = DatabaseManager.class.getSimpleName();

    private static final String dbFileName = AppConstant.DB_FILE_NAME;
    private static final String busTableName = AppConstant.DB_BUS_TABLE_NAME;
    private static final String[] BUS_TABLE_COLUMNS = {"id", "routeid", "routeno", "routedir", "routetype", "bustype", "buscompany", "companytel", "fstopname", "tstopname", "wd_start_time", "wd_end_time", "wd_max_interval", "wd_min_interval", "we_start_time", "we_end_time", "we_max_interval", "we_min_interval", "ws_start_time", "ws_end_time", "ws_max_interval", "ws_min_interval", "interval", "traveltime", "length", "operationcnt", "remark"};
    private static final String[] STATION_TABLE_COLUMNS = {"id", "stopid", "stopname", "stoplimousine", "stopx", "stopy", "stopremark"};

    private static final String stationTableName = AppConstant.DB_STATION_TABLE_NAME;
    private static final int dbVersion = 1;

    private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase sqliteDatabase;

    private Context mContext;

    public DatabaseManager(Context context) {
        this.mContext = context;
        this.databaseOpenHelper = new DatabaseOpenHelper(context, AppConstant.DB_BASE_URL +  dbFileName, null, dbVersion);
        this.sqliteDatabase = databaseOpenHelper.getWritableDatabase();
    }

    public ArrayList<BusInfo> findBusList(String busNumber) {

        ArrayList<BusInfo> result = new ArrayList<BusInfo>();

        String sql = "select * from " + busTableName + " where routedir in ('1', '3') and routeno like '%" + busNumber + "%';";
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BusInfo busInfo = new BusInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("routeid")), cursor.getString(cursor.getColumnIndex("routeno")), cursor.getString(cursor.getColumnIndex("routedir")), cursor.getString(cursor.getColumnIndex("routetype")), cursor.getString(cursor.getColumnIndex("bustype")), cursor.getString(cursor.getColumnIndex("buscompany")), cursor.getString(cursor.getColumnIndex("companytel")), cursor.getString(cursor.getColumnIndex("fstopname")), cursor.getString(cursor.getColumnIndex("tstopname")), cursor.getString(cursor.getColumnIndex("wd_start_time")), cursor.getString(cursor.getColumnIndex("wd_end_time")), cursor.getString(cursor.getColumnIndex("wd_max_interval")), cursor.getString(cursor.getColumnIndex("wd_min_interval")), cursor.getString(cursor.getColumnIndex("we_start_time")), cursor.getString(cursor.getColumnIndex("we_end_time")), cursor.getString(cursor.getColumnIndex("we_max_interval")), cursor.getString(cursor.getColumnIndex("we_min_interval")), cursor.getString(cursor.getColumnIndex("ws_start_time")), cursor.getString(cursor.getColumnIndex("ws_end_time")), cursor.getString(cursor.getColumnIndex("ws_max_interval")), cursor.getString(cursor.getColumnIndex("ws_min_interval")), cursor.getString(cursor.getColumnIndex("interval")), cursor.getString(cursor.getColumnIndex("traveltime")), cursor.getString(cursor.getColumnIndex("length")), cursor.getString(cursor.getColumnIndex("operationcnt")), cursor.getString(cursor.getColumnIndex("remark")));
            result.add(busInfo);
            cursor.moveToNext();
        }
        return result;
    }

    public ArrayList<StationInfo> findStationList(String station) {
        ArrayList<StationInfo> result = new ArrayList<StationInfo>();

        String sql = "select * from " + stationTableName + " where stopid like '%" + station + "%' or stopname like '%" + station + "%';";
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            StationInfo stationInfo = new StationInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("stopid")), cursor.getString(cursor.getColumnIndex("stopname")), cursor.getString(cursor.getColumnIndex("stoplimousine")), cursor.getString(cursor.getColumnIndex("stopx")), cursor.getString(cursor.getColumnIndex("stopy")), cursor.getString(cursor.getColumnIndex("stopremark")));
            result.add(stationInfo);
            cursor.moveToNext();
        }
        return result;
    }

    public BusInfo getBusInfo(int id) {
        Cursor cursor =
                sqliteDatabase.query(busTableName, // a. table
                        BUS_TABLE_COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();

            if ( cursor.getCount() > 0 ) {
                BusInfo busInfo = new BusInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("routeid")), cursor.getString(cursor.getColumnIndex("routeno")), cursor.getString(cursor.getColumnIndex("routedir")), cursor.getString(cursor.getColumnIndex("routetype")), cursor.getString(cursor.getColumnIndex("bustype")), cursor.getString(cursor.getColumnIndex("buscompany")), cursor.getString(cursor.getColumnIndex("companytel")), cursor.getString(cursor.getColumnIndex("fstopname")), cursor.getString(cursor.getColumnIndex("tstopname")), cursor.getString(cursor.getColumnIndex("wd_start_time")), cursor.getString(cursor.getColumnIndex("wd_end_time")), cursor.getString(cursor.getColumnIndex("wd_max_interval")), cursor.getString(cursor.getColumnIndex("wd_min_interval")), cursor.getString(cursor.getColumnIndex("we_start_time")), cursor.getString(cursor.getColumnIndex("we_end_time")), cursor.getString(cursor.getColumnIndex("we_max_interval")), cursor.getString(cursor.getColumnIndex("we_min_interval")), cursor.getString(cursor.getColumnIndex("ws_start_time")), cursor.getString(cursor.getColumnIndex("ws_end_time")), cursor.getString(cursor.getColumnIndex("ws_max_interval")), cursor.getString(cursor.getColumnIndex("ws_min_interval")), cursor.getString(cursor.getColumnIndex("interval")), cursor.getString(cursor.getColumnIndex("traveltime")), cursor.getString(cursor.getColumnIndex("length")), cursor.getString(cursor.getColumnIndex("operationcnt")), cursor.getString(cursor.getColumnIndex("remark")));
                return busInfo;
            }
        }
        return null;
    }

    public StationInfo getStationInfo(int id) {
        Cursor cursor =
                sqliteDatabase.query(stationTableName,
                        STATION_TABLE_COLUMNS,
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();

            if ( cursor.getCount() > 0 ) {
                StationInfo stationInfo = new StationInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("stopid")), cursor.getString(cursor.getColumnIndex("stopname")), cursor.getString(cursor.getColumnIndex("stoplimousine")), cursor.getString(cursor.getColumnIndex("stopx")), cursor.getString(cursor.getColumnIndex("stopy")), cursor.getString(cursor.getColumnIndex("stopremark")));
                return stationInfo;
            }
        }
        return null;
    }


    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
