package com.example.p1.Database.PersonDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Person.class}, version = 2)
public abstract class WeightTrackingDatabase extends RoomDatabase {
    public abstract PersonDao personDao();

    private static volatile WeightTrackingDatabase INSTANCE;

    public static WeightTrackingDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WeightTrackingDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WeightTrackingDatabase.class, "WeightTrackingDB")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
