package net.foucry.pilldroid;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.models.Prescription;

@Database(
        version = 3,
        entities = {Prescription.class},
        autoMigrations = {
                @AutoMigration(from = 1, to = 2),
                @AutoMigration(from = 2, to = 3),
        }
)

public abstract class PrescriptionDatabase extends RoomDatabase {
    public abstract PrescriptionsDAO getPrescriptionsDAO();
}
