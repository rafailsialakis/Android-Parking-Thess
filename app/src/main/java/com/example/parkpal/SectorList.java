package com.example.parkpal;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SectorList implements Parcelable {
    private ArrayList<Sector> list = new ArrayList<>();
    private double parkingCost;

    public SectorList() {
        list.add(new Sector("HONZOO", 100, 10));
        list.add(new Sector("ALAMOA", 200, 14));
        list.add(new Sector("INTMKT", 150, 11));
        list.add(new Sector("KEWALO", 130, 18));
        list.add(new Sector("BISQRE", 70, 50));
        list.add(new Sector("KALAKA", 55, 23));
        list.add(new Sector("KAKWTR", 55,0));
        parkingCost = 0.028;
    }

    public double getParkingCost(){
        return parkingCost;
    }

    public void setParkingCost(double cost){
        parkingCost = cost;
    }

    protected SectorList(Parcel in) {
        list = in.createTypedArrayList(Sector.CREATOR);
        parkingCost = in.readDouble();
    }
    public static final Creator<SectorList> CREATOR = new Creator<SectorList>() {
        @Override
        public SectorList createFromParcel(Parcel in) {
            return new SectorList(in);
        }

        @Override
        public SectorList[] newArray(int size) {
            return new SectorList[size];
        }
    };

    public Sector isInList(String code){
        for(Sector sector: list){
            if(sector.getCode().equals(code)){
                return sector;
            }
        }
        return null;
    }
    public void AddSector(String code, int capacity, int available_spots){
        list.add(new Sector(code, capacity, available_spots));
    }

    public boolean removeSector(String code){
        Sector toRemove = null;
        for(Sector sector: list){
            if(sector.getCode().equals(code)){
                toRemove = sector;
            }
        }
        if(toRemove != null){
            list.remove(toRemove);
            return true;
        }
        return false;
    }


    public ArrayList<Sector> getSectors() {
        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeTypedList(list);
        parcel.writeDouble(parkingCost);
    }

    public String[] getSectorCodes() {
        String[] codes = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            codes[i] = list.get(i).getCode();
        }
        return codes;
    }

}
