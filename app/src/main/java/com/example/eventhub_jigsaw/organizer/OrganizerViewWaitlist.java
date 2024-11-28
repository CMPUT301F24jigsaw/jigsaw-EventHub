package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.User;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class OrganizerViewWaitlist extends DialogFragment {

    private RecyclerView recyclerView;
    private WaitlistAdapter adapter;
    private List<User> userList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_view_waitlist, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewWaitlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WaitlistAdapter(userList);
        recyclerView.setAdapter(adapter);

        fetchWaitlistUsers();

        return view;
    }

    private void fetchWaitlistUsers() {
        String eventId = "nANFHl5f3GhUuhFsY8JM"; // Replace with the actual event ID

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the event's waiting list
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> waitingList = (List<String>) documentSnapshot.get("waitingList");

                if (waitingList != null) {
                    for (String userId : waitingList) {
                        // Fetch user details for each user in the waiting list
                        db.collection("users").document(userId).get().addOnSuccessListener(userSnapshot -> {
                            if (userSnapshot.exists()) {
                                User user = new User(
                                        userSnapshot.getString("name"),
                                        userSnapshot.getString("email"),
                                        userSnapshot.getString("userID"),
                                        userSnapshot.getLong("phone").intValue(),
                                        User.Role.valueOf(userSnapshot.getString("role"))
                                );

                                userList.add(user);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }
}
