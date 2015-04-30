package finedev.com.ulsanbus.station.bus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import finedev.com.ulsanbus.R;

public class StationBusListAdapter extends ArrayAdapter<StationBusInfo> {

    private Context mContext;
    private LayoutInflater mLayoutLinflater;
    private int mResource;
    private List<StationBusInfo> mItems;

    public StationBusListAdapter(Context context, int resource, List<StationBusInfo> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
        mLayoutLinflater = ((Activity) context).getLayoutInflater();
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        StationBusInfo stationBusInfo = getItem(position);

        ItemViewHolder itemViewHolder = null;
        if (view == null) {
            view = mLayoutLinflater.inflate(mResource, parent, false);
            itemViewHolder = new ItemViewHolder();
            itemViewHolder.textViewBusRouteNo = (TextView) view.findViewById(R.id.textView_bus_route_no);
            itemViewHolder.textViewBusRoute = (TextView) view.findViewById(R.id.textView_bus_route);
            itemViewHolder.textViewBusArrivalInfo = (TextView) view.findViewById(R.id.textView_bus_arrival_info);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        itemViewHolder.textViewBusRouteNo.setText(stationBusInfo.getBusInfo().getRouteNo());
        itemViewHolder.textViewBusRoute.setText(stationBusInfo.getBusInfo().getBusRoute());
        itemViewHolder.textViewBusArrivalInfo.setText(stationBusInfo.getBusArrivalInfo());
        if ( stationBusInfo.getArrivalInfoType() == StationBusInfo.ARRIVAL_INFO_TYPE_END ) {
            itemViewHolder.textViewBusArrivalInfo.setTextColor(mContext.getResources().getColor(R.color.list_header_text_color));
        } else if ( stationBusInfo.getArrivalInfoType() == StationBusInfo.ARRIVAL_INFO_TYPE_SOON ) {
            itemViewHolder.textViewBusArrivalInfo.setTextColor(mContext.getResources().getColor(R.color.ARRIVE));
        } else if ( stationBusInfo.getArrivalInfoType() == StationBusInfo.ARRIVAL_INFO_TYPE_SCHEDULE ) {
            itemViewHolder.textViewBusArrivalInfo.setTextColor(mContext.getResources().getColor(R.color.GREENSEA));
        }

        return view;
    }

    class ItemViewHolder {
        private TextView textViewBusRouteNo;
        private TextView textViewBusRoute;
        private TextView textViewBusArrivalInfo;
    }
}
