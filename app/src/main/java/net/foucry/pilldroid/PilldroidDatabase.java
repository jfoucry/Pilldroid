package net.foucry.pilldroid;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import net.foucry.pilldroid.dao.MedicDAO;
import net.foucry.pilldroid.models.Medic;

@Database(entities = {Medic.class}, version = 1)

public abstract class PilldroidDatabase extends RoomDatabase {
  public abstract MedicDAO getMedicDAO();
}
