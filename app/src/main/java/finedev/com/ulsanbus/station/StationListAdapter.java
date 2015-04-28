package finedev.com.ulsanbus.station;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import finedev.com.ulsanbus.R;

public class StationListAdapter extends ArrayAdapter<StationInfo> {

    LayoutInflater mLayoutLinflater;
    int mResource;
    private List<StationInfo> mItems;

    public StationListAdapter(Context context, int resource, List<StationInfo> items) {
        super(context, resource, items);
        mResource = resource;
        mLayoutLinflater = ((Activity) context).getLayoutInflater();
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        StationInfo stationInfo = getItem(position);

        ItemViewHolder itemViewHolder = null;
        if (view == null) {
            view = mLayoutLinflater.inflate(mResource, parent, false);
            itemViewHolder = new ItemViewHolder();
            itemViewHolder.textViewStopId = (TextView) view.findViewById(R.id.textView_station_stop_id);
            itemViewHolder.textViewStopName = (TextView) view.findViewById(R.id.textView_station_stop_name);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }


        itemViewHolder.textViewStopId.setText(stationInfo.getStopId());
        itemViewHolder.textViewStopName.setText(stationInfo.getStopName());

        return view;
    }

    class ItemViewHolder {
        private TextView textViewStopId;
        private TextView textViewStopName;

    }
}
