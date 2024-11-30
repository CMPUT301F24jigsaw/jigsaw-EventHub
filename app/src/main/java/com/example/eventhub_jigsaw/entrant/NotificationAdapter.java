package com.example.eventhub_jigsaw.entrant;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private List<String> notificationList;

    /**
     * Constructor for the NotificationAdapter.
     *
     * @param context          The context of the activity or fragment.
     * @param notificationList The list of notifications to display.
     */
    public NotificationAdapter(Context context, List<String> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    /**
     * Returns the number of items in the list.
     */
    @Override
    public int getCount() {
        return notificationList.size();
    }

    /**
     * Returns the item at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    /**
     * Returns the item's ID (position in this case).
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Creates or updates the view for each list item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // Reuse views for better performance
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.items_notification, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set notification text
        String notification = notificationList.get(position);
        viewHolder.notificationText.setText(notification);

        return convertView;
    }

    /**
     * ViewHolder pattern for better performance.
     */
    private static class ViewHolder {
        TextView notificationText;

        ViewHolder(View view) {
            notificationText = view.findViewById(R.id.notificationText);
        }
    }
}