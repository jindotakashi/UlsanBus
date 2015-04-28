package finedev.com.ulsanbus.station.bus.arrival;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import finedev.com.ulsanbus.R;

public class StationBusArrivalListAdapter extends ArrayAdapter<StationBusArrivalInfo> {

    LayoutInflater mLayoutLinflater;
    int mResource;
    private List<StationBusArrivalInfo> mItems;

    public StationBusArrivalListAdapter(Context context, int resource, List<StationBusArrivalInfo> items) {
        super(context, resource, items);
        mResource = resource;
        mLayoutLinflater = ((Activity) context).getLayoutInflater();
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        StationBusArrivalInfo stationBusArrivalInfo = getItem(position);

        ItemViewHolder itemViewHolder = null;
        if (view == null) {
            view = mLayoutLinflater.inflate(mResource, parent, false);
            itemViewHolder = new ItemViewHolder();
            itemViewHolder.textViewArrivalInfo = (TextView) view.findViewById(R.id.textView_arrival_info);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        itemViewHolder.textViewArrivalInfo.setText(stationBusArrivalInfo.getArrivalInfo());

        return view;
    }

    class ItemViewHolder {
        private TextView textViewArrivalInfo;
    }
}
