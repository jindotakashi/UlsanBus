package finedev.com.ulsanbus.station.bus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.SaxAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import finedev.com.ulsanbus.R;
import finedev.com.ulsanbus.bus.FindBusFragment;
import finedev.com.ulsanbus.db.DatabaseManager;
import finedev.com.ulsanbus.network.NetworkHelper;
import finedev.com.ulsanbus.station.StationInfo;
import finedev.com.ulsanbus.station.bus.arrival.StationBusArrivalInfo;
import finedev.com.ulsanbus.station.bus.arrival.StationBusArrivalListAdapter;

public class StationDetailFragment extends Fragment {

    private final String LOG_TAG = StationDetailFragment.class.getSimpleName();

    private static final String ARG_PARAM_STATIONINFO_ID = "paramStationInfoId";

    private DatabaseManager mDatabaseManager;

    // components
    private ListView listViewStationBus;

    private RelativeLayout relativeLayoutBusArrivalInfo;
    private TextView textViewBusArrivalInfoBusNo;
    private Button buttonShowBusRouteInfo;
    private ListView listViewBusArrivalInfo;
    ///////////////////

    private int mStationInfoId;
    private StationInfo mStationInfo;

    private List<StationBusInfo> mStationBusItems;
    private StationBusListAdapter mStationBusListAdapter;

    private List<StationBusArrivalInfo> mStationBusArrivalItems;
    private StationBusArrivalListAdapter mStationBusArrivalListAdapter;

    private FindBusFragment.OnBusItemSelectedListener mListener;
    private StationBusInfo mStationBusInfo;


