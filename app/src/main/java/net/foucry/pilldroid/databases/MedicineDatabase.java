package net.foucry.pilldroid.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.foucry.pilldroid.dao.MedicinesDAO;
import net.foucry.pilldroid.models.Medicine;

@Database(
        version = 1,
        entities = {Medicine.class}
)

public abstract class MedicineDatabase extends RoomDatabase {
    private static MedicineDatabase INSTANCE;

    public static MedicineDatabase getInstanceDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room
                            .databaseBuilder(context.getApplicationContext(), MedicineDatabase.class, "medicines")
                            .createFromAsset("drugs.db")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract MedicinesDAO getMedicinesDAO();
}