package finedev.com.ulsanbus.bus;

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
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.ArrayList;
import java.util.List;

import finedev.com.ulsanbus.R;
import finedev.com.ulsanbus.db.DatabaseManager;
import finedev.com.ulsanbus.db.RecentHistoryDbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link finedev.com.ulsanbus.bus.FindBusFragment.OnBusItemSelectedListener} interface
 * to handle interaction events.
 * Use the {@link FindBusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindBusFragment extends Fragment {

    private final String LOG_TAG = FindBusFragment.class.getSimpleName();

    private OnBusItemSelectedListener mListener;

    private EditText editTextSearchBusNumber;
    private TextView textViewRecentSearchHistoryInBus;

    private PinnedSectionListView listViewBus;
    private BusListAdapter busListAdapter;

    private List<BusInfo> mBusItems;

    public static FindBusFragment newInstance(String param1, String param2) {
        FindBusFragment fragment = new FindBusFragment();
        return fragment;
    }

    public FindBusFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.find_bus);
        mBusItems = new ArrayList<BusInfo>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_find_bus, container, false);

        editTextSearchBusNumber = (EditText) rootView.findViewById(R.id.editText_search_bus_number);
        editTextSearchBusNumber.addTextChangedListener(textWatcher);

        textViewRecentSearchHistoryInBus = (TextView) rootView.findViewById(R.id.textView_recent_search_history_in_bus);
        listViewBus = (PinnedSectionListView) rootView.findViewById(R.id.listView_bus_list);
        busListAdapter = new BusListAdapter(getActivity(), R.layout.listitem_bus_list_item, mBusItems);
        listViewBus.setAdapter(busListAdapter);
        listViewBus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (busListAdapter.getItemViewType(position) == BusInfo.ITEM) {
                    BusInfo busInfo = busListAdapter.getItem(position);
                    mListener.onBusItemSelected(busInfo.getId());
                }
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
        String text = editTextSearchBusNumber.getText().toString();
        DatabaseManager dbManager = new DatabaseManager(getActivity());
        if ( text.length() == 0 ) {
            showRecentHistory();
        } else {
            textViewRecentSearchHistoryInBus.setVisibility(View.GONE);
            List<BusInfo> busList = dbManager.findBusList(text);
            mBusItems.clear();
            mBusItems.addAll(busList);
            for ( int i=0; i< mBusItems.size(); i++ ) {
                if ( i == 0 || !mBusItems.get(i).getBusType().equals(mBusItems.get(i-1).getBusType())) {
                    mBusItems.add(i, new BusInfo(mBusItems.get(i).getBusType()));
                    i++;
                }
            }
        }
        busListAdapter.notifyDataSetChanged();
    }

    private void showRecentHistory() {
        DatabaseManager dbManager = new DatabaseManager(getActivity());
        textViewRecentSearchHistoryInBus.setVisibility(View.VISIBLE);
        RecentHistoryDbHelper recentHistoryDbHelper = new RecentHistoryDbHelper(getActivity());
        List<String> busRecentHistorys = recentHistoryDbHelper.getBusRecentHistory();
        List<BusInfo> busItems = new ArrayList<BusInfo>();
        for( String busId : busRecentHistorys ) {
            busItems.add(dbManager.getBusInfo(busId));
        }
        mBusItems.clear();
        mBusItems.addAll(busItems);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBusItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBusItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBusItemSelectedListener {
        public void onBusItemSelected(int busInfoId);
    }

}
