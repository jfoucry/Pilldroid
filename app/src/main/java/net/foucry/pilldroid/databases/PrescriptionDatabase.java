package net.foucry.pilldroid.databases;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.models.Prescription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        version = 1,
        entities = {Prescription.class}
)

public abstract class PrescriptionDatabase extends RoomDatabase {
    private static PrescriptionDatabase INSTANCE;
    public abstract PrescriptionsDAO getPrescriptionsDAO();
    public static PrescriptionDatabase getInstanceDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room
                            .databaseBuilder(context.getApplicationContext(),
                                    PrescriptionDatabase.class, "prescriptions")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
