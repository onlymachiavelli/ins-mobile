package com.example.insurrane;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        Button signUpButton = findViewById(R.id.signUpButton);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a Toast message when the button is clicked
                Toast.makeText(LoginPage.this, "Button Clicked", Toast.LENGTH_SHORT).show();

                // Intent to navigate to the Signup activity
                Intent intent = new Intent(LoginPage.this, signup.class);
                startActivity(intent);
            }
        });
    }
}
