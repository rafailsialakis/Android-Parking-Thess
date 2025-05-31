package com.example.parkpal;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class SectorList implements Parcelable {
    private ArrayList<Sector> list = new ArrayList<>();
    private double parkingCost;

    // We keep a reference to the DB helper so we can do CRUD
    private SectorDbHelper dbHelper;

    /**
     * PRIMARY constructor: pass a Context so we can instantiate
     * the DB helper, then load all existing sectors from SQLite.
     */
    public SectorList(Context context) {
        dbHelper = new SectorDbHelper(context.getApplicationContext());
        loadSectorsFromDb();
        parkingCost = 0.028; // default cost; you can store this in SharedPreferences or another table if you like
    }

    /**
     * No-arg constructor: this won’t load from DB.
     * You can use this only in very specific scenarios or tests.
     * If you call this, you must manually call initDbHelper(...) later.
     */
    public SectorList() {
        parkingCost = 0.028;
        dbHelper = null;
    }

    /**
     * Parcelable constructor: after unmarshalling, we only have the list & cost,
     * but no DB helper. Call initDbHelper(...) to re-attach the database.
     */
    protected SectorList(Parcel in) {
        list = in.createTypedArrayList(Sector.CREATOR);
        parkingCost = in.readDouble();
        dbHelper = null; // MUST call initDbHelper(context) in the receiver Activity
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

    /**
     * Must call this right after you do:
     *     SectorList sectorList = getIntent().getParcelableExtra("sector");
     * in your Activity. That way, the DB helper is re-attached and you can use
     * all the methods below that read/write from SQLite.
     */
    public void initDbHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new SectorDbHelper(context.getApplicationContext());
            loadSectorsFromDb();
        }
    }

    /** Load all existing sectors from SQLite into 'list'. */
    private void loadSectorsFromDb() {
        if (dbHelper == null) {
            Log.e("SectorList", "DB helper is null! Call initDbHelper(context) first.");
            return;
        }
        List<Sector> fromDb = dbHelper.getAllSectors();
        list.clear();
        list.addAll(fromDb);
    }

    /** Returns the current cost per minute. */
    public double getParkingCost() {
        return parkingCost;
    }

    /** Update the cost per minute (you might want to persist this separately). */
    public void setParkingCost(double cost) {
        parkingCost = cost;
        // If you need to persist this across restarts, consider SharedPreferences or a settings table.
    }

    /**
     * Checks if a sector with the given code exists in the in-memory list.
     * Returns the Sector object (in memory) or null.
     */
    public Sector isInList(String code) {
        for (Sector s : list) {
            if (s.getCode().equalsIgnoreCase(code)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Adds (or replaces) a sector. 1) writes to SQLite via dbHelper.insertSector(...),
     * 2) updates the in-memory list so getSectorCodes() etc. reflect it immediately.
     */
    public void AddSector(String code, int capacity, int available_spots) {
        if (dbHelper == null) {
            Log.e("SectorList", "DB helper not initialized! Call initDbHelper(context) first.");
            return;
        }
        long rowId = dbHelper.insertSector(code, capacity, available_spots);
        if (rowId != -1) {
            removeFromInMemoryList(code);
            list.add(new Sector(code, capacity, available_spots));
        }
    }

    /** Removes a sector by code from both SQLite and the in-memory list. */
    public boolean removeSector(String code) {
        if (dbHelper == null) {
            Log.e("SectorList", "DB helper not initialized! Call initDbHelper(context) first.");
            return false;
        }
        boolean deleted = dbHelper.deleteSector(code);
        if (deleted) {
            removeFromInMemoryList(code);
        }
        return deleted;
    }

    /** Helper that removes a matching Sector object from the in-memory 'list'. */
    private void removeFromInMemoryList(String code) {
        Sector toRemove = null;
        for (Sector s : list) {
            if (s.getCode().equalsIgnoreCase(code)) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            list.remove(toRemove);
        }
    }

    /** Returns a String[] of all sector codes (for populating Spinners, etc.). */
    public String[] getSectorCodes() {
        String[] codes = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            codes[i] = list.get(i).getCode();
        }
        return codes;
    }

    /**
     * Call this whenever you decrement/increment available_spots for a given sector.
     * E.g. after sector.newPark(), do:
     *     sectorList.updateSectorAvailability(code, sector.getAvailable_spots());
     */
    public void updateSectorAvailability(String code, int newAvailableSpots) {
        if (dbHelper == null) {
            Log.e("SectorList", "DB helper not initialized! Call initDbHelper(context) first.");
            return;
        }
        dbHelper.updateSectorAvailability(code, newAvailableSpots);

        // Update the in-memory Sector object as well:
        Sector s = isInList(code);
        if (s != null) {
            // We need the old capacity so we can reconstruct the new Sector object.
            // Add a getter getCapacity() to Sector.java if you don’t already have one:
            int oldCapacity = s.getCapacity();
            removeFromInMemoryList(code);
            list.add(new Sector(code, oldCapacity, newAvailableSpots));
        }
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
}
