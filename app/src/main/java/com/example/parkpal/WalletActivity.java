package com.example.parkpal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {
    long backPressedTime;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_activity);
        UserSession session = (UserSession) getIntent().getSerializableExtra("session");
        SectorList sectorList = getIntent().getParcelableExtra("sector");
        TextView euros = findViewById(R.id.EuroLabel);
        euros.setText(String.valueOf(session.getEuros()));
        TextView cents = findViewById(R.id.CentsLabel);
        int Numcents = session.getCents();
        cents.setText(String.format("%02d", Numcents));
        findViewById(R.id.CarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WalletActivity.this, MainActivity.class);
                i.putExtra("session", session);
                i.putExtra("sector", sectorList);
                startActivity(i);
            }
        });
        findViewById(R.id.LogoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession.isLoggedIn = false;
                Intent i = new Intent(WalletActivity.this, LoginActivity.class);
                i.putExtra("sector", sectorList);
                startActivity(i);
            }
        });
        findViewById(R.id.tendeposit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView euros = findViewById(R.id.EuroLabel);
                int balance = session.getEuros();
                balance += 10;
                session.setEuros(balance);
                euros.setText(String.valueOf(balance));
            }
        });
        findViewById(R.id.twentydeposit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView euros = findViewById(R.id.EuroLabel);
                int balance = session.getEuros();
                balance += 20;
                session.setEuros(balance);
                euros.setText(String.valueOf(balance));
            }
        });
        findViewById(R.id.fiftydeposit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView euros = findViewById(R.id.EuroLabel);
                int balance = session.getEuros();
                balance += 50;
                session.setEuros(balance);
                euros.setText(String.valueOf(balance));
            }
        });
        findViewById(R.id.MapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivity(WalletActivity.this, MapActivity.class, session, sectorList);
            }
        });
        findViewById(R.id.CarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivity(WalletActivity.this, MainActivity.class, session, sectorList);
            }
        });
        findViewById(R.id.HistoryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivity(WalletActivity.this, LogActivity.class, session, sectorList);
            }
        });
    }
    private void LaunchActivity(Context packageContext, Class<?> cls, UserSession session, SectorList sectorList){
        Intent i = new Intent(packageContext, cls);
        i.putExtra("session", session);
        i.putExtra("sector", sectorList);
        startActivity(i);
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
