package com.example.insurrane;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class requestlifeContract extends AppCompatActivity {

    private EditText policyTypeInput, faceAmountInput, premiumModeInput, premiumAmountInput,
            policyTermInput, beneficiaryNameInput, beneficiaryRelationshipInput,
            contingentBeneficiaryNameInput, contingentBeneficiaryRelationshipInput,
            effectiveDateInput, expirationDateInput;

    private Button submitContractButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestlife_contract);

        // Initialize inputs
        policyTypeInput = findViewById(R.id.policyTypeInput);
        faceAmountInput = findViewById(R.id.faceAmountInput);
        premiumModeInput = findViewById(R.id.premiumModeInput);
        premiumAmountInput = findViewById(R.id.premiumAmountInput);
        policyTermInput = findViewById(R.id.policyTermInput);
        beneficiaryNameInput = findViewById(R.id.beneficiaryNameInput);
        beneficiaryRelationshipInput = findViewById(R.id.beneficiaryRelationshipInput);
        contingentBeneficiaryNameInput = findViewById(R.id.contingentBeneficiaryNameInput);
        contingentBeneficiaryRelationshipInput = findViewById(R.id.contingentBeneficiaryRelationshipInput);
        effectiveDateInput = findViewById(R.id.effectiveDateInput);
        expirationDateInput = findViewById(R.id.expirationDateInput);

        submitContractButton = findViewById(R.id.submitPolicyButton);

        // Set button listener
        submitContractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect input values
                String policyType = policyTypeInput.getText().toString();
                int faceAmount = Integer.parseInt(faceAmountInput.getText().toString());
                String premiumMode = premiumModeInput.getText().toString();
                double premiumAmount = Double.parseDouble(premiumAmountInput.getText().toString());
                int policyTerm = Integer.parseInt(policyTermInput.getText().toString());
                String beneficiaryName = beneficiaryNameInput.getText().toString();
                String beneficiaryRelationship = beneficiaryRelationshipInput.getText().toString();
                String contingentBeneficiaryName = contingentBeneficiaryNameInput.getText().toString();
                String contingentBeneficiaryRelationship = contingentBeneficiaryRelationshipInput.getText().toString();
                String effectiveDate = effectiveDateInput.getText().toString();
                String expirationDate = expirationDateInput.getText().toString();

                // Send the life contract POST request
                if (sendLifeContractRequest(policyType, faceAmount, premiumMode, premiumAmount, policyTerm,
                        beneficiaryName, beneficiaryRelationship, contingentBeneficiaryName,
                        contingentBeneficiaryRelationship, effectiveDate, expirationDate)) {
                    Toast.makeText(requestlifeContract.this, "Contract submitted successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requestlifeContract.this, "Submission failed. Try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean sendLifeContractRequest(String policyType, int faceAmount, String premiumMode,
                                            double premiumAmount, int policyTerm, String beneficiaryName,
                                            String beneficiaryRelationship, String contingentBeneficiaryName,
                                            String contingentBeneficiaryRelationship, String effectiveDate,
                                            String expirationDate) {
        String apiUrl = "http://192.168.1.145:8080/contract/life"; // Replace with your backend URL

        try {
            // Retrieve the token from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");

            // Create the URL object for the life contract endpoint
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST"); // Set HTTP method to POST
            urlConnection.setRequestProperty("Content-Type", "application/json"); // Set content type to JSON
            urlConnection.setRequestProperty("Authorization", "Bearer " + token); // Set the Authorization header
            urlConnection.setDoOutput(true); // Allow writing to the output stream

            // Create the JSON payload
            String jsonPayload = "{"
                    + "\"policy_type\":\"" + policyType + "\","
                    + "\"face_amount\":" + faceAmount + ","
                    + "\"premium_mode\":\"" + premiumMode + "\","
                    + "\"premium_amount\":" + premiumAmount + ","
                    + "\"policy_term\":" + policyTerm + ","
                    + "\"benificiary_name\":\"" + beneficiaryName + "\","
                    + "\"benificiary_relationship\":\"" + beneficiaryRelationship + "\","
                    + "\"contingent_benificiary_name\":\"" + contingentBeneficiaryName + "\","
                    + "\"contingent_benificiary_relationship\":\"" + contingentBeneficiaryRelationship + "\","
                    + "\"effective_date\":\"" + effectiveDate + "\","
                    + "\"expiration_date\":\"" + expirationDate + "\""
                    + "}";

            // Write the JSON payload to the output stream
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(jsonPayload);
            outputStream.flush();
            outputStream.close();

            // Get the response code from the server
            int responseCode = urlConnection.getResponseCode();

            // If the response code is HTTP_OK (200), return success
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false; // Failed response
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
