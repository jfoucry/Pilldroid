package net.foucry.pilldroid;

import java.io.Serializable;
import java.lang.String;
import java.util.Calendar;
import java.util.Date;

import static  net.foucry.pilldroid.UtilDate.*;

/**
 * Created by jacques on 26/11/15.
 */
public class Medicament implements Serializable {

    /* part read form database */
    private int id;
    private String nom;
    private String cip13;
    private String cis;
    private String mode_administration;
    private String presentation;
    private double stock;
    private double prise;
    private int warnThreshold;
    private int alertThreshold;
    private long dateLastUpdate;

    /* calculate part */
    private Date dateEndOfStock;

    Medicament() {
    }

    Medicament(final String cis, final String cip13, final String nom, final String mode_administration, final String presentation,
               double stock, double prise, int warn, int alert, long dateLastUpdate) {
        super();

        this.cis = cis;
        this.cip13 = cip13;
        this.nom = nom;
        this.mode_administration = mode_administration;
        this.presentation = presentation;
        this.stock = stock;
        this.prise = prise;
        this.warnThreshold = warn;
        this.alertThreshold = alert;
        this.dateLastUpdate = dateLastUpdate;
    }


    public int getId() {
        return id;
    }

    String getNom() {
        return nom;
    }

    String getCip13() {
        return cip13;
    }

    String getCis() {
        return cis;
    }

    String getMode_administration() {
        return mode_administration;
    }

    String getPresentation() {
        return presentation;
    }

    double getStock() {
        return stock;
    }

    double getPrise() {
        return prise;
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

    void setNom(String nom) {
        this.nom = nom;
    }

    void setCip13(String cip13) {
        this.cip13 = cip13;
    }

    void setCis(String cis) {
        this.cis = cis;
    }

    void setMode_administration(String mode_administration) {
        this.mode_administration = mode_administration;
    }

    void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    void setStock(double stock) {
        this.stock = stock;
    }

    void setPrise(double prise) {
        this.prise = prise;
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
        int numberDayOfPrise;
        if (this.prise > 0) {
            numberDayOfPrise = (int) Math.floor(this.stock / this.prise);
        } else {
            numberDayOfPrise = 0;
        }

        Date aDate = dateAtNoon(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aDate);
        calendar.add(Calendar.DAY_OF_YEAR, numberDayOfPrise);

        this.dateEndOfStock = calendar.getTime();
    }

    void newStock() {
        Date lastUpdate = new Date(getDateLastUpdate());
        int numberOfDays = nbOfDaysBetweenDateAndToday(lastUpdate);
        if (numberOfDays > 0) {
            double takeDuringPeriod = this.prise * numberOfDays;
            setStock(getStock() - takeDuringPeriod);
            setDateLastUpdate(new Date().getTime());
        }
    }
}