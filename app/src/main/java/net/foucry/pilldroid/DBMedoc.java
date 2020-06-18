package net.foucry.pilldroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
class DBMedoc  extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static String dbName = "medicaments.db";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    private static final String TABLE_NAME  = "medicaments";
    private static final String MEDOC_CIS   = "cis";
    private static final String MEDOC_CIP13 = "cip13";
    private static final String MEDOC_ADMIN = "mode_administration";
    private static final String MEDOC_NOM   = "nom";
    private static final String MEDOC_PRES  = "presentation";

    private static final String[] COLUMNS_NAMES = {MEDOC_CIS, MEDOC_CIP13, MEDOC_ADMIN, MEDOC_NOM, MEDOC_PRES};

    private static final String TAG = DBMedoc.class.getName();


    DBMedoc(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {

        File dbFile = myContext.getDatabasePath(dbName);

        if (!dbFile.exists()) {
            SQLiteDatabase db = super.getWritableDatabase();
            copyDatabase(dbFile.getPath());
        }

        return  super.getWritableDatabase();
    }

    @Override
    public synchronized  SQLiteDatabase getReadableDatabase() {
        File dbFile = myContext.getDatabasePath(dbName);

        if (dbFile.exists()) return super.getReadableDatabase();

        SQLiteDatabase db = super.getReadableDatabase();
        copyDatabase(dbFile.getPath());
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void copyDatabase(String dbPath) {
        Log.d(TAG, "try to copy database");
        try {
            InputStream assetDB = myContext.getAssets().open(dbName);
            OutputStream appDB = new FileOutputStream(dbPath, false);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = assetDB.read(buffer)) > 0) {
                appDB.write(buffer,0, length);
            }

            appDB.flush();
            appDB.close();
            assetDB.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void openDatabase() throws SQLiteException {
        Log.e(TAG, "openDatabase called");
        String myPath = myContext.getDatabasePath(dbName).getPath();

        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
    }

    /**
     * Lookup in the DB for a record corresponding to cpi1
     * @param cip13 string representing the object we're looking for
     * @return return a medicament object
     */
    Medicament getMedocByCIP13(String cip13) {
        Log.e(TAG, "CIP13 - " + cip13);

        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_NAME,               // Which table
                COLUMNS_NAMES,                             // column names
                " cip13 =?",                               // selections
                new String[]{cip13},                       // selections args
                null,                                      // group by
                null,                                      // having
                null,                                      // order by
                null);                                     // limits

        if (cursor != null)
            cursor.moveToFirst();

        // Build medicament object
        Medicament medicament = new Medicament();
        // medicament.setId(Integer.parseInt(cursor.getString(0)));
        assert cursor != null;
        medicament.setCis(cursor.getString(0));
        medicament.setCip13(cursor.getString(1));
        medicament.setMode_administration(cursor.getString(2));
        medicament.setNom(cursor.getString(3));
        medicament.setPresentation(cursor.getString(4));
        /*medicament.setStock(Double.parseDouble(cursor.getString(5)));
        medicament.setPrise(Double.parseDouble(cursor.getString(6)));
        medicament.setWarnThreshold(Integer.parseInt(cursor.getString(7)));
        medicament.setAlertThreshold(Integer.parseInt(cursor.getString(8)));*/

        // Set default values
        medicament.setStock(0);
        medicament.setPrise(0);
        medicament.setWarnThreshold(14);
        medicament.setAlertThreshold(7);

        // Log
        Log.d(TAG, "getDrug(" + cip13 + ")" + medicament.toString());

        // Return medicament

        cursor.close();
        return medicament;
    }
}
