package finedev.com.ulsanbus.station;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import finedev.com.ulsanbus.R;
import finedev.com.ulsanbus.db.DatabaseManager;
import finedev.com.ulsanbus.db.RecentHistoryDbHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link finedev.com.ulsanbus.station.FindStationFragment.OnStationItemSelectedListener} interface
 * to handle interaction events.
 * Use the {@link FindStationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindStationFragment extends Fragment {

    private final String LOG_TAG = FindStationFragment.class.getSimpleName();

    private OnStationItemSelectedListener mListener;

    private EditText editTextSearchStation;
    private TextView textViewRecentSearchHistoryInStation;

    private ListView listViewStationList;
    private StationListAdapter stationListAdapter;

    private List<StationInfo> mStationItems;

    public static FindStationFragment newInstance(String param1, String param2) {
        FindStationFragment fragment = new FindStationFragment();
        return fragment;
    }

    public FindStationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.find_station);
        mStationItems = new ArrayList<StationInfo>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_station, container, false);

        editTextSearchStation       = (EditText) rootView.findViewById(R.id.editText_search_station);
        editTextSearchStation.addTextChangedListener(textWatcher);

        textViewRecentSearchHistoryInStation    = (TextView) rootView.findViewById(R.id.textView_recent_search_history_in_station);
        listViewStationList                     = (ListView) rootView.findViewById(R.id.listView_station_list);
        stationListAdapter                      = new StationListAdapter(getActivity(), R.layout.listitem_station_list_item, mStationItems);

        listViewStationList.setAdapter(stationListAdapter);
        listViewStationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StationInfo stationInfo = stationListAdapter.getItem(position);
                mListener.onStationItemClicked(stationInfo.getId());
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            refreshListView();
        }
    };

    private void refreshListView() {
        String text = editTextSearchStation.getText().toString();
        DatabaseManager dbManager = new DatabaseManager(getActivity());
        mStationItems.clear();
        if ( text.length() == 0 ) {
            showRecentHistory();
        } else {
            textViewRecentSearchHistoryInStation.setVisibility(View.GONE);
            List<StationInfo> stationList = dbManager.findStationList(text);
            mStationItems.addAll(stationList);
        }
        stationListAdapter.notifyDataSetChanged();
    }

    private void showRecentHistory() {
        DatabaseManager dbManager = new DatabaseManager(getActivity());
        textViewRecentSearchHistoryInStation.setVisibility(View.VISIBLE);
        RecentHistoryDbHelper recentHistoryDbHelper = new RecentHistoryDbHelper(getActivity());
        List<String> stationRecentHistorys = recentHistoryDbHelper.getStationRecentHistory();
        List<StationInfo> stationItems = new ArrayList<StationInfo>();
        for( String stationId : stationRecentHistorys ) {
            stationItems.add(dbManager.getStationInfo(stationId));
        }
        mStationItems.clear();
        mStationItems.addAll(stationItems);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStationItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStationItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnStationItemSelectedListener {
        public void onStationItemClicked(int stationInfoId);
    }

}
