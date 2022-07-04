package net.foucry.pilldroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jacques on 24/04/16.
 */



class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "prescription.db";

    private static final String TABLE_DRUG          = "drug";
    private static final String KEY_ID              = "id";
    private static final String KEY_CIS             = "cis";
    private static final String KEY_CIP13           = "cip13";
    private static final String KEY_NAME            = "name";
    private static final String KEY_ADMIN           = "administration_mode";
    private static final String KEY_PRES            = "presentation";
    private static final String KEY_STOCK           = "stock";
    private static final String KEY_TAKE            = "take";
    private static final String KEY_THRESHOLD_WARN  = "warning";
    private static final String KEY_THRESHOLD_ALERT = "alert";
    private static final String KEY_LAST_UPDATE     = "last_update";

    final List<Drug> drugs = new ArrayList<>();

    private static final String TAG = DBHelper.class.getName();

    private static final String[] COLUMNS = {KEY_ID, KEY_CIS,KEY_CIP13, KEY_NAME, KEY_ADMIN, KEY_PRES, KEY_STOCK, KEY_TAKE,
            KEY_THRESHOLD_WARN, KEY_THRESHOLD_ALERT, KEY_LAST_UPDATE};

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DRUG_TABLE = "CREATE TABLE drug ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cis TEXT, " +
                "cip13 TEXT, "  +
                "name TEXT, " +
                "administration_mode TEXT, " +
                "presentation TEXT, " +
                "stock REAL, " +
                "take REAL, " +
                "warning INT, " +
                "alert INT, " +
                "last_update LONG)";

        db.execSQL(CREATE_DRUG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old database
        db.execSQL("DROP TABLE IF EXISTS drug");

        // Create fresh book table
        this.onCreate(db);
    }

    /**
     * Drop current database. Debug code only
     */
    void dropDrug() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Drop drug table");
        db.execSQL("DROP TABLE IF EXISTS drug");

        this.onCreate(db);
    }

    /**
     * Split drug values into database record and record it to the DB
     * @param drug the drug object to be saved
     */
    void addDrug(Drug drug) {
        // Logging
        Log.d(TAG, drug.toString());

        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CIS, drug.getCis());
        values.put(KEY_CIP13, drug.getCip13());
        values.put(KEY_NAME, drug.getName());
        values.put(KEY_ADMIN, drug.getAdministration_mode());
        values.put(KEY_PRES, drug.getPresentation());
        values.put(KEY_STOCK, drug.getStock());
        values.put(KEY_TAKE, drug.getTake());
        values.put(KEY_THRESHOLD_WARN, drug.getWarnThreshold());
        values.put(KEY_THRESHOLD_ALERT, drug.getAlertThreshold());
        values.put(KEY_LAST_UPDATE, drug.getDateLastUpdate());

        // Calculate some drug's fields

        // Insert
        db.insert(TABLE_DRUG,   // table
                null,           // columns list not needed
                values);        // key/value

        // Close database
        db.close();
    }

    /**
     * return a drug from the DB with is id
     * @param id of the drug we looking for (not used)
     * @return return the found drug of null
     */
    public Drug getDrug(int id) {
        // Get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_DRUG,                    // Which table
                COLUMNS,                                         // column names
                " id = ?",                             // selections
                new String[] { String.valueOf(id) },             // selections args
                null,                                   // group by
                null,                                    // having
                null,                                   // order by
                null);                                     // limits

        Log.d(TAG, "Cursor == " + DatabaseUtils.dumpCursorToString(cursor));

        // if case we got result, go to the first one
        Drug drug = new Drug();
        if (cursor != null) {
            cursor.moveToFirst();

            // Build drug object
            drug.setId(Integer.parseInt(cursor.getString(0)));
            drug.setCis(cursor.getString(1));
            drug.setCip13(cursor.getString(2));
            drug.setName(cursor.getString(3));
            drug.setAdministration_mode(cursor.getString(4));
            drug.setPresentation(cursor.getString(5));
            drug.setStock(Double.parseDouble(cursor.getString(6)));
            drug.setTake(Double.parseDouble(cursor.getString(7)));
            drug.setWarnThreshold(Integer.parseInt(cursor.getString(8)));
            drug.setAlertThreshold(Integer.parseInt(cursor.getString(9)));
            drug.setDateLastUpdate(Long.parseLong(cursor.getString(10)));
        }
        // Log
        Log.d(TAG, "getDrug("+id+")" + drug);

        assert cursor != null;
        cursor.close();
        db.close();
        // Return drug

        return drug;
    }

    /**
     *
     * @param cip13 drug id in French nomenclature
     * @return the drug object found in DB or null
     */
    public Drug getDrugByCIP13(String cip13) {
        // Get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_DRUG,            // Which table
                COLUMNS,                                 // column names
                " cip13 = ?",                              // selections
                new String[]{String.valueOf(cip13)},    // selections args
                null,                                   // group by
                null,                                   // having
                null,                                   // order by
                null);                                  // limits

        // if case we got result, go to the first one
        Drug drug = new Drug();
        if (cursor != null) {
            cursor.moveToFirst();

            // Build drug object
            drug.setId(Integer.parseInt(cursor.getString(0)));
            drug.setCis(cursor.getString(1));
            drug.setCip13(cursor.getString(2));
            drug.setName(cursor.getString(3));
            drug.setAdministration_mode(cursor.getString(4));
            drug.setPresentation(cursor.getString(5));
            drug.setStock(Double.parseDouble(cursor.getString(6)));
            drug.setTake(Double.parseDouble(cursor.getString(7)));
            drug.setWarnThreshold(Integer.parseInt(cursor.getString(8)));
            drug.setAlertThreshold(Integer.parseInt(cursor.getString(9)));
            drug.setDateLastUpdate(Long.parseLong(cursor.getString(10)));
        }

        assert cursor != null;
        cursor.close();

        Log.d(TAG, "getDrug(" + cip13 + ")" + drug);

        return drug;
    }

    /**
     *
     * @return a Sorted and updated by dateEndOfStock List of All drugs presents in database
     */

    List<Drug> getAllDrugs() {

        // Build the query
        String query = "SELECT * FROM " + TABLE_DRUG;

        // Get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d(TAG, "Cursor == " + DatabaseUtils.dumpCursorToString(cursor));

        // For Each row, build a drug and add it to the list
        Drug drug;
        if (cursor.moveToFirst()) {
            do {
                drug = new Drug();
                drug.setId(Integer.parseInt(cursor.getString(0)));
                drug.setCis(cursor.getString(1));
                drug.setCip13(cursor.getString(2));
                drug.setName(cursor.getString(3));
                drug.setAdministration_mode(cursor.getString(4));
                drug.setPresentation(cursor.getString(5));
                drug.setStock(Double.parseDouble(cursor.getString(6)));
                drug.setTake(Double.parseDouble(cursor.getString(7)));
                drug.setWarnThreshold(Integer.parseInt(cursor.getString(8)));
                drug.setAlertThreshold(Integer.parseInt(cursor.getString(9)));
                drug.setDateLastUpdate(Long.parseLong(cursor.getString(10)));
                // Call calcul method
                drug.setDateEndOfStock();


                // Add drug to Drugs
                drugs.add(drug);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Drug currentDrug;
        for (int position = 0 ; position < getCount() ; position++ ) {
            currentDrug = getItem(position);

            if (!DateUtils.isToday(currentDrug.getDateLastUpdate()))
            {
                currentDrug.newStock();
                updateDrug(currentDrug);
            }
        }



        Log.d(TAG, "Before sort == " + drugs);

        drugs.sort(new Comparator<Drug>() {
            @Override
            public int compare(Drug lhs, Drug rhs) {
                if (lhs.getDateEndOfStock().compareTo(rhs.getDateEndOfStock()) != 0)
                    return lhs.getDateEndOfStock().compareTo(rhs.getDateEndOfStock());
                else
                    return (int) (lhs.getStock() - rhs.getStock());
            }
        });
        Log.d(TAG, "After sort " + drugs);

        // Move drug with prise = 0 at the end of the list
        // todo: If some drug moved, must redo all the loop
        int position = 0 ;
        for ( int nbOps = 0;  nbOps < getCount() ; nbOps ++ ) {
            currentDrug = getItem(position);
            double currentTake = currentDrug.getTake();
            if (currentTake == 0)
            {
                drug = drugs.remove(position);
                drugs.add(drug);
            } else
            {
                position++;
            }
        }
        return drugs;
    }

    /**
     *
     * @param drug object to be updated in DB
     */
    public void updateDrug(Drug drug) {

        Log.d(TAG, "Update Drug == " + drug.toString());
        Log.d(TAG, "drug last_update == " + UtilDate.convertDate(drug.getDateLastUpdate()));

        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add column/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID,              drug.getId());
        values.put(KEY_CIS,             drug.getCis());
        values.put(KEY_CIP13,           drug.getCip13());
        values.put(KEY_NAME,            drug.getName());
        values.put(KEY_ADMIN,           drug.getAdministration_mode());
        values.put(KEY_PRES,            drug.getPresentation());
        values.put(KEY_STOCK,           drug.getStock());
        values.put(KEY_TAKE,            drug.getTake());
        values.put(KEY_THRESHOLD_WARN,  drug.getWarnThreshold());
        values.put(KEY_THRESHOLD_ALERT, drug.getAlertThreshold());
        values.put(KEY_LAST_UPDATE,     drug.getDateLastUpdate());

        String[] selectionArgs = { String.valueOf(drug.getId()) };

        db.update(TABLE_DRUG,                       // table
                values,                             // column/value
                KEY_ID + " = ?",        // selections
                selectionArgs);

        // Close DB
        db.close();
        Log.d(TAG, "values == " + values);
    }

    /**
     * Delete a drug object in database
     * @param drug object to be delete in the DB
     */
    public void deleteDrug(Drug drug) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete record
        db.delete(TABLE_DRUG,           // table
                KEY_ID+ " = ?",         // selections
                new String[] { String.valueOf(drug.getId()) } );  // selections args

        // Close DB
        db.close();

        // log
        Log.d(TAG, "delete drug "+ drug);
    }

    /**
     * Get count of all drug present in database
     * @return number of drug in DB
     */
    int getCount() {

        String query = "SELECT count (*) FROM " + TABLE_DRUG;

        // Get reference to readable DB (tutorial parle de writable, mais bof... on verra)
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery(query, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);

        mCount.close();
        db.close();
        return count;
    }

    public Drug getItem(int position) {
        return drugs.get(position);
    }

    boolean isDrugExist(String cip13) {
        boolean value = false;
        try {
            Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_DRUG + " where cip13 = "+cip13, null);

            if(c.getCount()>0)
            {
                value = true;
            }
            c.close();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }
}

