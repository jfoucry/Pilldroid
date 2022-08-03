package net.foucry.pilldroid;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by jfoucry on 5/25/16.
 */
class DBDrugs extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String dbName = "drugs.db";
    private static final String TABLE_NAME = "drugs";
    private static final String DRUG_CIS = "cis";
    private static final String DRUG_CIP13 = "cip13";
    private static final String DRUG_CIP7 = "cip7";
    private static final String DRUG_ADMIN = "administration_mode";
    private static final String DRUG_NAME = "name";
    private static final String DRUG_PRES = "presentation";
    private static final String[] COLUMNS_NAMES = {DRUG_CIS, DRUG_CIP13, DRUG_CIP7, DRUG_ADMIN, DRUG_NAME, DRUG_PRES};
    private static final String TAG = DBDrugs.class.getName();
    private final Context myContext;
    private final SQLiteDatabase myDataBase = null;


    DBDrugs(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public boolean isDBFileExist(File database) {
        try {
            myContext.getDatabasePath(String.valueOf(database));
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {

        File dbFile = myContext.getDatabasePath(dbName);

        if (!isDBFileExist(dbFile)) {
            copyDatabase(dbFile.getPath());
        }

        return super.getWritableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        File dbFile = myContext.getDatabasePath(dbName);

        PrefManager prefManager = new PrefManager(myContext);
        int oldVersion = prefManager.getDatabaseVersion();

        if (oldVersion == DATABASE_VERSION) return super.getReadableDatabase();
        copyDatabase(dbFile.getPath());
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void copyDatabase(String dbPath) {
        Log.d(TAG, "try to copy database");
        try {
            InputStream assetDB = myContext.getAssets().open(dbName);
            OutputStream appDB = new FileOutputStream(dbPath, false);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = assetDB.read(buffer)) > 0) {
                appDB.write(buffer, 0, length);
            }

            appDB.flush();
            appDB.close();
            assetDB.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        assert false;
        myDataBase.close();
    }

    /**
     * Lookup in the DB for a record corresponding to cip13
     *
     * @param cip13 string representing the object we're looking for
     * @return return a drug object
     */
    Drug getDrugByCIP13(String cip13) {
        Log.d(TAG, "CIP13 - " + cip13);

        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_NAME,               // Which table
                COLUMNS_NAMES,                             // column names
                " cip13 =?",                       // selections
                new String[]{cip13},                       // selections args
                null,                              // group by
                null,                               // having
                null,                              // order by
                null);                                // limits
        //Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where cip13 = " + cip13, null);
        if (cursor.getCount() != 0) {

            cursor.moveToFirst();

            // Build drug object
            Drug drug = new Drug();
            // drug.setId(Integer.parseInt(cursor.getString(0)));
            drug.setCis(cursor.getString(0));
            drug.setCip13(cursor.getString(1));
            drug.setAdministration_mode(cursor.getString(3));
            drug.setName(cursor.getString(4));
            drug.setPresentation(cursor.getString(5));

            // Set default values
            drug.setStock(0);
            drug.setTake(0);
            drug.setWarnThreshold(14);
            drug.setAlertThreshold(7);

            // Log
            Log.d(TAG, "getDrug(" + cip13 + ")" + drug);

            // Return drug

            cursor.close();
            return drug;
        } else
            return null;
    }

    String getCIP13FromCIP7(String cip7) {

        String cip13 = null;

        try {
            Cursor c = this.getReadableDatabase().rawQuery("SELECT cip13 FROM " + TABLE_NAME + " where cip7 = " + cip7, null);

            Log.d(TAG, "Cursor == " + DatabaseUtils.dumpCursorToString(c));

            c.moveToFirst();

            if (c.getCount() > 0) {
                cip13 = c.getString(0);
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cip13;
    }

    Drug getDrugByCIP7(String cip7) {
        Log.d(TAG, "CIP7 - " + cip7);

        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_NAME,               // Which table
                COLUMNS_NAMES,                             // column names
                " cip7 =?",                       // selections
                new String[]{cip7},                       // selections args
                null,                              // group by
                null,                               // having
                null,                              // order by
                null);                                // limits
        //Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where cip13 = " + cip13, null);
        if (cursor.getCount() != 0) {

            cursor.moveToFirst();

            // Build drug object
            Drug drug = new Drug();
            // drug.setId(Integer.parseInt(cursor.getString(0)));
            drug.setCis(cursor.getString(0));
            drug.setCip13(cursor.getString(1));
            drug.setAdministration_mode(cursor.getString(3));
            drug.setName(cursor.getString(4));
            drug.setPresentation(cursor.getString(5));

            // Set default values
            drug.setStock(0);
            drug.setTake(0);
            drug.setWarnThreshold(14);
            drug.setAlertThreshold(7);

            // Log
            Log.d(TAG, "getDrug(" + cip7 + ")" + drug);

            // Return drug

            cursor.close();
            return drug;
        } else
            return null;
    }
}
