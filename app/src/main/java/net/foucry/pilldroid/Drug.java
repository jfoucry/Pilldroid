package net.foucry.pilldroid;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static net.foucry.pilldroid.UtilDate.dateAtNoon;
import static net.foucry.pilldroid.UtilDate.nbOfDaysBetweenDateAndToday;

/**
 * Created by jacques on 26/11/15.
 */
public class Drug implements Serializable {

    private static final String TAG = Drug.class.getName();
    /* part read form database */
    private int id;
    private String name;
    private String cip13;
    private String cis;
    private String administration_mode;
    private String presentation;
    private double stock;
    private double take;
    private int warnThreshold;
    private int alertThreshold;
    private long dateLastUpdate;

    /* calculate part */
    private Date dateEndOfStock;

    Drug() {
    }

    Drug(final String cis, final String cip13, final String name, final String administration_mode, final String presentation,
         double stock, double take, int warn, int alert, long dateLastUpdate) {
        super();

        this.cis = cis;
        this.cip13 = cip13;
        this.name = name;
        this.administration_mode = administration_mode;
        this.presentation = presentation;
        this.stock = stock;
        this.take = take;
        this.warnThreshold = warn;
        this.alertThreshold = alert;
        this.dateLastUpdate = dateLastUpdate;
    }


    public int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getCip13() {
        return cip13;
    }

    String getCis() {
        return cis;
    }

    String getAdministration_mode() {
        return administration_mode;
    }

    String getPresentation() {
        return presentation;
    }

    double getStock() {
        return stock;
    }

    double getTake() {
        return take;
    }

    int getAlertThreshold() {
        return alertThreshold;
    }

    int getWarnThreshold() {
        return warnThreshold;
    }

    long getDateLastUpdate() {
        return dateLastUpdate;
    }

    Date getDateEndOfStock() {
        return dateEndOfStock;
    }

    public void setId(int id) {
        this.id = id;
    }

    void setName(String name) {
        this.name = name;
    }

    void setCip13(String cip13) {
        this.cip13 = cip13;
    }

    void setCis(String cis) {
        this.cis = cis;
    }

    void setAdministration_mode(String administration_mode) {
        this.administration_mode = administration_mode;
    }

    void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    void setStock(double stock) {
        this.stock = stock;
    }

    void setTake(double take) {
        this.take = take;
    }

    void setWarnThreshold(int warn) {
        if (warn == 0)
            warn = 14;
        this.warnThreshold = warn;
    }

    void setAlertThreshold(int alert) {
        if (alert == 0)
            alert = 7;
        this.alertThreshold = alert;
    }

    void setDateLastUpdate(long l) {
        this.dateLastUpdate = l;
    }

    void setDateEndOfStock() {
        int numberDayOfTake;
        if (this.take > 0) {
            numberDayOfTake = (int) Math.floor(this.stock / this.take);
        } else {
            numberDayOfTake = 0;
        }

        Date aDate = dateAtNoon(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aDate);
        calendar.add(Calendar.DAY_OF_YEAR, numberDayOfTake);

        this.dateEndOfStock = calendar.getTime();
    }

    void newStock() {
        Log.d(TAG, "current drug = " + this.toString());

        Date lastUpdate = new Date(getDateLastUpdate());

        int numberOfDays = nbOfDaysBetweenDateAndToday(lastUpdate);
        if (numberOfDays > 0) {
            double takeDuringPeriod = this.take * numberOfDays;
            setStock(getStock() - takeDuringPeriod);
            setDateLastUpdate(new Date().getTime());
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drug drug = (Drug) o;
        return stock == drug.stock &&
                take == drug.take &&
                alertThreshold == drug.alertThreshold &&
                warnThreshold == drug.warnThreshold &&
                Objects.equals(name, drug.name);
    }
}