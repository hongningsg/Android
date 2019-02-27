package com.example.user.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by User on 16/10/2018.
 */

public class MySensorsAdapter extends ArrayAdapter<Sensor> {
    private int textViewResourceID;

    private static class ViewHolder{
        private TextView itemview;
    }

    public MySensorsAdapter(Context context, int textViewResourceID, List<Sensor> items){
        super(context, textViewResourceID, items);
        this.textViewResourceID = textViewResourceID;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(this.getContext()).inflate(textViewResourceID, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemview = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Sensor item = getItem(position);
        if (item != null){
            viewHolder.itemview.setText("Name: "+item.getName() + " / Vendor :" + item.getVendor() + " / Version :" + item.getVersion() + " / Resolution :"
            + item.getResolution() + " Maximum Range :" + item.getMaximumRange() + " MinDelay " + item.getMinDelay() );
        }

        return convertView;
    }
}
