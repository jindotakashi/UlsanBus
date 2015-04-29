package finedev.com.ulsanbus.db;

import android.provider.BaseColumns;

/**
 * Created by ram on 2015-04-29.
 */
public class FavoriteContract {



    public static final String PATH_BUS = "bus";
    public static final String PATH_STATION = "station";

    public static final class BusEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite_bus";

        public static final String COLUMN_BUS_ID = "bus_id";

        public static final String[] TABLE_COLUMNS = {COLUMN_BUS_ID};
    }

    public static final class StationEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite_station";

        public static final String COLUMN_STATION_ID = "station_id";

        public static final String[] TABLE_COLUMNS = {COLUMN_STATION_ID};
    }

}
