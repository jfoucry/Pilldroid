package net.foucry.pilldroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jacques on 24/04/16.
 */


class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "ordonnance.db";

    private static final String TABLE_DRUG      = "drug";
    private static final String KEY_ID          = "id";
    private static final String KEY_CIS         = "cis";
    private static final String KEY_CIP13       = "cip13";
    private static final String KEY_NAME        = "nom";
    private static final String KEY_ADMIN       = "mode_administration";
    private static final String KEY_PRES        = "presentation";
    private static final String KEY_STOCK       = "stock";
    private static final String KEY_PRISE       = "prise";
    private static final String KEY_SEUIL_WARN  = "warning";
    private static final String KEY_SEUIL_ALERT = "alerte";

    private static DBHelper sInstance;

    private static final String TAG = DBHelper.class.getName();

    private static final String[] COLUMS = {KEY_ID, KEY_CIS,KEY_CIP13, KEY_NAME, KEY_ADMIN, KEY_PRES, KEY_STOCK, KEY_PRISE,
    KEY_SEUIL_WARN, KEY_SEUIL_ALERT};

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DRUG_TABLE = "CREATE TABLE drug ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cis TEXT, " +
                "cip13 TEXT, "  +
                "nom TEXT, " +
                "mode_administration TEXT, " +
                "presentation TEXT, " +
                "stock REAL, " +
                "prise REAL, " +
                "warning INT, " +
                "alerte INT)";

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
     * Split medicament values into database record and record it to the DB
     * @param medicament the medicament object to be saved
     */
    void addDrug(Medicament medicament) {
        // Logging
        Log.d(TAG, medicament.toString());

        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CIS, medicament.getCis());
        values.put(KEY_CIP13, medicament.getCip13());
        values.put(KEY_NAME, medicament.getNom());
        values.put(KEY_ADMIN, medicament.getMode_administration());
        values.put(KEY_PRES, medicament.getPresentation());
        values.put(KEY_STOCK, medicament.getStock());
        values.put(KEY_PRISE, medicament.getPrise());
        values.put(KEY_SEUIL_WARN, medicament.getWarnThreshold());
        values.put(KEY_SEUIL_ALERT,medicament.getAlertThreshold());

        // Calculate some medicament's fields

        // Insert
        db.insert(TABLE_DRUG,   // table
                null,           // colunms list not needed
                values);        // key/value

        // Close database
        db.close();
    }

    /**
     * return a medicament from the DB with is id
     * @param id of the medicament we looking for (not used)
     * @return return the found medicament of null
     */
    public Medicament getDrug(int id) {
        // Get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_DRUG,            // Which table
                COLUMS,                                 // column names
                " id = ?",                              // selections
                new String[] { String.valueOf(id) },    // selections args
                null,                                   // group by
                null,                                   // having
                null,                                   // order by
                null);                                  // limits

        // if case we got result, go to the first one
        Medicament medicament = new Medicament();
        if (cursor != null) {
            cursor.moveToFirst();

            // Build medicament object
            medicament.setId(Integer.parseInt(cursor.getString(0)));
            medicament.setCis(cursor.getString(1));
            medicament.setCip13(cursor.getString(2));
            medicament.setNom(cursor.getString(3));
            medicament.setMode_administration(cursor.getString(4));
            medicament.setPresentation(cursor.getString(5));
            medicament.setStock(Double.parseDouble(cursor.getString(6)));
            medicament.setPrise(Double.parseDouble(cursor.getString(7)));
            medicament.setWarnThreshold(Integer.parseInt(cursor.getString(8)));
            medicament.setAlertThreshold(Integer.parseInt(cursor.getString(9)));
        }
        // Log
        Log.d(TAG, "getDrug("+id+")" + medicament.toString());

        if (null != cursor) cursor.close();
        // Return medicament

        return medicament;
    }

    /**
     *
     * @param cip13 drug id in French nomemclature
     * @return the medicament object found in DB or null
     */
    public Medicament getDrugByCIP13(String cip13) {
        // Get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_DRUG,            // Which table
                COLUMS,                                 // column names
                " cip13 = ?",                              // selections
                new String[]{String.valueOf(cip13)},    // selections args
                null,                                   // group by
                null,                                   // having
                null,                                   // order by
                null);                                  // limits

        // if case we got result, go to the first one
        Medicament medicament = new Medicament();
        if (cursor != null) {
            cursor.moveToFirst();

            // Build medicament object
            medicament.setId(Integer.parseInt(cursor.getString(0)));
            medicament.setCis(cursor.getString(1));
            medicament.setCip13(cursor.getString(2));
            medicament.setNom(cursor.getString(3));
            medicament.setMode_administration(cursor.getString(4));
            medicament.setPresentation(cursor.getString(5));
            medicament.setStock(Double.parseDouble(cursor.getString(6)));
            medicament.setPrise(Double.parseDouble(cursor.getString(7)));
            medicament.setWarnThreshold(Integer.parseInt(cursor.getString(8)));
            medicament.setAlertThreshold(Integer.parseInt(cursor.getString(9)));
        }

        if (null != cursor) cursor.close();

        Log.d(TAG, "getDrug(" + cip13 + ")" + medicament.toString());

        return medicament;
    }

    /**
     *
     * @return a List of All medicaments presents in database
     */

    List<Medicament> getAllDrugs() {
        List<Medicament> medicaments = new LinkedList<Medicament>();

        // Build the query
        String query = "SELECT * FROM " + TABLE_DRUG;

        // Get reference to readable DB (tutorial parle de writable, mais bof... on verra)
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // For Each row, build a medicament and add it to the list
        Medicament medicament;
        if (cursor.moveToFirst()) {
            do {
                medicament = new Medicament();
                medicament.setId(Integer.parseInt(cursor.getString(0)));
                medicament.setCis(cursor.getString(1));
                medicament.setCip13(cursor.getString(2));
                medicament.setNom(cursor.getString(3));
                medicament.setMode_administration(cursor.getString(4));
                medicament.setPresentation(cursor.getString(5));
                medicament.setStock(Double.parseDouble(cursor.getString(6)));
                medicament.setPrise(Double.parseDouble(cursor.getString(7)));
                medicament.setWarnThreshold(Integer.parseInt(cursor.getString(8)));
                medicament.setAlertThreshold(Integer.parseInt(cursor.getString(9)));

                // Call calcul method
                medicament.setDateEndOfStock();
                medicament.setDateLastUpdate();

                // Add medicament to medicaments
                medicaments.add(medicament);
            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.d(TAG, "getAllDrugs " + medicaments.toString());

        return medicaments;
    }

    /**
     *
     * @param medicament object to be updated in DB
     * @return code of update operation (should be 0)
     */
    public int updateDrug(Medicament medicament) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add columnm/value
        ContentValues values = new ContentValues();
        values.put(KEY_CIS, medicament.getCis());
        values.put(KEY_CIP13, medicament.getCip13());
        values.put(KEY_NAME, medicament.getNom());
        values.put(KEY_ADMIN, medicament.getMode_administration());
        values.put(KEY_PRES, medicament.getPresentation());
        values.put(KEY_STOCK, medicament.getStock());

        // Update row
        int i = db.update(TABLE_DRUG,           // table
                values,                         // column/value
                KEY_ID+" = ?",       // selections
                new String[] {String.valueOf(medicament.getId()) } ); // selections args

        // Close DB
        db.close();

        return i;
    }

    /**
     * Delete a medicament object in database
     * @param medicament object to be delete in the DB
     */
    public void deleteDrug(Medicament medicament) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete record
        db.delete(TABLE_DRUG,           // table
                KEY_ID+ " = ?",         // selections
                new String[] { String.valueOf(medicament.getId()) } );  // selections args

        // Close DB
        db.close();

        // log
        Log.d(TAG, "delete drug "+medicament.toString());
    }

    /**
     * Get count of all medicament present in database
     * @return number of medicament in DB
     */
    int getCount() {

        String query = "SELECT count (*) FROM " + TABLE_DRUG;

        // Get reference to readable DB (tutorial parle de writable, mais bof... on verra)
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery(query, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);

        mCount.close();
        return count;
    }
}

