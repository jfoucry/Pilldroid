package net.foucry.pilldroid.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import net.foucry.pilldroid.UtilDate;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "prescriptions")
public class Prescription implements Serializable {
    private static final String TAG = Prescription.class.getName();
    @PrimaryKey
    @NonNull
    private String cis;
    private String cip13;
    private String name;
    private String administration_mode;
    private String presentation;
    private Float stock;
    private Float take;
    private Integer warning;
    private Integer alert;
    private Long last_update;
    private String label_group;
    private Integer generic_type;

    @NonNull
    public String getCis() {
        return this.cis;
    }

    public void setCis(@NonNull String cis) {
        this.cis = cis;
    }

    public Integer getWarning() {
        return warning;
    }

    public void setWarning(Integer warning) {
        this.warning = warning;
    }

    public String getCip13() {
        return cip13;
    }

    public void setCip13(String cip13) {
        this.cip13 = cip13;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdministration_mode() {
        return administration_mode;
    }

    public void setAdministration_mode(String administration_mode) {
        this.administration_mode = administration_mode;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public Float getStock() {
        return stock;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }

    public Float getTake() {
        return take;
    }

    public void setTake(Float take) {
        this.take = take;
    }

    public Integer getAlert() {
        return alert;
    }

    public void setAlert(Integer alert) {
        this.alert = alert;
    }

    public Long getLast_update() {
        return last_update;
    }

    public void setLast_update(Long last_update) {
        this.last_update = last_update;
    }

    public int getAlertThreshold() {
        return this.alert;
    }

    public int getWarnThreshold() {
        return this.warning;
    }

    public String getLabel_group() {
        return label_group;
    }

    public void setLabel_group(String label_group) {
        this.label_group = label_group;
    }

    public Integer getGeneric_type() {
        return generic_type;
    }

    public void setGeneric_type(Integer generic_type) {
        this.generic_type = generic_type;
    }

    public Date getDateEndOfStock() {
        Date aDate = UtilDate.dateAtNoon(new Date());
        Calendar calendar = Calendar.getInstance();

        int numberDayOfTake = 0;
        if (getTake() > 0) {
            calendar.setTime(aDate);
            numberDayOfTake = (int) Math.floor(this.getStock() / this.getTake());
            calendar.add(Calendar.DAY_OF_YEAR, numberDayOfTake);
        } else {
            calendar.set(Calendar.YEAR, 9999);
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        return calendar.getTime();
    }

    public void newStock() {
        Date lastUpdate = new Date(getLast_update());

        int numberOfDays = UtilDate.nbOfDaysBetweenDateAndToday(lastUpdate);

        /*if (BuildConfig.DEBUG) {
            numberOfDays = 1;
            Log.d(TAG, "Set NumberOfDays = 1");
        }*/

        if (numberOfDays > 0) {
            double takeDuringPeriod = this.take * numberOfDays;
            setStock((float) (getStock() - takeDuringPeriod));
            setLast_update(new Date().getTime());
        }
    }
}




