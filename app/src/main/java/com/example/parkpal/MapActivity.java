package com.example.parkpal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {
    long backPressedTime;
    private SectorList sectorList;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        session = (UserSession) getIntent().getSerializableExtra("session");
        sectorList = getIntent().getParcelableExtra("sector");

        Spinner sectorSpinner = findViewById(R.id.SectorSpinner);

        // Load sector codes into Spinner
        String[] sectorCodes = sectorList.getSectorCodes(); // Implement this method in SectorList
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sectorCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectorSpinner.setAdapter(adapter);

        sectorSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            boolean firstSelect = true;

            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (firstSelect) {
                    firstSelect = false; // Skip first call triggered on load
                    return;
                }
                String selectedCode = parent.getItemAtPosition(position).toString();
                Sector selectedSector = sectorList.isInList(selectedCode);

                if (selectedSector != null) {
                    Toast.makeText(MapActivity.this, "Sector " + selectedCode + " has " + selectedSector.getAvailable_spots() + " spots.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, "This sector doesn't exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Logout button
        View logoutBtn = findViewById(R.id.LogoutButton);
        logoutBtn.setVisibility(UserSession.isLoggedIn ? View.VISIBLE : View.INVISIBLE);
        logoutBtn.setOnClickListener(v -> {
            UserSession.isLoggedIn = false;
            Intent i = new Intent(MapActivity.this, LoginActivity.class);
            i.putExtra("sector", sectorList);
            startActivity(i);
        });

        findViewById(R.id.CarButton).setOnClickListener(v -> LaunchActivity(MapActivity.this, MainActivity.class, session, sectorList));
        findViewById(R.id.WalletButton).setOnClickListener(v -> LaunchActivity(MapActivity.this, WalletActivity.class, session, sectorList));
        findViewById(R.id.HistoryButton).setOnClickListener(v -> LaunchActivity(MapActivity.this, LogActivity.class, session, sectorList));
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

    private void LaunchActivity(Context packageContext, Class<?> cls, UserSession session, SectorList sectorList) {
        Intent i = new Intent(packageContext, cls);
        i.putExtra("session", session);
        i.putExtra("sector", sectorList);
        startActivity(i);
    }
}
