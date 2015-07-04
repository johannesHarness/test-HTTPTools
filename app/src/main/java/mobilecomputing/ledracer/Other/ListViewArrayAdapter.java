package mobilecomputing.ledracer.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import mobilecomputing.ledracer.Database.Tables.History;
import mobilecomputing.ledracer.R;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Johannes on 05.03.2015.
 */
public class ListViewArrayAdapter extends ArrayAdapter<History> {

    private static String GET_PAST_TIME_DESCIPTION(long timeInMs) {
        long diff = System.currentTimeMillis() - timeInMs;
        diff /= 1000;

        //diff in secs
        if(diff < 60) {
            return "A moment ago";
        }

        diff /= 60;
        //diff in minutes
        if(diff < 60) return String.format("%d min" + (diff > 1 ? "s" : "") + " ago", diff);

        diff /= 60;
        //diff in hours
        if(diff < 24) return String.format("%d hour" + (diff > 1 ? "s" : "") + " ago", diff);

        diff /= 24;
        //diff in days
        if(diff <  7) return String.format("%d day" + (diff > 1 ? "s" : "") + " ago", diff);

        diff /= 7;
        //diff in weeks
        if(diff < 5) return String.format("%d week" + (diff > 1 ? "s" : "") + " ago", diff);


        return new Date(timeInMs).toString();
    }


    private final Context context;
    private final ArrayList<History> itemsArrayList;

    public ListViewArrayAdapter(Context context, ArrayList<History> itemsArrayList) {

        super(context, R.layout.listview_item, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.listview_item, parent, false);

        // 3. Get elements
        TextView tvLevel = (TextView)rowView.findViewById(R.id.tvLevel);
        TextView tvScore = (TextView)rowView.findViewById(R.id.tvScore);
        TextView tvDate = (TextView)rowView.findViewById(R.id.tvDate);

        //4. set values
        tvLevel.setText(String.format(this.context.getString(R.string.txt_level) + " %d", itemsArrayList.get(position).getLevel()));
        tvScore.setText(String.format(this.context.getString(R.string.txt_score) + " %d", itemsArrayList.get(position).getScore()));
        tvDate.setText(this.GET_PAST_TIME_DESCIPTION(itemsArrayList.get(position).getDate()));


        // 5. return rowView
        return rowView;
    }
}