    public static StationDetailFragment newInstance(int stationInfoId) {
        StationDetailFragment fragment = new StationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_STATIONINFO_ID, stationInfoId);
        fragment.setArguments(args);
        return fragment;
    }

    public StationDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStationInfoId = getArguments().getInt(ARG_PARAM_STATIONINFO_ID);
            DatabaseManager dbManager = new DatabaseManager(getActivity());
            mStationInfo = dbManager.getStationInfo(mStationInfoId);
        }
        getActivity().setTitle(mStationInfo.getStopName()+"["+mStationInfo.getStopId()+"]");
        mStationBusItems = new ArrayList<StationBusInfo>();
        mStationBusArrivalItems = new ArrayList<StationBusArrivalInfo>();
        mDatabaseManager = new DatabaseManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_detail, container, false);

        listViewStationBus = (ListView) view.findViewById(R.id.listView_station_bus);
        mStationBusListAdapter = new StationBusListAdapter(
                getActivity(),
                R.layout.listitem_station_bus_list_item,
                mStationBusItems);
        listViewStationBus.setAdapter(mStationBusListAdapter);
        listViewStationBus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mStationBusInfo = mStationBusListAdapter.getItem(position);
                textViewBusArrivalInfoBusNo.setText(mStationBusInfo.getBusInfo().getRouteNo());
                NetworkHelper.getStationBusArrivalInfo(mStationBusInfo.getRouteid(), mStationInfo.getStopId(), saxStationBusArrivalAsyncHttpResponseHandler);
            }
        });

        relativeLayoutBusArrivalInfo = (RelativeLayout) view.findViewById(R.id.relativeLayout_bus_arrival_info);
        textViewBusArrivalInfoBusNo = (TextView) view.findViewById(R.id.textView_bus_arrival_info_bus_no);
        buttonShowBusRouteInfo = (Button) view.findViewById(R.id.button_show_bus_route_info);
        listViewBusArrivalInfo = (ListView) view.findViewById(R.id.listView_bus_arrival_info);

        buttonShowBusRouteInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBusItemSelected(mStationBusInfo.getBusInfo().getId());
            }
        });
        mStationBusArrivalListAdapter = new StationBusArrivalListAdapter(
                getActivity(),
                R.layout.listitem_station_bus_arrival_list_item,
                mStationBusArrivalItems );
        listViewBusArrivalInfo.setAdapter(mStationBusArrivalListAdapter);

        return view;
    }

    private SaxAsyncHttpResponseHandler saxStationBusArrivalAsyncHttpResponseHandler =
            new SaxAsyncHttpResponseHandler(new SaxStationBusArrivalHandler()) {
                @Override
                public void onStart() {
                    super.onStart();
                    relativeLayoutBusArrivalInfo.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onSuccess(int i, Header[] headers, DefaultHandler defaultHandler) {
                    mStationBusArrivalListAdapter.notifyDataSetChanged();
                    relativeLayoutBusArrivalInfo.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(int i, Header[] headers, DefaultHandler defaultHandler) {
                }
            };

    private class SaxStationBusArrivalHandler extends DefaultHandler {

        private String busNo;
        private String busType;
        private String lowType;
        private String status;
        private String stopId;
        private String stopName;
        private String remainStopCnt;
        private String remainTime;
        private String emergencyCd;

        private String value;
        private List<StationBusArrivalInfo> stationBusArrivalItems = new ArrayList<StationBusArrivalInfo>();

        @Override
        public void startDocument() throws SAXException {
            Log.i(LOG_TAG, "SaxStationBusArrivalHandler::startDocument()");
        }
        @Override
        public void endDocument() throws SAXException {
            Log.i(LOG_TAG, "SaxStationBusArrivalHandler::endDocument()");
            mStationBusArrivalItems.clear();
            mStationBusArrivalItems.addAll(stationBusArrivalItems);
        }
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            value = new String(ch, start, length).trim();
        }
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ( qName.equals("BUSNO")) {
                busNo = new String(value);
            } else if ( qName.equals("BUSTYPE")) {
                busType = new String(value);
            } else if ( qName.equals("LOWTYPE")) {
                lowType = new String(value);
            } else if ( qName.equals("STATUS")) {
                status = new String(value);
            } else if ( qName.equals("STOPID")) {
                stopId = new String(value);
            } else if ( qName.equals("STOPNAME")) {
                stopName = new String(value);
            } else if ( qName.equals("REMAINSTOPCNT")) {
                remainStopCnt = new String(value);
            } else if ( qName.equals("REMAINTIME")) {
                remainTime = new String(value);
            } else if ( qName.equals("EMERGENCYCD")) {
                emergencyCd = new String(value);
            } else if ( qName.equals("ArrivalInfoTable")) {
                StationBusArrivalInfo stationBusArrivalInfo = new StationBusArrivalInfo(busNo, busType, lowType, status, stopId, stopName, remainStopCnt, remainTime, emergencyCd);
                stationBusArrivalItems.add(stationBusArrivalInfo);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getStationBusInfo();
    }

    @Override
    public void onPause() {
        saxStationBusAsyncHttpResponseHandler.sendCancelMessage();
        super.onPause();
    }

    private SaxAsyncHttpResponseHandler saxStationBusAsyncHttpResponseHandler =
            new SaxAsyncHttpResponseHandler(new SaxStationBusHandler()) {
                @Override
                public void onSuccess(int i, Header[] headers, DefaultHandler defaultHandler) {
                    mStationBusListAdapter.notifyDataSetChanged();
                }
                @Override
                public void onFailure(int i, Header[] headers, DefaultHandler defaultHandler) {
                }
            };

    private void getStationBusInfo() {
        Log.i(LOG_TAG, "getStationBusInfo()::Start");
        NetworkHelper.getStationBusInfo(mStationInfo.getStopId(), saxStationBusAsyncHttpResponseHandler);
    }

    private class SaxStationBusHandler extends DefaultHandler {

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

        private String value;

        private List<StationBusInfo> stationBusItems = new ArrayList<StationBusInfo>();

        public void startDocument() throws SAXException {
            Log.i(LOG_TAG, "SaxStationBusHandler::startDocument()");
        }

        @Override
        public void endDocument() throws SAXException {
            Log.i(LOG_TAG, "SaxStationBusHandler::endDocument()");
            mStationBusItems.clear();
            mStationBusItems.addAll(stationBusItems);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            value = new String(ch, start, length).trim();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("ROUTEID")) {
                routeId = new String(value);
            } else if (qName.equals("BUSNO")) {
                busNo = new String(value);
            } else if (qName.equals("BUSTYPE")) {
                busType = new String(value);
            } else if (qName.equals("LOWTYPE")) {
                lowType = new String(value);
            } else if (qName.equals("STATUS")) {
                status = new String(value);
            } else if (qName.equals("FSTOPNAME")) {
                fStopName = new String(value);
            } else if (qName.equals("TSTOPNAME")) {
                tStopName = new String(value);
            } else if (qName.equals("STOPID")) {
                stopId = new String(value);
            } else if (qName.equals("REMAINSTOPCNT")) {
                remainStopCnt = new String(value);
            } else if (qName.equals("REMAINTIME")) {
                remainTime = new String(value);
            } else if (qName.equals("EMERGENCYCD")) {
                emergencyCd = new String(value);
            } else if (qName.equals("AllBusArrivalInfoTable")) {
                StationBusInfo stationBusInfo = new StationBusInfo(
                        routeId, busNo, busType, lowType, status, fStopName, tStopName,
                        stopId, remainStopCnt, remainTime, emergencyCd);
                stationBusInfo.setBusInfo( mDatabaseManager.getBusInfo(routeId) );
                stationBusItems.add(stationBusInfo);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FindBusFragment.OnBusItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FindBusFragment.OnBusItemSelectedListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}