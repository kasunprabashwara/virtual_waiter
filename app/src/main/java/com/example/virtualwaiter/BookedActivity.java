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
    public String name;
    public Integer tableID;
    public String date;
    public String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);
        Intent intent= getIntent();
        tableID = intent.getIntExtra("tableID", 1);
        date = intent.getStringExtra("dateTime");
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        EditText email = findViewById(R.id.bookedEmailAddress);
        EditText password = findViewById(R.id.bookedPassword);
        Button login = findViewById(R.id.bookedLoginButton);
        TextView bookedName= findViewById(R.id.bookedName);
        bookedName.setText("For Mr/Mrs "+name);

        login.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if(emailText.equals(this.email)){
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(BookedActivity.this, "Wellcome Mr/Mrs "+name+"!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(BookedActivity.this, MainActivity.class);
                                startActivity(intent1);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(BookedActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            else{
                Toast.makeText(BookedActivity.this, "Wrong email address",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}