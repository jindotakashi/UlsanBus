package finedev.com.ulsanbus.favorite;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;

import finedev.com.ulsanbus.R;
import finedev.com.ulsanbus.bus.BusInfo;
import finedev.com.ulsanbus.station.StationInfo;

public class FavoriteListAdapter extends ArrayAdapter<FavoriteInfo> implements PinnedSectionListView.PinnedSectionListAdapter {

    LayoutInflater mLayoutLinflater;
    int mResource;
    private List<FavoriteInfo> mItems;

    public FavoriteListAdapter(Context context, int resource, List<FavoriteInfo> items) {
        super(context, resource, items);
        mResource = resource;
        mLayoutLinflater = ((Activity)context).getLayoutInflater();
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        FavoriteInfo favoriteInfo = getItem(position);

        BusItemViewHolder busItemViewHolder = null;
        StationItemViewHolder stationItemViewHolder = null;
        SectionViewHolder sectionViewHolder = null;
        if ( view == null ) {
            if( favoriteInfo.type == FavoriteInfo.BUS_ITEM ) {
                view = mLayoutLinflater.inflate(R.layout.listitem_bus_list_item, parent, false);
                busItemViewHolder = new BusItemViewHolder();
                busItemViewHolder.textViewBusNo = (TextView) view.findViewById(R.id.textView_bus_no);
                busItemViewHolder.textViewBusRoute = (TextView) view.findViewById(R.id.textView_bus_route);
                busItemViewHolder.textViewBusInterval = (TextView) view.findViewById(R.id.textView_bus_interval);
                view.setTag(busItemViewHolder);
            } else if( favoriteInfo.type == FavoriteInfo.STATION_ITEM ) {
                view = mLayoutLinflater.inflate(R.layout.listitem_station_list_item, parent, false);
                stationItemViewHolder = new StationItemViewHolder();
                stationItemViewHolder.textViewStopId = (TextView) view.findViewById(R.id.textView_station_stop_id);
                stationItemViewHolder.textViewStopName = (TextView) view.findViewById(R.id.textView_station_stop_name);
                view.setTag(stationItemViewHolder);
            } else if ( favoriteInfo.type == FavoriteInfo.SECTION ) {
                view = mLayoutLinflater.inflate(R.layout.listitem_bus_list_section, parent, false);
                sectionViewHolder = new SectionViewHolder();
                sectionViewHolder.textViewSectionName = (TextView) view.findViewById(R.id.textView_section_name);
                view.setTag(sectionViewHolder);
            }
        } else {
            if( favoriteInfo.type == FavoriteInfo.BUS_ITEM ) {
                busItemViewHolder = (BusItemViewHolder) view.getTag();
            } else if( favoriteInfo.type == FavoriteInfo.STATION_ITEM ) {
                stationItemViewHolder = (StationItemViewHolder) view.getTag();
            } else if( favoriteInfo.type == FavoriteInfo.SECTION ) {
                sectionViewHolder = (SectionViewHolder) view.getTag();
            }
        }

        if( favoriteInfo.type == FavoriteInfo.BUS_ITEM ) {
            BusInfo busInfo = favoriteInfo.getBusInfo();
            busItemViewHolder.textViewBusNo.setText(busInfo.getRouteNo());
            busItemViewHolder.textViewBusRoute.setText(busInfo.getBusRoute());
            busItemViewHolder.textViewBusInterval.setText(busInfo.getBusInterval());
        } else if( favoriteInfo.type == FavoriteInfo.STATION_ITEM ) {
            StationInfo stationInfo = favoriteInfo.getStationInfo();
            stationItemViewHolder.textViewStopId.setText(stationInfo.getStopId());
            stationItemViewHolder.textViewStopName.setText(stationInfo.getStopName());
        } else if( favoriteInfo.type == FavoriteInfo.SECTION ) {
            sectionViewHolder.textViewSectionName.setText(favoriteInfo.getSection());
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == FavoriteInfo.SECTION;
    }

    class BusItemViewHolder {
        private TextView textViewBusNo;
        private TextView textViewBusRoute;
        private TextView textViewBusInterval;
    }

    class StationItemViewHolder {
        private TextView textViewStopId;
        private TextView textViewStopName;
    }
    class SectionViewHolder {
        private TextView textViewSectionName;
    }
}
