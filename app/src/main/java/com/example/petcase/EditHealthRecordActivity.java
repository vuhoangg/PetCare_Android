package com.example.petcase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcase.Domain.HealthRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditHealthRecordActivity extends AppCompatActivity {
    private EditText etDate, etDescription, etVaccine;
    private TextView tvPetName;  // TextView to display the pet's name
    private String petId, healthRecordId ;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_health_record);

        // Get the petId from the Intent
        petId = getIntent().getStringExtra("petId");

        // Initialize the fields
        btnUpdate = findViewById(R.id.btnUpdate); //
        tvPetName = findViewById(R.id.tvPetName);  // TextView for pet name
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        etVaccine = findViewById(R.id.etVaccine);


        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        // truyền các sữ liệu vừa lấy vào các EditText Tương ứng
        healthRecordId = intent.getStringExtra("HEALTHRECORD_ID");
        etDate.setText(intent.getStringExtra("HEALTHRECORD_DATE"));
        etDescription.setText(intent.getStringExtra("HEALTHRECORD_DESCRIPTION"));
        etVaccine.setText(intent.getStringExtra("HEALTHRECORD_VACCINE"));

        // Get the petId from the Intent
        petId = getIntent().getStringExtra("petId");

        // Retrieve pet name from Firebase using the petId
        DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("Pet").child(petId);
        petRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String petName = dataSnapshot.child("name").getValue(String.class);  // Assuming the name field exists
                    if (petName != null) {
                        tvPetName.setText("Pet Name: " + petName);
                        // Display the pet's name
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditHealthRecordActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Save the health record to Firebase when the Save button is clicked
        btnUpdate.setOnClickListener(v -> {
            String date = etDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String vaccine = etVaccine.getText().toString().trim();

            if (date.isEmpty() || description.isEmpty() || vaccine.isEmpty()) {
                Toast.makeText(EditHealthRecordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {


                // Create HealthRecord object
                HealthRecord healthRecord = new HealthRecord(healthRecordId, petId, date, description, vaccine);

                // Upload the health record data to Firebase
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HealthRecord");
                if (healthRecordId != null) {
                    databaseReference.child(healthRecordId).setValue(healthRecord)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(EditHealthRecordActivity.this, "Health record added successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EditHealthRecordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });




    }
}