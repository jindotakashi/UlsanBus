package finedev.com.ulsanbus.bus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loopj.android.http.SaxAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import finedev.com.ulsanbus.R;
import finedev.com.ulsanbus.db.DatabaseManager;
import finedev.com.ulsanbus.db.FavoriteDbHelper;
import finedev.com.ulsanbus.db.RecentHistoryDbHelper;
import finedev.com.ulsanbus.network.NetworkHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link finedev.com.ulsanbus.bus.BusDetailFragment.OnBusStationItemSelectedListener} interface
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

    private OnBusStationItemSelectedListener mListener;

    private MenuItem mMenuRefresh;
    private MenuItem mMenuFavorite;

    private boolean isFavorited;
    private FavoriteDbHelper mFavoriteDbHelper;
    private DatabaseManager mDatabaseManager;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorite_refresh, menu);
        mMenuRefresh = menu.findItem(R.id.menu_refresh);
        mMenuFavorite = menu.findItem(R.id.menu_favorite);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setFavoriteMenuIcon();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.menu_refresh ) {
            getBusRouteInfo();
        } else if ( item.getItemId() == R.id.menu_favorite ) {
            if ( isFavorited ) {
                mFavoriteDbHelper.deleteBusFavorite(mBusInfo.getRouteId());
            } else {
                mFavoriteDbHelper.insertBusFavorite(mBusInfo.getRouteId());
            }
            setFavoriteMenuIcon();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFavoriteMenuIcon() {
        isFavorited = mFavoriteDbHelper.isExistBusFavorite(mBusInfo.getRouteId());
        if ( isFavorited ) {
            mMenuFavorite.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        } else {
            mMenuFavorite.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
        }
    }

    private void setRefreshMenuProgress(boolean progress) {
        if ( progress ) {
            MenuItemCompat.setActionView(mMenuRefresh, R.layout.actionbar_indeterminate_progress);
        } else {
            MenuItemCompat.setActionView(mMenuRefresh, null);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseManager = new DatabaseManager(getActivity());
        mFavoriteDbHelper = new FavoriteDbHelper(getActivity());
        if (getArguments() != null) {
            mBusInfoId = getArguments().getInt(ARG_PARAM_BUSINFO_ID);
            mBusInfo = mDatabaseManager.getBusInfo(mBusInfoId);
            RecentHistoryDbHelper recentHistoryDbHelper = new RecentHistoryDbHelper(getActivity());
            recentHistoryDbHelper.insertBusRecentHistory(mBusInfo.getRouteId());
        }
        getActivity().setTitle(mBusInfo.getRouteNo()+"번 버스");
        mBusRouteItems = new ArrayList<BusRouteInfo>();
        setHasOptionsMenu(true);
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

        mBusRouteListAdapter = new BusRouteListAdapter(
                getActivity(),
                R.layout.listitem_bus_route_list_item,
                mBusRouteItems);
        listViewBusRoute.setAdapter(mBusRouteListAdapter);
        listViewBusRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusRouteInfo busRouteInfo = mBusRouteListAdapter.getItem(position);
                mListener.onBusStationItemSelect(busRouteInfo.getStopId());
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

    private SaxAsyncHttpResponseHandler saxBusRouteAsyncHttpResponseHandler =
            new SaxAsyncHttpResponseHandler(new SaxBusRouteHandler()) {

                @Override
                public void onStart() {
                    super.onStart();
                    setRefreshMenuProgress(true);
                }

                @Override
                public void onFinish() {
                    setRefreshMenuProgress(false);
                    super.onFinish();
                }

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
        public void onStart() {
            setRefreshMenuProgress(true);
        }

        @Override
        public void onFinish() {
            setRefreshMenuProgress(false);
        }

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
            Log.i(LOG_TAG, "SaxBusRouteHandler::endDocument()");
            mBusRouteItems.clear();
            mBusRouteItems.addAll(busRouteItems);
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBusStationItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBusStationItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBusStationItemSelectedListener {
        public void onBusStationItemSelect(String stopId);
    }

}
