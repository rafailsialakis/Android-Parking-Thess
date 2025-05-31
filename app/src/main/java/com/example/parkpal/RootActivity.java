package com.example.parkpal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RootActivity extends AppCompatActivity {
    long backPressedTime;
    private SectorList sectorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_activity);

        // Retrieve SectorList from Intent and initialize DB helper
        sectorList = getIntent().getParcelableExtra("sector");
        if (sectorList == null) {
            sectorList = new SectorList(this);
        } else {
            sectorList.initDbHelper(this);
        }

        findViewById(R.id.LogoutButton).setOnClickListener(view -> {
            UserSession.isLoggedIn = false;
            Intent i = new Intent(RootActivity.this, LoginActivity.class);
            i.putExtra("sector", sectorList);
            startActivity(i);
        });

        findViewById(R.id.AddSectorButton).setOnClickListener(view -> {
            TextView codeView = findViewById(R.id.CodeField);
            TextView totalView = findViewById(R.id.TotalField);
            TextView availableView = findViewById(R.id.AvailableField);

            String code = codeView.getText().toString().trim();
            String total = totalView.getText().toString().trim();
            String available = availableView.getText().toString().trim();

            if (code.isEmpty() || total.isEmpty() || available.isEmpty()) {
                Toast.makeText(RootActivity.this, "Please insert all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sectorList.isInList(code) != null) {
                Toast.makeText(RootActivity.this, "This sector is already registered.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int totalInt = Integer.parseInt(total);
                int availableInt = Integer.parseInt(available);

                // This now writes to SQLite and updates the in-memory list:
                sectorList.AddSector(code, totalInt, availableInt);
                Toast.makeText(RootActivity.this, "Sector added successfully.", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(RootActivity.this, "Total and Available must be valid numbers.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.deleteSectorButton).setOnClickListener(view -> {
            TextView deleteField = findViewById(R.id.DeleteField);
            String toDelete = deleteField.getText().toString().trim();
            if (toDelete.isEmpty()) {
                Toast.makeText(RootActivity.this, "Please enter a sector code to delete.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean deletedFlag = sectorList.removeSector(toDelete);
            if (deletedFlag) {
                Toast.makeText(RootActivity.this, "Sector " + toDelete + " was deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RootActivity.this, "Error. Cannot delete a sector that doesn't exist.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.CostChangeButton).setOnClickListener(view -> {
            TextView costView = findViewById(R.id.CostField);
            String cost = costView.getText().toString().trim();
            try {
                double numericalCost = Double.parseDouble(cost);
                sectorList.setParkingCost(numericalCost);
                Toast.makeText(RootActivity.this,
                        "Cost changed successfully to " + numericalCost + " euros/min.",
                        Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(RootActivity.this, "Please enter a valid cost.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
