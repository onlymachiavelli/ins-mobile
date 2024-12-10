package com.example.insurrane;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable network operation on the main thread (not recommended for production apps)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Find the "Get Started" button by its ID
        Button getStartedButton = findViewById(R.id.getStartedButton);

        // Set the OnClickListener for the button
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a Toast message when the button is clicked
                Toast.makeText(MainActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();

                // Check the backend health status
                if (checkBackendHealth()) {
                    // If the status is 200, navigate to LoginPage
                    Intent intent = new Intent(MainActivity.this, LoginPage.class);
                    startActivity(intent);
                } else {
                    // If the status is not 200, show an error
                    Toast.makeText(MainActivity.this, "Backend is down. Try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Method to check the backend health status
    private boolean checkBackendHealth() {
        String apiUrl = "http://192.168.1.145:8080/health";
        try {
            // Create a URL object
            URL url = new URL(apiUrl);

            // Open a connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");  // HTTP method GET

            

            // Get the response code
            int responseCode = urlConnection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // If the response code is 200, return true, else false
            //toast 
            Toast.makeText(this,
                    "Response Code: " + responseCode
                    , Toast.LENGTH_SHORT).show();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}
