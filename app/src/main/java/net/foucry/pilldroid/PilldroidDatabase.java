package net.foucry.pilldroid;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import net.foucry.pilldroid.dao.MedicDAO;
import net.foucry.pilldroid.models.Medic;

@Database(
        version = 3,
        entities = {Medic.class},
        autoMigrations = {
                @AutoMigration(from = 1, to = 2),
                @AutoMigration(from = 2, to = 3),
        }
)

public abstract class PilldroidDatabase extends RoomDatabase {
    public abstract MedicDAO getMedicDAO();
}
