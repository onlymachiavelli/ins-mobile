package com.example.insurrane;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class dashboard extends AppCompatActivity {

    private TextView acceptedTextView, pendingTextView, rejectedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize TextViews
        acceptedTextView = findViewById(R.id.accepted);
        pendingTextView = findViewById(R.id.pending);
        rejectedTextView = findViewById(R.id.rejected);

        // Call the API
        fetchStats();


        Button req = findViewById(R.id.requestContractButton);

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Signup activity
                Intent intent = new Intent(dashboard.this, requestlifeContract.class); // Update with your signup activity class
                startActivity(intent);
            }
        });
    }

    private void fetchStats() {
        String apiUrl = "http://192.168.1.145:8080/contract/life/stats"; // Replace with your actual API URL

        new Thread(() -> {
            try {
                // Get token from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", "");

                if (token.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(dashboard.this, "Token not found. Please log in.", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Create the URL object for the stats endpoint
                URL url = new URL(apiUrl);

                // Open a connection to the URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET"); // Set HTTP method to GET
                urlConnection.setRequestProperty("Authorization", "Bearer " + token); // Set the Authorization header
                urlConnection.setRequestProperty("Content-Type", "application/json");

                // Get the response code from the server
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Parse the response
                    String response = new String(urlConnection.getInputStream().readAllBytes());
                    JSONObject jsonResponse = new JSONObject(response);

                    // Extract stats
                    int accepted = jsonResponse.getInt("accepted");
                    int pending = jsonResponse.getInt("pending");
                    int rejected = jsonResponse.getInt("rejected");

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        acceptedTextView.setText(String.valueOf(accepted));
                        pendingTextView.setText(String.valueOf(pending));
                        rejectedTextView.setText(String.valueOf(rejected));
                    });
                } else {
                    Log.e("API_ERROR", "Failed to fetch stats. Response code: " + responseCode);
                    runOnUiThread(() -> Toast.makeText(dashboard.this, "Failed to fetch stats.", Toast.LENGTH_SHORT).show());
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(dashboard.this, "An error occurred.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
