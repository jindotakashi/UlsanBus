package finedev.com.ulsanbus.bus;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loopj.android.http.SaxAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import finedev.com.ulsanbus.R;
import finedev.com.ulsanbus.db.DatabaseManager;
import finedev.com.ulsanbus.network.NetworkHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BusDetailFragment.OnBusDetailItemSelectedListener} interface
 * to handle interaction events.
 * Use the {@link BusDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusDetailFragment extends Fragment {

    private final String LOG_TAG = BusDetailFragment.class.getSimpleName();

    private static final String ARG_PARAM_BUSINFO_ID = "paramBusInfoId";

    // components
    private LinearLayout linearLayoutDirectionButtonContainer;
    private ToggleButton toggleButtonDirectionFirst;
    private ToggleButton toggleButtonDirectionSecond;
    private TextView textViewBusSummary;
    private TextView textViewBusInterval;
    private ListView listViewBusRoute;
    //////////////////////

    private int mBusInfoId;
    private BusInfo mBusInfo;
    private String mCurrentRouteId;

    private List<BusRouteInfo> mBusRouteItems;
    private BusRouteListAdapter mBusRouteListAdapter;

    private OnBusDetailItemSelectedListener mListener;

    public static BusDetailFragment newInstance(int busInfoId) {
        BusDetailFragment fragment = new BusDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_BUSINFO_ID, busInfoId);
        fragment.setArguments(args);
        return fragment;
    }

    public BusDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBusInfoId = getArguments().getInt(ARG_PARAM_BUSINFO_ID);
            DatabaseManager dbManager = new DatabaseManager(getActivity());
            mBusInfo = dbManager.getBusInfo(mBusInfoId);
        }
        mBusRouteItems = new ArrayList<BusRouteInfo>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_detail, container, false);

        linearLayoutDirectionButtonContainer = (LinearLayout) view.findViewById(R.id.linearLayout_direction_button_container);
        toggleButtonDirectionFirst = (ToggleButton) view.findViewById(R.id.toggleButton_direction_first);
        toggleButtonDirectionSecond = (ToggleButton) view.findViewById(R.id.toggleButton_direction_second);
        textViewBusSummary = (TextView) view.findViewById(R.id.textView_bus_summary);
        textViewBusInterval = (TextView) view.findViewById(R.id.textView_bus_interval);
        listViewBusRoute = (ListView) view.findViewById(R.id.listView_bus_route);


        mCurrentRouteId = mBusInfo.getRouteId();
        if ( mBusInfo.getRouteDir().equals("3") ) {
            linearLayoutDirectionButtonContainer.setVisibility(View.GONE);
        } else {
            toggleButtonDirectionFirst.setTextOn(mBusInfo.getTStopDisplayName());
            toggleButtonDirectionFirst.setTextOff(mBusInfo.getTStopDisplayName());
            toggleButtonDirectionFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentRouteId = mBusInfo.getRouteId();
                    toggleButtonDirectionFirst.setChecked(true);
                    toggleButtonDirectionSecond.setChecked(false);
                    getBusRouteInfo();
                }
            });
            toggleButtonDirectionSecond.setTextOn(mBusInfo.getFStopDisplayName());
            toggleButtonDirectionSecond.setTextOff(mBusInfo.getFStopDisplayName());
            toggleButtonDirectionSecond.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentRouteId = mBusInfo.getRouteId().substring(0,mBusInfo.getRouteId().length()-1)+"2";
                    toggleButtonDirectionFirst.setChecked(false);
                    toggleButtonDirectionSecond.setChecked(true);
                    getBusRouteInfo();
                }
            });
            toggleButtonDirectionFirst.setChecked(true);
            toggleButtonDirectionSecond.setChecked(false);
        }

        textViewBusSummary.setText( mBusInfo.getBusSummary() );
        textViewBusInterval.setText( mBusInfo.getBusOperationTimeForDisplay() );

        mBusRouteListAdapter = new BusRouteListAdapter(getActivity(), R.layout.listitem_bus_route_list_item, mBusRouteItems);
        listViewBusRoute.setAdapter(mBusRouteListAdapter);
        listViewBusRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getBusRouteInfo();
    }

    @Override
    public void onPause() {
        saxBusRouteAsyncHttpResponseHandler.sendCancelMessage();
        saxBusLocationAsyncHttpResponseHandler.sendCancelMessage();
        super.onPause();
    }

    private SaxAsyncHttpResponseHandler saxBusRouteAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler( new SaxBusRouteHandler() ) {
        @Override
        public void onSuccess(int i, Header[] headers, DefaultHandler defaultHandler) {
            mBusRouteListAdapter.notifyDataSetChanged();
            NetworkHelper.getBusLocationInfo(mCurrentRouteId, saxBusLocationAsyncHttpResponseHandler);
        }
        @Override
        public void onFailure(int i, Header[] headers, DefaultHandler defaultHandler) {
        }
    };

    private SaxAsyncHttpResponseHandler saxBusLocationAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler( new SaxBusLocationHandler() ) {

        @Override
        public void onSuccess(int i, Header[] headers, DefaultHandler defaultHandler) {
            mBusRouteListAdapter.notifyDataSetChanged();
        }
        @Override
        public void onFailure(int i, Header[] headers, DefaultHandler defaultHandler) {
        }
    };

    private void getBusRouteInfo() {
        Log.i(LOG_TAG, "getBusRouteInfo()::Start");
        NetworkHelper.getBusRouteInfo( mCurrentRouteId, saxBusRouteAsyncHttpResponseHandler);
    }

    private class SaxBusLocationHandler extends DefaultHandler {

        private String stopId;
        private Set<String> stopIdSet = new HashSet<String>();

        private String value;

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            value = new String(ch, start, length).trim();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("STOPID")) {
                stopId = new String(value);
            } else if (qName.equals("BusLocationInfoTable")) {
                stopIdSet.add(stopId);
            }
        }

        @Override
        public void startDocument() throws SAXException {
            Log.i(LOG_TAG, "SaxBusLocationHandler::startDocument()");
        }

        @Override
        public void endDocument() throws SAXException {
            Log.i(LOG_TAG, "SaxBusLocationHandler::endDocument()");
            for ( BusRouteInfo busRouteInfo : mBusRouteItems) {
                if ( stopIdSet.contains(busRouteInfo.getStopId()) ) {
                    busRouteInfo.setBusExist(true);
                }
            }
        }
    }

    private class SaxBusRouteHandler extends DefaultHandler {

        private String stopId;
        private String stopName;
        private String stopX;
        private String stopY;

        private String value;

        private List<BusRouteInfo> busRouteItems = new ArrayList<BusRouteInfo>();

        @Override
        public void startDocument() throws SAXException {
            Log.i(LOG_TAG, "SaxBusRouteHandler::startDocument()");
        }

        @Override
        public void endDocument() throws SAXException {
            mBusRouteItems.clear();
            mBusRouteItems.addAll(busRouteItems);
            Log.i(LOG_TAG, "SaxBusRouteHandler::endDocument()");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//            super.startElement(uri, localName, qName, attributes);
            if ( qName.equals("StopInfoTable")) {

            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            value = new String(ch, start, length).trim();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
//            Log.i(LOG_TAG, "SaxBusRouteHandler::endElement()::qName="+qName);
            if ( qName.equals("STOPID")) {
                stopId = new String(value);
            } else if ( qName.equals("STOPNAME")) {
                stopName = new String(value);
            } else if ( qName.equals("STOPX")) {
                stopX = new String(value);
            } else if ( qName.equals("STOPY")) {
                stopY = new String(value);
            } else if ( qName.equals("StopInfoTable")) {
                BusRouteInfo busRouteInfo = new BusRouteInfo(stopId, stopName, stopX, stopY );
                busRouteItems.add(busRouteInfo);
            }
        }
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.OnBusDetailItemSelectedListener(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnBusDetailItemSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBusDetailItemSelectedListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
