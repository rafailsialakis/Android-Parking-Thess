package com.example.parkpal;

import android.os.Parcel;
import android.os.Parcelable;

public class Sector implements Parcelable {
    private String code;
    private int capacity;
    private int available_spots;

    public Sector(String code, int capacity, int available_spots) {
        this.code = code;
        this.capacity = capacity;
        this.available_spots = available_spots;
    }

    protected Sector(Parcel in) {
        code = in.readString();
        capacity = in.readInt();
        available_spots = in.readInt();
    }

    public static final Creator<Sector> CREATOR = new Creator<Sector>() {
        @Override
        public Sector createFromParcel(Parcel in) {
            return new Sector(in);
        }

        @Override
        public Sector[] newArray(int size) {
            return new Sector[size];
        }
    };

    public void newPark(){
        this.available_spots--;
    }

    public void finishPark(){
        this.available_spots++;
    }

    public String getCode() {
        return code;
    }

    public int getAvailable_spots() {
        return available_spots;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(code);
        parcel.writeInt(capacity);
        parcel.writeInt(available_spots);
    }
}
