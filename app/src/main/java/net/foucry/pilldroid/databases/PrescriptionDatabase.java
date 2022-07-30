package net.foucry.pilldroid.databases;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RenameColumn;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;

import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.models.Prescription;

@Database(
    version = 2,
    entities = {Prescription.class},
    autoMigrations = {
        @AutoMigration(
            from = 1,
            to = 2,
            spec = PrescriptionDatabase.generic_typeMigration.class
        )
    }

)

public abstract class PrescriptionDatabase extends RoomDatabase {
    private static PrescriptionDatabase INSTANCE;

    public abstract PrescriptionsDAO getPrescriptionsDAO();

    @RenameColumn(tableName = "prescriptions", fromColumnName = "genetic_type", toColumnName = "generic_type")
    static class generic_typeMigration implements AutoMigrationSpec { }

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
