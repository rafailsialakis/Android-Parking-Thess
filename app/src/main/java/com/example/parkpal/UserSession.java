package com.example.parkpal;

import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class UserSession implements Serializable {
    private String username;
    private String password;
    private int euros;
    private int cents;
    private int totalParkingHours;
    private int totalParkingMinutes;

    private ArrayList<String> logsList;
    public UserSession(String username, String password){
        this.username = username;
        this.password = password;
        logsList = new ArrayList<>();
        totalParkingHours = 0;
        totalParkingMinutes = 0;
    }
    public static boolean isLoggedIn = false;
    public void setEuros(int euros){
        this.euros = euros;
    }
    public void setCents(int cents){
        this.cents = cents;
    }
    public int getEuros(){
        return euros;
    };
    public int getCents(){
        return cents;
    };

    public String getUsername(){
        return username;
    }

    public void insertLog(String start, String end, long parkingHours, long parkingMinutes){
        String toAdd = "Parking started at " + start + " and finishes at " + end;
        logsList.add(toAdd);
        totalParkingHours += parkingHours;
        totalParkingMinutes += parkingMinutes;
        if (totalParkingMinutes >= 60){
            totalParkingHours += totalParkingMinutes / 60;
            totalParkingMinutes = totalParkingMinutes % 60;
        }
    }

    public ArrayList<String> getLogsList(){
        return logsList;
    }

    public int getTotalParkingHours(){
        return totalParkingHours;
    }

    public int getTotalParkingMinutes(){
        return totalParkingMinutes;
    }
}
