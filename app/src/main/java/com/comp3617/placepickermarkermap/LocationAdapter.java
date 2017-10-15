package com.comp3617.placepickermarkermap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * FinalProject : com.comp3617.placepickermarkermap
 * Created by G.E. Eidsness on 12/12/2016.
 */
class LocationAdapter extends ArrayAdapter<Location> {

    private static final String LOG_TAG = LocationAdapter.class.getSimpleName();
    private Context ctx;
    private List<Location> locationList;

    LocationAdapter(Context ctx, List<Location> locations) {
        super(ctx, 0, locations);
        this.ctx = ctx;
        this.locationList = locations;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Location getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return position;
        return locationList.get(position).hashCode();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Location currentItem = locationList.get(position);
        viewHolder.itemName.setText(currentItem.getName());
        viewHolder.itemAddress.setText(currentItem.getAddress());
        viewHolder.itemRemarks.setText(currentItem.getRemarks());

        return convertView;
    }

    /* *********************************
	 * We use the ViewHolder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/
    private class ViewHolder {
        TextView itemName, itemAddress, itemRemarks;

        private ViewHolder(View view){
            itemName = (TextView) view.findViewById(R.id.tvName);
            itemAddress = (TextView) view.findViewById(R.id.tvAddress);
            itemRemarks = (TextView) view.findViewById(R.id.tvRemarks);
        }
    }
}

