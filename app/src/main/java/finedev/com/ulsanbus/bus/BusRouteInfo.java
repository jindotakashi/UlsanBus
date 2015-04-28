package finedev.com.ulsanbus.bus;

public class BusRouteInfo {

    private String stopId;
    private String stopName;
    private String stopX;
    private String stopY;

    private boolean busExist = false;


    public BusRouteInfo(String stopId, String stopName, String stopX, String stopY) {
        this.stopId     = stopId;
        this.stopName   = stopName;
        this.stopX      = stopX;
        this.stopY      = stopY;
    }

    public String getStopId() { return stopId; }
    public String getStopName() { return stopName; }
    public String getStopX() { return stopX; }
    public String getStopY() { return stopY; }

    public void setBusExist(boolean busExist) {
        this.busExist = busExist;
    }

    public boolean isBusExist() {
        return busExist;
    }
}
