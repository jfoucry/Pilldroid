package net.foucry.pilldroid.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import net.foucry.pilldroid.models.Prescription;

import java.util.List;

@Dao
public interface PrescriptionsDAO {
    @Insert
    void insert(Prescription... prescriptions);

    @Update
    void update(Prescription... prescriptions);

    @Delete
    void delete(Prescription prescription);

    @Query("SELECT * FROM prescriptions")
    List<Prescription> getAllMedics();

    @Query("SELECT * FROM prescriptions WHERE cip13 = :cip13")
    Prescription getMedicByCIP13(String cip13);

    @Query("SELECT count(*) FROM prescriptions")
    int getMedicCount();
}


