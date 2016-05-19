package net.foucry.pilldroid;

import java.io.Serializable;
import java.lang.String;
import java.text.SimpleDateFormat;
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
    private String dateLastUpdate;

    /* calculate part */
    private Date dateEndOfStock;

    private static final String[] COLUMN_LIST = {"id","cis", "cip13", "nom", "mode_administration", "presentation", "stock", "prise",
    "seuil_warn", "seuil_alert", "dateLastUpdate"};

    public Medicament() {}

    public Medicament(String cis, String cip13, String nom, String mode_administration, String presentation,
                      double stock, double prise, int warn, int alert) {
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
    }

//    private Medicament(Cursor cursor) {
//        if (cursor == null) throw new AssertionError();
//
//        this.setCis(cursor.getString(0));
//        this.setCip13(cursor.getString(1));
//        this.setNom(cursor.getString(2));
//        this.setMode_administration(cursor.getString(3));
//        this.setPresentation(cursor.getString(4));
//        this.setStock(cursor.getFloat(5));
//        this.setPrise(cursor.getFloat(6));
//    }



    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getCip13() {
        return cip13;
    }

    public String getCis() {
        return cis;
    }

    public String getMode_administration() {
        return mode_administration;
    }

    public String getPresentation() {
        return presentation;
    }

    public double getStock() {
        return stock;
    }

    public double getPrise() {
        return prise;
    }

    public int getAlertThreshold() {
        return alertThreshold;
    }

    public int getWarnThreshold() {
        return warnThreshold;
    }

    public String getDateLastUpdate() {
        return dateLastUpdate;
    }

    public Date getDateEndOfStock() {
        return dateEndOfStock;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCip13(String cip13) {
        this.cip13 = cip13;
    }

    public void setCis(String cis) {
        this.cis = cis;
    }

    public void setMode_administration(String mode_administration) {
        this.mode_administration = mode_administration;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public void setPrise(double prise) {
        this.prise = prise;
    }

    public void setWarnThreshold(int warn) {
        if (warn == 0)
            warn = 14;
        this.warnThreshold = warn;
    }
    public void setAlertThreshold(int alert) {
        if (alert == 0)
            alert = 7;
        this.alertThreshold = alert;
    }

    public void setDateLastUpdate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateLastUpdate = date2String(dateAtNoon(new Date()), dateFormat);
    }

    public void setDateEndOfStock() {
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

    public double newStock(double currentStock) {
        Date lastUpdate = string2Date(this.dateLastUpdate);
        int numberOfDays = nbOfDaysBetweenDateAndToday(lastUpdate);
        double takeDuringPeriod = this.prise * numberOfDays;

        return currentStock - takeDuringPeriod;
    }
}
