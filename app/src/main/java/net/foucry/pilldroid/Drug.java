package net.foucry.pilldroid;

import static net.foucry.pilldroid.UtilDate.dateAtNoon;
import static net.foucry.pilldroid.UtilDate.nbOfDaysBetweenDateAndToday;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

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

    public void setId(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getCip13() {
        return cip13;
    }

    void setCip13(String cip13) {
        this.cip13 = cip13;
    }

    String getCis() {
        return cis;
    }

    void setCis(String cis) {
        this.cis = cis;
    }

    String getAdministration_mode() {
        return administration_mode;
    }

    void setAdministration_mode(String administration_mode) {
        this.administration_mode = administration_mode;
    }

    String getPresentation() {
        return presentation;
    }

    void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    double getStock() {
        return stock;
    }

    void setStock(double stock) {
        this.stock = stock;
    }

    double getTake() {
        return take;
    }

    void setTake(double take) {
        this.take = take;
    }

    int getAlertThreshold() {
        return alertThreshold;
    }

    void setAlertThreshold(int alert) {
        if (alert == 0)
            alert = 7;
        this.alertThreshold = alert;
    }

    int getWarnThreshold() {
        return warnThreshold;
    }

    void setWarnThreshold(int warn) {
        if (warn == 0)
            warn = 14;
        this.warnThreshold = warn;
    }

    long getDateLastUpdate() {
        return dateLastUpdate;
    }

    void setDateLastUpdate(long l) {
        this.dateLastUpdate = l;
    }

    Date getDateEndOfStock() {
        return dateEndOfStock;
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
        Log.d(TAG, "current drug = " + this);

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