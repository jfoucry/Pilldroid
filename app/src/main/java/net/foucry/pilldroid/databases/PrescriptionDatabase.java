package net.foucry.pilldroid.databases;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.models.Prescription;

@Database(
        version = 1,
        entities = {Prescription.class}
)

public abstract class PrescriptionDatabase extends RoomDatabase {
    public abstract PrescriptionsDAO getPrescriptionsDAO();
}
