package net.foucry.pilldroid.dao;

import androidx.room.Dao;
import androidx.room.Query;

import net.foucry.pilldroid.models.Medicine;

import java.util.List;

@Dao
public interface MedicinesDAO {
    @Query("SELECT * FROM medicines")
    List<Medicine> getAllMedicines();

    @Query("SELECT * FROM medicines WHERE cip13 = :cip13")
    Medicine getMedicineByCIP13(String cip13);

    @Query("SELECT count(*) FROM medicines")
    int getMedicineCount();
}
