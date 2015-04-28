package finedev.com.ulsanbus.bus;

import java.util.Calendar;

/**
 * Created by ramju on 4/18/15.
 */
public class BusInfo {

    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public int type = ITEM;

    private int id;
    private String routeid;
    private String routeno;
    private String routedir;
    private String routetype;
    private String bustype;
    private String buscompany;
    private String companytel;
    private String fstopname;
    private String tstopname;
    private String wd_start_time;
    private String wd_end_time;
    private String wd_max_interval;
    private String wd_min_interval;
    private String we_start_time;
    private String we_end_time;
    private String we_max_interval;
    private String we_min_interval;
    private String ws_start_time;
    private String ws_end_time;
    private String ws_max_interval;
    private String ws_min_interval;
    private String interval;
    private String traveltime;
    private String length;
    private String operationcnt;
    private String remark;
    private String TStopName;

    public BusInfo(String bustype) {
        this.bustype = bustype;
        type = SECTION;
    }

    public BusInfo(int id, String routeid, String routeno, String routedir, String routetype, String bustype, String buscompany, String companytel, String fstopname, String tstopname, String wd_start_time, String wd_end_time, String wd_max_interval, String wd_min_interval, String we_start_time, String we_end_time, String we_max_interval, String we_min_interval, String ws_start_time, String ws_end_time, String ws_max_interval, String ws_min_interval, String interval, String traveltime, String length, String operationcnt, String remark) {
        this.id = id;
        this.routeid = routeid;
        this.routeno = routeno;
        this.routedir = routedir;
        this.routetype = routetype;
        this.bustype = bustype;
        this.buscompany = buscompany;
        this.companytel = companytel;
        this.fstopname = fstopname;
        this.tstopname = tstopname;
        this.wd_start_time = wd_start_time;
        this.wd_end_time = wd_end_time;
        this.wd_max_interval = wd_max_interval;
        this.wd_min_interval = wd_min_interval;
        this.we_start_time = we_start_time;
        this.we_end_time = we_end_time;
        this.we_max_interval = we_max_interval;
        this.we_min_interval = we_min_interval;
        this.ws_start_time = ws_start_time;
        this.ws_end_time = ws_end_time;
        this.ws_max_interval = ws_max_interval;
        this.ws_min_interval = ws_min_interval;
        this.interval = interval;
        this.traveltime = traveltime;
        this.length = length;
        this.operationcnt = operationcnt;
        this.remark = remark;
    }

    public int getId() { return id;}

    public String getRouteNo() {
        return routeno;
    }
    public String getBusType() { return bustype; }
    public String getBusTypeName() {
        if ( bustype.equals("0") ) {
            return "일반";
        } else if ( bustype.equals("1") ) {
            return "좌석";
        } else if ( bustype.equals("2") ) {
            return "리무진";
        } else if ( bustype.equals("3") ) {
            return "마을";
        } else if ( bustype.equals("4") ) {
            return "지선";
        }
        return bustype;
    }
    public String getBusRoute() {
        return fstopname + "->" + tstopname;
    }

    public String getBusInterval() {
        String busInterval;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                busInterval = wd_min_interval+"분 ~ "+wd_max_interval+"분";
                break;
            case Calendar.SATURDAY:
                busInterval = we_min_interval+"분 ~ "+we_max_interval+"분";
                break;
            default:
                busInterval = ws_min_interval+"분 ~ "+ws_max_interval+"분";
                break;
        }
        return busInterval;
    }


    public String getRouteDir() {
        return routedir;
    }

    public String getFStopName() {
        return fstopname;
    }
    public String getFStopDisplayName() {
        return getFStopName() + "방면";
    }

    public String getTStopName() {
        return tstopname;
    }
    public String getTStopDisplayName() {
        return getTStopName() + "방면";
    }

    public String getBusSummary() {
        return getRouteNo() + "번 버스 [ 배차간격 : " + getBusInterval() + " ]";
    }

    public String getBusOperationTimeForDisplay() {
        return "기점 운행시간 : " + getBusOperationTime();
    }

    public String getBusOperationStartTime() {
        String busOperationStartTime;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                busOperationStartTime = wd_start_time.substring(0,2) + ":" + wd_start_time.substring(2,4);
                break;
            case Calendar.SATURDAY:
                busOperationStartTime = we_start_time.substring(0,2) + ":" + we_start_time.substring(2,4);
                break;
            default:
                busOperationStartTime = ws_start_time.substring(0,2) + ":" + ws_start_time.substring(2,4);
                break;
        }
        return busOperationStartTime;
    }

    public String getBusOperationEndTime() {
        String busOperationStartTime;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                busOperationStartTime = wd_end_time.substring(0,2) + ":" + wd_end_time.substring(2,4);
                break;
            case Calendar.SATURDAY:
                busOperationStartTime = we_end_time.substring(0,2) + ":" + we_end_time.substring(2,4);
                break;
            default:
                busOperationStartTime = ws_end_time.substring(0,2) + ":" + ws_end_time.substring(2,4);
                break;
        }
        return busOperationStartTime;
    }

    public String getBusOperationTime() {
        String busOperationTime;
        busOperationTime = getBusOperationStartTime() + " ~ " + getBusOperationEndTime();
        return busOperationTime;
    }

    public String getRouteId() {
        return routeid;
    }
}
