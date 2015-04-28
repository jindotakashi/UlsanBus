package finedev.com.ulsanbus.station;

/**
 * Created by ramju on 4/18/15.
 */
public class StationInfo {

    private int id;
    private String stopid;
    private String stopname;
    private String stoplimousine;
    private String stopx;
    private String stopy;
    private String stopremark;

    public StationInfo(int id, String stopid, String stopname, String stoplimousine, String stopx, String stopy, String stopremark) {
        this.id = id;
        this.stopid = stopid;
        this.stopname = stopname;
        this.stoplimousine = stoplimousine;
        this.stopx = stopx;
        this.stopy = stopy;
        this.stopremark = stopremark;
    }

    public int getId() { return id; }
    public String getStopId() { return stopid; }
    public String getStopName() { return stopname; }

}
