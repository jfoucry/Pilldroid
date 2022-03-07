package net.foucry.pilldroid.models;

import android.arch.persitence.room.Entity;
import android.arch.persitence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "medics")
public class medic {
  @PrimaryKey
  @NonNull private Long id;
  private String  cis;
  private String  cip13;
  private String  name;
  private String  administration_mode;
  private String  presentation;
  private Long    stock;
  private Long    take;
  private Int     warning;
  private Int     alert;
  private Long    last_update;
}

