package net.foucry.pilldroid.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import net.foucry.pilldroid.UtilDate;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "medics")
public class Medic {
  @PrimaryKey
  @NonNull private String  cis;
  private String  cip13;
  private String  name;
  private String  administration_mode;
  private String  presentation;
  private Double  stock;
  private Double  take;
  private Integer warning;
  private Integer alert;
  private Long    last_update;


/*  public Medic(@NonNull Integer id) {
    this.id = id;
  }*/

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
  public void setStock(Double stock) {
  this.stock = stock;
}

  public void setTake(Double take) {
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

//@NonNull
/*public Integer getId() {
  return  this.id;
}*/

@NonNull
  public String getCis() {
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

  public Double getStock() {
    return stock;
  }

  public Double getTake() {
    return take;
  }

  public Integer getAlert() {
    return alert;
  }

  public Long getLast_update() {
    return last_update;
  }

  public int getAlertThreshold() {
    return this.alert;
  }

  public int getWarnThreshold() {
  return this.warning;
  }

  public Date getDateEndOfStock() {
    int numberDayOfTake;
    if (this.getTake() > 0) {
      numberDayOfTake = (int) Math.floor(this.getStock() / this.getTake());
    } else {
      numberDayOfTake = 0;
    }

    Date aDate = UtilDate.dateAtNoon(new Date());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(aDate);
    calendar.add(Calendar.DAY_OF_YEAR, numberDayOfTake);

    return calendar.getTime();
  }
}




