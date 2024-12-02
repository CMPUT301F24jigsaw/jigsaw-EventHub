package com.example.eventhub_jigsaw.organizer;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.User;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WaitlistAdapter displays users in the waitlist within a RecyclerView.
 */

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.ViewHolder> {

    private List<User> userList;
    private boolean geolocation;
    private Context context;
    private ExecutorService executor;

    /**
     * Initializes the adapter with a given context, user list, and geolocation option.
     *
     * @param context
     * @param userList The list of users to display.
     * @param geolocation Checking if geolocation is enabled
     */
    public WaitlistAdapter(Context context, List<User> userList, boolean geolocation) {
        this.context = context;
        this.userList = userList;
        this.geolocation = geolocation;
        this.executor = Executors.newFixedThreadPool(3); // Limit geolocation threads to 3
    }

    /**
     * Creates a new ViewHolder for displaying a user in the waitlist.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds user data to the view for a specific item.
     *
     * @param holder To bind data to.
     * @param position The position of the user.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.text1.setText(user.getName());

        if (geolocation) {
            String tag = "GeolocationTask_" + position;
            holder.text2.setTag(tag); // Set a tag for this ViewHolder

            getAddressFromLocation(user.getLatitude(), user.getLongitude(), address -> {
                if (tag.equals(holder.text2.getTag())) { // Ensure this task matches the ViewHolder
                    String locationInfo = address != null ? address : "Location not available";
                    new Handler(Looper.getMainLooper()).post(() -> holder.text2.setText(
                            "Email: " + user.getEmail() + " | Phone: " + user.getPhone() + " | Location: " + locationInfo
                    ));
                }
            });
        } else {
            holder.text2.setText("Email: " + user.getEmail() + " | Phone: " + user.getPhone());
        }
    }

    /**
     * Returns the total number of users in the list.
     *
     * @return The number of users.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Cleans up the executor when the RecyclerView is detached.
     *
     * @param recyclerView The RecyclerView that was detached.
     */
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        executor.shutdown(); // Clean up executor when RecyclerView is detached
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }

    /**
     * Retrieves the address from a user's geolocation coordinates.
     *
     * @param latitude The latitude of the user's location.
     * @param longitude The longitude of the user's location.
     * @param listener To handle the retrieved address.
     */
    private void getAddressFromLocation(double latitude, double longitude, OnAddressRetrievedListener listener) {
        executor.submit(() -> {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        listener.onAddressRetrieved(addresses.get(0).getAddressLine(0));
                    } else {
                        Log.e("Geocoder", "No address found for the location.");
                        listener.onAddressRetrieved(null);
                    }
                } else {
                    Log.e("Geocoder", "Invalid latitude/longitude: " + latitude + ", " + longitude);
                    listener.onAddressRetrieved(null);
                }
            } catch (IOException e) {
                Log.e("Geocoder", "Error getting address from location", e);
                listener.onAddressRetrieved(null);
            }
        });
    }

    // Listener interface for address retrieval callback
    interface OnAddressRetrievedListener {
        void onAddressRetrieved(String address);
    }
}
