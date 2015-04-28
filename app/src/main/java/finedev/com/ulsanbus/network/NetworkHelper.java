package finedev.com.ulsanbus.network;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SaxAsyncHttpResponseHandler;

import finedev.com.ulsanbus.AppConstant;

public class NetworkHelper {

    private final static String LOG_TAG = NetworkHelper.class.getSimpleName();

    private static final String BASE_CAFE24_URL = AppConstant.BASE_CAFE24_URL;
//    private static final String BASE_ULSAN_ITS_URL = "http://api.twitter.com/1/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getDatabaseInfo(JsonHttpResponseHandler jsonResponseHandler) {
        client.get( BASE_CAFE24_URL + "Ulsan/UlsanBus/get_newest_info_json.php", jsonResponseHandler);
    }

    public static void downloadFile(String url, FileAsyncHttpResponseHandler fileResponseHandler) {
        client.get( url, fileResponseHandler);
    }




    public static void getBusRouteInfo(String routeId, SaxAsyncHttpResponseHandler xmlResponseHandler) {
        String url = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailInfo2.xo?ctype=A&routeid="+routeId;
        Log.i(LOG_TAG, "getBusRouteInfo()::url="+url);
        client.get(url, xmlResponseHandler);
    }
    public static void getBusLocationInfo(String routeId, SaxAsyncHttpResponseHandler xmlResponseHandler) {
        String url = "http://apis.its.ulsan.kr:8088/Service4.svc/BusLocationInfo.xo?ctype=A&routeid="+routeId;
        Log.i(LOG_TAG, "getBusLocationInfo()::url="+url);
        client.get(url, xmlResponseHandler);
    }

    public static void getStationBusInfo(String stopId, SaxAsyncHttpResponseHandler xmlResponseHandler) {
        String url = "http://apis.its.ulsan.kr:8088/Service4.svc/AllBusArrivalInfo.xo?ctype=A&stopid="+stopId+"&busOrder=1";
        Log.i(LOG_TAG, "getStationBusInfo()::url="+url);
        client.get(url, xmlResponseHandler);
    }
//

//    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.get(getAbsoluteUrl(url), params, responseHandler);
//    }
//
//    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(getAbsoluteUrl(url), params, responseHandler);
//    }
//
//    private static String getAbsoluteUrl(String relativeUrl) {
//        return BASE_ULSAN_ITS_URL + relativeUrl;
//    }

}
