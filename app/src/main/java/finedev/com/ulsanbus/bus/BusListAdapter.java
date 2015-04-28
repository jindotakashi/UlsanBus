package finedev.com.ulsanbus.bus;

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

public class BusListAdapter extends ArrayAdapter<BusInfo> implements PinnedSectionListView.PinnedSectionListAdapter {

    LayoutInflater mLayoutLinflater;
    int mResource;
    private List<BusInfo> mItems;

    public BusListAdapter(Context context, int resource, List<BusInfo> items) {
        super(context, resource, items);
        mResource = resource;
        mLayoutLinflater = ((Activity)context).getLayoutInflater();
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        BusInfo busInfo = getItem(position);

        ItemViewHolder itemViewHolder = null;
        SectionViewHolder sectionViewHolder = null;
        if ( view == null ) {
            if( busInfo.type == BusInfo.ITEM ) {
                view = mLayoutLinflater.inflate(mResource, parent, false);
                itemViewHolder = new ItemViewHolder();
                itemViewHolder.textViewBusNo = (TextView) view.findViewById(R.id.textView_bus_no);
                itemViewHolder.textViewBusRoute = (TextView) view.findViewById(R.id.textView_bus_route);
                itemViewHolder.textViewBusInterval = (TextView) view.findViewById(R.id.textView_bus_interval);
                view.setTag(itemViewHolder);
            } else if( busInfo.type == BusInfo.SECTION ) {
                view = mLayoutLinflater.inflate(R.layout.listitem_bus_list_section, parent, false);
                sectionViewHolder = new SectionViewHolder();
                sectionViewHolder.textViewSectionName = (TextView) view.findViewById(R.id.textView_section_name);
                view.setTag(sectionViewHolder);
            }
        } else {
            if( busInfo.type == BusInfo.ITEM ) {
                itemViewHolder = (ItemViewHolder) view.getTag();
            } else if( busInfo.type == BusInfo.SECTION ) {
                sectionViewHolder = (SectionViewHolder) view.getTag();
            }
        }


        if( busInfo.type == BusInfo.ITEM ) {
            itemViewHolder.textViewBusNo.setText(busInfo.getRouteNo());
            itemViewHolder.textViewBusRoute.setText(busInfo.getBusRoute());
            itemViewHolder.textViewBusInterval.setText(busInfo.getBusInterval());
        } else if( busInfo.type == BusInfo.SECTION ) {
            sectionViewHolder.textViewSectionName.setText(busInfo.getBusTypeName());
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == BusInfo.SECTION;
    }

    class ItemViewHolder {
        private TextView textViewBusNo;
        private TextView textViewBusRoute;
        private TextView textViewBusInterval;
    }

    class SectionViewHolder {
        private TextView textViewSectionName;
    }
}
