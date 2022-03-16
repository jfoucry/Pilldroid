package net.foucry.pilldroid.databases;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import net.foucry.pilldroid.dao.MedicinesDAO;
import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.models.Medicine;
import net.foucry.pilldroid.models.Prescription;

@Database(
        version = 1,
        entities = {Medicine.class}
)

public abstract class MedicineDatabase extends RoomDatabase {
    public abstract MedicinesDAO getMedicinesDAO();
}