package finedev.com.ulsanbus.station.bus;

import finedev.com.ulsanbus.bus.BusInfo;

public class StationBusInfo {

    private String routeId;
    private String busNo;
    private String busType;
    private String lowType;
    private String status;
    private String fStopName;
    private String tStopName;
    private String stopId;
    private String remainStopCnt;
    private String remainTime;
    private String emergencyCd;

    private BusInfo busInfo;

    private int arrivalInfoType;
    public static final int ARRIVAL_INFO_TYPE_END = 0;
    public static final int ARRIVAL_INFO_TYPE_SOON = 1;
    public static final int ARRIVAL_INFO_TYPE_SCHEDULE = 2;

    public StationBusInfo(String routeId, String busNo, String busType, String lowType, String status, String fStopName, String tStopName, String stopId, String remainStopCnt, String remainTime, String emergencyCd) {
        this.routeId = routeId;
        this.busNo = busNo;
        this.busType = busType;
        this.lowType = lowType;
        this.status = status;
        this.fStopName = fStopName;
        this.tStopName = tStopName;
        this.stopId = stopId;
        this.remainStopCnt = remainStopCnt;
        this.remainTime = remainTime;
        this.emergencyCd = emergencyCd;
    }

    public void setBusInfo(BusInfo busInfo) {
        this.busInfo = busInfo;
    }

    public BusInfo getBusInfo(){ return busInfo; }

    public String getRouteid(){ return routeId; }

    public String getBusNo() {
        return busNo;
    }

    public String getBusRoute() {
        return fStopName + " ~ " + tStopName;
    }

    public String getBusArrivalInfo() {
        if ( status.equals("0")) {
            int remainTime = Integer.valueOf(this.remainTime);
            if (remainTime == 0) {
                arrivalInfoType = ARRIVAL_INFO_TYPE_END;
                return "운행종료";
            } else {
                arrivalInfoType = ARRIVAL_INFO_TYPE_SOON;

                int remainMinutes = (remainTime / 60);
                if ( remainMinutes == 0 ) return "1분 이내 도착";
                else return remainMinutes + "분 후 도착";
            }
        } else {
            arrivalInfoType = ARRIVAL_INFO_TYPE_SCHEDULE;
            return remainTime.substring(0,2)+":"+remainTime.substring(2,4)+" 출발 예정";
        }
    }

    public int getArrivalInfoType() { return arrivalInfoType; }
}
