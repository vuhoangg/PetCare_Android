package com.example.petcase;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.petcase.Domain.HealthRecord;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AddHealthRecord extends AppCompatActivity {

    private EditText etDate, etDescription, etVaccine;
    private TextView tvPetName;  // TextView to display the pet's name
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_health_record);

        // Get the petId from the Intent
        petId = getIntent().getStringExtra("petId");

        // Initialize the fields
        tvPetName = findViewById(R.id.tvPetName);  // TextView for pet name
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        etVaccine = findViewById(R.id.etVaccine);

        // Retrieve pet name from Firebase using the petId
        DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("Pet").child(petId);
        petRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String petName = dataSnapshot.child("name").getValue(String.class);  // Assuming the name field exists
                    if (petName != null) {
                        tvPetName.setText("Pet Name: " + petName);  // Display the pet's name
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddHealthRecord.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Save the health record to Firebase when the Save button is clicked
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String date = etDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String vaccine = etVaccine.getText().toString().trim();

            if (date.isEmpty() || description.isEmpty() || vaccine.isEmpty()) {
                Toast.makeText(AddHealthRecord.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Automatically generate HealthRecordId using Firebase pushId
                String healthRecordId = FirebaseDatabase.getInstance().getReference("HealthRecord").push().getKey();

                // Create HealthRecord object
                HealthRecord healthRecord = new HealthRecord(healthRecordId, petId, date, description, vaccine);

                // Upload the health record data to Firebase
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HealthRecord");
                if (healthRecordId != null) {
                    databaseReference.child(healthRecordId).setValue(healthRecord)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddHealthRecord.this, "Health record added successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddHealthRecord.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }
}
