package com.example.insurrane;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginPage extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize the views
        emailEditText = findViewById(R.id.emailInput);
        passwordEditText = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        // SignUp button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Signup activity
                Intent intent = new Intent(LoginPage.this, signup.class); // Update with your signup activity class
                startActivity(intent);
            }
        });

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    // Call the method to send login request
                    if (sendLoginRequest(email, password)) {
                        // Retrieve the token from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        String token = sharedPreferences.getString("token", "");

                        // Pass the token to the next activity
                        Intent intent = new Intent(LoginPage.this, dashboard.class); // Update with your next activity class
                        intent.putExtra("token", token);
                        startActivity(intent);

                        Toast.makeText(LoginPage.this, "Login successful.", Toast.LENGTH_LONG).show();
                    } else {
                        // Show error message
                        Toast.makeText(LoginPage.this, "Login failed. Try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Show error message if fields are empty
                    Toast.makeText(LoginPage.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to send login POST request
    private boolean sendLoginRequest(String email, String password) {
        String apiUrl = "http://192.168.1.145:8080/client/login"; // Replace with your backend URL

        try {
            // Create the URL object for the login endpoint
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST"); // Set HTTP method to POST
            urlConnection.setRequestProperty("Content-Type", "application/json"); // Set content type to JSON
            urlConnection.setDoOutput(true); // Allow writing to the output stream

            // Create the JSON payload
            String jsonPayload = "{"
                    + "\"email\":\"" + email + "\","
                    + "\"password\":\"" + password + "\""
                    + "}";

            // Write the JSON payload to the output stream
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(jsonPayload);
            outputStream.flush();
            outputStream.close();

            // Get the response code from the server
            int responseCode = urlConnection.getResponseCode();

            // If the response code is HTTP_OK (200), handle the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Parse the response
                String response = new String(urlConnection.getInputStream().readAllBytes());
                JSONObject jsonResponse = new JSONObject(response);

                // Extract token and user data from the response
                String token = jsonResponse.getString("token");
                JSONObject user = jsonResponse.getJSONObject("user");

                // Store token and user data in SharedPreferences
                saveUserData(token, user);

                return true; // Login successful
            } else {
                return false; // Login failed
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to save the token and user data in SharedPreferences
    private void saveUserData(String token, JSONObject user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store the token and user data
        editor.putString("token", token);
        editor.putString("email", user.optString("email"));
        editor.putString("first_name", user.optString("first_name"));
        editor.putString("last_name", user.optString("last_name"));
        editor.putString("phone", user.optString("phone"));
        editor.putInt("user_id", user.optInt("id"));
        editor.putBoolean("verified", user.optBoolean("verified"));

        // Commit the changes
        editor.apply();
    }
}
