package finedev.com.ulsanbus.bus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import finedev.com.ulsanbus.R;

public class BusRouteListAdapter extends ArrayAdapter<BusRouteInfo> {

    LayoutInflater mLayoutLinflater;
    int mResource;
    private List<BusRouteInfo> mItems;

    public BusRouteListAdapter(Context context, int resource, List<BusRouteInfo> items) {
        super(context, resource, items);
        mResource = resource;
        mLayoutLinflater = ((Activity) context).getLayoutInflater();
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        BusRouteInfo busRouteInfo = getItem(position);

        ItemViewHolder itemViewHolder = null;
        if (view == null) {
            view = mLayoutLinflater.inflate(mResource, parent, false);
            itemViewHolder = new ItemViewHolder();
            itemViewHolder.textViewBusStopId = (TextView) view.findViewById(R.id.textView_bus_stop_id);
            itemViewHolder.textViewBusStopName = (TextView) view.findViewById(R.id.textView_bus_stop_name);
            itemViewHolder.imageViewBusExist = (ImageView) view.findViewById(R.id.imageView_bus_exist);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        itemViewHolder.textViewBusStopId.setText(busRouteInfo.getStopId());
        itemViewHolder.textViewBusStopName.setText(busRouteInfo.getStopName());
        if ( busRouteInfo.isBusExist() ) {
            itemViewHolder.imageViewBusExist.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.imageViewBusExist.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    class ItemViewHolder {
        private TextView textViewBusStopId;
        private TextView textViewBusStopName;
        private ImageView imageViewBusExist;
    }
}
