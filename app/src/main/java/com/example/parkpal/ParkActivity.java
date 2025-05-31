package com.example.parkpal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ParkActivity extends AppCompatActivity {
    long backPressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_activity);
        UserSession session = (UserSession) getIntent().getSerializableExtra("session");
        SectorList sectorList = getIntent().getParcelableExtra("sector");
        TextView cost = findViewById(R.id.CostOfService);
        TextView balance = findViewById(R.id.BalanceLeft);
        String formattedBalance = String.format("%d.%02d", session.getEuros(), session.getCents()) + "€";
        float accountbalance = session.getEuros() + (session.getCents())/100.0f;
        Button parkButton = findViewById(R.id.StartPark);
        balance.setText(formattedBalance);
        cost.setText("0.00€"); // or String.format("%.2f", 0.0)
        TimePicker picker = findViewById(R.id.timePicker1);
        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                LocalTime selectedTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    selectedTime = LocalTime.of(hour, minute);
                    LocalTime now = LocalTime.now();
                    long minutesDiff = Duration.between(now, selectedTime).toMinutes();
                    if (minutesDiff < 0) {
                        minutesDiff += 24 * 60;
                    }

                    int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
                    double costpermin;
                    if(dayOfWeek == 7)
                        costpermin = 0; //Sunday is for free.
                    else
                        costpermin = sectorList.getParkingCost();
                    double costToPark = minutesDiff * costpermin;
                    cost.setText(String.format("%.2f€", costToPark));
                    if(costToPark < accountbalance){
                        double remaining = accountbalance - costToPark;
                        balance.setText(String.format("%.2f€", remaining));
                        int newEuros = (int) remaining;
                        int newCents = (int) Math.round((remaining - newEuros) * 100);
                        session.setEuros(newEuros);
                        session.setCents(newCents);
                        parkButton.setEnabled(true);
                        balance.setTextColor(Color.BLACK);
                    }else {
                        double remaining = accountbalance - costToPark;
                        balance.setText(String.format("%.2f€", remaining));
                        TextView BalanceLeft = findViewById(R.id.BalanceLeft);
                        BalanceLeft.setTextColor(Color.RED);
                        parkButton.setEnabled(false);
                    }
                }
            }
        });

        parkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedHour, selectedMinute;
                long hours = 0;
                long minutes = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    selectedHour = picker.getHour();
                    selectedMinute = picker.getMinute();


                // Convert to LocalTime
                    LocalTime selectedTime = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        selectedTime = LocalTime.of(selectedHour, selectedMinute);
                    }
                    LocalTime currentTime = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        currentTime = LocalTime.now();
                    }
                    String currentTimeStr = "";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        currentTimeStr = currentTime.format(formatter);  // Format current time
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Duration duration = Duration.between(currentTime, selectedTime);
                        if (duration.isNegative()) {
                            duration = duration.plusHours(24);
                        }

                        long totalMinutes = duration.toMinutes();
                        hours = totalMinutes / 60;
                        minutes = totalMinutes % 60;
                    }

                    String selectedTimeStr = selectedTime.toString();
                    String current = String.valueOf(selectedHour);
                    TextView LicenceField = findViewById(R.id.LicencePlateField);
                    TextView SectorField = findViewById(R.id.SectorField);
                    String Licence = LicenceField.getText().toString();
                    String sectorcode = SectorField.getText().toString();
                    if(Licence.equals("") || (sectorcode.equals(""))){
                        Toast.makeText(getApplicationContext(), "Please enter both license plate and sector.", Toast.LENGTH_SHORT).show();
                    }else {
                        Sector sector = sectorList.isInList(sectorcode);
                        if(sector == null){
                            Toast.makeText(ParkActivity.this, "This sector doesn't exist.", Toast.LENGTH_SHORT).show();
                        }else{
                            if(sector.getAvailable_spots() == 0){
                                Toast.makeText(ParkActivity.this, "No parking spots available. Change sector.", Toast.LENGTH_SHORT).show();
                            }else{
                                double remainingBalance = session.getEuros() + session.getCents() / 100.0;
                                int newEuros = (int) remainingBalance;
                                int newCents = (int) Math.round((remainingBalance - newEuros) * 100);
                                session.setEuros(newEuros);
                                session.setCents(newCents);
                                sector.newPark();
                                session.insertLog(currentTimeStr,selectedTimeStr,hours,minutes);
                                Toast.makeText(getApplicationContext(), "Parking started!", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent(ParkActivity.this, MainActivity.class);
                                resultIntent.putExtra("session", session);
                                resultIntent.putExtra("sector", sectorList);
                                startActivity(resultIntent);
                            }
                        }
                    }
                }
            }
        });
        findViewById(R.id.LogoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession.isLoggedIn = false;
                Intent i = new Intent(ParkActivity.this, LoginActivity.class);
                i.putExtra("sector", sectorList);
                startActivity(i);
            }
        });
    }
}
