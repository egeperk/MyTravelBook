package com.egeperk.mytravelbook.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.egeperk.mytravelbook.Location;

@Database(entities = {Location.class}, version = 1)
public abstract class LocationDatabase extends RoomDatabase {

    public abstract LocationDao locationDao();

}
