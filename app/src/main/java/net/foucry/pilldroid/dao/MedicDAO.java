package net.foucry.pilldroid.dao;

import  android.arch.persistence.room.Dao;
import  android.arch.persistence.room.Delete;
import  android.arch.persistence.room.Insert;
import  android.arch.persistence.room.Update;
import  net.foucry.pilldroid.models.Medic;

import  android.arch.persistence.room.Query;
import  java.util.list;

@Dao
public interface MedicDAO {
    @Insert
  public void insert(Medic... medics);
    @Upfdate
  public void update(Medic... medics);
    @Delete
  public void delete(Medic medic);
}

@Query("SELECT * FROM medics")
public  List<Medic> getMedics();

@Query("SELECT * FROM medics WHERE id = :id")
public Medic getMedicBy(Long id);

@Query("SELECT * FROM medics WHERE cip13 = :cip13")
public Medic getMedicByCIP13(String cip13);

@Query("SELECT count(*) FROM medics")
public int getMedicCount();
