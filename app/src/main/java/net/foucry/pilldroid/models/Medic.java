package net.foucry.pilldroid.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "medics")
public class Medic {
  @PrimaryKey
  @NonNull private final Integer id;
  private String  cis;
  private String  cip13;
  private String  name;
  private String  administration_mode;
  private String  presentation;
  private Long    stock;
  private Long    take;
  private Integer warning;
  private Integer alert;
  private Long    last_update;

  public Medic(@NonNull Integer id) {
    this.id = id;
  }

  public void setCis(String cis)
{
  this.cis = cis;
}


  public void setCip13(String cip13) {
    this.cip13 = cip13;
  }

  public void setName(String name) {
    this.name = name;
  }

public void setAdministration_mode(String administration_mode) {

  this.administration_mode = administration_mode;
}

public void setPresentation(String presentation) {
  this.presentation = presentation;
}
public void setStock(Long stock) {
  this.stock = stock;
}

public void setTake(Long take) {
  this.take = take;
}

public void setWarning(Integer warning) {
  this.warning = warning;
}

public void setAlert(Integer alert) {
  this.alert = alert;
}

public void setLast_update(Long last_update) {
  this.last_update = last_update;
}

@NonNull
Integer getId() {
  return  this.id;
}

String getCis() {
  return this.cis;
}

  public Integer getWarning() {
    return warning;
  }

  public String getCip13() {
    return cip13;
  }

  public String getName() {
    return name;
  }

  public String getAdministration_mode() {
    return administration_mode;
  }

  public String getPresentation() {
    return presentation;
  }

  public Long getStock() {
    return stock;
  }

  public Long getTake() {
    return take;
  }

  public Integer getAlert() {
    return alert;
  }

  public Long getLast_update() {
    return last_update;
  }
}




