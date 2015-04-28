package finedev.com.ulsanbus.station.bus.arrival;

import finedev.com.ulsanbus.bus.BusInfo;

public class StationBusArrivalInfo {

    private String busNo;
    private String busType;
    private String lowType;
    private String status;
    private String stopId;
    private String stopName;
    private String remainStopCnt;
    private String remainTime;
    private String emergencyCd;

    public StationBusArrivalInfo(String busNo, String busType, String lowType, String status,
                                 String stopId, String stopName, String remainStopCnt,
                                 String remainTime, String emergencyCd) {
        this.busNo = busNo;
        this.busType = busType;
        this.lowType = lowType;
        this.status = status;
        this.stopId = stopId;
        this.stopName = stopName;
        this.remainStopCnt = remainStopCnt;
        this.remainTime = remainTime;
        this.emergencyCd = emergencyCd;
    }

    public int getRemainTimeInMinutes() {
        int remainTimeInMinutes = Integer.valueOf(remainTime)/60;
        return remainTimeInMinutes;
    }

    public String getRemainTimeForDisplay() {
        int remainTimeInMinutes = getRemainTimeInMinutes();
        if ( remainTimeInMinutes == 0 ) return "잠시 후 도착";
        else return remainTimeInMinutes + "분 후 도착";
    }

    public String getArrivalInfo() {
        return getRemainTimeForDisplay() + "( " + remainStopCnt + " 전 )";
    }
}
