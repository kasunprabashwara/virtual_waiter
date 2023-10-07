package com.example.virtualwaiter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

public class BookedActivity extends AppCompatActivity {
    private String name;
    private Integer tableID;
    private String date;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);
        Intent intent= getIntent();
        tableID = intent.getIntExtra("tableID", 1);
        date = intent.getStringExtra("dateTime");
        name = intent.getStringExtra("name");
        key = intent.getStringExtra("key");
        EditText keyEditText = findViewById(R.id.bookingKey);
        Button login = findViewById(R.id.bookedLoginButton);
        TextView bookedName= findViewById(R.id.bookedName);
        bookedName.setText("For Mr/Mrs "+name);

        login.setOnClickListener(v -> {
            String enteredKey = keyEditText.getText().toString();
            if (enteredKey.equals(this.key)) {
                Toast.makeText(BookedActivity.this, "Welcome Mr/Mrs "+name+"!",
                        Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(BookedActivity.this, MainActivity.class);
                startActivity(intent1);
            } else {
                Toast.makeText(BookedActivity.this, "Wrong Key.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}