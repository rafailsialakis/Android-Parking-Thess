package com.example.parkpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class wallet_activity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_activity);
        UserSession session = (UserSession) getIntent().getSerializableExtra("session");
        SectorList sectorList = getIntent().getParcelableExtra("sector");
        TextView euros = findViewById(R.id.EuroLabel);
        euros.setText(String.valueOf(session.getEuros()));
        TextView cents = findViewById(R.id.CentsLabel);
        cents.setText(String.valueOf(session.getCents()));
        findViewById(R.id.CarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(wallet_activity.this, MainActivity.class);
                i.putExtra("session", session);
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
                Intent i;
                i = new Intent(wallet_activity.this, MapActivity.class);
                i.putExtra("session", session);
                i.putExtra("sector", sectorList);
                startActivity(i);
            }
        });
        findViewById(R.id.CarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(wallet_activity.this, MainActivity.class);
                i.putExtra("session", session);
                i.putExtra("sector", sectorList);
                startActivity(i);
            }
        });
    }

}
