package com.example.parkpal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {
    long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_activity);

        UserSession session = (UserSession) getIntent().getSerializableExtra("session");
        SectorList sectorList = getIntent().getParcelableExtra("sector");

        ArrayList<String> logs = session.getLogsList();

        TextView logsTextView = findViewById(R.id.logsTextView);

        if (logs != null && !logs.isEmpty()) {
            StringBuilder logText = new StringBuilder();

            for (String log : logs) {
                logText.append(log).append("\n");
            }
            logsTextView.setText(logText.toString());
        } else {
            logsTextView.setText("No logs available.");
        }
        TextView HourLabel = findViewById(R.id.hourLabel);
        TextView MinuteLabel = findViewById(R.id.MinuteLabel);
        HourLabel.setText(String.valueOf(session.getTotalParkingHours()));
        MinuteLabel.setText(String.valueOf(session.getTotalParkingMinutes()));
        findViewById(R.id.LogoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession.isLoggedIn = false;
                Intent i = new Intent(LogActivity.this, LoginActivity.class);
                i.putExtra("sector", sectorList);
                startActivity(i);
            }
        });
        findViewById(R.id.CarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivity(LogActivity.this, MainActivity.class, session, sectorList);
            }
        });
        findViewById(R.id.MapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivity(LogActivity.this, MapActivity.class, session, sectorList);
            }
        });
        findViewById(R.id.WalletButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivity(LogActivity.this, WalletActivity.class, session, sectorList);
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
    private void LaunchActivity(Context packageContext, Class<?> cls, UserSession session, SectorList sectorList){
        Intent i = new Intent(packageContext, cls);
        i.putExtra("session", session);
        i.putExtra("sector", sectorList);
        startActivity(i);
    }
}
