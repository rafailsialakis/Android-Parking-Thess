package com.example.parkpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    SectorList list = new SectorList();
    UserSession session;
    long backPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        list = getIntent().getParcelableExtra("sector");
        if (list == null) {
            list = new SectorList();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            int todayDay = today.getDayOfMonth();
            String todayMonth = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            int tomorrowDay = tomorrow.getDayOfMonth();
            String tomorrowMonth = tomorrow.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            int todayYear = today.getYear();
            int tomorrowYear = tomorrow.getYear();

            TextView todayView = findViewById(R.id.TodayView);
            TextView tomorrowView = findViewById(R.id.TomorrowView);
            String formattedTodayDay = (todayDay < 10) ? "0" + todayDay : String.valueOf(todayDay);
            todayView.setText(formattedTodayDay);
            String formattedTomorrowDay = (tomorrowDay < 10) ? "0" + tomorrowDay : String.valueOf(tomorrowDay);
            tomorrowView.setText(formattedTomorrowDay);

            String formattedTodayMonthYear = todayMonth + " " + todayYear;
            String formattedTomorrowMonthYear = tomorrowMonth + " " + tomorrowYear;
            TextView MonthFirstView = findViewById(R.id.MonthFirstView);
            TextView MonthSecondView = findViewById(R.id.MonthSecondView);
            MonthFirstView.setText(formattedTodayMonthYear);
            MonthSecondView.setText(formattedTomorrowMonthYear);

            TextView TodayField = findViewById(R.id.TodayFIeld);
            TextView TomorrowField = findViewById(R.id.TomorrowField);
            String formattedTodayField = "Today · " + todayMonth;
            String formattedTomorrowField = "Tomorrow · " + tomorrowMonth;
            TodayField.setText(formattedTodayField);
            TomorrowField.setText(formattedTomorrowField);

            TextView todayPriceView = findViewById(R.id.FirstDayPriceView);
            TextView tomorrowPriceView = findViewById(R.id.SecondDayPriceView);
            if (today.getDayOfWeek().getValue() == 7)
                todayPriceView.setText("Free");
            else
                todayPriceView.setText("08:00 - 21:00");

            if (tomorrow.getDayOfWeek().getValue() == 7)
                tomorrowPriceView.setText("Free");
            else
                tomorrowPriceView.setText("08:00 - 21:00");
        }

        UserSession session = (UserSession) getIntent().getSerializableExtra("session");
        Button registerButton = findViewById(R.id.toRegisterButton);

        if (!UserSession.isLoggedIn) {
            findViewById(R.id.LogoutButton).setVisibility(View.INVISIBLE);
            registerButton.setText("Create an account:");
        } else {
            findViewById(R.id.LogoutButton).setVisibility(View.VISIBLE);
            registerButton.setText("Welcome back " + session.getUsername());
        }

        // Declare a single Intent variable here
        final Intent[] i = new Intent[1];

        findViewById(R.id.WalletButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserSession.isLoggedIn) {
                    Toast.makeText(getApplicationContext(), "You must be logged in to access your wallet.", Toast.LENGTH_LONG).show();
                    i[0] = new Intent(MainActivity.this, LoginActivity.class);
                    i[0].putExtra("sector", list);
                    startActivityForResult(i[0], 1);
                } else {
                    i[0] = new Intent(MainActivity.this, WalletActivity.class);
                    i[0].putExtra("session", session);
                    i[0].putExtra("sector", list);
                    startActivity(i[0]);
                }
            }
        });

        findViewById(R.id.MapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserSession.isLoggedIn) {
                    Toast.makeText(getApplicationContext(), "You must be logged in to access maps.", Toast.LENGTH_LONG).show();
                    i[0] = new Intent(MainActivity.this, LoginActivity.class);
                    i[0].putExtra("sector", list);
                    startActivityForResult(i[0], 1);
                } else {
                    i[0] = new Intent(MainActivity.this, MapActivity.class);
                    i[0].putExtra("session", session);
                    i[0].putExtra("sector", list);
                    startActivity(i[0]);
                }
            }
        });

        findViewById(R.id.newParkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserSession.isLoggedIn) {
                    Toast.makeText(getApplicationContext(), "You must be logged in to acquire a parking spot.", Toast.LENGTH_LONG).show();
                    i[0] = new Intent(MainActivity.this, LoginActivity.class);
                    i[0].putExtra("sector", list);
                    startActivityForResult(i[0], 1);
                } else {
                    if (session.getEuros() + session.getCents() == 0) {
                        Toast.makeText(getApplicationContext(), "In order to park, you have to deposit money.", Toast.LENGTH_LONG).show();
                        i[0] = new Intent(MainActivity.this, WalletActivity.class);
                        i[0].putExtra("session", session);
                        i[0].putExtra("sector", list);
                        startActivity(i[0]);
                    } else {
                        i[0] = new Intent(MainActivity.this, ParkActivity.class);
                        i[0].putExtra("session", session);
                        i[0].putExtra("sector", list);
                        startActivity(i[0]);
                    }
                }
            }
        });

        findViewById(R.id.LogoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession.isLoggedIn = false;
                i[0] = new Intent(MainActivity.this, LoginActivity.class);
                i[0].putExtra("sector", list);
                startActivity(i[0]);
            }
        });

        findViewById(R.id.HistoryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserSession.isLoggedIn) {
                    Toast.makeText(getApplicationContext(), "You must be logged in to check history.", Toast.LENGTH_LONG).show();
                    i[0] = new Intent(MainActivity.this, LoginActivity.class);
                    i[0].putExtra("sector", list);
                    startActivityForResult(i[0], 1);
                } else {
                    i[0] = new Intent(MainActivity.this, LogActivity.class);
                    i[0].putExtra("session", session);
                    i[0].putExtra("sector", list);
                    startActivity(i[0]);
                }
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

    // Handle result from LoginActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            session = (UserSession) data.getSerializableExtra("session");
            list = getIntent().getParcelableExtra("sector");
        }
    }

}
