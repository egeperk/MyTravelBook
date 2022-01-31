package com.egeperk.mytravelbook.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.egeperk.mytravelbook.Location;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM Location")
    Flowable<List<Location>> getAll();

    @Insert
    Completable insert(Location location);

    @Delete
    Completable delete(Location location);


}
