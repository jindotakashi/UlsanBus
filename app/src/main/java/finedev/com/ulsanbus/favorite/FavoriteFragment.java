package finedev.com.ulsanbus.favorite;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hb.views.PinnedSectionListView;

import java.util.ArrayList;
import java.util.List;

import finedev.com.ulsanbus.R;
import finedev.com.ulsanbus.bus.BusInfo;
import finedev.com.ulsanbus.bus.FindBusFragment;
import finedev.com.ulsanbus.db.DatabaseManager;
import finedev.com.ulsanbus.db.FavoriteDbHelper;
import finedev.com.ulsanbus.station.FindStationFragment;
import finedev.com.ulsanbus.station.StationInfo;

public class FavoriteFragment extends Fragment {

    private final String LOG_TAG = FavoriteFragment.class.getSimpleName();

    private OnFavoriteItemSelectedListener mListener;

    private PinnedSectionListView listViewFavorite;
    private FavoriteListAdapter mFavoriteListAdapter;

    private List<FavoriteInfo> mFavoriteItems;
    private FavoriteDbHelper mFavoriteDbHelper;
    private DatabaseManager mDbManager;

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.favorite);
        mFavoriteItems = new ArrayList<FavoriteInfo>();
        mFavoriteDbHelper = new FavoriteDbHelper(getActivity());
        mDbManager = new DatabaseManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        listViewFavorite = (PinnedSectionListView) rootView.findViewById(R.id.listView_favorite);
        mFavoriteListAdapter = new FavoriteListAdapter(getActivity(), 0, mFavoriteItems);
        listViewFavorite.setAdapter(mFavoriteListAdapter);
        listViewFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFavoriteListAdapter.getItemViewType(position) == FavoriteInfo.BUS_ITEM) {
                    BusInfo busInfo = mFavoriteListAdapter.getItem(position).getBusInfo();
                    mListener.onBusItemSelected(busInfo.getId());
                } else if (mFavoriteListAdapter.getItemViewType(position) == FavoriteInfo.STATION_ITEM) {
                    StationInfo stationInfo = mFavoriteListAdapter.getItem(position).getStationInfo();
                    mListener.onStationItemClicked(stationInfo.getId());
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

    private void refreshListView() {
        List<FavoriteInfo> favoriteItems = new ArrayList<FavoriteInfo>();
        List<String> busFavorites = mFavoriteDbHelper.getBusFavorite();
        favoriteItems.add(new FavoriteInfo("버스 즐겨찾기"));
        for( String busId : busFavorites ) {
            favoriteItems.add(new FavoriteInfo(mDbManager.getBusInfo(busId)));
        }
        List<String> stationFavorites = mFavoriteDbHelper.getStationFavorite();
        favoriteItems.add(new FavoriteInfo("정류소 즐겨찾기"));
        for( String stationId : stationFavorites ) {
            favoriteItems.add(new FavoriteInfo(mDbManager.getStationInfo(stationId)));
        }
        mFavoriteItems.clear();
        mFavoriteItems.addAll(favoriteItems);
        mFavoriteListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFavoriteItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFavoriteItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFavoriteItemSelectedListener extends FindBusFragment.OnBusItemSelectedListener, FindStationFragment.OnStationItemSelectedListener {
    }

}
