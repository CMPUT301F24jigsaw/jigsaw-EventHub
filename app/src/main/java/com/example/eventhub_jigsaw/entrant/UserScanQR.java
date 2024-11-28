package com.example.eventhub_jigsaw.entrant;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventhub_jigsaw.EventDetailsActivity;
import com.example.eventhub_jigsaw.CaptureAct;
import com.example.eventhub_jigsaw.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class UserScanQR extends Fragment {

    private Button btnScan;

    private final ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            // Handle scanned QR code
            String scannedData = result.getContents();

            // Check if scanned data is a valid deep link
            if (scannedData.startsWith("https://yourapp.example.com/event/")) {
                // Extract event ID from the URL (last segment of the path)
                Uri uri = Uri.parse(scannedData);
                String eventId = uri.getLastPathSegment();

                // Open EventDetailsDialogFragment
                EventDetailsActivity dialogFragment = new EventDetailsActivity();
                Bundle args = new Bundle();
                args.putString("event_id", eventId); // Pass the event ID to the dialog fragment
                dialogFragment.setArguments(args);
                dialogFragment.show(getChildFragmentManager(), "EventDetailsDialog");
            } else {
                // Show dialog for invalid QR code
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Invalid QR Code");
                builder.setMessage("The scanned QR code is not a valid event link.");
                builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_scan_qr, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the scan button
        btnScan = view.findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(v -> scanCode());
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
}
