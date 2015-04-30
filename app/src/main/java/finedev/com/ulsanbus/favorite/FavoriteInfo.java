package finedev.com.ulsanbus.favorite;

import java.util.Calendar;

import finedev.com.ulsanbus.bus.BusInfo;
import finedev.com.ulsanbus.station.StationInfo;

public class FavoriteInfo {

    public static final int BUS_ITEM = 0;
    public static final int STATION_ITEM = 1;
    public static final int SECTION = 2;

    public int type = SECTION;

    private BusInfo busInfo;
    private StationInfo stationInfo;
    private String section;

    public FavoriteInfo(BusInfo busInfo) {
        this.busInfo = busInfo;
        type = BUS_ITEM;
    }
    public FavoriteInfo(StationInfo stationInfo) {
        this.stationInfo = stationInfo;
        type = STATION_ITEM;
    }
    public FavoriteInfo(String section) {
        this.section = section;
        type = SECTION;
    }

    public BusInfo getBusInfo() {
        return busInfo;
    }

    public String getSection() {
        return section;
    }

    public StationInfo getStationInfo() {
        return stationInfo;
    }
}
