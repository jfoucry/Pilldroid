package net.foucry.pilldroid;

import android.arch.persistence.root.Database;
import android.arch.persistence.root.RoomDatabase;

import net.foucry.pilldroid.dao.MedicDAO;
import net.foucry.pilldroid.model.Medic;

@Database(entities = {Medic.class}, version = 1)

public abstract class PilldroidDatabase extends RoomDatabase {
  public abstract MedicDAO getMedicDAO();
}
