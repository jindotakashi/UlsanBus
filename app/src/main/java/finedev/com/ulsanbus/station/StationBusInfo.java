package finedev.com.ulsanbus.station;

public class StationBusInfo {

    private String routeId;
    private String busNo;
    private String busType;
    private String lowType;
    private String status;
    private String fStopName;
    private String tStopName;
    private String stopId;
    private String stopName;
    private String remainStopCnt;
    private String remainTime;
    private String emergencyCd;

    public StationBusInfo(String routeId, String busNo, String busType, String lowType, String status, String fStopName, String tStopName, String stopId, String stopName, String remainStopCnt, String remainTime, String emergencyCd) {
        this.routeId = routeId;
        this.busNo = busNo;
        this.busType = busType;
        this.lowType = lowType;
        this.status = status;
        this.fStopName = fStopName;
        this.tStopName = tStopName;
        this.stopId = stopId;
        this.stopName = stopName;
        this.remainStopCnt = remainStopCnt;
        this.remainTime = remainTime;
        this.emergencyCd = emergencyCd;
    }

}
